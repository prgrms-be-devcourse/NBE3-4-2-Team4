import { format } from "date-fns";

export function formatDate(date: string) {
  return format(new Date(date), "yyyy년 MM월 dd일 HH:mm:ss");
}