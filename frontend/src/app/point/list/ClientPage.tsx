"use client";

import { format } from "date-fns";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import PayInfoForm from "./PayInfoForm";
import TransferForm from "./TransferForm";
import AttendanceButton from "./AttendanceButton";
import { Calendar } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import Pagination1 from "@/lib/business/components/Pagination1";
import { useToast } from "@/hooks/use-toast";
import { components } from "@/lib/backend/apiV1/schema";

type PageDtoPointHistoryRes = components["schemas"]["PageDtoPointHistoryRes"];
type PointHistoryRes = components["schemas"]["PointHistoryRes"];

function formatDate(date: string) {
  return format(new Date(date), "yyyy년 MM월 dd일 HH:mm:ss");
}

export default function ClientPage({
  body,
  point,
  cash,
}: {
  body: PageDtoPointHistoryRes;
  point: number;
  cash : number;
}) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const data: PointHistoryRes = body.data;
  const { toast } = useToast();

  const currentPage = Number(searchParams.get("page")) || 1;
  const selectedCategory = String(searchParams.get("assetCategory") || "");
  const selectedType = String(searchParams.get("assetType") || "");

  const handleCategoryChange = (value: string) => {
    const newCategory = value === "전체" ? "" : value;

    const queryParams = new URLSearchParams();

    if (newCategory) {
      queryParams.set("assetCategory", newCategory);
    } else {
      queryParams.delete("assetCategory");
    }

    router.push(`?${queryParams.toString()}`);
  };

    const handleTypeChange = (value: string) => {
      const newType = value === "전체" ? "" : value;

      const queryParams = new URLSearchParams();

      if (newType) {
        queryParams.set("assetType", newType);
      } else {
        queryParams.delete("assetType");
      }

      router.push(`?${queryParams.toString()}`);
    };

  //날짜 부분

  const today = new Date().toISOString().split("T")[0];

  const pastDate = new Date();
  pastDate.setDate(pastDate.getDate() - 30);
  const formattedPastDate = pastDate.toISOString().split("T")[0];

  const initialStartDate = searchParams.get("startDate") || formattedPastDate;
  const initialEndDate = searchParams.get("endDate") || today;

  const [startDate, setStartDate] = useState(initialStartDate);
  const [endDate, setEndDate] = useState(initialEndDate);

  const handleEndDateChange = (value: string) => {
    setEndDate(value);
  };

  const handleStartDateChange = (value: string) => {
    setStartDate(value);
  };

  const applyFilters = (e) => {
    const queryParams = new URLSearchParams(searchParams.toString());

    const start = new Date(startDate);
    const end = new Date(endDate);
    const today = new Date();

    if (end > today) {
      e.preventDefault();
      //alert("끝날짜가 현재 이후일 수 없습니다");
      toast({
        title: "검색 실패",
        description: "끝날짜가 현재 이후일 수 없습니다",
        variant: "destructive",
      });
      return;
    }

    const diffDays = Math.ceil(
      (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)
    );

    if (diffDays < 30) {
      e.preventDefault();
      //alert("시작과 끝날짜가 30일 이상이어야 합니다");
      toast({
        title: "검색 실패",
        description: "시작과 끝날짜가 30일 이상이어야 합니다",
        variant: "destructive",
      });
      return;
    }

    queryParams.set("startDate", startDate);
    queryParams.set("endDate", endDate);
    queryParams.set("page", 1);

    router.push(`?${queryParams.toString()}`);
  };

  console.log(currentPage, data.hasMore);
  return (
    <div className="container mx-auto px-4 flex flex-col gap-6">
      <div className="mt-20 mb-10 text-center relative">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          포인트
        </h2>
        <p className="text-md text-gray-400 mt-3">
          여러분의 활동이 포인트가 됩니다. <br />
          다양한 활동으로 포인트를 얻어보세요.
        </p>
        <div className="sm:absolute sm:right-0 sm:top-1/2 sm:-translate-y-1/2 sm:mt-0 mt-5">
          <AttendanceButton />
        </div>
      </div>
      <PayInfoForm point={point} cash={cash} />
      <TransferForm point={point} cash={cash} />
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar size={16} />
            기간 별 포인트 내역 조회
          </CardTitle>
        </CardHeader>
        <CardContent className="flex gap-3 sm:flex-row flex-col sm:items-center items-start sm:flex-row flex-col">
          {/* 시작 날짜 선택 */}
          <div>
            <Input
              id="startDate"
              type="date"
              className="md:w-[250px] w-auto"
              value={startDate}
              onChange={(e) => handleStartDateChange(e.target.value)}
              max={today} // 시작 날짜는 오늘까지 선택 가능
            />
          </div>

          <span>~</span>

          {/* 끝 날짜 선택 */}
          <div>
            <Input
              id="endDate"
              type="date"
              className="md:w-[250px] w-auto"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              min={startDate} // 끝 날짜는 시작 날짜 이후여야 함
              max={today} // 끝 날짜는 오늘을 초과할 수 없음
            />
          </div>

          {/* 적용 버튼 */}
          <Button onClick={applyFilters} className="sm:w-auto w-full">
            검색
          </Button>
        </CardContent>
      </Card>
      <div className="flex flex-col gap-2">
        <div className="flex gap-2 justify-between items-center">
          <span className="text-2xl font-bold">포인트 내역 보기</span>
          <Select
            value={selectedCategory}
            onValueChange={(value: string) => handleCategoryChange(value)}
          >
            <SelectTrigger className="md:w-[180px] w-[120px]" id="category">
              <SelectValue placeholder="카테고리별 검색" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="전체">전체</SelectItem>
              <SelectItem value="ANSWER">답변채택</SelectItem>
              <SelectItem value="PURCHASE">구매</SelectItem>
              <SelectItem value="TRANSFER">송금</SelectItem>
              <SelectItem value="ATTENDANCE">출석</SelectItem>
              <SelectItem value="ADMIN">관리자</SelectItem>
              <SelectItem value="QUESTION">질문등록</SelectItem>
              <SelectItem value="EXPIRED_QUESTION">만료된 질문</SelectItem>
              <SelectItem value="REFUND">포인트 반환</SelectItem>
              <SelectItem value="RANKING">랭킹</SelectItem>
            </SelectContent>
          </Select>

                 <Select
                      value={selectedType}
                      onValueChange={(value: string) => handleTypeChange(value)}
                    >
                      <SelectTrigger className="md:w-[180px] w-[120px]" id="category">
                        <SelectValue placeholder="재화 타입으로 검색" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="전체">전체</SelectItem>
                        <SelectItem value="POINT">포인트</SelectItem>
                        <SelectItem value="CASH">캐시</SelectItem>
                      </SelectContent>
                 </Select>
        </div>

        <ul>
          {body.data.items?.map((item: PointHistoryRes, index: number) => (
            <li
              key={index}
              className={`py-5 ${
                index === 0
                  ? "border-none"
                  : "border-t border-gray-300 border-dashed"
              }`}
            >
              <div className="flex justify-between sm:flex-row flex-col sm:items-center items-start">
                <div>
                  <div className="flex items-center gap-5">
                    <div className="text-lg font-bold">
                      {item.assetCategory}
                    </div>
                    {item.counterAccountUsername && (
                      <Badge variant="outline">
                        수신자 {item.counterAccountUsername}
                      </Badge>
                    )}
                  </div>
                  <div className="flex items-center gap-2 mt-1">
                    <div className="text-xs text-gray-400 flex items-center gap-1">
                      {formatDate(item.createdAt)}
                    </div>
                  </div>
                </div>
                <div
                  className={`text-3xl my-3 font-bold ${
                    item.amount >= 0 ? "text-sky-400" : "text-rose-500"
                  }`}
                >
                  {`${item.amount >= 0 ? "+" : ""}${item.amount} ${item.assetType}`}
                </div>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {/* 페이지 이동 버튼 */}
      <Pagination1 totalPages={data.totalPages ?? 0} />
    </div>
  );
}
