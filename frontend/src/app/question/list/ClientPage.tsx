"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import Link from "next/link";

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

  const createQuestion = () => {
    router.push("/question/write");
  }

  return (
    <div className="container mx-auto px-4">
      <h2>지식인 리스트</h2>
      <hr /><br />

      <div className="flex justify-between items-center">
        <button
        className="border-2 border-teal-500 bg-teal-500 text-white px-4 py-2 font-bold rounded-md hover:bg-teal-600"
        onClick={createQuestion}
        >글쓰기</button>
        <div style={{ display: "flex", justifyContent: "flex-end", gap: "1rem" }}>
          <input type="text" placeholder="제목을 입력하세요"
          className="border-2 border-gray-300 px-2 rounded-md focus:outline-none focus:border-blue-500"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}/>
          <button
          className="border-2 border-blue-500 bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
          onClick={handleSearch}>
          검색</button>
        </div>
      </div>
      <br />

      <ul className="flex flex-col gap-4">
        {body.items?.map((item: QuestionDto) => (
          <li key={item.id}
          className="flex items-center dark:bg-gray-800 justify-between border-2 border-gray-300 p-3 rounded-md">
            <Link href={`/question/${item.id}`} className="flex items-center justify-between w-full block">
              <div className="flex-1 font-semibold text-lg truncate">{item.title}</div>
              <div className="w-40 text-sm text-center">{item.name}</div>
              <div className="w-56 text-sm text-right">작성 일시: {formatDate(item.createdAt)}</div><br />
            </Link>
          </li>
        ))}
      </ul>
      <br />

      {/* 페이지 이동 버튼 */}
      <div className="flex justify-center gap-2">
        <button onClick={() => changePage(currentPage - 1)} disabled={currentPage === 1}
          className={`px-4 py-2 rounded-md text-white font-semibold transition ${
            currentPage === 1
              ? "bg-gray-300 cursor-not-allowed" // 이전, 다음 페이지 없을 시 비활성화
              : "bg-blue-500 hover:bg-blue-600"
          }`}
          >
          이전
        </button>
        <button onClick={() => changePage(currentPage + 1)} disabled={currentPage === body.totalPages}
          className={`px-4 py-2 rounded-md text-white font-semibold transition ${
            currentPage === body.totalPages
              ? "bg-gray-300 cursor-not-allowed"
              : "bg-blue-500 hover:bg-blue-600"
          }`}
          >
          다음
        </button>
      </div>
    </div>
  );
}
