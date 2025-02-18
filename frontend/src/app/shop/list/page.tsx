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
    saleStateKeyword = "ONSALE",
  } = await searchParams;

  // 상품 리스트와 카테고리 데이터를 병렬로 조회
  const [productsResponse, categoriesResponse] = await Promise.all([
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
    }),
    client.GET("/api/products/categories/keyword"),
  ]);

  const itemPage = productsResponse.data!;
  const categories = categoriesResponse.data!.data;

  return <ClientPage itemPage={itemPage} categories={categories} />;
}
