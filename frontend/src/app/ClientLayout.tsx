"use client";

import { Lock } from "lucide-react";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import ThemeToggleButton from "@/lib/business/components/ThemeToggleButton";
import { ThemeProvider as NextThemesProvider } from "next-themes";
import {
  Copyright,
  GraduationCap,
  MessageCircleQuestion,
  MonitorCog,
  ShoppingCart,
} from "lucide-react";
import Link from "next/link";



export function ClientLayout({
  children,
}: React.ComponentProps<typeof NextThemesProvider>) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [nickname, setNickname] = useState<string | null>(null); // 닉네임 저장

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/members/thumbnail", {
          method: "GET",
          credentials: "include", // 쿠키를 포함한 요청을 보낼 때 사용
        });

        if (response.status === 204) {
          return { isAuthenticated: false, nickname: null };
        }

        if (response.ok) {
          const data = await response.json();
          console.log(data);

          if (data?.result_code === "200-1") { // status 대신 result_code를 확인
            // 로그인 되어 있을 때
            return { isAuthenticated: true, nickname: data?.data?.nickname || null };
          } else {
            // 로그인 안 되어 있을 때
            return { isAuthenticated: false, nickname: null };
          }
        } else {
          console.error("로그인 상태 확인 실패:", response.status);
          return { isAuthenticated: false, nickname: null };
        }
      } catch (error) {
        console.error("로그인 상태 확인 중 오류 발생:", error);
        return { isAuthenticated: false, nickname: null };
      }
    };

    // 비동기 함수 호출 후 처리
    checkLoginStatus().then((result) => {
      setIsAuthenticated(result.isAuthenticated);
      setNickname(result.nickname);
    });
  }, []);

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/logout", {
        method: "POST",
        credentials: "include", // 쿠키를 포함한 요청을 보낼 때 사용
        redirect: "follow", // 자동 리다이렉트 따라가기
      });

      if (response.ok) {
        // 로그아웃 완료 후 리다이렉트 URL을 받는다
        const data = await response.json();
        const locationUrl = data.data;
        if (locationUrl) {
          window.location.href = locationUrl;
        } else {
          console.error("로그아웃 응답 데이터 오류");
        }
      } else {
        console.error("로그아웃 실패:", response.status);
      }
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
    }
  };


  return (
    <NextThemesProvider
      attribute="class"
      defaultTheme="system"
      enableSystem
      disableTransitionOnChange
    >
      <header>
        <div className={`flex container mx-auto py-2`}>
          <Button variant="link" asChild>
            <Link href="/" className="font-bold">
              <GraduationCap/> WikiPoint
            </Link>
          </Button>
          <Button variant="link" asChild>
            <Link href="/question/list">
              <MessageCircleQuestion/> 지식인
            </Link>
          </Button>
          <Button variant="link" asChild>
            <Link href="/shop/list">
              <ShoppingCart/> 포인트 쇼핑
            </Link>
          </Button>
          <div className="flex-grow"></div>
          {isAuthenticated ? (
              <>
                <span className="text-sm font-medium flex items-center">환영합니다,</span>
                <Link href="/mypage" className="text-sm font-medium flex items-center cursor-pointer">
                  {nickname}
                </Link>
                <span className="text-sm font-medium flex items-center">님</span>
                <Button variant="link" onClick={handleLogout}>
                  <Lock className="mr-1"/>
                  로그아웃
                </Button>
              </>
          ) : (
              <Button variant="link">
                <Lock className="mr-2"/>
                <Link href="/login">로그인</Link>
              </Button>
          )}
          <ThemeToggleButton/>
        </div>
      </header>
      <main className="flex-1 flex flex-col">{children}</main>
      <footer className="p-2 flex justify-center items-center">
        <Copyright className="w-4 h-4 mr-1" /> 2025 WikiPoint
        <Button variant="link" asChild>
          <Link href="/adm">
            <MonitorCog /> 관리자 홈
          </Link>
        </Button>
      </footer>
    </NextThemesProvider>
  );
}
