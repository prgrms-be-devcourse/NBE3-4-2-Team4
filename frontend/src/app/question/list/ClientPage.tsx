"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import { MessageCircle } from "lucide-react";
import { ThumbsUp } from "lucide-react";
import { Banknote } from "lucide-react";
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
  const [keywordType, setKeywordType] = useState(searchParams.get("keywordType") || "ALL");

  const options = ['전체', '제목', '내용', '작성자', '답변 내용'];
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(options[0]);

  const optionMapping: { [key: string]: string } = {
    '전체': 'ALL',
    '제목': 'TITLE',
    '내용': 'CONTENT',
    '작성자': 'AUTHOR',
    '답변 내용': 'ANSWER_CONTENT',
  };

  const toggleDropdown = () => { // 검색 조건 드롭다운
    setIsOpen(!isOpen);
  };

  const selectOption = (option: string) => {
    setSelectedOption(option);
    setKeywordType(optionMapping[option]);
    setIsOpen(false); // 선택 후 드롭다운 닫기
  };

  // 페이지 이동 함수
  const changePage = (newPage: number) => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", newPage.toString());

    if (searchKeyword) queryParams.set("searchKeyword", searchKeyword);
    if (keywordType) queryParams.set("keywordType", keywordType);
    router.push(`?${queryParams.toString()}`);
  };

  // 검색 실행 함수
  const handleSearch = () => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1"); // 검색 시 1페이지부터 시작

    if (searchKeyword) queryParams.set("searchKeyword", searchKeyword);
    if (keywordType) queryParams.set("keywordType", keywordType);
    router.push(`?${queryParams.toString()}`);
  };

  const createQuestion = () => {
    router.push("/question/write");
  }

  const showRanking = () => {
    router.push("/question/rank");
  }

  return (
    <div className="container mx-auto px-4">
      <div className="flex items-center justify-between mb-2">
        <h2>지식인 게시판</h2>
        <button
        className="border-2 border-cyan-500 bg-cyan-500 text-white px-2 font-bold rounded-md hover:bg-cyan-600"
        onClick={showRanking}>
            인기글 보기
        </button>
      </div>

      <hr /><br />

      <div className="flex justify-between items-center">
        <button
        className="border-2 border-teal-500 bg-teal-500 text-white px-4 py-2 font-bold rounded-md hover:bg-teal-600"
        onClick={createQuestion}
        >글쓰기</button>
        <div style={{ display: "flex", justifyContent: "flex-end", gap: "1rem" }}>
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
                  <li key={index}
                    className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    onClick={() => selectOption(option)}>
                  {option}
                  </li>
                ))}
              </ul>
            )}
          </div>

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
              <div className="flex flex-1 font-semibold text-lg truncate gap-4 space-between">
                {item.title}
                <div className="flex gap-2 items-center text-yellow-500 text-sm font-medium">
                  <Banknote size={20} />
                  {item.point}
                </div>
              </div>
              <div className="w-40 text-sm text-center flex items-center justify-between">
              {item.recommendCount && item.recommendCount > 0 ? (
                <span className="flex items-center gap-1 text-purple-500 font-medium">
                  <ThumbsUp size={16} />
                  {item.recommendCount}
                </span>
              ) : <span></span>}
              {item.answers && item.answers?.length > 0 ? (
                <span className="flex items-center gap-1 text-blue-500 font-medium">
                  <MessageCircle size={16} />
                  {item.answers?.length}
                </span>
              ) : <span></span>}
              <span>{item.name}</span>
              </div>
              <div className="w-56 text-sm text-right">작성 일시: {formatDate(item.createdAt)}</div><br />
            </Link>
          </li>
        ))}
      </ul>
      <br />

      {/* 페이지 이동 버튼 */}
      <div className="flex justify-center gap-2 mb-3">
        <button onClick={() => changePage(currentPage - 1)} disabled={currentPage === 1}
          className={`px-4 py-2 rounded-md text-white font-semibold transition ${
            currentPage === 1
              ? "bg-gray-300 cursor-not-allowed" // 이전, 다음 페이지 없을 시 비활성화
              : "bg-blue-500 hover:bg-blue-600"
          }`}
          >
          이전
        </button>

        {/* 페이지 번호 버튼 */}
        {Array.from({ length: body.totalPages || 1 }, (_, i) => i + 1).map((page) => (
          <button key={page} onClick={() => changePage(page)}
            className={`px-3 py-2 rounded-md text-white font-semibold transition ${
              currentPage === page
                ? "bg-blue-100 cursor-not-allowed" // 현재 페이지 비활성화
                : "bg-blue-300 hover:bg-blue-500"
            }`}
            >
            {page}
          </button>
        ))}

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
