"use client";

import { Button } from "@/components/ui/button";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { Download, Eye } from "lucide-react";
import Image from "next/image";
import { components } from "@/lib/backend/apiV1/schema";
import { useEffect, useState } from "react";
import imageLoader from "@/utils/imageLoader";
import { getFileSize } from "@/utils/fileSize";
import Link from "next/link";

type GenFile = components["schemas"]["AnswerGenFileDto"];

export const AnswerAttachmentFiles = ({
  questionId,
  answerId,
}: {
  questionId: number;
  answerId: number;
}) => {
  const { toast } = useToast();
  const [files, setFiles] = useState<GenFile[]>([]);

  useEffect(() => {
    const fetchFiles = async () => {
      const response = await client.GET("/api/answers/{answerId}/genFiles", {
        params: { path: { answerId } },
      });

      console.log("API 응답:", response.data);

      if (response.error) {
        toast({
          title: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      setFiles(response.data ?? []);
    };

    fetchFiles();
  }, [answerId, toast]);

  return (
    <div className="flex flex-wrap gap-2 p-2">
      {files
        .filter((file) => file.type_code === "attachment")
        .map((file) => (
          <Button
            key={file.id}
            variant="outline"
            size="sm"
            className="h-8 px-3"
            asChild
          >
            {file.file_ext_type_code === "img" ||
            file.file_ext_type_code === "audio" ||
            file.file_ext_type_code === "video" ? (
              <Link
                href={`/question/${questionId}/answer/${answerId}/genFilePreview/${file.id}`}
                className="flex items-center gap-1.5"
              >
                <Eye className="h-3.5 w-3.5" />
                <span className="max-w-[100px] truncate">
                  {file.original_file_name}
                </span>
              </Link>
            ) : (
              <a
                href={file.download_url}
                className="flex items-center gap-1.5"
                title={file.original_file_name}
              >
                <Download className="h-3.5 w-3.5" />
                <span className="max-w-[100px] truncate">
                  {file.original_file_name}
                </span>
                <span className="text-xs text-gray-500">
                  ({getFileSize(file.file_size)})
                </span>
              </a>
            )}
          </Button>
        ))}
    </div>
  );
};
