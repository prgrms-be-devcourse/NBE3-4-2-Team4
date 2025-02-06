"use client";
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

  // 페이지 이동 함수
  const changePage = (newPage: number) => {
    router.push(`?page=${newPage}`);
  };

  return (
    <div className="container mx-auto px-4">
      <h2>지식인 리스트</h2>
        <div>
        <div>currentPageNumber: {body.currentPageNumber}</div>
        <div>pageSize: {body.pageSize}</div>
        <div>totalPages: {body.totalPages}</div>
        <div>totalItems: {body.totalItems}</div>
        
        <hr /><br />
        <ul>
          {body.items?.map((item: QuestionDto) => (
            <li key={item.id}>
              <div>id: {item.id}</div>
              <div>title: {item.title}</div>
              <div>content: {item.content}</div>
              <div>authorName: {item.name}</div>
              <div>createdAt: {item.createdAt}</div>
              <div>modifiedAt: {item.modifiedAt}</div><br />
            </li>
          ))}
        </ul>
      </div>

      {/* 페이지 이동 버튼 */}
      <div>
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
