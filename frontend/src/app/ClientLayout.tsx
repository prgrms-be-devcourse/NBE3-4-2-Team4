"use client";

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
  Lock,
  Coins
} from "lucide-react";
import Link from "next/link";
import { IdProvider, useId } from "@/context/IdContext";
import { NicknameProvider, useNickname } from "@/context/NicknameContext";
import { RoleProvider, useRole } from "@/context/RoleContext";

export function ClientLayout({ children }: { children: React.ReactNode }) {
  return (
      <IdProvider>
        <NicknameProvider>
          <RoleProvider>
            <ClientLayoutContent>{children}</ClientLayoutContent>
          </RoleProvider>
        </NicknameProvider>
      </IdProvider>
  );
}

function ClientLayoutContent({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const { nickname } = useNickname();
  const { setNickname } = useNickname();
  const { setId } = useId();
  const { setRole } = useRole();

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/members/thumbnail", {
          method: "GET",
          credentials: "include",
        });

        if (response.status === 204) {
          return { isAuthenticated: false, nickname: null, id: null, role: null};
        }

        if (response.ok) {
          const data = await response.json();

          if (data?.result_code === "200-1") {
            return { isAuthenticated: true, nickname: data?.data?.nickname || null, id: data?.data?.id , role: data?.data?.role};
          }
        }
      } catch (error) {
        console.error("로그인 상태 확인 중 오류 발생:", error);
        return { isAuthenticated: false, nickname: null, id: null, role: null};
      }
      return { isAuthenticated: false, nickname: null, id: null, role: null};
    };

    checkLoginStatus().then((result) => {
      setIsAuthenticated(result.isAuthenticated);
      setNickname(result.nickname);
      setId(result.id);
      setRole(result.role);
    });
  }, []);

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/logout", {
        method: "POST",
        credentials: "include",
        redirect: "follow",
      });

      if (response.ok) {
        const data = await response.json();
        if (data.data) {
          window.location.href = data.data;
        }
      } else {
        console.error("로그아웃 실패:", response.status);
      }
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
    }
  };

  if (isAuthenticated === null) {
    // 로딩 중 상태에서는 레이아웃 깨지지 않도록 유지
    return (
        <NextThemesProvider attribute="class" defaultTheme="system" enableSystem disableTransitionOnChange>
          <div className="min-h-screen flex items-center justify-center">
            <p>로딩 중...</p>
          </div>
        </NextThemesProvider>
    );
  }

  return (
      <NextThemesProvider attribute="class" defaultTheme="system" enableSystem disableTransitionOnChange>
        <header>
          <div className="flex container mx-auto py-2">
            <Button variant="link" asChild>
              <Link href="/" className="font-bold">
                <GraduationCap /> WikiPoint
              </Link>
            </Button>
            <Button variant="link" asChild>
              <Link href="/question/list">
                <MessageCircleQuestion /> 지식인
              </Link>
            </Button>
            <Button variant="link" asChild>
              <Link href="/shop/list">
                <ShoppingCart /> 포인트 쇼핑
              </Link>
            </Button>
            <Button variant="link" asChild>
              <Link href="/point/list">
                <Coins /> 포인트
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
                    <Lock className="mr-1" />
                    로그아웃
                  </Button>
                </>
            ) : (
                <Button variant="link">
                  <Lock className="mr-2" />
                  <Link href="/login">로그인</Link>
                </Button>
            )}
            <ThemeToggleButton />
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

