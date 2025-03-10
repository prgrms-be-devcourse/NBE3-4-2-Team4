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
import client from "@/lib/backend/client";
import RejectDialog from "@/lib/business/components/RejectDialog";
import ChatWindow from "@/lib/business/components/ChatWindow";

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
    sender_id: number;
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
  const { events, clearEvents } = useSSE(id);
  const [notification, setNotification] = useState<NotificationEvent | null>(
    null
  );
  const [isOpen, setIsOpen] = useState(false);
  const [activeChatUser, setActiveChatUser] = useState<string | null>(null);
  const [showChat, setShowChat] = useState(false);
  const [showRejectDialog, setShowRejectDialog] = useState(false);
  const [rejectMessage, setRejectMessage] = useState("");

  useEffect(() => {
    if (events.length > 0) {
      const latestEvent = events[events.length - 1];
      const parsedEvent: NotificationEvent =
        typeof latestEvent === "string" ? JSON.parse(latestEvent) : latestEvent;

      // 채팅 요청 처리
      if (parsedEvent.sender_username) {
        console.log(
          "Current chat state - activeChatUser:",
          activeChatUser,
          "showChat:",
          showChat
        );

        if (showChat || activeChatUser) {
          // 현재 채팅 중인 사용자와 동일한 사용자의 요청은 무시
          // if (parsedEvent.sender_username === activeChatUser) {
          //   clearEvents();
          //   return;
          // }

          console.log("Sending reject notification - user is in chat");
          const recipientId = parsedEvent.sender_id;
          client
            .POST(`/api/notifications/reject/${recipientId}`, {
              body: {
                message: `${nickname}님이 현재 다른 채팅에 참여 중입니다.`,
              },
            })
            .then(() => {
              clearEvents();
            });
        } else {
          console.log("Opening chat request dialog");
          setNotification(parsedEvent);
          setIsOpen(true);
        }
      } else if (
        parsedEvent.message &&
        !showChat &&
        !activeChatUser &&
        (parsedEvent.message.includes("거절") ||
          parsedEvent.message.includes("다른 채팅에 참여 중"))
      ) {
        // 채팅 중이 아닐 때만 거절 메시지 표시
        setRejectMessage(parsedEvent.message);
        setShowRejectDialog(true);
        clearEvents();
      }
    }
  }, [events, activeChatUser, nickname, id, showChat]);

  const handleChatStart = (username: string) => {
    if (!notification) return;
    console.log("Starting chat with:", username);
    console.log("Current notification:", notification);
    setShowChat(true);
    setActiveChatUser(username);
    setIsOpen(false);
    clearEvents(); // 채팅 시작 시 이벤트 초기화
  };

  const handleChatEnd = () => {
    console.log("Ending chat");
    setActiveChatUser(null);
    setShowChat(false);
    setNotification(null);
    setIsOpen(false);
    clearEvents();
  };

  // notification과 showChat 상태가 변경될 때마다 로그 출력
  useEffect(() => {
    console.log("Notification state:", notification);
    console.log("ShowChat state:", showChat);
  }, [notification, showChat]);

  // 컴포넌트 언마운트 시 정리
  useEffect(() => {
    return () => {
      setActiveChatUser(null);
      setShowChat(false);
      setNotification(null);
    };
  }, []);

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
          clearEvents();
          window.location.href = data.data;
        }
      } else {
        console.error("로그아웃 실패:", response.status);
      }
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
    }
  };

  // AlertDialog의 onCancel 핸들러
  const handleChatReject = () => {
    if (notification) {
      client.POST(`/api/notifications/reject/${notification.sender_id}`, {
        body: {
          message: `${nickname}님이 채팅 요청을 거절했습니다.`,
        },
      });
    }
    setIsOpen(false);
    setNotification(null);
  };

  // RejectDialog가 닫힐 때도 이벤트 초기화
  const handleRejectDialogClose = (open: boolean) => {
    setShowRejectDialog(open);
    if (!open) {
      clearEvents();
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
      {/* 채팅 요청 AlertDialog 사용 */}
      <AlertDialog
        open={isOpen}
        onOpenChange={setIsOpen}
        title="채팅 요청"
        description={notification?.message}
        onConfirm={() => handleChatStart(notification?.sender_username!)}
        onCancel={handleChatReject}
      />

      {/* 채팅창 렌더링 */}
      {showChat && notification && (
        <div className="fixed bottom-4 right-4 z-50">
          <ChatWindow
            senderName={notification.sender_name}
            senderUsername={notification.sender_username}
            onClose={handleChatEnd}
          />
        </div>
      )}

      {/* 거절 메시지 다이얼로그 */}
      <RejectDialog
        open={showRejectDialog}
        onOpenChange={handleRejectDialogClose}
        message={rejectMessage}
      />
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
