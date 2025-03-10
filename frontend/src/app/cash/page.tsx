import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export default async function Page({
                                       searchParams,
                                   }: {
    searchParams: {
        page?: number;
        startDate?: string;
        endDate?: string;
        assetCategory?: string;
        assetType?: string;
    };
}) {

    const {
        page = 1,
        startDate = "",
        endDate = "",
        assetCategory = "",
        assetType = "CASH"
    } = await searchParams;

    const cookieHeader = await cookies();

    // 유저 정보 조회
    const userResponse = await client.GET("/api/members/details", {
        headers: {
            cookie: cookieHeader.toString(),
        },
    });

    // 캐시 사용 내역 조회
    const cashHistoryResponse = await client.GET("/api/asset", {
        params: {
            query: {
                page: page,
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

    const user = userResponse.data?.data ?? [];
    const cashHistory = cashHistoryResponse.data?.data ?? [];

    if (!userResponse.response.ok) {
        redirect("/login");
    }

    return <ClientPage
        user={{
            username: user.username,
            emailAddress: user.email_address,
            cash: user.cash.amount,
        }}
        cashHistory={cashHistory}
        cookieString={cookieHeader}
    />;
}