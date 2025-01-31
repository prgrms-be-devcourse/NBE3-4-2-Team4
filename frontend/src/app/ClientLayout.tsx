"use client";

import { Button } from "@/components/ui/button";
import ThemeToggleButton from "@/lib/business/components/ThemeToggleButton";
import { ThemeProvider as NextThemesProvider } from "next-themes";
import {
  Copyright,
  GraduationCap,
  MessageCircleQuestion,
  MonitorCog,
  ShoppingCart,
} from "lucide-react";
import Link from "next/link";

export function ClientLayout({
  children,
}: React.ComponentProps<typeof NextThemesProvider>) {
  return (
    <NextThemesProvider
      attribute="class"
      defaultTheme="system"
      enableSystem
      disableTransitionOnChange
    >
      <header>
        <div className={`flex container mx-auto py-2`}>
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
              <ShoppingCart /> 포인트 쇼핑
            </Link>
          </Button>
          <div className="flex-grow"></div>
          <ThemeToggleButton />
        </div>
      </header>
      <main className="flex-1 flex flex-col">{children}</main>
      <footer className="p-2 flex justify-center items-center">
        <Copyright className="w-4 h-4 mr-1" /> 2025 WikiPoint
        <Button variant="link" asChild>
          <Link href="/adm">
            <MonitorCog /> 관리자 홈
          </Link>
        </Button>
      </footer>
    </NextThemesProvider>
  );
}
