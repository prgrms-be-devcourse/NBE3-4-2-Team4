"use client"

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import type {components} from "@/lib/backend/apiV1/schema";
import {ThemeProvider as NextThemesProvider} from "next-themes";

type MemberDetailInfoResponseDto = components["schemas"]["MemberDetailInfoResponseDto"];

export default function ClientPage({
                                 children,
                             }: React.ComponentProps<typeof NextThemesProvider>){
    const [memberInfo, setMemberInfo] = useState<MemberDetailInfoResponseDto | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchMemberDetail = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/members/details", {
                    method: "GET",
                    credentials: "include", // 쿠키를 포함한 요청
                });

                if (response.ok) {
                    const data = await response.json();
                    if (data.result_code === "200-1") {
                        setMemberInfo(data.data);
                    } else {
                        setError("회원 정보를 불러오는 데 실패했습니다.");
                    }
                } else {
                    setError("로그인 상태가 아니거나 서버에 문제가 발생했습니다.");
                }
            } catch (err) {
                setError("네트워크 오류가 발생했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchMemberDetail();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className="container mx-auto p-4 flex flex-col justify-center items-center h-screen">
            <h1 className="text-xl font-bold mb-4">마이페이지</h1>

            <div className="mt-6 w-full max-w-lg">
                <table className="min-w-full table-auto border-collapse">
                    <thead>
                    <tr>
                        <th className="border-b py-2 px-4 text-center">작성 질문 수</th>
                        <th className="border-b py-2 px-4 text-center">작성 답변 수</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td className="border-b py-2 px-4 text-center">
                            <Link href="/mypage/questions" className="text-blue-500 hover:underline">
                                {memberInfo?.questionSize !== undefined ? memberInfo?.questionSize : "0"}
                            </Link>
                        </td>
                        <td className="border-b py-2 px-4 text-center">
                            <Link href="/mypage/answers" className="text-blue-500 hover:underline">
                                {memberInfo?.answerSize !== undefined ? memberInfo?.answerSize : "0"}
                            </Link>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <div className="mt-4 text-center">
                <p><strong>닉네임:</strong> {memberInfo?.nickname}</p>
                <p><strong>현재 포인트:</strong> {memberInfo?.point}</p>
            </div>
        </div>
    );
};
