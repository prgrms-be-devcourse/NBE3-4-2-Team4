"use client";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { useToast } from "@/hooks/use-toast";
import { formatDate } from "@/utils/dateUtils";
import { useId } from "@/context/IdContext";
import { MessageCircle, ThumbsUp, Banknote, Coins, Star } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import Pagination2 from "@/lib/business/components/Pagination2";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

type QuestionDto = components["schemas"]["QuestionDto"];
type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

interface ClientPageProps {
  body: PageDtoQuestionDto;
}

export default function ClientPage({ body }: ClientPageProps) {
  const router = useRouter();

  const { id } = useId();
  const { toast } = useToast();

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          내 글 보기
        </h2>
        <p className="text-md text-gray-400 mt-3">
          지식을 나누고 포인트도 얻으세요!
          <br />
          함께 성장하는 지식 공유 공간입니다.
        </p>
      </div>

      <ul className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {body.items?.map((item: QuestionDto) => (
          <li key={item.id}>
            <Link href={`/question/${item.id}`}>
              <Card className="hover:shadow-[0_0_10px_0_rgba(0,0,0,0.2)] hover:dark:shadow-[0_0_10px_0_rgba(255,255,255,0.3)] transition-colors">
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Badge
                      variant="secondary"
                      className="flex items-center gap-2 grow-0 shrink-0"
                    >
                      {item.categoryName}
                    </Badge>
                    <span className="overflow-hidden text-ellipsis whitespace-nowrap">
                      {item.title}
                    </span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="flex items-center space-x-4">
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-foreground">
                        {item.name}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {formatDate(item.createdAt)}
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="flex items-center gap-1 text-amber-500">
                        <Coins size={16} />
                        {item.point}
                      </div>
                      {(item.recommendCount ?? 0) > 0 && (
                        <span className="flex items-center gap-1 text-sky-400 font-medium">
                          <ThumbsUp size={16} />
                          {item.recommendCount}
                        </span>
                      )}
                      {(item.answers?.length ?? 0) > 0 && (
                        <span className="flex items-center gap-1 text-teal-500 font-medium">
                          <MessageCircle size={16} />
                          {item.answers?.length}
                        </span>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            </Link>
          </li>
        ))}
      </ul>

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={body.totalPages ?? 0} />
    </div>
  );
}
