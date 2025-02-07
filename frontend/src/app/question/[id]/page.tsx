import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";
import React from "react";

async function getQuestionDetail(id: string) {
  try {
    const response = await fetch(`http://localhost:8080/api/questions/${id}`);
    if (!response.ok) {
      throw new Error("API 응답이 올바르지 않습니다.");
    }
    return await response.json();
  } catch (error) {
    console.error("API 요청 실패:", error);
    return null;
  }
}

export default async function Page({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: Promise<{ pageSize?: string; page?: string }>;
}) {
  const param = await params;
  const searchParam = await searchParams;

  const question = await getQuestionDetail(param.id);
  const body = convertSnakeToCamel(question);

  const pageSize = Number(searchParam.pageSize) || 5;
  const page = Number(searchParam.page) || 1;

  const responseAnswer = await client.GET(
    "/api/questions/{questionId}/answers",
    {
      params: {
        path: {
          questionId: Number(param.id),
        },
        query: {
          pageSize,
          page,
        },
      },
    }
  );

  const answers = convertSnakeToCamel(responseAnswer.data) ?? {
    items: [],
    currentPageNumber: 1,
    pageSize,
    totalPages: 0,
    totalItems: 0,
    hasMore: false,
  };

  if (!question) {
    return (
      <div className="flex justify-center items-center h-96">
        데이터를 불러오는 중 오류가 발생했습니다.
      </div>
    );
  }

  return (
    <ClientPage
      question={body}
      pageSize={pageSize}
      page={page}
      answers={answers}
    />
  );
}
