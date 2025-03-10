"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import ThemeToggleButton from "@/lib/business/components/ThemeToggleButton";
import { ThemeProvider as NextThemesProvider } from "next-themes";
import {
  Copyright,
  GraduationCap,
  MessageCircleQuestion,
  ShoppingCart,
  Lock,
  Settings,
  LockOpen,
  UserRound,
  MessageSquare,
  Wallet,
  Scale,
} from "lucide-react";
import MessageNumIcon from "@/components/icon/message-icon";
import Link from "next/link";
import { IdProvider, useId } from "@/context/IdContext";
import { NicknameProvider, useNickname } from "@/context/NicknameContext";
import { RoleProvider, useRole } from "@/context/RoleContext";
import { Toaster } from "@/components/ui/toaster";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { useToast } from "@/hooks/use-toast";
import useSSE from "@/lib/hooks/useSSE";
import AlertDialog from "@/lib/business/components/AlertDialog";

export function ClientLayout({ children }: { children: React.ReactNode }) {
  return (
    <IdProvider>
      <NicknameProvider>
        <RoleProvider>
          <ClientLayoutContent>
            {children} <Toaster />
          </ClientLayoutContent>
        </RoleProvider>
      </NicknameProvider>
    </IdProvider>
  );
}

function ClientLayoutContent({ children }: { children: React.ReactNode }) {
  interface NotificationEvent {
    message: string;
    sender_name: string;
    sender_username: string;
  }

  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const { nickname } = useNickname();
  const { setNickname } = useNickname();
  const [unreadMessages, setUnreadMessages] = useState<number>(0);
  const { id, setId } = useId();
  const { role, setRole } = useRole();
  const pathname = usePathname();
  const isAdminPage = pathname.startsWith("/adm") && pathname !== "/adm/login";
  const isUserPage = !isAdminPage;
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const searchParams = useSearchParams();
  const attendanceMessage = searchParams.get("attendanceMessage");
  const router = useRouter();
  const { toast } = useToast();
  const { events, closeSSE } = useSSE(id ?? null);
  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState("");

  const [notification, setNotification] = useState<NotificationEvent | null>(
    null
  );

  useEffect(() => {
    if (events.length > 0) {
      const latestEvent = events[events.length - 1];
      const parsedEvent =
        typeof latestEvent === "string" ? JSON.parse(latestEvent) : latestEvent;

      setNotification(parsedEvent);
      setIsOpen(true);
    }
  }, [events]);

  useEffect(() => {
    if (attendanceMessage) {
      toast({
        title: decodeURIComponent(attendanceMessage),
        variant: "destructive",
      });
      // alert()
      router.push("/"); // 파라미터 제거된 URL로 이동
    }
  }, [attendanceMessage]);

  const fetchUnreadMessages = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/messages/receive/unread",
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (response.ok) {
        const data = await response.json();
        setUnreadMessages(data || 0);
      }
    } catch (error) {
      console.error("쪽지 개수 가져오기 실패:", error);
    }
  };

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/members/thumbnail",
          {
            method: "GET",
            credentials: "include",
          }
        );

        if (response.status === 204) {
          return {
            isAuthenticated: false,
            nickname: null,
            id: null,
            role: null,
          };
        }

        if (response.ok) {
          const data = await response.json();

          if (data?.result_code === "200-1") {
            fetchUnreadMessages();
            return {
              isAuthenticated: true,
              nickname: data?.data?.nickname || null,
              id: data?.data?.id,
              role: data?.data?.role,
            };
          }
        }
      } catch (error) {
        console.error("로그인 상태 확인 중 오류 발생:", error);
        return { isAuthenticated: false, nickname: null, id: null, role: null };
      }
      return { isAuthenticated: false, nickname: null, id: null, role: null };
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
          setRole(null);
          setNickname(null);
          setId(null);
          closeSSE();
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
      <NextThemesProvider
        attribute="class"
        defaultTheme="system"
        enableSystem
        disableTransitionOnChange
      >
        <div className="min-h-screen flex items-center justify-center">
          <p>로딩 중...</p>
        </div>
      </NextThemesProvider>
    );
  }

  return (
    <NextThemesProvider
      attribute="class"
      defaultTheme="system"
      enableSystem
      disableTransitionOnChange
    >
      <header className="relative z-10">
        <div className="flex container mx-auto py-2">
          {isUserPage && (
            <>
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
                  <ShoppingCart /> 쇼핑
                </Link>
              </Button>
              <Button variant="link" asChild>
                <Link href="/point/list">
                  <Wallet /> 자금 관리
                </Link>
              </Button>
            </>
          )}

          {isAdminPage && (
            <>
              <Button variant="link" asChild>
                <Link href="/adm/products/list" className="font-bold">
                  <Settings /> 관리자 홈
                </Link>
              </Button>
              <Button variant="link" asChild>
                <Link href="/" className="font-bold">
                  <GraduationCap /> WikiPoint
                </Link>
              </Button>
              <Button variant="link" asChild>
                <Link href="/adm/products/list">
                  <ShoppingCart /> 상품 관리
                </Link>
              </Button>
              <Button variant="link" asChild>
                <Link href="/adm/point">
                  <Scale /> 포인트/캐시 관리
                </Link>
              </Button>
              <Button variant="link" asChild>
                <Link href="/adm/question">
                  <MessageSquare /> 질문 관리
                </Link>
              </Button>
            </>
          )}

          <div className="flex-grow"></div>
          {isAuthenticated ? (
            <div className="flex items-center">
              <MessageNumIcon count={unreadMessages} />

              <span className="text-sm font-medium flex items-center">
                환영합니다,
              </span>
              <DropdownMenu
                open={isDropdownOpen}
                onOpenChange={setIsDropdownOpen}
              >
                <DropdownMenuTrigger asChild>
                  <Button variant="link">{nickname} 님</Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem>
                    <Button
                      variant="link"
                      className="w-full justify-start text-sm font-medium flex items-center"
                      asChild
                      onClick={() => setIsDropdownOpen(false)}
                    >
                      <Link href="/mypage">
                        <UserRound /> 마이 페이지
                      </Link>
                    </Button>
                  </DropdownMenuItem>
                  {role === "ADMIN" && (
                    <DropdownMenuItem>
                      <Button
                        variant="link"
                        className="w-full justify-start"
                        asChild
                        onClick={() => setIsDropdownOpen(false)}
                      >
                        <Link href="/adm">
                          <Settings /> 관리자 홈
                        </Link>
                      </Button>
                    </DropdownMenuItem>
                  )}
                  <DropdownMenuItem>
                    <Button
                      variant="link"
                      onClick={() => {
                        setIsDropdownOpen(false);
                        handleLogout();
                      }}
                    >
                      <LockOpen className="mr-1" />
                      로그아웃
                    </Button>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
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
      {notification && (
        <AlertDialog
          message={notification.message}
          senderName={notification.sender_name}
          senderUsername={notification.sender_username}
          open={isOpen}
          onClose={() => setIsOpen(false)}
        />
      )}
      <footer className="p-2 flex justify-center items-center">
        <Copyright className="w-4 h-4 mr-1" /> 2025 WikiPoint
        {/* {role === "ADMIN" && (
          <Button variant="link" asChild>
            <Link href="/adm">
              <Settings /> 관리자 홈
            </Link>
          </Button>
        )} */}
      </footer>
    </NextThemesProvider>
  );
}
