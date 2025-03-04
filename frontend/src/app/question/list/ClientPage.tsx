"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { useToast } from "@/hooks/use-toast";
import { useId } from "@/context/IdContext";
import {
  SelectTrigger,
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import Pagination2 from "@/lib/business/components/Pagination2";
import { Input } from "@/components/ui/input";
import QuestionList from "@/lib/business/components/QuestionList";

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
  const [assetValue, setAssetValue] = useState("ALL");

  // 검색 실행 함수
  const handleSearch = () => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1"); // 검색 시 1페이지부터 시작

    if (searchKeyword) queryParams.set("searchKeyword", searchKeyword);
    if (keywordType) queryParams.set("keywordType", keywordType);

    setCategoryValue("전체");
    setAssetValue("ALL");
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
    // assetType 값도 추가하여 동시에 반영
    if (assetValue !== "전체") {
      queryParams.set("assetType", assetValue);
    }

    router.push(`?${queryParams.toString()}`);
    setCategoryValue(value);
  };

  const handleAssetSearch = (value: string) => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1");
    if (value !== "전체") {
      queryParams.set("assetType", value);
    } else {
      queryParams.set("assetType", "ALL");
    }

    if (categoryValue !== "전체") {
      queryParams.set("categoryId", categoryValue);
    }

    router.push(`?${queryParams.toString()}`);
    setAssetValue(value);
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

      <div className="flex mb-4 md:flex-row flex-col gap-2 md:items-center items-start justify-between">
        <div className="flex gap-2">
          <Button onClick={createQuestion}>글쓰기</Button>
          <div>
            <Select onValueChange={handleCategorySearch} value={categoryValue}>
              <SelectTrigger className="md:w-[130px] w-[100px]" id="category">
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

          {/* 포인트/캐시 구분 */}
          <Select onValueChange={handleAssetSearch} value={assetValue}>
            <SelectTrigger className="md:w-[130px] w-[100px]">
              <SelectValue placeholder="포인트/캐시" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">전체</SelectItem>
              <SelectItem value="POINT">포인트</SelectItem>
              <SelectItem value="CASH">캐시</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="flex gap-2">
          {/* 검색 입력창 */}
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

      <QuestionList body={body} />

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={body.totalPages ?? 0} />
    </div>
  );
}
