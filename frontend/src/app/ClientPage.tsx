"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import {
  MessageCircle,
  ThumbsUp,
  Award,
  Coins,
  Star,
  CircleDollarSign,
} from "lucide-react";
import Link from "next/link";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import Pagination2 from "@/lib/business/components/Pagination2";

type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];
type QuestionDto = components["schemas"]["QuestionDto"];

interface ClientPageProps {
  body: PageDtoQuestionDto;
}

export default function ClientPage({ body }: ClientPageProps) {
  let currentRank = 0; // 순위 계산 변수
  let previousRecommendCount = -1;

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          <Award width={37} height={37} />
          인기글 랭킹
          <Award width={37} height={37} />
        </h2>
        <p className="text-md text-gray-400 mt-3">
          인기글 선정에 축하드립니다. <br />
          순위에 따라 포인트 보상이 주어집니다.
        </p>
      </div>

      <ul className="grid grid-cols-1 gap-6">
        {body.items?.map((item: QuestionDto, index) => {
          // 공동 순위 반영
          if (item.recommendCount !== previousRecommendCount) {
            currentRank = index + 1;
          }
          previousRecommendCount = item.recommendCount!!;

          return (
            <li key={item.id}>
              <Link href={`/question/${item.id}`}>
                <Card className="hover:shadow-[0_0_10px_0_rgba(0,0,0,0.2)] hover:dark:shadow-[0_0_10px_0_rgba(255,255,255,0.3)] transition-colors">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Badge
                        variant="default"
                        className="flex items-center gap-2"
                      >
                        <Star width={16} height={16} />
                        {currentRank}
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
                        <div
                          className={`flex items-center gap-1 
                      ${
                        item.assetType === "포인트"
                          ? "text-lime-500"
                          : "text-amber-500"
                      }`}
                        >
                          {item.assetType === "포인트" ? (
                            <Coins size={16} />
                          ) : (
                            <CircleDollarSign size={16} />
                          )}
                          {item.amount}
                        </div>
                        {item.recommendCount && item.recommendCount > 0 && (
                          <span className="flex items-center gap-1 text-sky-400 font-medium">
                            <ThumbsUp size={16} />
                            {item.recommendCount}
                          </span>
                        )}
                        {item.answers && item.answers?.length > 0 && (
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
          );
        })}
      </ul>
      <br />

      {/* 페이지 이동 버튼 */}
      <Pagination2 totalPages={body.totalPages ?? 0} />
    </div>
  );
}
