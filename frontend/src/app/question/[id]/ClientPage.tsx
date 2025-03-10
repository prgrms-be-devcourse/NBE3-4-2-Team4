"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Clock,
  Coins,
  Crown,
  Lightbulb,
  Pencil,
  PencilLine,
} from "lucide-react";
import Link from "next/link";
import React from "react";
import { useRouter } from "next/navigation";
import { useId } from "@/context/IdContext";
import { useNickname } from "@/context/NicknameContext";
import { useToast } from "@/hooks/use-toast";
import Pagination1 from "@/lib/business/components/Pagination1";
import client from "@/lib/backend/client";
import { AttachmentFiles } from "@/lib/business/components/AttachmentFiles";
import { useRole } from "@/context/RoleContext";

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
  const { id } = useId();
  const { role, setRole } = useRole();
  const { nickname } = useNickname();
  const { toast } = useToast();

  const handleEdit = () => {
    router.push(`/question/${question.id}/edit`);
  };

  const handleRecommend = async () => {
    try {
      const response = await client.PUT(
        "/api/questions/{questionId}/recommend",
        {
          params: { path: { questionId: question.id } },
        }
      );

      if (response.error) {
        toast({
          title: response.error.msg,
          variant: "destructive",
        });
        return;
      }
      toast({
        title: response.data.msg,
      });
      router.refresh();
    } catch (error) {
      toast({
        title: "질문 추천 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  const writeMessage = (senderName: string) => {
    if (senderName) {
      localStorage.setItem('senderName', senderName);
    }
    router.push("/message/write");
  }

  // HTML 엔티티를 실제 문자로 변환하는 함수
  function decodeHtml(html: string): string {
    var txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
  }

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          지식인 게시판
        </h2>
        <p className="text-md text-gray-400 mt-3">
          지식을 나누고 포인트도 얻으세요!
          <br />
          함께 성장하는 지식 공유 공간입니다.
        </p>
      </div>

      {/* 질문 카드 */}
      <div>
        <Card className="shadow-md">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Badge
                variant="outline"
                className="flex items-center grow-0 shrink-0"
              >
                {question.categoryName}
              </Badge>
              <span>{question.title}</span>
              {question.name != nickname && id && (
                <Button className="ml-auto bg-gray-400 hover:bg-gray-500"
                  onClick={() => writeMessage(question.name)}>
                  <Link href="/message/write">쪽지 쓰기</Link>
                </Button>
              )}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div
              dangerouslySetInnerHTML={{
                __html: question.content,
              }}
            />
          </CardContent>
          <CardFooter className="flex justify-end">
            <AttachmentFiles
              questionId={question.id}
              parentId={question.id}
              entityType="questions"
            />
          </CardFooter>
          <CardFooter className="flex justify-between gap-2 sm:flex-row flex-col sm:items-center items-start">
            <div>
              <div className="flex items-center gap-2">
                <Badge
                  variant="default"
                  className="flex items-center gap-1 bg-amber-500 hover:bg-amber-500 text-white"
                >
                  <Coins size={16} />
                  {question.amount}
                </Badge>
                <Badge variant="secondary" className="flex items-center gap-1">
                  <Pencil width={14} height={14} />
                  {question.name}
                </Badge>
              </div>
              <div className="flex items-center gap-1 text-sm text-gray-400 mt-2">
                <Clock width={14} height={14} />
                {formatDate(question.createdAt)}
              </div>
            </div>
            <div className="flex items-center gap-2 sm:self-auto self-end">
              {question.name != nickname && (
                <Button
                  onClick={() => handleRecommend()}
                  className="bg-sky-400 hover:bg-sky-500 text-white"
                >
                  <span>추천</span>
                  <span className="font-bold">{question.recommendCount}</span>
                </Button>
              )}
              {/* 수정 버튼 (작성자만 가능) */}
              {question.name === nickname && !question.closed && (
                <Button variant="outline" onClick={handleEdit}>
                  수정
                </Button>
              )}

              {/* 삭제 버튼 (작성자만 가능) */}
              {question.name === nickname && !question.closed && (
                <Button variant="destructive" asChild>
                  <Link href={`/question/${question.id}/delete`}>삭제</Link>
                </Button>
              )}
            </div>
          </CardFooter>
        </Card>
      </div>

      {/* 연관 기사 */}
      <div className="mt-8">
        <Card className="shadow-md">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              연관 기사
            </CardTitle>
          </CardHeader>
          <CardContent>
            {question.articles?.map((article, index) => (
              <div key={index}>
                <h3>{decodeHtml(article.title!!.replace(/<[^>]+>/g, ''))}</h3>
                <p>{decodeHtml(article.description!!.replace(/<[^>]+>/g, ''))}</p>
                <Link href={article.link!!} target="_blank" rel="noopener noreferrer" style={{ color: 'blue' }}>
                  {article.link}
                </Link>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>

      {/* 답변 리스트 */}
      <div className="flex justify-between items-center mt-10 mb-2">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-1">
          <Lightbulb width={20} height={20} />{" "}
          <em className="not-italic text-xl">{question.answers?.length}개</em>의
          답변이 있습니다.
        </h3>
        {id && !question.closed && id !== question.authorId && (
          <Button asChild>
            <Link href={`/question/${question.id}/answer/write`}>
              <PencilLine />
              답변 작성하기
            </Link>
          </Button>
        )}
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
                <div
                  dangerouslySetInnerHTML={{
                    __html: question.selectedAnswer.content,
                  }}
                />
              </CardContent>
              <CardFooter className="flex justify-end">
                <AttachmentFiles
                  questionId={question.id}
                  parentId={question.selectedAnswer.id}
                  entityType="answers"
                />
              </CardFooter>
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
                <div
                  dangerouslySetInnerHTML={{
                    __html: answer.content,
                  }}
                />
              </CardContent>
              <CardFooter className="flex justify-end">
                <AttachmentFiles
                  questionId={question.id}
                  parentId={answer.id}
                  entityType="answers"
                />
              </CardFooter>
              {!question.closed && (
                <CardFooter className="flex justify-end gap-2">
                  {id === question.authorId && (
                    <Button variant="default" asChild>
                      <Link
                        href={`/question/${question.id}/answer/${answer.id}/select`}
                      >
                        답변 채택
                      </Link>
                    </Button>
                  )}
                  {id === answer.authorId && (
                    <Button variant="outline" asChild>
                      <Link
                        href={`/question/${question.id}/answer/${answer.id}/modify`}
                      >
                        수정
                      </Link>
                    </Button>
                  )}
                  {(id === answer.authorId || role === "ADMIN") && (
                    <Button variant="destructive" asChild>
                      <Link
                        href={`/question/${question.id}/answer/${answer.id}/delete`}
                      >
                        삭제
                      </Link>
                    </Button>
                  )}
                </CardFooter>
              )}
            </Card>
          ))}
      </div>

      {/* 페이지 이동 버튼 */}
      <Pagination1 totalPages={answers.totalPages ?? 0} />
    </div>
  );
}
