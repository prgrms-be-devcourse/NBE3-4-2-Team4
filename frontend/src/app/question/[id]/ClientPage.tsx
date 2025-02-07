"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";

type QuestionDto = components["schemas"]["QuestionDto"];

export default function ClientPage({ question } : { question: QuestionDto }) {
  return (
    <div className="container mx-auto px-4">
        <h2 className="text-2xl font-bold mb-4 border-b pb-2">질문 상세</h2>

        {/* 질문 카드 */}
        <div className="bg-white shadow-md rounded-lg p-6 border border-gray-200">
            <div className="flex justify-between">
                {/* 제목, 내용 */}
                <div className="flex-1">
                    <h3 className="text-xl font-semibold text-gray-900 mb-3">{question.title}</h3>
                    <p className="text-gray-800 leading-relaxed mb-4">{question.content}</p>
                </div>
                
                {/* 작성 정보 */}
                <div className="flex flex-col items-end text-gray-600 text-sm gap-2">
                    <span>작성자: {question.name}</span>
                    <span>{formatDate(question.createdAt)}</span>
                </div>
            </div>

            {/* 카테고리 및 추천 수 */}
            <div className="flex justify-between items-center mt-4">
            <span className="text-sm bg-blue-100 text-blue-600 px-3 py-1 rounded-full">
                {question.categoryName}
            </span>

            {/* 추천 버튼 */}
            <div className="flex items-center gap-2">
                <button className="flex items-center gap-1 bg-purple-500 text-white px-4 py-2 rounded-md hover:bg-purple-600 transition">
                <span>추천</span>
                <span className="font-bold">{question.recommendCount}</span>
                </button>
            </div>
            </div>
        </div>
    </div>
  );
}