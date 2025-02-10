import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export async function customFetch(url: string, options: RequestInit = {}) {
  if (typeof window === "undefined") {
    // 서버에서는 실행되지 않도록 예외 처리
    return fetch(url, options);
  }

  const cookies = document.cookie
      .split("; ")
      .find((row) => row.startsWith("accessToken="));

  const accessToken = cookies ? cookies.split("=")[1] : null;

  const headers = {
    ...options.headers,
    ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
  };

  return fetch(url, { ...options, headers });
}