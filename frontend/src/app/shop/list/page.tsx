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
  const { page = 1, pageSize = 12 } = await searchParams;
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

  return <ClientPage page={page} pageSize={pageSize} itemPage={itemPage} />;
}
