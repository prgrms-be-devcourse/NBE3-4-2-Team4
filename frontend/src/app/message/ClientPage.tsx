"use client";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import { Button } from "@/components/ui/button";
import Link from "next/link";

type MessageDto = components["schemas"]["MessageDto"];

interface ClientPageProps {
  receive: MessageDto[];
  send: MessageDto[];
}

export default function ClientPage({ receive, send }: ClientPageProps) {
  const router = useRouter();
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState('received');
  const [viewMessage, setViewMessage] = useState<MessageDto | null>(null);
  const [selectedMessages, setSelectedMessages] = useState<number[]>([]);

  // 현재 탭에서 표시할 메시지 배열 가져오기
  const messages = activeTab === "received" ? receive : send;

  // 전체 선택 체크박스 상태
  const allChecked = messages.length > 0 && messages.every((msg) => selectedMessages.includes(msg.id!!));

  // 개별 체크박스 상태 변경 핸들러
  const handleCheckboxChange = (id: number) => {
    setSelectedMessages((prevSelected) =>
      prevSelected.includes(id) ? prevSelected.filter((msgId) => msgId !== id) : [...prevSelected, id]
    );
  };

  // 전체 체크박스 상태 변경 핸들러
  const handleAllCheckboxChange = () => {
    if (allChecked) {
      setSelectedMessages([]); // 전체 해제
    } else {
      setSelectedMessages(messages.map((msg) => msg.id!!)); // 전체 선택
    }
  };

  // 메시지 클릭 시 상세 보기 설정
  const handleViewMessage = (message: MessageDto) => {
    setViewMessage(message);
  };

  // 상세 보기 닫기
  const handleCloseDetail = () => {
    setViewMessage(null);
  };

  const writeMessage = (senderName: string) => {
    if (senderName) {
      localStorage.setItem('senderName', senderName);
    }
    router.push("/message/write");
  }

  const handleDeleteMessages = async () => {
    if (selectedMessages.length === 0) {
      toast({
        title: "삭제할 메시지를 선택해주세요.",
        variant: "destructive",
      });
      return;
    }

    try {
      const response = await client.DELETE("/api/messages", {
        body: selectedMessages,
      });
      if (response.error) {
        console.error("쪽지 삭제 실패:", response.error);
        return;
      }

      toast({
        title: `${selectedMessages.length}개의 쪽지가 삭제되었습니다.`,
        variant: "default",
      });
      setSelectedMessages([]);
      window.location.reload();
    } catch (error) {
      console.error("쪽지 삭제 중 오류 발생:", error);
    }
  };

  const handleReadMessages = async () => {
    if (selectedMessages.length === 0) {
      toast({
        title: "읽을 메시지를 선택해주세요.",
        variant: "destructive",
      });
      return;
    }

    try {
      const response = await client.PUT("/api/messages", {
        body: selectedMessages,
      });
      if (response.error) {
        console.error("쪽지 읽기 실패:", response.error);
        return;
      }

      toast({
        title: `${selectedMessages.length}개의 쪽지를 읽었습니다.`,
        variant: "default",
      });
      setSelectedMessages([]);
      window.location.reload();
    } catch (error) {
      console.error("쪽지 읽는 중 오류 발생:", error);
    }
  };

  useEffect(() => {
    setSelectedMessages([]); // 탭이 변경될 때 선택된 메시지 초기화
  }, [activeTab]);

  return (
    <div className="container mx-auto px-4">
      <h1 className="text-xl font-semibold mb-4">쪽지 보관함</h1>

      {/* 선택된 메시지 표시 영역 */}
      {viewMessage && (
        <div className="p-4 mb-4 border rounded-lg bg-gray-100">
          <h2 className="text-lg font-semibold">{viewMessage.title}</h2>
          <div className="flex justify-between mt-2">
            <p className="text-sm text-gray-500">
              {activeTab === "received" ? `보낸 사람: ${viewMessage.senderName}` : `받는 사람: ${viewMessage.receiverName}`}
            </p>
            <p className="text-sm text-gray-400">{"작성 일시: " + formatDate(viewMessage.createdAt!!)}</p>
          </div>
          <p className="mt-2 text-m mt-5">{viewMessage.content}</p>
          <div className="flex justify-end gap-2">
            {activeTab === "received" && (
              <div className="flex justify-end gap-2">
                <Button className="mt-3 w-13 h-8" onClick={() => writeMessage(viewMessage.senderName!!)}>
                  답장하기
                </Button>
              </div>
            )}
            <Button className="mt-3 w-12 h-8" onClick={handleCloseDetail}>
              닫기
            </Button>
          </div>
        </div>
      )}

      <div className="flex justify-between">
        <div className="flex space-x-4">
          <button
            className={`py-2 px-4 rounded-t-lg 
            ${activeTab === 'received' ? 'bg-gray-300' : 'bg-gray-100'}`}
            onClick={() => {
              setActiveTab('received');
              setViewMessage(null);
            }}
          >
            받은 쪽지
          </button>
          <button
            className={`py-2 px-4 rounded-t-lg 
            ${activeTab === 'sent' ? 'bg-gray-300' : 'bg-gray-100'}`}
            onClick={() => {
              setActiveTab('sent');
              setViewMessage(null);
            }}
          >
            보낸 쪽지
          </button>
        </div>
        {selectedMessages.length > 0 && (
          <div className="flex gap-2 items-center">
            <span>{selectedMessages.length + "개가 선택됨"}</span>
            {activeTab === 'received' && (
              <Button className="bg-blue-400 h-8" onClick={handleReadMessages}>읽기</Button>
            )}
            <Button className="bg-red-500 h-8" onClick={handleDeleteMessages}>삭제</Button>
          </div>
        )}
      </div>

      {/* 탭 내용 영역 */}
      <div className="border border-t-0 rounded-b-lg">

        {/* 헤더 부분 */}
        <div className="flex py-2 border-b font-semibold justify-between px-4 bg-gray-300">
          <input type="checkbox" checked={allChecked} onChange={handleAllCheckboxChange} />
          <span className="flex-3 text-center">제목</span>
          <span className="flex-2 text-center">{activeTab === 'received' ? "보낸 사람" : "받는 사람"}</span>
          <span className="flex-2 text-center">작성 일시</span>
          <span className="flex-2 text-center">수신 여부</span>
        </div>

        {/* 받은 쪽지 목록 */}
        {activeTab === 'received' ? (
          <div>
            {receive.map((message) => (
              <div key={message.id} className="flex px-4 py-2 border-b w-full justify-between">
                <input
                  type="checkbox"
                  checked={selectedMessages.includes(message.id!!)}
                  onChange={() => handleCheckboxChange(message.id!!)}
                  className="mr-4"
                />
                <Link href="" onClick={() => handleViewMessage(message)} className="flex gap-[200px] mx-2">
                  <h3 className="flex-3 font-medium text-center">{message.title}</h3>
                  <p className="flex-2 text-sm text-gray-500 text-center">{message.senderName}</p>
                  <p className="flex-2 text-sm text-gray-400 text-center">{formatDate(message.createdAt!!)}</p>
                  <p className="flex-2 text-sm text-gray-500 text-center">{message.checked ? "읽음" : "읽지않음"}</p>
                </Link>
              </div>
            ))}
          </div>
        ) : (
          <div>
            {/* 보낸 쪽지 목록 */}
            {send.map((message) => (
              <div key={message.id} className="flex px-4 py-2 border-b w-full justify-between">
                <input
                  type="checkbox"
                  checked={selectedMessages.includes(message.id!!)}
                  onChange={() => handleCheckboxChange(message.id!!)}
                  className="mr-4"
                />
                <Link href="" onClick={() => handleViewMessage(message)} className="flex gap-[200px] mx-2">
                  <h3 className="flex-3 font-medium text-center">{message.title}</h3>
                  <p className="flex-2 text-sm text-gray-500 text-center">{message.receiverName}</p>
                  <p className="flex-2 text-sm text-gray-400 text-center">{formatDate(message.createdAt!!)}</p>
                  <p className="flex-2 text-sm text-gray-500 text-center">{message.checked ? "읽음" : "읽지않음"}</p>
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}