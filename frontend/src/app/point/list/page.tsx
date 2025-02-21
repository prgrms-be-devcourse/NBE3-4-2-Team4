
import ClientPage from "./ClientPage";
import createClient from "openapi-fetch";
import type { paths } from "@/lib/backend/apiV1/schema";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";



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
                                 assetCategory?: string;
                               };
                             }) {

                                 try {

    const { page = 1, startDate, endDate, pointCategory } = await searchParams;
//             const stringCookies = await cookies().toString();
//             console.log(stringCookies, " cookie")
        const cookieHeader = await cookies();
        const response = await client.GET("/api/points", {
          params: {
            query: {
              page: Number(page),
              startDate,
              endDate,
              pointCategory
            },
          },
          headers: {
            cookie: cookieHeader.toString(),
          },
        });

        if (!response || !response.data) {
          throw new Error(response.error.result_code.split("-")[0]);
        }

         const userResponse = await client.GET("/api/members/details", {
          headers: {
            cookie: cookieHeader.toString(),
          },
//                     credentials: "include",
                });
        console.log(userResponse);
        const point = userResponse.data.data.point.amount;
        console.log(point);
        const data = response.data;
        const body = convertSnakeToCamel(data);
        console.log(data);
        return <ClientPage body={body} point={point}/>;
    } catch (error) {


                                                 if (error.message === "401") {
                                                         return redirect("/login");
                                                     }
            console.error("API 요청 실패:", error);
            //console.log("서버에서 보낸 에러 JSON:", response.error.data);

            return (
              <div className="flex justify-center items-center h-96">
                데이터를 불러오는 중 오류가 발생했습니다.
              </div>
            );
    }
}