import { cookies } from "next/headers";
import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { redirect } from "next/navigation";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    page?: number;
    pageSize?: number;
    keyword_type?: string;
    keyword?: string;
  };
}) {
  const cookieHeader = await cookies();

  // 유저 정보 조회
  const userResponse = await client.GET("/api/members/details", {
    headers: {
      cookie: cookieHeader.toString(),
    },
  });

  // 401 응답이면 로그인 페이지로 리디렉션
  if (userResponse.error || userResponse?.response?.status === 401) {
    return redirect("/login");
  }

  // 상품 리스트 조회
  const {
    page = 1,
    pageSize = 12,
    keyword_type = "ALL",
    keyword = "",
  } = await searchParams;
  const response = await client.GET("/api/products", {
    params: {
      query: {
        page,
        pageSize,
        keyword_type: keyword_type as | "ALL" | "NAME" | undefined,
        keyword
      },
    },
    headers: {
      cookie: cookieHeader.toString(),
    },
  });

  const itemPage = response.data!;

  // 상품 키워드 조회
  const categoriesResponse = await client.GET("/api/products/categories/keyword", {
    headers: {
      cookie: cookieHeader.toString(),
    },
  });

  const categories = categoriesResponse.data!.data;

  return <ClientPage
      page={page}
      pageSize={pageSize}
      itemPage={itemPage}
      categories={categories}
  />;
}
