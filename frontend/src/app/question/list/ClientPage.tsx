"use client";
import type { components } from "@/lib/backend/apiV1/schema";

type QuestionDto = components["schemas"]["QuestionDto"];
type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

interface ClientPageProps {
  body: PageDtoQuestionDto;
}

export default function ClientPage({body}: ClientPageProps) {
  return (
    <div className="container mx-auto px-4">
      지식인 리스트
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
    </div>
  );
}
