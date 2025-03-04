import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export default async function Page() {
    const cookieHeader = await cookies();

    // 유저 정보 조회
    const userResponse = await client.GET("/api/members/details", {
        headers: {
            cookie: cookieHeader.toString(),
        },
    });

    // 은행 계좌 목록 조회
    const bankAccountResponse = await client.GET("/api/banks/accounts", {
        headers: {
            cookie: cookieHeader.toString(),
        },
    });

    const user = userResponse.data?.data ?? [];
    const bankAccounts = bankAccountResponse.data?.data ?? [];

    if (user.length === 0) {
        redirect("/login");
    }

    return <ClientPage user={user} bankAccounts={bankAccounts} cookieString={cookieHeader} />;
}
