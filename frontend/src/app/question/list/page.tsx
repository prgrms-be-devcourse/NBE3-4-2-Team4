import ClientPage from "./ClientPage";
import type { components } from "@/lib/backend/apiV1/schema";
import { NextResponse } from "next/server";

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

type QuestionDto = components["schemas"]["QuestionDto"];
type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

export default async function Page() {
  const response = await fetch("http://localhost:8080/api/questions");
  if (!response.ok) {
    console.error("API 요청 실패:", response.status, response.statusText);
    return <div>데이터를 불러오는 중 오류가 발생했습니다.</div>;
  }

  const data = await response.json();
  const body: PageDtoQuestionDto = convertSnakeToCamel(data);
  console.log("API 응답:", body); // 응답 확인

  return (
    <div>
      <div>currentPageNumber: {body.currentPageNumber}</div>
      <div>pageSize: {body.pageSize}</div>
      <div>totalPages: {body.totalPages}</div>
      <div>totalItems: {body.totalItems}</div>
      
      <hr /><br />
      <ul>
        {body.items?.map((item: QuestionDto) => (
          <li key={item.id}>
            <div>id: {item.id}</div>
            <div>title: {item.title}</div>
            <div>content: {item.content}</div>
            <div>authorName: {item.name}</div>
            <div>createdAt: {item.createdAt}</div>
            <div>modifiedAt: {item.modifiedAt}</div><br />
          </li>
        ))}
      </ul>
    </div>
  );

  // return <ClientPage />;
}
