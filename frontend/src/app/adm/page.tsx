"use client";

import { redirect } from "next/navigation";
import ClientPage from "./ClientPage";

export default function Page() {
  //return <ClientPage />;
  return redirect("/adm/products/list");
}
