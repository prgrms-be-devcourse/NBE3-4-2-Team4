"use client";

import { components } from "@/lib/backend/apiV1/schema";
import { GenFileListForEdit } from "@/lib/business/components/GenFileListForEdit";

export default function ClientPage({
  question,
  genFiles,
}: {
  question: components["schemas"]["QuestionDto"];
  genFiles: components["schemas"]["GenFileDto"][];
}) {
  return (
    <GenFileListForEdit
      description={`${question.id}번 답변의 파일들`}
      genFiles={genFiles}
      url={`/question/${question.id}/genFile`}
    />
  );
}
