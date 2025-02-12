"use client"

import { useEffect, useState } from "react";
import Link from "next/link";
import type {components} from "@/lib/backend/apiV1/schema";
import {ThemeProvider as NextThemesProvider} from "next-themes";
import { useRedirectIfNotAuthenticated } from "@/lib/hooks/useRedirect";
type MemberDetailInfoResponseDto = components["schemas"]["MemberDetailInfoResponseDto"];

export default function ClientPage({
                                 children,
                             }: React.ComponentProps<typeof NextThemesProvider>){
    const [memberInfo, setMemberInfo] = useState<MemberDetailInfoResponseDto | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    useRedirectIfNotAuthenticated();

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
                            <Link href="#" className="text-blue-500 hover:underline">
                                {memberInfo?.questionSize !== undefined ? memberInfo?.questionSize : "0"}
                            </Link>
                        </td>
                        <td className="border-b py-2 px-4 text-center">
                            <Link href="#" className="text-blue-500 hover:underline">
                                {memberInfo?.answerSize !== undefined ? memberInfo?.answerSize : "0"}
                            </Link>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <div className="mt-4 text-center flex items-center justify-center">
                <p className="mr-2"><strong>아이디:</strong> {memberInfo?.username}</p>
            </div>
            <div className="mt-4 text-center flex items-center justify-center">
                <p className="mr-2"><strong>닉네임:</strong> {memberInfo?.nickname}</p>
                <Link href="/mypage/edit/nickname">
                    <button className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                        수정
                    </button>
                </Link>
            </div>
            <p><strong>현재 포인트:</strong> {memberInfo?.point}</p>
            <div className="mt-4 text-center">
                <Link href="/mypage/withdrawal">
                    <button className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
                        회원탈퇴
                    </button>
                </Link>
            </div>
        </div>
    )
        ;
};
