"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { components } from "@/lib/backend/apiV1/schema";
import { formatDate } from "@/utils/dateUtils";
import { CircleDollarSign, Coins, MessageCircle, ThumbsUp } from "lucide-react";
import Link from "next/link";

type QuestionDto = components["schemas"]["QuestionDto"];
type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

interface QuestionListProps {
  body: components["schemas"]["PageDtoQuestionDto"] | null;
}

export default function QuestionList({ body }: QuestionListProps) {
  if (!body)
    return (
      <div className="flex flex-col min-h-[calc(100dvh-280px)] items-center justify-center py-12 text-muted-foreground">
        <p>데이터가 없습니다.</p>
      </div>
    );

  return (
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
  );
}
