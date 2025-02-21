import { cookies } from "next/headers";
import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";

export default async function Page({
                                       searchParams,
                                   }: {
    searchParams: {
        page?: number;
        pageSize?: number;
        searchKeywordType?: string;
        searchKeyword?: string;
        categoryKeyword?: string;
        saleStateKeyword?: string;
    };
}) {
    const {
        page = 1,
        pageSize = 12,
        searchKeywordType = "ALL",
        searchKeyword = "",
        categoryKeyword = "",
        saleStateKeyword = "ALL",
    } = await searchParams;

    const cookieHeader = await cookies();

    // 상품 리스트 + 카테고리 + 판매 상태 데이터를 병렬로 조회
    const [productsResponse, categoriesResponse, saleStatesResponse] = await Promise.all([
        client.GET("/api/products", {
            params: {
                query: {
                    page,
                    page_size: pageSize,
                    search_keyword_type: searchKeywordType as "ALL" | "NAME" | "CATEGORY" | undefined,
                    search_keyword: searchKeyword,
                    category_keyword: categoryKeyword,
                    sale_state: saleStateKeyword,
                },
            },
            headers: {
                cookie: cookieHeader.toString(),
            }
        }),

        client.GET("/api/products/categories/keyword", {
            headers: {
                cookie: cookieHeader.toString(),
            }
        }),

        client.GET("/api/products/states/keyword", {
            headers: {
                cookie: cookieHeader.toString(),
            }
        })
    ]);

    const itemPage = productsResponse.data!;
    const categories = categoriesResponse.data!.data;
    const saleStates = saleStatesResponse.data!.data;

    return <ClientPage itemPage={itemPage} categories={categories} saleStates={saleStates} />;
}