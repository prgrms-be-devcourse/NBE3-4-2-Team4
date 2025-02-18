"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";

import { components } from "@/lib/backend/apiV1/schema";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Download } from "lucide-react";
import { getFileSize } from "@/utils/fileSize";
import imageLoader from "@/utils/imageLoader";

export default function ClientPage({
  id,
  genFile,
}: {
  id: string;
  genFile: components["schemas"]["AnswerGenFileDto"];
}) {
  const router = useRouter();

  return (
    <Dialog
      open
      onOpenChange={() => {
        router.back();
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>파일 미리보기</DialogTitle>
          <DialogDescription>{genFile.original_file_name}</DialogDescription>
        </DialogHeader>
        <div className="flex justify-center">
          {genFile.file_ext_type_code == "img" && (
            <Image
              loader={imageLoader}
              src={genFile.public_url}
              alt={genFile.original_file_name}
              width={100}
              height={100}
              className="max-w-[80dvh] max-w-[80dvh] w-full"
            />
          )}
          {genFile.file_ext_type_code == "audio" && (
            <audio src={genFile.public_url} controls />
          )}
          {genFile.file_ext_type_code == "video" && (
            <video src={genFile.public_url} controls />
          )}
        </div>
        <DialogFooter className="gap-2">
          <Button variant="link" asChild className="justify-start">
            <a href={genFile.download_url} className="flex items-center gap-2">
              <Download />
              <span>
                {genFile.originalFileName}({getFileSize(genFile.file_size)})
                다운로드
              </span>
            </a>
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
