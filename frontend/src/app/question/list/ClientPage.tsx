"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";

type QuestionDto = components["schemas"]["QuestionDto"];
type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

interface ClientPageProps {
  body: PageDtoQuestionDto;
}

export default function ClientPage({ body }: ClientPageProps) {
  const router = useRouter();
  const searchParams = useSearchParams();

  const currentPage = Number(searchParams.get("page")) || 1;
  const [searchKeyword, setSearchKeyword] = useState(searchParams.get("searchKeyword") || "");

  // 페이지 이동 함수
  const changePage = (newPage: number) => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", newPage.toString());

    if (searchKeyword) queryParams.set("searchKeyword", searchKeyword);
    router.push(`?${queryParams.toString()}`);
  };

  // 검색 실행 함수
  const handleSearch = () => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1"); // 검색 시 1페이지부터 시작

    if (searchKeyword) queryParams.set("searchKeyword", searchKeyword);
    router.push(`?${queryParams.toString()}`);
  };

  return (
    <div className="container mx-auto px-4">
      <h2>지식인 리스트</h2>
      <hr /><br />

      <div style={{ display: "flex", justifyContent: "flex-end", gap: "1rem" }}>
        <input type="text" placeholder="검색어를 입력하세요"
         className="border-2 border-gray-300 px-2 rounded-md focus:outline-none focus:border-blue-500"
         value={searchKeyword}
         onChange={(e) => setSearchKeyword(e.target.value)}/>
        <button
        className="border-2 border-blue-500 bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
        onClick={handleSearch}>
        검색</button>
      </div>

      <ul>
        {body.items?.map((item: QuestionDto) => (
          <li key={item.id}>
            <div>제목: {item.title}</div>
            <div>내용: {item.content}</div>
            <div>작성자: {item.name}</div>
            <div>작성 일시: {item.createdAt}</div><br />
          </li>
        ))}
      </ul>

      {/* 페이지 이동 버튼 */}
      <div className="flex justify-center gap-2">
        <button onClick={() => changePage(currentPage - 1)} disabled={currentPage === 1}>
          이전
        </button>
        <button onClick={() => changePage(currentPage + 1)} disabled={currentPage === body.totalPages}>
          다음
        </button>
      </div>

    </div>
  );
}
