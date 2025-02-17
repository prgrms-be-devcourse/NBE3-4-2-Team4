"use client";

import { Button } from "@/components/ui/button";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { Download } from "lucide-react";
import Image from "next/image";
import { components } from "@/lib/backend/apiV1/schema";
import { useEffect, useState } from "react";
import imageLoader from "@/utils/imageLoader";

type GenFile = components["schemas"]["AnswerGenFileDto"];

export const AttachmentFiles = ({ answerId }: { answerId: number }) => {
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
    <div className="-mx-4 flex justify-end">
      {files
        .filter((file) => file.type_code === "attachment")
        .map((file) => (
          <Button key={file.id} variant="link" asChild>
            <a
              href={`http://localhost:8080/answer/genFile/download/${answerId}/${file.file_name}`}
              className="flex items-center gap-2"
            >
              {/* <Image
                src={`http://localhost:8080/gen/answerGenFile/${file.type_code}/${file.file_date_dir}/${file.file_name}`}
                alt={file.original_file_name}
                loader={imageLoader}
                width={16}
                height={16}
                className="align-self h-[16px] w-[16px]"
              /> */}
              <Download />
              <span>{file.original_file_name}</span>
            </a>
          </Button>
        ))}
    </div>
  );
};
