import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import client from "@/lib/backend/client";
import type { components } from "@/lib/backend/apiV1/schema";
import { useUsername } from "@/context/UsernameContext";
import { useNickname } from "@/context/NicknameContext";
import { CircleX } from "lucide-react";

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

const ChatWindow = ({
  senderName,
  senderUsername,
  onClose,
}: {
  senderName: string;
  senderUsername: string;
  onClose: () => void;
}) => {
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState("");
  const [chatRoomId, setChatRoomId] = useState<number | null>(null);
  const { username } = useUsername();
  const { nickname } = useNickname();

  // localStorage에서 직접 username을 가져옵니다
  const currentUsername = username || localStorage.getItem("username");

  useEffect(() => {
    // 채팅방 생성
    const createChatRoom = async () => {
      try {
        const response = await client.POST("/api/chatRooms", {
          body: {
            recipient_username: senderUsername,
            name: `${senderName}님과의 채팅`,
          },
        });

        if (response.error) {
          console.error("채팅방 생성 실패:", response.error);
          return;
        }

        setChatRoomId(response.data.data.id);
      } catch (error) {
        console.error("채팅방 생성 실패:", error);
      }
    };

    createChatRoom();
  }, [setChatRoomId]);

  useEffect(() => {
    if (!chatRoomId) return;

    const client = new Client({
      webSocketFactory: () =>
        new SockJS("http://localhost:8080/ws", null, {
          transports: ["websocket"],
        }),
      connectHeaders: {
        // JWT 토큰을 헤더에 추가
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      debug: function (str) {
        console.log("STOMP: " + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log("웹소켓 연결됨");
      client.subscribe(`/topic/chatRooms/${chatRoomId}`, (message) => {
        const receivedMessage = JSON.parse(message.body);
        setMessages((prev) => [...prev, receivedMessage]);
      });
    };

    client.onStompError = (frame) => {
      console.error("STOMP error:", frame);
    };

    client.activate();
    setStompClient(client);

    return () => {
      client.deactivate();
    };
  }, [chatRoomId]);

  const sendMessage = () => {
    if (stompClient && stompClient.connected && inputMessage.trim()) {
      const messageData = {
        chat_room_id: chatRoomId,
        sender_username: currentUsername,
        content: inputMessage,
      };

      console.log("Sending message:", messageData);

      stompClient.publish({
        destination: "/chat/sendMessage",
        body: JSON.stringify(messageData),
      });
      setInputMessage("");
    }
  };

  const handleClose = () => {
    if (stompClient && stompClient.connected && chatRoomId) {
      // 상대방에게 채팅방 나감 메시지 전송
      const leaveMessage = {
        chat_room_id: chatRoomId,
        sender_username: currentUsername,
        content: `${nickname}님이 채팅방을 나갔습니다.`,
      };

      stompClient.publish({
        destination: "/chat/sendMessage",
        body: JSON.stringify(leaveMessage),
      });

      // 웹소켓 연결 종료
      stompClient.deactivate();
    }

    // 채팅방 닫기
    onClose();
  };

  return (
    <div className="fixed bottom-4 right-4 w-80 h-96 bg-white shadow-lg rounded-lg flex flex-col border border-gray-200">
      <div className="p-4 border-b flex justify-between items-center text-black">
        <h3 className="font-bold">{senderName}님과의 채팅</h3>
        <CircleX
          size={32}
          className="cursor-pointer hover:text-gray-600"
          onClick={handleClose}
        />
      </div>

      <div className="flex-1 overflow-y-auto p-4">
        {messages.map((message) => (
          <div
            key={message.id}
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
          onKeyPress={(e) => e.key === "Enter" && sendMessage()}
        />
        <Button onClick={sendMessage}>전송</Button>
      </div>
    </div>
  );
};

export default ChatWindow;
