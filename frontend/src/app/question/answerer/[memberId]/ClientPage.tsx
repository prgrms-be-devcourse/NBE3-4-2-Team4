"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import Pagination2 from "@/lib/business/components/Pagination2";
import QuestionList from "@/lib/business/components/QuestionList";

type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

interface ClientPageProps {
  body: PageDtoQuestionDto | null;
}

export default function ClientPage({ body }: ClientPageProps) {
  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          내가 쓴 답변 모아 보기
        </h2>
      </div>

      <QuestionList body={body} />

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={body?.totalPages ?? 0} />
    </div>
  );
}
