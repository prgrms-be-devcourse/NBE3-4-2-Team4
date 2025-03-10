import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import client from "@/lib/backend/client";
import type { components } from "@/lib/backend/apiV1/schema";

type ChatMessage = {
  chatRoomId: number;
  content: string;
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

  useEffect(() => {
    // 채팅방 생성
    const createChatRoom = async () => {
      try {
        const response = await client.POST("/api/chatRooms", {
          body: {
            recipient_username: senderUsername,
            name: `${senderName}과의 채팅`,
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
    if (!chatRoomId) return; // chatRoomId가 없으면 연결 시도하지 않음

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
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
      stompClient.publish({
        destination: "/chat/sendMessage",
        body: JSON.stringify({
          chatRoomId: chatRoomId,
          content: inputMessage,
        }),
      });
      setInputMessage("");
    }
  };

  return (
    <div className="fixed bottom-4 right-4 w-80 h-96 bg-white shadow-lg rounded-lg flex flex-col">
      <div className="p-4 border-b flex justify-between items-center">
        <h3 className="font-bold">{senderName}과의 채팅</h3>
        <Button variant="ghost" size="sm" onClick={onClose}>
          X
        </Button>
      </div>

      <div className="flex-1 overflow-y-auto p-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`mb-2 ${
              message.sender === "user" ? "text-right" : "text-left"
            }`}
          >
            <span className="inline-block p-2 rounded-lg bg-blue-100">
              {message.text}
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
