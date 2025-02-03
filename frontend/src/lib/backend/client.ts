import createClient from "openapi-fetch";

import type { paths } from "@/lib/backend/apiV1/schema";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
  credentials: "include",
});

export default client;
