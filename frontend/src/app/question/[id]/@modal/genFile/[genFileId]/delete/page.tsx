"use client";

import { use } from "react";

import ClientPage from "./ClientPage";

export default function Page({
  params,
}: {
  params: Promise<{ id: string; genFileId: string }>;
}) {
  const { id, genFileId } = use(params);

  return <ClientPage id={id} genFileId={genFileId} />;
}
