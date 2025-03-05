import client from "@/lib/backend/client";
import type { components } from "@/lib/backend/apiV1/schema";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

export default async function Page({
  params,
  searchParams,
}: {
  params: { memberId: string };
  searchParams: { pageSize?: string; page?: string };
}) {
  const { memberId } = await params;
  const { pageSize: pageSizeParam, page: pageParam } = await searchParams;

  const pageSize = Number(pageSizeParam) || 10;
  const page = Number(pageParam) || 1;

  const response = await client.GET("/api/questions/answerer/{memberId}", {
    params: {
      path: {
        memberId: Number(memberId),
      },
      query: {
        pageSize,
        page,
      },
    },
  });

  if (!response || !response.data) {
    throw new Error("API 응답이 유효하지 않습니다.");
  }

  const body = convertSnakeToCamel(response.data!!);

  return <ClientPage body={body} />;
}
