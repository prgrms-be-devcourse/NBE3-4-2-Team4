"use client";
import type { components } from "@/lib/backend/apiV1/schema";

type QuestionDto = components["schemas"]["QuestionDto"];

export default function ClientPage({ question } : { question: QuestionDto }) {
  return (
    <div className="container mx-auto px-4">
      <h2>질문 상세</h2>
      <hr /><br />
      <div>
        <h3>{question.title}</h3>
        <p>{question.content}</p>
        <p>작성자: {question.name}</p>
        <p>작성일: {question.createdAt}</p>
        <p>카테고리: {question.categoryName}</p>
        <p>추천 수: {question.recommendCount}</p>
      </div>
    </div>
  );
}