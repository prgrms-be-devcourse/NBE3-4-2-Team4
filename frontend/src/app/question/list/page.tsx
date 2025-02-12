import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    page?: string;
    searchKeyword?: string;
    keywordType?: string;
    categoryId?: number;
  };
}) {
  const {
    page = 1,
    searchKeyword = "",
    keywordType = "ALL",
    categoryId = 0,
  } = await searchParams;

  try {
    const response = await client.GET("/api/questions", {
      params: {
        query: {
          page: Number(page),
          searchKeyword,
          keywordType: keywordType as
            | "ALL"
            | "TITLE"
            | "CONTENT"
            | "AUTHOR"
            | "ANSWER_CONTENT"
            | undefined,
          categoryId: Number(categoryId),
        },
      },
    });

    const responseCategory = await client.GET("/api/questions/categories");

    // response 에러 반환 시 처리
    if (!response || !response.data) {
      throw new Error("API 응답이 유효하지 않습니다.");
    }

    const data = response.data;
    const body = convertSnakeToCamel(data);

    const category = responseCategory.data ?? [];

    return <ClientPage body={body} category={category} />;
  } catch (error) {
    console.error("API 요청 실패:", error);

    return (
      <div className="flex justify-center items-center h-96">
        데이터를 불러오는 중 오류가 발생했습니다.
      </div>
    );
  }
}
