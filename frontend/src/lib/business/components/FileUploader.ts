import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";

type EntityType = "answers" | "questions" | "products";

interface FileUploaderOptions {
  entityType: EntityType;
}

interface EnhancedFile extends File {
  uploadedUrl?: string;
  blobId?: string;
}

export const useFileUploader = ({ entityType }: FileUploaderOptions) => {
  const { toast } = useToast();

  const uploadFiles = async (
    files: EnhancedFile[] | File[],
    parentId: number,
    typeCode: "body" | "attachment"
  ) => {
    const formData = new FormData();
    const filesToUpload =
      typeCode === "attachment" ? [...files].reverse() : files;

    for (const file of filesToUpload) {
      formData.append("files", file);
    }
    

    const uploadResponse = await client.POST(
      `/api/${entityType}/{parentId}/genFiles/{typeCode}`,
      {
        params: {
          path: {
            parentId,
            typeCode,
          },
        },
        body: formData as any,
      }
    );

    if (uploadResponse.error) {
      toast({
        title: uploadResponse.error.msg,
        variant: "destructive",
      });
      throw uploadResponse.error;
    }

    return uploadResponse;
  };

  return { uploadFiles };
};