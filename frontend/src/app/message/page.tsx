"use client";
import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useToast } from "@/hooks/use-toast";

export default function Page() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [receiveData, setReceiveData] = useState<any>(null);
  const [sendData, setSendData] = useState<any>(null);
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
    const fetchMessages = async () => {
      const isLoggedIn = await checkLoginStatus();

      if (isLoggedIn) {
        try {
          const responseReceive = await client.GET("/api/messages/receive");
          const responseSend = await client.GET("/api/messages/send");

          // API 응답 검증
          if (!responseReceive || !responseReceive.data || !responseSend || !responseSend.data) {
            throw new Error("API 응답이 유효하지 않습니다.");
          }

          const receiveData = convertSnakeToCamel(responseReceive.data);
          const sendData = convertSnakeToCamel(responseSend.data);

          setReceiveData(receiveData);
          setSendData(sendData);
        } catch (error) {
          console.error("쪽지 목록 가져오기 실패:", error);
          toast({
            title: "쪽지 목록을 불러오는 중 오류가 발생했습니다.",
            variant: "destructive",
          });
        }
      } else {
        toast({
          title: "로그인이 필요합니다.",
          variant: "destructive",
        });
        router.push("/login"); // 로그인 페이지로 리다이렉트
      }
    };

    fetchMessages();
  }, [toast, router]);

  // 로딩 상태 또는 로그인 여부에 따른 UI 처리
  if (isAuthenticated === null) {
    return (
      <div className="flex justify-center items-center h-96">
        로딩 중...
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="flex justify-center items-center h-96">
        로그인되어 있지 않습니다. 로그인 후 다시 시도해 주세요.
      </div>
    );
  }
  if (receiveData && sendData) {
    return <ClientPage receive={receiveData} send={sendData} />;
  }

  return (
    <div className="flex justify-center items-center h-96">
      데이터를 불러오는 중 오류가 발생했습니다.
    </div>
  );
}
