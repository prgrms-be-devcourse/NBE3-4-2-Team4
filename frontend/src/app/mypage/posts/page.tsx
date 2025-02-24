import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    page?: string;
  };
}) {
  const {
    page = 1,
  } = await searchParams;

  try {
    const response = await client.GET("/api/questions", {
      params: {
        query: {
          page: Number(page),
        },
      },
    });

    if (!response || !response.data) {
      throw new Error("API 응답이 유효하지 않습니다.");
    }

    const data = response.data;
    const body = convertSnakeToCamel(data);

    return <ClientPage body={body} />;
  } catch (error) {
    console.error("API 요청 실패:", error);

    return (
      <div className="flex justify-center items-center h-96">
        데이터를 불러오는 중 오류가 발생했습니다.
      </div>
    );
  }
}
