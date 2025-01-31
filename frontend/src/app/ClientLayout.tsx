"use client";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  
  return (
    <>
    <header className="p-2 flex justify-center">
      헤더
        </header>
        <main className="flex-1 flex flex-col">{children}</main>
        <footer className="p-2 flex justify-center">
        (c) 2025 WikiPoint
        </footer>
    </>
  );
}