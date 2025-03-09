"use client";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { usePathname } from "next/navigation";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import Pagination2 from "@/lib/business/components/Pagination2";

type PageDtoMessageDto = components["schemas"]["PageDtoMessageDto"];
type MessageDto = components["schemas"]["MessageDto"];

interface ClientPageProps {
  receive: PageDtoMessageDto;
  send: PageDtoMessageDto;
}

export default function ClientPage({ receive, send }: ClientPageProps) {
  const router = useRouter();
  const currentPath = usePathname();
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState('received');
  const [viewMessage, setViewMessage] = useState<MessageDto | null>(null);
  const [selectedMessages, setSelectedMessages] = useState<number[]>([]);

  // 현재 탭에서 표시할 메시지 배열 가져오기
  const messages = activeTab === "received" ? receive : send;

  // 전체 선택 체크박스 상태
  const allChecked = messages.items!!.length > 0 && messages.items!!.every((msg) => selectedMessages.includes(msg.id!!));

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
      setSelectedMessages(messages.items!!.map((msg) => msg.id!!)); // 전체 선택
    }
  };

  const readMessage = async (message: MessageDto) => {
    if (activeTab === "sent" || message.checked) {
      return true; // 보낸 쪽지, 읽은 쪽지는 읽음 처리 x
    }

    const messageId = message.id!!;
    const success = await markMessagesAsRead([messageId]);
    if (success) {
      toast({
        title: "쪽지가 읽음 처리되었습니다.",
        variant: "default",
      });
      router.push(currentPath);
    } else {
      toast({
        title: "쪽지 읽기 실패",
        variant: "destructive",
      });
    }
  };

  // 상세보기 설정
  const handleViewMessage = (message: MessageDto) => {
    setViewMessage(message);
    readMessage(message); // 메시지 ID를 넘겨서 읽음 처리
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
        title: "삭제할 쪽지를 선택해주세요.",
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

      setSelectedMessages([]);
      window.location.reload();
    } catch (error) {
      console.error("쪽지 삭제 중 오류 발생:", error);
    }
  };

  const markMessagesAsRead = async (messageIds: number[]) => {    
    try {
      const response = await client.PUT("/api/messages", {
        body: messageIds,
      });
      if (response.error) {
        console.error("쪽지 읽기 실패:", response.error);
        return false;
      }
      return true;
    } catch (error) {
      console.error("쪽지 읽는 중 오류 발생:", error);
      return false;
    }
  };

  const handleReadMessages = async () => {
    if (selectedMessages.length === 0) {
      toast({
        title: "읽을 쪽지를 선택해주세요.",
        variant: "destructive",
      });
      return;
    }

    const success = await markMessagesAsRead(selectedMessages);
    if (success) {
      setSelectedMessages([]);
      toast({
        title: `${selectedMessages.length}개의 쪽지를 읽었습니다.`,
        variant: "default",
      });
    } else {
      toast({
        title: "쪽지 읽기 실패",
        variant: "destructive",
      });
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
          <Button
            className={`py-4 px-4 text-black rounded-none hover:bg-gray-300
            ${activeTab === 'received' ? 'bg-gray-300' : 'bg-gray-100'}`}
            onClick={() => {
              setActiveTab('received');
              setViewMessage(null);
            }}
          >
            받은 쪽지
          </Button>
          <Button
            className={`py-4 px-4 text-black rounded-none hover:bg-gray-300
            ${activeTab === 'sent' ? 'bg-gray-300' : 'bg-gray-100'}`}
            onClick={() => {
              setActiveTab('sent');
              setViewMessage(null);
            }}
          >
            보낸 쪽지
          </Button>
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
      <Table className="min-w-[1000px] w-full">

        {/* 헤더 부분 */}
        <TableHeader className="bg-gray-300">
          <TableRow>
            <TableHead className="w-[50px] text-center">
              <input
                type="checkbox"
                checked={allChecked}
                onChange={handleAllCheckboxChange}
              />
            </TableHead>
            <TableHead className="w-[400px] text-center">제목</TableHead>
            <TableHead className="w-[150px] text-center">
              {activeTab === 'received' ? "보낸 사람" : "받는 사람"}
            </TableHead>
            <TableHead className="w-[200px] text-center">작성 일시</TableHead>
            <TableHead className="w-[150px] text-center">수신 여부</TableHead>
          </TableRow>
        </TableHeader>

        {/* 받은 쪽지 목록 */}
        {activeTab === 'received' ? (
          <TableBody>
            {receive.items?.map((message) => (
              <TableRow key={message.id} className="px-4 py-1">
                <TableCell className="text-center">
                  <input
                    type="checkbox"
                    checked={selectedMessages.includes(message.id!!)}
                    onChange={() => handleCheckboxChange(message.id!!)}
                  />
                </TableCell>
                <TableCell className="w-[400px] font-medium text-center">
                  <Link href="" onClick={() => handleViewMessage(message)} className="text-blue-500">
                    {message.title}
                  </Link>
                </TableCell>
                <TableCell className="w-[150px] text-center">
                  {message.senderName}
                </TableCell>
                <TableCell className="w-[200px] text-center">
                  {formatDate(message.createdAt!!)}
                </TableCell>
                <TableCell className="w-[150px] text-center">
                  {message.checked ? "읽음" : "읽지않음"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        ) : (
          <TableBody>
            {/* 보낸 쪽지 목록 */}
            {send.items?.map((message) => (
              <TableRow key={message.id} className="px-4 py-1">
                <TableCell className="text-center">
                  <input
                    type="checkbox"
                    checked={selectedMessages.includes(message.id!!)}
                    onChange={() => handleCheckboxChange(message.id!!)}
                  />
                </TableCell>
                <TableCell className="w-[400px] font-medium text-center">
                  <Link href="" onClick={() => handleViewMessage(message)} className="text-blue-500">
                    {message.title}
                  </Link>
                </TableCell>
                <TableCell className="w-[150px] text-center">
                  {message.receiverName}
                </TableCell>
                <TableCell className="w-[200px] text-center">
                  {formatDate(message.createdAt!!)}
                </TableCell>
                <TableCell className="w-[150px] text-center">
                  {message.checked ? "읽음" : "읽지않음"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        )}
      </Table>

      {/* 페이지 이동 버튼 */}
      {activeTab === 'received' && <Pagination2 totalPages={receive.totalPages ?? 0} />}
      {activeTab === 'sent' && <Pagination2 totalPages={send.totalPages ?? 0} />}
    </div>
  );
}