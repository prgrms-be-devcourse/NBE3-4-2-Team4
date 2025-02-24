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
import { convertSnakeToCamel } from "@/utils/convertCase";

export default function ClientPage({
  id,
  genFile,
}: {
  id: string;
  genFile: components["schemas"]["GenFileDto"];
}) {
  const router = useRouter();
  genFile = convertSnakeToCamel(genFile);

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
          <DialogDescription>{genFile.originalFileName}</DialogDescription>
        </DialogHeader>
        <div className="flex justify-center">
          {genFile.fileExtTypeCode == "img" && (
            <Image
              loader={imageLoader}
              src={genFile.publicUrl}
              alt={genFile.originalFileName}
              width={100}
              height={100}
              quality={100}
              className="max-w-[80dvh] max-w-[80dvh] w-full"
            />
          )}
          {genFile.fileExtTypeCode == "audio" && (
            <audio src={genFile.publicUrl} controls />
          )}
          {genFile.fileExtTypeCode == "video" && (
            <video src={genFile.publicUrl} controls />
          )}
        </div>
        <DialogFooter className="gap-2">
          <Button variant="link" asChild className="justify-start">
            <a href={genFile.downloadUrl} className="flex items-center gap-2">
              <Download />
              <span>
                {genFile.originalFileName}({getFileSize(genFile.fileSize)})
                다운로드
              </span>
            </a>
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
