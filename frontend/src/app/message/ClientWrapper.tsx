"use client";

import client from "@/lib/backend/client";
import { convertSnakeToCamel } from "@/utils/convertCase";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useToast } from "@/hooks/use-toast";
import ClientPage from "./ClientPage";

interface ClientWrapperProps {
  page: number;
}

export default function ClientWrapper({ page }: ClientWrapperProps) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [data, setData] = useState<any>(null);
  const [activeTab, setActiveTab] = useState<'received' | 'sent'>('received');
  const { toast } = useToast();
  const router = useRouter();

  // 로그인 상태 확인 함수
  const checkLoginStatus = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/members/thumbnail", {
        method: "GET",
        credentials: "include",
      });

      if (response.status === 204) {
        setIsAuthenticated(false);
        return false;
      }

      if (response.ok) {
        const data = await response.json();
        if (data?.result_code === "200-1") {
          setIsAuthenticated(true);
          return true;
        }
      }

      setIsAuthenticated(false);
      return false;
    } catch (error) {
      console.error("로그인 상태 확인 중 오류 발생:", error);
      setIsAuthenticated(false);
      return false;
    }
  };

  // 로그인 상태에 따라 쪽지 목록 가져오기
  useEffect(() => {
    let isMounted = true; // 여러 번 실행 방지

    const fetchMessages = async () => {
      const isLoggedIn = await checkLoginStatus();
      if (!isLoggedIn || !isMounted) return;

      try {
        const endpoint = activeTab === 'received' ? "/api/messages/receive" : "/api/messages/send";
        const response = await client.GET(endpoint, {
          params: { query: { page } },
        });

        if (!response?.data) {
          throw new Error("API 응답이 유효하지 않습니다.");
        }

        setData(convertSnakeToCamel(response.data));
      } catch (error) {
        console.error("쪽지 목록 가져오기 실패:", error);
        toast({ title: "쪽지 목록을 불러오는 중 오류가 발생했습니다.", variant: "destructive" });
      }
    };

    fetchMessages();

    return () => {
      isMounted = false;
    };
  }, [page, activeTab]); // page와 activeTab 변경 시만 호출

  if (isAuthenticated === null) {
    return <div className="flex justify-center items-center h-96">로딩 중...</div>;
  }

  if (!isAuthenticated) {
    return <div className="flex justify-center items-center h-96">로그인이 필요합니다.</div>;
  }

  if (data) {
    return (
      <ClientPage
        data={data}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />
    );
  }

  return <div className="flex justify-center items-center h-96">데이터를 불러오는 중 오류가 발생했습니다.</div>;
}
