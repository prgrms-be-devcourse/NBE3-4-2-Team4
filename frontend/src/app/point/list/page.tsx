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
        assetType?: string;
    };
}) {
    try {
        const { page = "1", startDate, endDate, assetCategory, assetType } = searchParams;

        const cookieHeader = cookies();

        const response = await client.GET("/api/asset", {
            params: {
                query: {
                    page: Number(page),
                    startDate,
                    endDate,
                    assetCategory,
                    assetType,
                },
            },
            headers: {
                cookie: cookieHeader.toString(),
            },
        });

        if (!response || !response.data) {
            const errorCode = response?.error?.result_code?.split("-")[0] || "UNKNOWN_ERROR";
            throw new Error(errorCode);
        }

        const userResponse = await client.GET("/api/members/details", {
            headers: {
                cookie: cookieHeader.toString(),
            },
        });

        const user = userResponse?.data?.data || null;
        if (!user) {
            throw new Error("USER_NOT_FOUND");
        }

        const body = convertSnakeToCamel(response.data);

        return (
            <ClientPage
                body={body}
                user={{
                    username: user.username,
                    emailAddress: user.email_address,
                    point: user.point.amount,
                    cash: user.cash.amount,
                }}
            />
        );
    } catch (error) {
        if (error.message === "401") {
            return redirect("/login");
        }

        console.error("API 요청 실패:", error);

        return (
            <div className="flex justify-center items-center h-96">
                데이터를 불러오는 중 오류가 발생했습니다.
            </div>
        );
    }
}
