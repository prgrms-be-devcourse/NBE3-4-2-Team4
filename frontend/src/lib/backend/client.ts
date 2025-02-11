import createClient from "openapi-fetch";
//import { cookies } from "next/headers";

import type { paths } from "@/lib/backend/apiV1/schema";

// const getAccessToken = () => {
//   const cookieStore = cookies();
//   console.log("cookie:", cookieStore);
//   return cookieStore.get("accessToken")?.value; // 쿠키에서 accessToken 가져오기
// };

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
  credentials: "include",
  // beforeRequest: (request) => {
  //   const accessToken = getAccessToken();
  //   if (accessToken) {
  //     request.headers.set("Authorization", `Bearer ${accessToken}`);
  //   }
  // }
});

export default client;
