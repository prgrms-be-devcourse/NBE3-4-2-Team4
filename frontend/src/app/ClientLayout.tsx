"use client";

import { Lock } from "lucide-react";
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

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/logout", {
        method: "POST",
        credentials: "include", // 쿠키를 포함한 요청을 보낼 때 사용
      });

      if (response.ok) {
        // 로그아웃 후 로그인 페이지로 리다이렉트
        window.location.href = "/login"; // 직접 리다이렉트
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
          {/*{isAuthenticated ? (*/}
          {/*    <Button variant="link" onClick={handleLogout}>*/}
          {/*      로그아웃*/}
          {/*    </Button>*/}
          {/*) : (*/}
              <Button variant="link">
                <Lock className="mr-2" />
                <Link href="/login">로그인</Link>
              </Button>
          {/*// )}*/}
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
