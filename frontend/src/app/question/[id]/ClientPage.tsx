"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Clock, Crown, Lightbulb, Pencil, PencilLine } from "lucide-react";
import Link from "next/link";
import React from "react";
import { useRouter } from "next/navigation";

type QuestionDto = components["schemas"]["QuestionDto"];

export default function ClientPage({
  question,
  pageSize,
  page,
  answers,
}: {
  question: QuestionDto;
  pageSize: number;
  page: number;
  answers: components["schemas"]["PageDtoAnswerDto"];
}) {
  const router = useRouter();
  const currentPage = Number(page) || 1;

  // 페이지 이동 함수
  const changePage = (newPage: number) => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", newPage.toString());

    router.push(`?${queryParams.toString()}`);
  };

  return (
    <div className="container mx-auto px-4">
      <h2 className="text-2xl font-bold mb-4 border-b pb-2">질문 상세</h2>

      {/* 질문 카드 */}
      <div className="bg-white dark:bg-gray-800 shadow-md rounded-lg p-6 border border-gray-200">
        <div className="flex justify-between">
          {/* 제목, 내용 */}
          <div className="flex-1">
            <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-3">
              {question.title}
            </h3>
            <p className="text-gray-800 dark:text-gray-100 leading-relaxed mb-4">
              {question.content}
            </p>
          </div>

          {/* 작성 정보 */}
          <div className="flex flex-col items-end text-gray-600 dark:text-gray-100 text-sm gap-2">
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

      {/* 답변 리스트 */}
      <div className="flex justify-between items-center mt-10 mb-2">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-1">
          <Lightbulb width={20} height={20} />{" "}
          <em className="not-italic text-xl">{question.answers?.length}개</em>의
          답변이 있습니다.
        </h3>
        <Button asChild>
          <Link href={`/question/${question.id}/answer/write`}>
            <PencilLine />
            답변 작성하기
          </Link>
        </Button>
      </div>

      <div className="flex flex-col gap-2 mt-6">
        {question.selectedAnswer && (
          <div className="mb-6 pb-8 border-b border-dashed border-gray-200 dark:border-[rgba(255,255,255,0.1)]">
            <Card className="shadow-[0_0_10px_0_rgba(0,0,0,0.2)] dark:shadow-[0_0_10px_0_rgba(255,255,255,0.3)]">
              <CardHeader>
                <CardTitle className="flex justify-between items-center">
                  <div className="flex items-center gap-2">
                    <Badge variant="default" className="flex items-center">
                      <Crown width={15} height={15} className="mr-2" />
                      채택된 답변
                    </Badge>
                    <Badge variant="secondary" className="flex items-center">
                      <Pencil width={14} height={14} className="mr-2" />
                      {question.selectedAnswer.authorName}
                    </Badge>
                  </div>
                  <p className="text-sm text-gray-400 font-light flex justify-end items-center">
                    <Clock width={14} height={14} className="mr-2" />
                    {formatDate(question.selectedAnswer.createdAt)}
                  </p>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="whitespace-pre-line">
                  {question.selectedAnswer.content}
                </div>
              </CardContent>
            </Card>
          </div>
        )}
        {answers.items
          ?.filter((answer) => !answer.selected)
          .map((answer) => (
            <Card key={answer.id}>
              <CardHeader>
                <CardTitle className="flex justify-between items-center">
                  <Badge variant="secondary">
                    <Pencil width={14} height={14} className="mr-2" />
                    {answer.authorName}
                  </Badge>
                  <p className="text-sm text-gray-400 font-light flex justify-end items-center">
                    <Clock width={14} height={14} className="mr-2" />
                    {formatDate(answer.createdAt)}
                  </p>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="whitespace-pre-line">{answer.content}</div>
              </CardContent>
              <CardFooter className="flex justify-end">
                <Button variant="default" asChild>
                  <Link
                    href={`/question/${question.id}/answer/${answer.id}/select`}
                  >
                    답변 채택
                  </Link>
                </Button>
              </CardFooter>
              <CardFooter className="flex justify-end gap-2">
                <Button variant="outline" asChild>
                  <Link
                    href={`/question/${question.id}/answer/${answer.id}/modify`}
                  >
                    수정
                  </Link>
                </Button>
                <Button variant="destructive" asChild>
                  <Link
                    href={`/question/${question.id}/answer/${answer.id}/delete`}
                  >
                    삭제
                  </Link>
                </Button>
              </CardFooter>
            </Card>
          ))}
      </div>

      {/* 페이지 이동 버튼 */}
      {(answers.totalPages ?? 0) > 1 && (
        <div className="flex justify-center gap-2 my-10">
          <Button
            onClick={() => changePage(currentPage - 1)}
            disabled={currentPage === 1}
            variant={currentPage === 1 ? "secondary" : "default"}
            className={`${currentPage === 1 ? "cursor-not-allowed" : ""}`}
          >
            이전
          </Button>
          <Button
            onClick={() => changePage(currentPage + 1)}
            disabled={currentPage === answers.totalPages}
            variant={
              currentPage === answers.totalPages ? "secondary" : "default"
            }
            className={`${
              currentPage === answers.totalPages ? "cursor-not-allowed" : ""
            }`}
          >
            다음
          </Button>
        </div>
      )}

      {/* <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md mt-6 border border-gray-200">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          답변 작성
        </h3>
        <textarea
          className="w-full p-3 border border-gray-300 dark:border-gray-600 rounded-md bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring focus:ring-blue-500"
          placeholder="답변을 입력하세요..."
          rows={4}
        />
        <button className="mt-4 px-3 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-md">
          답변 등록
        </button>
      </div> */}
    </div>
  );
}
