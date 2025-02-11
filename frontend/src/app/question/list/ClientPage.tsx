"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { useToast } from "@/hooks/use-toast";
import { formatDate } from "@/utils/dateUtils";
import { useId } from "@/context/IdContext";
import { MessageCircle, ThumbsUp, Banknote, Coins, Star } from "lucide-react";
import Link from "next/link";
import { Label } from "@/components/ui/label";
import {
  SelectTrigger,
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { number } from "zod";
import Pagination2 from "@/lib/business/components/Pagination2";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

type QuestionDto = components["schemas"]["QuestionDto"];
type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

interface ClientPageProps {
  body: PageDtoQuestionDto;
  category: components["schemas"]["QuestionCategoryDto"][];
}

export default function ClientPage({ body, category }: ClientPageProps) {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [searchKeyword, setSearchKeyword] = useState(
    searchParams.get("searchKeyword") || ""
  );
  const [keywordType, setKeywordType] = useState(
    searchParams.get("keywordType") || "ALL"
  );

  const options = ["전체", "제목", "내용", "작성자", "답변 내용"];
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(options[0]);

  const optionMapping: { [key: string]: string } = {
    전체: "ALL",
    제목: "TITLE",
    내용: "CONTENT",
    작성자: "AUTHOR",
    "답변 내용": "ANSWER_CONTENT",
  };

  const { id } = useId();
  const { toast } = useToast();

  const [categoryValue, setCategoryValue] = useState("전체");

  const toggleDropdown = () => {
    // 검색 조건 드롭다운
    setIsOpen(!isOpen);
  };

  const selectOption = (option: string) => {
    setSelectedOption(option);
    setKeywordType(optionMapping[option]);
    setIsOpen(false); // 선택 후 드롭다운 닫기
  };

  // 검색 실행 함수
  const handleSearch = () => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1"); // 검색 시 1페이지부터 시작

    if (searchKeyword) queryParams.set("searchKeyword", searchKeyword);
    //if (keywordType) queryParams.set("keywordType", keywordType);
    if (keywordType) queryParams.set("keywordType", keywordType);

    setCategoryValue("전체");
    router.push(`?${queryParams.toString()}`);
  };

  const handleCategorySearch = (value: string) => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1");
    if (value !== "전체") {
      queryParams.set("categoryId", value);
    } else {
      queryParams.set("categoryId", "0");
    }
    router.push(`?${queryParams.toString()}`);
    setCategoryValue(value);
  };

  const createQuestion = () => {
    if (!id) {
      toast({
        title: "로그인 후 이용해주세요.",
        variant: "destructive",
      });
      return;
    }
    router.push("/question/write");
  };

  const showRanking = () => {
    router.push("/question/rank");
  };

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          지식인 게시판
        </h2>
        <p className="text-md text-gray-400 mt-3">
          지식을 나누고 포인트도 얻으세요!
          <br />
          함께 성장하는 지식 공유 공간입니다.
        </p>
      </div>
      {/* <div className="flex items-center justify-between mb-2">
        <button
          className="border-2 border-cyan-500 bg-cyan-500 text-white px-2 font-bold rounded-md hover:bg-cyan-600"
          onClick={showRanking}
        >
          인기글 보기
        </button> 
      </div> */}

      <div className="flex mb-4 md:flex-row flex-col gap-2 md:items-center items-start justify-between">
        <div className="flex gap-2">
          <Button onClick={createQuestion}>글쓰기</Button>
          <div>
            <Select onValueChange={handleCategorySearch} value={categoryValue}>
              <SelectTrigger className="md:w-[180px] w-[100px]" id="category">
                <SelectValue placeholder="카테고리로 검색" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="전체">전체</SelectItem>
                {category.map((item) => (
                  <SelectItem key={item.id} value={String(item.id)}>
                    {item.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
        <div className="flex gap-2">
          {/* 검색 입력창 */}
          {/* <div className="relative">
            <button
              className="px-4 py-2 border rounded-md flex items-center justify-between w-40"
              onClick={toggleDropdown}
            >
              {selectedOption ? selectedOption : "선택하세요"}
              <span className="ml-2">&#9662;</span>
            </button>
            {isOpen && (
              <ul className="absolute bg-white border rounded shadow w-full mt-2">
                {options.map((option, index) => (
                  <li
                    key={index}
                    className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    onClick={() => selectOption(option)}
                  >
                    {option}
                  </li>
                ))}
              </ul>
            )}
          </div> */}
          <div>
            <Select
              value={selectedOption}
              onValueChange={(value) => {
                setSelectedOption(value);
                setKeywordType(optionMapping[value]);
              }}
            >
              <SelectTrigger className="md:w-[180px] w-[100px]" id="category">
                <SelectValue placeholder="전체" />
              </SelectTrigger>
              <SelectContent>
                {options.map((option, index) => (
                  <SelectItem key={index} value={option}>
                    {option}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <Input
            type="text"
            placeholder="검색어을 입력하세요"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <Button onClick={handleSearch}>검색</Button>
        </div>
      </div>

      <ul className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {body.items?.map((item: QuestionDto) => (
          <li key={item.id}>
            <Link href={`/question/${item.id}`}>
              <Card className="hover:shadow-[0_0_10px_0_rgba(0,0,0,0.2)] hover:dark:shadow-[0_0_10px_0_rgba(255,255,255,0.3)] transition-colors">
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Badge
                      variant="secondary"
                      className="flex items-center gap-2 grow-0 shrink-0"
                    >
                      {item.categoryName}
                    </Badge>
                    <span className="overflow-hidden text-ellipsis whitespace-nowrap">
                      {item.title}
                    </span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="flex items-center space-x-4">
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-foreground">
                        {item.name}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {formatDate(item.createdAt)}
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="flex items-center gap-1 text-amber-500">
                        <Coins size={16} />
                        {item.point}
                      </div>
                      {(item.recommendCount ?? 0) > 0 && (
                        <span className="flex items-center gap-1 text-sky-400 font-medium">
                          <ThumbsUp size={16} />
                          {item.recommendCount}
                        </span>
                      )}
                      {(item.answers?.length ?? 0) > 0 && (
                        <span className="flex items-center gap-1 text-teal-500 font-medium">
                          <MessageCircle size={16} />
                          {item.answers?.length}
                        </span>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            </Link>
          </li>
        ))}
      </ul>

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={body.totalPages ?? 0} />
    </div>
  );
}
