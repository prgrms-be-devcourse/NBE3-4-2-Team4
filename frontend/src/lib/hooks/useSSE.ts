import { useEffect, useState } from "react";

interface NotificationEvent {
  message: string;
  sender_name: string;
  sender_username: string;
}


const useSSE = (userId: number | null) => {
  const [events, setEvents] = useState<NotificationEvent[]>([]);
  const [eventSource, setEventSource] = useState<EventSource | null>(null);

  const connectSSE = () => {
    if (!userId) return null; // userId가 없으면 연결하지 않음

    const es = new EventSource(`http://localhost:8080/api/notifications/${userId}`);
    setEventSource(es);

    es.onmessage = (event) => {
      console.log("받은 알림:", event.data);
      setEvents((prev) => [...prev, event.data]);
    };

    es.onerror = () => {
      console.log("SSE 연결 오류 발생");
      es.close();
      setEventSource(null);
      
      // userId가 있을 때만 재연결 시도
      if (userId) {
        console.log("재연결 시도...");
        setTimeout(() => {
          connectSSE();
        }, 1000);
      }
    };

    return es;
  };

  useEffect(() => {
    const es = connectSSE();

    return () => {
      if (es) {
        es.close();
        setEventSource(null);
      }
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