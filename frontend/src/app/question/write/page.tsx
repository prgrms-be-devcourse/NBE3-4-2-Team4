import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";

export default async function Page() {
  // 카테고리 목록 조회
  const response = await client.GET("/api/questions/categories");
  if (!response || !response.data) {
    throw new Error("API 응답이 유효하지 않습니다.");
  }

  const categories = response.data;
  return <ClientPage categories={categories} />;
}
