"use client";

import { GenFileDelete } from "@/lib/business/components/GenFileDelete";

export default function ClientPage({
  id,
  genFileId,
}: {
  id: string;
  genFileId: string;
}) {
  return (
    <GenFileDelete
      parentId={parseInt(id)}
      genFileId={parseInt(genFileId)}
      entityType="questions"
    />
  );
}
