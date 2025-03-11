import { useEffect, useState, useCallback } from "react";

interface NotificationEvent {
  message: string;
  sender_name: string;
  sender_username: string;
  sender_id: number;
}


const useSSE = (userId: number | null) => {
  const [events, setEvents] = useState<NotificationEvent[]>([]);
  const [eventSource, setEventSource] = useState<EventSource | null>(null);

  const clearEvents = () => {
    setEvents([]);
  };

  useEffect(() => {
    if (!userId) {
      if (eventSource) {
        eventSource.close();
        setEventSource(null);
      }
      return;
    }

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
      
      if (userId) {
        console.log("재연결 시도...");
        setTimeout(() => {
          const newEs = new EventSource(`http://localhost:8080/api/notifications/${userId}`);
          setEventSource(newEs);
        }, 1000);
      }
    };

    return () => {
      if (es) {
        es.close();
        setEventSource(null);
      }
    };
  }, [userId]);

  const closeSSE = () => {
    if (eventSource) {
      eventSource.close();
      setEventSource(null);
      setEvents([]);
      console.log("SSE 연결 종료");
    }
  };

  return { events, closeSSE, clearEvents };
};

export default useSSE;