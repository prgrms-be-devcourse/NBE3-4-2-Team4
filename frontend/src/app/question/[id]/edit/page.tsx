import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page({ params }: { params: { id: string } }) {
  // 카테고리 목록 조회
  const response = await client.GET("/api/questions/categories");
  if (!response || !response.data) {
    throw new Error("API 응답이 유효하지 않습니다.");
  }

  // 특정 questionId의 데이터 조회
  const questionResponse = await client.GET(`/api/questions/{id}`, {
    params: { path: { id: Number(params.id) } },
  });
  if (!questionResponse || !questionResponse.data) {
    throw new Error("질문 데이터를 불러오지 못했습니다.");
  }

  const categories = response.data;
  const questionData = convertSnakeToCamel(questionResponse.data); // 기존 질문 데이터
  return <ClientPage categories={categories} questionData={questionData} />;
}
