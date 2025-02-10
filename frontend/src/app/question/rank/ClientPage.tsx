"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import { useRouter, useSearchParams } from "next/navigation";
import { formatDate } from "@/utils/dateUtils";
import { MessageCircle, ThumbsUp, Banknote } from "lucide-react";
import Link from "next/link";

type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];
type QuestionDto = components["schemas"]["QuestionDto"];

interface ClientPageProps {
    body: PageDtoQuestionDto;
}

export default function ClientPage({ body }: ClientPageProps) {
    const router = useRouter();
    const searchParams = useSearchParams();

    const currentPage = Number(searchParams.get("page")) || 1;

    // 페이지 이동 함수
    const changePage = (newPage: number) => {
        const queryParams = new URLSearchParams();
        queryParams.set("page", newPage.toString());

        router.push(`?${queryParams.toString()}`);
    };

    let currentRank = 0; // 순위 계산 변수
    let previousRecommendCount = -1;

    return (
        <div className="container mx-auto px-4">
            <h1 className="mb-3">인기글 랭킹</h1>
            <hr /><br />

            <ul className="flex flex-col gap-4">
                {body.items?.map((item: QuestionDto, index) => {
                    // 공동 순위 반영
                    if (item.recommendCount !== previousRecommendCount) {
                        currentRank = index + 1;
                    }
                    previousRecommendCount = item.recommendCount!!;
                    
                    return (
                        <li key={item.id}
                        className={`flex items-center dark:bg-gray-800 justify-between border-2 border-gray-300 p-3 rounded-md
                            ${currentRank === 1 ? "bg-yellow-300 bg-opacity-60" : ""}
                            ${currentRank === 2 ? "bg-gray-300 bg-opacity-60" : ""} 
                            ${currentRank === 3 ? "bg-orange-300 bg-opacity-60" : ""}`}>
                            <Link href={`/question/${item.id}`} className="flex items-center justify-between w-full block">
                            <div className="flex gap-2 flex-1 font-semibold text-lg truncate">
                                <span>{currentRank}.</span>
                                <span>{item.title}</span>
                                <div className="flex gap-2 items-center text-yellow-500 text-sm font-medium">
                                    <Banknote size={20} />
                                    {item.point}
                                </div>
                            </div>
                            <div className="w-40 text-sm text-center flex items-center justify-between">
                            {item.recommendCount && item.recommendCount > 0 ? (
                                <span className="flex items-center gap-1 text-purple-500 font-medium">
                                <ThumbsUp size={16} />
                                {item.recommendCount}
                                </span>
                            ) : <span></span>}
                            {item.answers && item.answers?.length > 0 ? (
                                <span className="flex items-center gap-1 text-blue-500 font-medium">
                                <MessageCircle size={16} />
                                {item.answers?.length}
                                </span>
                            ) : <span></span>}
                            <span>{item.name}</span>
                            </div>
                            <div className="w-56 text-sm text-right ms-8">작성 일시: {formatDate(item.createdAt)}</div><br />
                            </Link>
                        </li>
                    );
                })}
            </ul>
            <br />

            {/* 페이지 이동 버튼 */}
            <div className="flex justify-center gap-2 mb-3">
                <button onClick={() => changePage(currentPage - 1)} disabled={currentPage === 1}
                className={`px-4 py-2 rounded-md text-white font-semibold transition ${
                    currentPage === 1
                    ? "bg-gray-300 cursor-not-allowed" // 이전, 다음 페이지 없을 시 비활성화
                    : "bg-blue-500 hover:bg-blue-600"
                }`}
                >
                이전
                </button>

                {/* 페이지 번호 버튼 */}
                {Array.from({ length: body.totalPages || 1 }, (_, i) => i + 1).map((page) => (
                <button key={page} onClick={() => changePage(page)}
                    className={`px-3 py-2 rounded-md text-white font-semibold transition ${
                    currentPage === page
                        ? "bg-blue-100 cursor-not-allowed" // 현재 페이지 비활성화
                        : "bg-blue-300 hover:bg-blue-500"
                    }`}
                    >
                    {page}
                </button>
                ))}

                <button onClick={() => changePage(currentPage + 1)} disabled={currentPage === body.totalPages}
                className={`px-4 py-2 rounded-md text-white font-semibold transition ${
                    currentPage === body.totalPages
                    ? "bg-gray-300 cursor-not-allowed"
                    : "bg-blue-500 hover:bg-blue-600"
                }`}
                >
                다음
                </button>
            </div>
        </div>
    );
}