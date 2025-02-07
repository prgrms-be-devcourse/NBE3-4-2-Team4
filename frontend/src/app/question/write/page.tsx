import ClientPage from "./ClientPage";
import createClient from "openapi-fetch";
import type { paths } from "@/lib/backend/apiV1/schema";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default async function Page() {
    const response = await client.GET("/api/questions/categories");
    if (!response || !response.data) {
        throw new Error("API 응답이 유효하지 않습니다.");
    }

    const categories = response.data;
    return <ClientPage categories={categories}/>;
}