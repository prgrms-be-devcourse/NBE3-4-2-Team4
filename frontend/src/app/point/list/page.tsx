
import ClientPage from "./ClientPage";
import createClient from "openapi-fetch";
import type { paths } from "@/lib/backend/apiV1/schema";

    function convertSnakeToCamel<T>(obj: T): T {
      if (Array.isArray(obj)) {
        return obj.map((item) => convertSnakeToCamel(item)) as T;
      } else if (typeof obj === "object" && obj !== null) {
        return Object.fromEntries(
          Object.entries(obj).map(([key, value]) => [
            key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase()),
            convertSnakeToCamel(value),
          ])
        ) as T;
      }
      return obj;
    }

    const client = createClient<paths>({
      baseUrl: "http://localhost:8080",
    });

export default async function Page({
                               searchParams,
                             }: {
                               searchParams: {
                                 page?: string;
                                 startDate?: string;
                                 endDate?: string;
                                 pointCategory?: string;
                               };
                             }) {
                                 try {
    const { page = 1, startDate, endDate, pointCategory } = await searchParams;

        const response = await client.GET("/api/points", {
          params: {
            query: {
              page: Number(page),
              startDate,
              endDate,
              pointCategory
            },
          },
        });

        if (!response || !response.data) {
          throw new Error("API 응답이 유효하지 않습니다.");
        }

        const data = response.data;
        const body = convertSnakeToCamel(data);
        console.log(data);
        return <ClientPage body={body}/>;
    } catch (error) {
            console.error("API 요청 실패:", error);
            console.log("서버에서 보낸 에러 JSON:", response.error.data);

            return (
              <div className="flex justify-center items-center h-96">
                데이터를 불러오는 중 오류가 발생했습니다.
              </div>
            );
    }
}