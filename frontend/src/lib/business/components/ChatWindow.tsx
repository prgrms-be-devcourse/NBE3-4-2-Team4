import { useEffect, useRef, useState, useCallback } from "react";
import { Button } from "@/components/ui/button";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import client from "@/lib/backend/client";
import { useNickname } from "@/context/NicknameContext";
import { CircleX } from "lucide-react";
import { toast } from "@/hooks/use-toast";

type ChatMessage = {
  id: number;
  createdAt: string;
  chatRoomId: number;
  senderId: number;
  senderName: string;
  content: string;
  isRead: boolean;
  readAt: string | null;
};

interface ChatWindowProps {
  senderName: string;
  senderUsername: string;
  senderId: number;
  onClose: () => void;
  onChatRoomCreated: (chatRoomId: number) => void;
  chatRoomId: number;
}

const ChatWindow = ({
  senderName,
  senderUsername,
  senderId,
  chatRoomId: initialChatRoomId = 0,
  onClose,
  onChatRoomCreated,
}: ChatWindowProps) => {
  const [chatRoomId, setChatRoomId] = useState<number>(0);
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [messages, setMessages] = useState<any[]>([]);
  const [inputMessage, setInputMessage] = useState("");
  const { nickname } = useNickname();
  const [myUsername, setMyUsername] = useState<string | null>(null);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const hasInitialized = useRef(false);
  const hasClosedRoom = useRef(false);
  const messageQueue = useRef<string[]>([]);

  const handleMessage = useCallback((message: any) => {
    try {
      const data = JSON.parse(message.body);

      setMessages((prev) => [...prev, data]);
    } catch (error) {
      toast({
        title: "메시지 처리 오류",
        description: error?.toString(),
      });
    }
  }, []);

  useEffect(() => {
    const initializeChatRoom = async () => {
      if (hasInitialized.current || hasClosedRoom.current) {
        return;
      }

      hasInitialized.current = true;

      try {
        // 기존 채팅방 ID가 있는 경우
        if (initialChatRoomId !== 0) {
          setChatRoomId(initialChatRoomId);

          return;
        }

        // 새 채팅방 생성
        const response = await client.POST("/api/chatRooms", {
          body: {
            recipient_username: senderUsername,
            name: `${senderName}님과의 채팅`,
          },
        });

        if (response.error) {
          toast({
            title: "채팅방 생성 실패",
            description: response.error.msg,
          });

          hasInitialized.current = false;

          return;
        }

        const newChatRoomId = response.data.data.id;

        setChatRoomId(newChatRoomId);
        onChatRoomCreated(newChatRoomId);
      } catch (error) {
        toast({
          title: "채팅방 초기화 실패",
          description: error?.toString(),
        });

        hasInitialized.current = false;
      }
    };

    initializeChatRoom();
  }, [initialChatRoomId, senderUsername, senderName, onChatRoomCreated]);

  useEffect(() => {
    if (
      !chatRoomId ||
      chatRoomId === 0 ||
      stompClient ||
      hasClosedRoom.current
    ) {
      console.log("STOMP 연결 건너뜀:", {
        chatRoomId,
        hasStompClient: !!stompClient,
        hasClosedRoom: hasClosedRoom.current,
      });
      return;
    }

    console.log("STOMP 연결 시도:", chatRoomId);

    const socket = new SockJS("http://localhost:8080/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${null}`,
      },
      debug: function (str) {
        console.log("STOMP: " + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      if (!hasClosedRoom.current) {
        client.subscribe(`/topic/chatRooms/${chatRoomId}`, handleMessage);
      }
    };

    client.onStompError = (frame) => {
      //console.error("STOMP 에러:", frame);
      toast({
        title: "채팅 연결 오류",
        description: "채팅 서버와의 연결에 실패했습니다.",
      });
    };

    client.onWebSocketError = (event) => {
      //console.error("WebSocket 에러:", event);
      toast({
        title: "채팅 연결 오류",
        description: "채팅 서버와의 연결에 실패했습니다.",
      });
    };

    try {
      client.activate();
      setStompClient(client);
    } catch (error) {
      //console.error("STOMP 클라이언트 활성화 실패:", error);
      toast({
        title: "STOMP 클라이언트 활성화 실패",
        description: error?.toString(),
      });
    }

    return () => {
      if (client.connected) {
        client.deactivate();
      }
    };
  }, [chatRoomId, handleMessage]);

  useEffect(() => {
    const fetchMyUsername = async () => {
      try {
        const response = await client.GET("/api/members/details");

        if (response.error) {
          toast({
            title: "사용자 정보 가져오기 실패",
            description: response.error.msg,
          });

          return;
        }

        const username = response.data.data.username;

        console.log("username", response.data);

        if (username) {
          setMyUsername(username);
          localStorage.setItem("username", username);
        }
      } catch (error) {
        //console.error("사용자 정보 가져오기 실패:", error);
        toast({
          title: "사용자 정보 가져오기 실패",
          description: error?.toString(),
        });
      }
    };

    // localStorage에 username이 없을 경우에만 API 요청
    const storedUsername = localStorage.getItem("username");

    console.log("storedUsername", storedUsername);

    if (!storedUsername) {
      fetchMyUsername();
    } else {
      setMyUsername(storedUsername);
    }
  }, []);

  const sendMessage = useCallback(
    (message: string) => {
      if (!stompClient?.connected || !chatRoomId) {
        console.error("메시지 전송 실패: 연결되지 않음", {
          connected: stompClient?.connected,
          chatRoomId,
        });
        return;
      }

      const messageData = {
        chat_room_id: chatRoomId,
        sender_username: myUsername,
        content: message,
      };

      console.log("myUsername", myUsername);

      //console.log("메시지 전송 시도:", { messageData, chatRoomId });

      const messageStr = JSON.stringify(messageData);

      if (messageQueue.current.includes(messageStr)) {
        console.log("중복 메시지 전송 방지:", messageStr);
        return;
      }

      try {
        stompClient.publish({
          destination: "/chat/sendMessage",
          body: messageStr,
        });
        messageQueue.current.push(messageStr);

        // 메시지 큐 정리 (일정 시간 후)
        setTimeout(() => {
          messageQueue.current = messageQueue.current.filter(
            (m) => m !== messageStr
          );
        }, 1000);

        // 입력창 초기화
        setInputMessage("");
      } catch (error) {
        //console.error("메시지 전송 실패:", error);
        toast({
          title: "메시지 전송 실패",
          description: "메시지를 전송하는데 실패했습니다.",
        });
      }
    },
    [stompClient, chatRoomId, myUsername]
  );

  const handleClose = useCallback(() => {
    if (hasClosedRoom.current) {
      console.log("이미 종료된 채팅방");
      return;
    }

    hasClosedRoom.current = true;

    if (stompClient?.connected && chatRoomId) {
      const leaveMessage = {
        chat_room_id: chatRoomId,
        sender_username: myUsername,
        content: `${nickname}님이 채팅방을 나갔습니다.`,
      };

      sendMessage(leaveMessage.content);
      stompClient.deactivate();
    }

    setChatRoomId(0);
    setMessages([]);
    setInputMessage("");
    setStompClient(null);
    onClose();
  }, [chatRoomId, myUsername, nickname, stompClient, onClose, sendMessage]);

  // cleanup effect
  useEffect(() => {
    return () => {
      if (!hasClosedRoom.current && stompClient?.connected) {
        handleClose();
      }
    };
  }, [handleClose]);

  return (
    <div className="fixed bottom-4 right-4 w-80 h-96 bg-white shadow-lg rounded-lg flex flex-col border border-gray-200 z-[1000]">
      <div className="p-4 border-b flex justify-between items-center text-black">
        <h3 className="font-bold">{senderName}님과의 채팅</h3>
        <CircleX
          size={32}
          className="cursor-pointer hover:text-gray-600"
          onClick={handleClose}
        />
      </div>

      <div className="flex-1 overflow-y-auto p-4">
        {messages.map((message, index) => (
          <div
            key={message.id || index}
            className={`mb-2 ${
              message.sender_name === nickname ? "text-right" : "text-left"
            }`}
          >
            <span
              className={`inline-block px-3 py-1 rounded-md text-sm
              ${
                message.sender_name === nickname
                  ? "bg-gray-800 text-white"
                  : "bg-gray-100 text-black"
              }
              `}
            >
              {message.content}
            </span>
          </div>
        ))}
      </div>

      <div className="p-4 border-t flex gap-2">
        <input
          type="text"
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          className="flex-1 border rounded-lg px-2 py-1"
          onKeyPress={(e) => {
            if (e.key === "Enter" && inputMessage.trim()) {
              sendMessage(inputMessage);
            }
          }}
        />
        <Button
          onClick={() => {
            if (inputMessage.trim()) {
              sendMessage(inputMessage);
            }
          }}
          disabled={!inputMessage.trim()}
        >
          전송
        </Button>
      </div>
    </div>
  );
};

export default ChatWindow;
