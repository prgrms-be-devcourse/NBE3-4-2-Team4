import ClientPage from "./ClientPage";
import { cookies } from "next/headers";
import client from "@/lib/backend/client";


export default async function Page() {

    const cookieHeader = await cookies();

    const response = await client.GET("/api/admin/adminAssetCategory", {
        params: {
            query: {}
            },
        headers: {
            cookie: cookieHeader.toString(),
            }
        })


    const categories = response.data?.data

    console.log(categories)

    return <ClientPage categories={categories}/>;
}