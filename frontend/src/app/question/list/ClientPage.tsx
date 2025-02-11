"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { useToast } from "@/hooks/use-toast";
import { formatDate } from "@/utils/dateUtils";
import { useId } from "@/context/IdContext";
import { MessageCircle, ThumbsUp, Banknote } from "lucide-react";
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
      <div className="flex items-center justify-between mb-2">
        <h2>지식인 게시판</h2>
        <button
          className="border-2 border-cyan-500 bg-cyan-500 text-white px-2 font-bold rounded-md hover:bg-cyan-600"
          onClick={showRanking}
        >
          인기글 보기
        </button>
      </div>

      <hr />
      <br />

      <div className="flex justify-between items-center">
        <div className="flex gap-2">
          <Button onClick={createQuestion}>글쓰기</Button>
          <div>
            <Select onValueChange={handleCategorySearch} value={categoryValue}>
              <SelectTrigger className="w-[180px]" id="category">
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
        <div
          style={{ display: "flex", justifyContent: "flex-end", gap: "1rem" }}
        >
          {/* 검색 입력창 */}
          <div className="relative">
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
          </div>

          <input
            type="text"
            placeholder="제목을 입력하세요"
            className="border-2 border-gray-300 px-2 rounded-md focus:outline-none focus:border-blue-500"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <button
            className="border-2 border-blue-500 bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
            onClick={handleSearch}
          >
            검색
          </button>
        </div>
      </div>
      <br />

      <ul className="flex flex-col gap-4">
        {body.items?.map((item: QuestionDto) => (
          <li
            key={item.id}
            className="flex items-center dark:bg-gray-800 justify-between border-2 border-gray-300 p-3 rounded-md"
          >
            <Link
              href={`/question/${item.id}`}
              className="flex items-center justify-between w-full block"
            >
              <div className="flex flex-1 font-semibold text-lg truncate gap-4">
                {item.title}
                <div className="flex gap-2 items-center text-yellow-500 text-sm font-medium">
                  <Banknote size={20} />
                  {item.point}
                </div>
                <div>{item.categoryName}</div>
              </div>
              <div className="w-40 text-sm text-center flex items-center justify-between">
                {item.recommendCount && item.recommendCount > 0 ? (
                  <span className="flex items-center gap-1 text-purple-500 font-medium">
                    <ThumbsUp size={16} />
                    {item.recommendCount}
                  </span>
                ) : (
                  <span></span>
                )}
                {item.answers && item.answers?.length > 0 ? (
                  <span className="flex items-center gap-1 text-blue-500 font-medium">
                    <MessageCircle size={16} />
                    {item.answers?.length}
                  </span>
                ) : (
                  <span></span>
                )}
                <span>{item.name}</span>
              </div>
              <div className="w-56 text-sm text-right ms-8">
                작성 일시: {formatDate(item.createdAt)}
              </div>
              <br />
            </Link>
          </li>
        ))}
      </ul>
      <br />

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={body.totalPages ?? 0} />
    </div>
  );
}
