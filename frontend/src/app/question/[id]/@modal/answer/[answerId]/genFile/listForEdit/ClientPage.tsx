"use client";

import { components } from "@/lib/backend/apiV1/schema";
import { GenFileListForEdit } from "@/lib/business/components/GenFileListForEdit";

export default function ClientPage({
  answer,
  genFiles,
}: {
  answer: components["schemas"]["AnswerDto"];
  genFiles: components["schemas"]["GenFileDto"][];
}) {
  return (
    <GenFileListForEdit
      description={`${answer.id}번 답변의 파일들`}
      genFiles={genFiles}
      url={`/question/${answer.questionId}/answer/${answer.id}/genFile`}
    />
  );
}
