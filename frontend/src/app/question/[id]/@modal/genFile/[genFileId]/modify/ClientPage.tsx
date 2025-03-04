"use client";

import { GenFileModify } from "@/lib/business/components/GenFileModify";
import { components } from "@/lib/backend/apiV1/schema";

export default function ClientPage({
  id,
  genFile,
}: {
  id: string;
  genFile: components["schemas"]["GenFileDto"];
}) {
  return (
    <GenFileModify
      genFile={genFile}
      parentId={parseInt(id)}
      entityType="questions"
    />
  );
}
