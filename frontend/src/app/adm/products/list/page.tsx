import { cookies } from "next/headers";
import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";

export default async function Page({
                                       searchParams,
                                   }: {
    searchParams: {
        page?: number;
        pageSize?: number;
    };
}) {
    const { page = 1, pageSize = 10 } = await searchParams;

    const cookieHeader = await cookies();

    const response = await client.GET("/api/products", {
        params: {
            query: {
                page,
                pageSize,
            },
        },
        headers: {
            cookie: cookieHeader.toString(),
        },
    });

    const itemPage = response.data!;

    return (
        <ClientPage
            page={page}
            pageSize={pageSize}
            itemPage={itemPage}
        />
    );
}