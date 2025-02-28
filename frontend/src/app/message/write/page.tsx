"use client";
import ClientPage from "./ClientPage";
import { useState, useEffect } from "react";

export default function Page() {
  const [user, setUser] = useState<string | null>(null);
  
    // 페이지가 로드될 때 user 정보를 가져옴
    useEffect(() => {
      const storedUser = localStorage.getItem("senderName");
      if (storedUser) {
        setUser(storedUser);
      } else {
        console.error("User 정보가 없습니다.");
      }
    }, []);

  return <ClientPage user={user}/>;
}