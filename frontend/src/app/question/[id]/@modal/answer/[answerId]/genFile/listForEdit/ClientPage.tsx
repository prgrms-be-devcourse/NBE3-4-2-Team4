"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

import { components } from "@/lib/backend/apiV1/schema";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { Download, Eye, Pencil, Trash } from "lucide-react";
import { getFileSize } from "@/utils/fileSize";
import { useEffect } from "react";

export default function ClientPage({
  answer,
  genFiles,
}: {
  answer: components["schemas"]["AnswerDto"];
  genFiles: components["schemas"]["GenFileDto"][];
}) {
  const router = useRouter();

  useEffect(() => {
    const needToRefresh = window.sessionStorage.getItem("needToRefresh");

    if (needToRefresh === "true") {
      window.sessionStorage.removeItem("needToRefresh");
      router.refresh();
    }
  }, [router]);

  const attachmentGenFiles = genFiles.filter(
    (genFile) => genFile.typeCode === "attachment"
  );

  return (
    <Dialog
      open
      onOpenChange={() => {
        router.back();
      }}
    >
      <DialogContent className="max-w-[100dvh]">
        <DialogHeader>
          <DialogTitle>파일 관리</DialogTitle>
          <DialogDescription>{answer.id}번 답변의 파일들</DialogDescription>
        </DialogHeader>

        {attachmentGenFiles.length == 0 && (
          <div className="text-center text-sm text-gray-500">
            첨부 파일이 없습니다.
          </div>
        )}

        <div className="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
          {attachmentGenFiles.map((genFile) => (
            <div
              key={genFile.id}
              className="grid gap-2 border border-gray-200 p-3 rounded-lg"
            >
              <Button variant="link" asChild className="justify-start">
                <a
                  href={genFile.downloadUrl}
                  className="flex items-center gap-2"
                >
                  <Download width={13} height={13} />

                  <span className="text-xs">
                    {genFile.originalFileName}
                    <br />({getFileSize(genFile.fileSize)})
                  </span>
                </a>
              </Button>

              <div className="flex flex-wrap">
                <Button variant="link" className="justify-start" asChild>
                  <Link
                    href={`/question/${answer.questionId}/answer/${answer.id}/genFile/${genFile.id}`}
                  >
                    <Eye />
                    <span>미리보기</span>
                  </Link>
                </Button>

                <Button variant="link" className="justify-start" asChild>
                  <Link
                    href={`/question/${answer.questionId}/answer/${answer.id}/genFile/${genFile.id}/modify`}
                  >
                    <Pencil />
                    <span>수정</span>
                  </Link>
                </Button>

                <Button variant="link" className="justify-start" asChild>
                  <Link
                    href={`/question/${answer.questionId}/answer/${answer.id}/genFile/${genFile.id}/delete`}
                  >
                    <Trash />
                    <span>삭제</span>
                  </Link>
                </Button>
              </div>
            </div>
          ))}
        </div>
      </DialogContent>
    </Dialog>
  );
}
