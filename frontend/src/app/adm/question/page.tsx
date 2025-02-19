import client from "@/lib/backend/client";
import ClientPage from "./ClientPage"

export default async function Page() {
  const response = await client.GET("/api/questions/categories");
  if (!response || !response.data) {
    throw new Error("API 응답이 유효하지 않습니다.");
  }

  const categories = response.data;

  return (
    <div className="container max-w-[600px] mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          질문 관리
        </h2>
      </div>
      <ClientPage categories={categories} />
    </div>
  );
}