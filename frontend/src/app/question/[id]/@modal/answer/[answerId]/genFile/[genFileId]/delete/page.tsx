"use client";

import { use } from "react";

import ClientPage from "./ClientPage";

export default function Page({
  params,
}: {
  params: Promise<{ id: string; answerId: string; genFileId: string }>;
}) {
  const { id, answerId, genFileId } = use(params);

  return <ClientPage id={id} answerId={answerId} genFileId={genFileId} />;
}
