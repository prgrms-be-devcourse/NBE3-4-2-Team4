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
import NameButton from "@/lib/business/components/NameButton";
import { Clock } from "lucide-react";

type PageDtoMessageDto = components["schemas"]["PageDtoMessageDto"];
type MessageDto = components["schemas"]["MessageDto"];

interface ClientPageProps {
  data: PageDtoMessageDto;
  activeTab: 'received' | 'sent';
  setActiveTab: React.Dispatch<React.SetStateAction<'received' | 'sent'>>;
}

export default function ClientPage({ data, activeTab, setActiveTab }: ClientPageProps) {
  const router = useRouter();
  const currentPath = usePathname();
  const { toast } = useToast();
  const [viewMessage, setViewMessage] = useState<MessageDto | null>(null);
  const [selectedMessages, setSelectedMessages] = useState<number[]>([]);

  // 전체 선택 체크박스 상태
  const allChecked = data.items!!.length > 0 && data.items!!.every((msg) => selectedMessages.includes(msg.id!!));

  // 개별 체크박스 상태 변경 핸들러
  const handleCheckboxChange = (id: number) => {
    setSelectedMessages((prevSelected) =>
      prevSelected.includes(id)
        ? prevSelected.filter((msgId) => msgId !== id)
        : [...prevSelected, id]
    );
  };

  // 전체 체크박스 상태 변경 핸들러
  const handleAllCheckboxChange = () => {
    if (allChecked) {
      setSelectedMessages([]); // 전체 해제
    } else {
      setSelectedMessages(data.items!!.map((msg) => msg.id!!)); // 전체 선택
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
      localStorage.setItem("senderName", senderName);
    }
    router.push("/message/write");
  };

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
      selectedMessages.forEach((msgId) => {
        const message = data.items?.find((msg) => msg.id === msgId);
        message!!.checked = true;
      });
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
    router.replace(`/message`, { scroll: false }); // page 파라미터를 제거
    setSelectedMessages([]); // 탭이 변경될 때 선택된 메시지 초기화
  }, [activeTab]);

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          쪽지 보관함
        </h2>
      </div>

      {/* 선택된 메시지 표시 영역 */}
      {viewMessage && (
        <div className="p-5 mb-10 border rounded-lg shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
          <h2 className="flex justify-between text-xl border-b border-gray-200 border-dashed pb-5 font-semibold">
            {viewMessage.title}
            <span className="flex items-center text-sm text-gray-400 font-light">
              <Clock width={14} height={14} className="mr-1" />
              {formatDate(viewMessage.createdAt!!)}
            </span>
          </h2>
          <div className="flex justify-end mt-4">
            <p className="text-sm text-gray-400">
              <span className="">
                {activeTab === "received" ? "보낸 사람" : "받는 사람"}
              </span>
              <span className="text-gray-600 font-semibold ml-2 pl-2 relative before:content-[''] before:mr-1 before:absolute before:left-0 before:top-1/2 before:-translate-y-1/2 before:w-[1px] before:h-[12px] before:bg-gray-300">
                {activeTab === "received"
                  ? viewMessage.senderName
                  : viewMessage.receiverName}
              </span>
            </p>
          </div>
          <p className="mt-2 text-m mt-5">{viewMessage.content}</p>
          <div className="flex justify-end gap-2">
            {activeTab === "received" && (
              <div className="flex justify-end gap-2">
                <Button
                  className="mt-3 w-13 h-8"
                  onClick={() => writeMessage(viewMessage.senderName!!)}
                >
                  답장하기
                </Button>
              </div>
            )}
            <Button
              className="mt-3 w-12 h-8"
              onClick={handleCloseDetail}
              variant="outline"
            >
              닫기
            </Button>
          </div>
        </div>
      )}

      <div className="flex justify-between">
        <div className="flex gap-1">
          <Button
            className={`py-6 px-10 text-black rounded-t rounded-b-none text-md relative z-10 text-white
            ${
              activeTab === "received"
                ? "before:content-[''] before:absolute before:left-1/2 before:-bottom-[6px] before:w-3 before:h-3 before:bg-gray-900 before:transform before:rotate-45 before:-translate-x-1/2 hover:bg-gray-900 bg-gray-900"
                : "hover:bg-gray-300 bg-gray-300"
            }`}
            onClick={() => {
              setActiveTab("received");
              setViewMessage(null);
            }}
          >
            받은 쪽지
          </Button>
          <Button
            className={`py-6 px-10 text-black rounded-t rounded-b-none text-md relative z-10 text-white
            ${
              activeTab === "sent"
                ? "before:content-[''] before:absolute before:left-1/2 before:-bottom-[6px] before:w-3 before:h-3 before:bg-gray-900 before:transform before:rotate-45 before:-translate-x-1/2 hover:bg-gray-900 bg-gray-900"
                : "hover:bg-gray-300 bg-gray-300"
            }`}
            onClick={() => {
              setActiveTab("sent");
              setViewMessage(null);
            }}
          >
            보낸 쪽지
          </Button>
        </div>
        {selectedMessages.length > 0 && (
          <div className="flex gap-2 items-center">
            <span>{selectedMessages.length + "개가 선택됨"}</span>
            {activeTab === "received" && (
              <Button onClick={handleReadMessages} variant="outline">
                읽기
              </Button>
            )}
            <Button onClick={handleDeleteMessages} variant="destructive">
              삭제
            </Button>
          </div>
        )}
      </div>

      {/* 탭 내용 영역 */}
      <Table className="min-w-[1000px] w-full border-t-2 border-gray-900 border-b-2">
        {/* 헤더 부분 */}
        <TableHeader className="bg-white">
          <TableRow className="hover:bg-white">
            <TableHead className="w-[50px] text-center py-3 text-lg text-black border-b border-gray-900">
              <input
                type="checkbox"
                checked={allChecked}
                onChange={handleAllCheckboxChange}
              />
            </TableHead>
            <TableHead className="w-[400px] text-center py-3 text-lg text-black border-b border-gray-900">
              제목
            </TableHead>
            <TableHead className="w-[150px] text-center py-3 text-lg text-black border-b border-gray-900">
              {activeTab === "received" ? "보낸 사람" : "받는 사람"}
            </TableHead>
            <TableHead className="w-[200px] text-center py-3 text-lg text-black border-b border-gray-900">
              작성 일시
            </TableHead>
            <TableHead className="w-[150px] text-center py-3 text-lg text-black border-b border-gray-900">
              수신 여부
            </TableHead>
          </TableRow>
        </TableHeader>

        {/* 쪽지 목록 */}
        <TableBody>
          {data.items?.map((message) => (
            <TableRow key={message.id} className="px-4 py-1">
              <TableCell className="text-center">
                <input
                  type="checkbox"
                  checked={selectedMessages.includes(message.id!!)}
                  onChange={() => handleCheckboxChange(message.id!!)}
                />
              </TableCell>
              <TableCell className="w-[400px] font-medium text-center">
                <Link href="" 
                onClick={() => {
                  handleViewMessage(message)
                  message.checked = true
                }}
                className="text-blue-500">
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
      </Table>

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={data.totalPages ?? 0} />
    </div>
  );
}
