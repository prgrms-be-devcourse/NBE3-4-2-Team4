"use client";

import { GenFileDelete } from "@/lib/business/components/GenFileDelete";

export default function ClientPage({
  id,
  answerId,
  genFileId,
}: {
  id: string;
  answerId: string;
  genFileId: string;
}) {
  return (
    <GenFileDelete
      parentId={parseInt(answerId)}
      genFileId={parseInt(genFileId)}
      entityType="answers"
    />
  );
}
