import type { Metadata } from "next";
import "./globals.css";
import { ClientLayout } from "./ClientLayout";
import localFont from "next/font/local";

const pretendard = localFont({
  src: "./../../node_modules/pretendard/dist/web/variable/woff2/PretendardVariable.woff2",
  display: "swap",
  weight: "45 920",
  variable: "--font-pretendard",
});

export const metadata: Metadata = {
  title: "WikiPoint",
  description: "지식 공유하고 포인트 받자!",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="ko"
      className={`${pretendard.variable}`}
      suppressHydrationWarning
    >
      <body className={`${pretendard.className} antialiased`}>
        <div
          className="flex flex-col min-h-[100dvh] bg-background"
          vaul-drawer-wrapper="true"
        >
          <ClientLayout>{children}</ClientLayout>
        </div>
      </body>
    </html>
  );
}
