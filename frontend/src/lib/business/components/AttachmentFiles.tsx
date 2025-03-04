"use client";

import { Button } from "@/components/ui/button";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { Download, Eye } from "lucide-react";
import { components } from "@/lib/backend/apiV1/schema";
import { useEffect, useState } from "react";
import { getFileSize } from "@/utils/fileSize";
import Link from "next/link";
import { convertSnakeToCamel } from "@/utils/convertCase";

type GenFile = components["schemas"]["GenFileDto"];
type EntityType = "answers" | "questions";

export const AttachmentFiles = ({
  questionId,
  parentId,
  entityType,
}: {
  questionId: number;
  parentId: number;
  entityType: EntityType;
}) => {
  const { toast } = useToast();
  const [files, setFiles] = useState<GenFile[]>([]);

  useEffect(() => {
    const fetchFiles = async () => {
      const response = await client.GET(
        `/api/${entityType}/{parentId}/genFiles`,
        {
          params: { path: { parentId: parentId } },
        }
      );

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
  }, [parentId, toast]);

  const getViewPath = (file: GenFile) => {
    if (entityType === "answers") {
      return `/question/${questionId}/answer/${parentId}/genFile/${file.id}`;
    }
    return `/question/${questionId}/genFile/${file.id}`;
  };

  return (
    <div className="flex flex-wrap gap-2 p-2">
      {convertSnakeToCamel(files)
        .filter((file) => file.typeCode === "attachment")
        .map((file) => (
          <Button
            key={file.id}
            variant="outline"
            size="sm"
            className="h-8 px-3"
            asChild
          >
            {file.fileExtTypeCode === "img" ||
            file.fileExtTypeCode === "audio" ||
            file.fileExtTypeCode === "video" ? (
              <Link
                href={getViewPath(file)}
                className="flex items-center gap-1.5"
              >
                <Eye className="h-3.5 w-3.5" />
                <span className="max-w-[100px] truncate text-xs">
                  {file.originalFileName}
                </span>
              </Link>
            ) : (
              <a
                href={file.downloadUrl}
                className="flex items-center gap-1.5"
                title={file.originalFileName}
              >
                <Download className="h-3.5 w-3.5" />
                <span className="max-w-[100px] truncate text-xs">
                  {file.originalFileName}
                </span>
                <span className="text-xs text-gray-500">
                  ({getFileSize(file.fileSize)})
                </span>
              </a>
            )}
          </Button>
        ))}
    </div>
  );
};
