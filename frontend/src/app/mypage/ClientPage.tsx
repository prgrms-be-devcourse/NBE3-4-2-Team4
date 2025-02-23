"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import type { components } from "@/lib/backend/apiV1/schema";
import { ThemeProvider as NextThemesProvider } from "next-themes";
import { useRedirectIfNotAuthenticated } from "@/lib/hooks/useRedirect";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Coins } from "lucide-react";
type MemberDetailInfoResponseDto =
  components["schemas"]["MemberDetailInfoResponseDto"];

export default function ClientPage({
  children,
}: React.ComponentProps<typeof NextThemesProvider>) {
  const [memberInfo, setMemberInfo] =
    useState<MemberDetailInfoResponseDto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  useRedirectIfNotAuthenticated();

  useEffect(() => {
    const fetchMemberDetail = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/members/details",
          {
            method: "GET",
            credentials: "include", // 쿠키를 포함한 요청
          }
        );

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
    <div className="container mx-auto p-4 flex flex-col justify-center items-center h-screen mt-[-104px]">
      <div className="mb-10 text-center relative">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          마이페이지
        </h2>
      </div>

      <Card className="w-[400px] max-w-full">
        <CardHeader>
          <CardTitle className="flex justify-between items-center gap-2">
            {memberInfo?.username}
            <p className="flex items-center gap-2 text-amber-500">
              <Coins size={16} /> {memberInfo?.point?.amount}
            </p>
            <Button variant="outline" asChild>
              <Link href="/mypage/posts">내글 보기</Link>
            </Button>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center flex items-center justify-between">
            <p className="mr-2">{memberInfo?.nickname}</p>
            <Button variant="outline" asChild>
              <Link href="/mypage/edit/nickname">수정하기</Link>
            </Button>
          </div>
        </CardContent>
        <CardFooter>
          <Button variant="destructive" className="w-full" asChild>
            <Link href="/mypage/withdrawal">회원탈퇴</Link>
          </Button>
        </CardFooter>
      </Card>

      {/* <div className="mt-6 w-full max-w-lg">
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
                  {memberInfo?.questionSize !== undefined
                    ? memberInfo?.questionSize
                    : "0"}
                </Link>
              </td>
              <td className="border-b py-2 px-4 text-center">
                <Link href="#" className="text-blue-500 hover:underline">
                  {memberInfo?.answerSize !== undefined
                    ? memberInfo?.answerSize
                    : "0"}
                </Link>
              </td>
            </tr>
          </tbody>
        </table>
      </div> */}
    </div>
  );
}
