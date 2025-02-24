"use client";
import client from "@/lib/backend/client";
import { useState, useEffect } from "react";
import type { components } from "@/lib/backend/apiV1/schema";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

type PageDtoQuestionDto = components["schemas"]["PageDtoQuestionDto"];

export default function Page() {
  const [user, setUser] = useState<string | null>(null);
  const [body, setBody] = useState<PageDtoQuestionDto | null>(null);

  // 페이지가 로드될 때 user 정보를 가져옴
  useEffect(() => {
    const storedUser = localStorage.getItem("username");
    if (storedUser) {
      setUser(storedUser);
    } else {
      console.error("User 정보가 없습니다.");
    }
  }, []);

  useEffect(() => {
    if (!user) return; // user 정보가 없으면 요청하지 않음

    const fetchPosts = async () => {
      try {
        const response = await client.POST("/api/questions/me", {
          body: {
            username: user
          }
        })

        if (!response || !response.data) {
          throw new Error("API 응답이 유효하지 않습니다.");
        }
        setBody(convertSnakeToCamel(response.data!!));
      } catch (error) {
        console.error(error);
      }
    }

    fetchPosts();
  }, [user]);

  return <ClientPage body={body} />;
}
