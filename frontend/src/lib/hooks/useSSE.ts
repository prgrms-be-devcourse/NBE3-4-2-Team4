import { useEffect, useState } from "react";

const useSSE = (userId: number | null) => {
  const [events, setEvents] = useState<string[]>([]);
  const [eventSource, setEventSource] = useState<EventSource | null>(null); // 상태로 관리

  useEffect(() => {
    if (!userId) return; // userId가 없으면 SSE 실행 X

    const es = new EventSource(`http://localhost:8080/api/notifications/${userId}`);

    setEventSource(es); // 현재 SSE 인스턴스를 저장

    es.onmessage = (event) => {
      console.log("받은 알림:", event.data);

      setEvents((prev) => [...prev, event.data]);
    };

    es.onerror = () => {
      console.log("SSE 연결 오류 발생, 연결 종료");

      es.close();

      setEventSource(null); // 종료 후 상태 초기화
    };

    return () => {
      es.close();

      setEventSource(null); // 언마운트 시 정리
    };
  }, [userId]);

  // 🔹 외부에서 SSE 종료할 수 있도록 함수 반환
  const closeSSE = () => {
    if (eventSource) {
      eventSource.close();

      setEventSource(null);

      console.log("SSE 연결 종료");
    }
  };

  return { events, closeSSE };
};

export default useSSE;