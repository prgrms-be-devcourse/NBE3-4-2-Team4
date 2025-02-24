"use client";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Editor as TinyMCEEditor } from "@tinymce/tinymce-react";
import { Input } from "@/components/ui/input";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import React from "react";

const answerWriteFormSchema = z.object({
  content: z
    .string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
  attachment_0: z.array(z.instanceof(File)).optional(),
});

type AnswerWriteFormInputs = z.infer<typeof answerWriteFormSchema>;

interface EnhancedFile extends File {
  uploadedUrl?: string;
  blobId?: string;
}

export default function ClientPage({ params }: { params: { id: string } }) {
  const [uploadedImages, setUploadedImages] = React.useState<EnhancedFile[]>(
    []
  );

  const { id } = params;
  const router = useRouter();
  const { toast } = useToast();
  const form = useForm<AnswerWriteFormInputs>({
    resolver: zodResolver(answerWriteFormSchema),
    defaultValues: {
      content: "",
    },
  });

  const MyEditor = React.useMemo(() => {
    return (
      <TinyMCEEditor
        apiKey={process.env.NEXT_PUBLIC_TINYMCE_API_KEY}
        initialValue={form.getValues("content")}
        onEditorChange={(content, editor) => {
          form.setValue("content", content);

          // 현재 에디터 이미지들 중 임시 이미지만 추출(이게 없으면 에디터에 올렸다가 지운 이미지까지 업로드 됨)
          const currentImages = editor.dom
            .select("img")
            .map((img) => img.src)
            .filter((src) => src.startsWith("blob:"));

          // 업로드할 이미지들 중 현재 에디터에 있는 이미지들만 필터링
          setUploadedImages((prev) => {
            const updated = prev.filter((file) => {
              return currentImages.includes(file.uploadedUrl || "");
            });

            return updated;
          });
        }}
        init={{
          language: "ko_KR",
          height: 500,
          menubar: false,
          plugins: [
            "advlist",
            "autolink",
            "codesample",
            "emoticons",
            "lists",
            "link",
            "image",
            "charmap",
            "preview",
            "anchor",
            "searchreplace",
            "wordcount",
            "media",
            "table",
          ],
          toolbar:
            "undo redo | blocks | " +
            "bold italic underline strikethrough subscript superscript | " +
            "forecolor backcolor | " +
            "alignleft aligncenter alignright | " +
            "bullist numlist outdent indent | " +
            "codesample emoticons | link image media | table",
          file_picker_types: "file media",
          link_picker_callback: false,
          link_quicklink: true,
          media_alt_source: false,
          media_poster: false,
          images_upload_handler: async function (blobInfo, progress) {
            try {
              const currentBlob = blobInfo.blob();
              const blobId = blobInfo.id();

              // File 객체 생성 시 직접 Blob을 사용하고 lastModified 추가
              const imageFile = new File([currentBlob], blobInfo.filename(), {
                type: currentBlob.type,
                lastModified: new Date().getTime(),
              }) as EnhancedFile;

              // 이미지 처리를 Promise로 래핑
              const objectUrl = await new Promise<string>((resolve) => {
                const reader = new FileReader();
                reader.onloadend = () => {
                  const url = URL.createObjectURL(imageFile);
                  imageFile.uploadedUrl = url;
                  imageFile.blobId = blobId;

                  setUploadedImages((prev) => {
                    // blobId를 이용해 중복 확인
                    const isDuplicate = prev.some(
                      (file) => file.blobId === blobId
                    );
                    if (isDuplicate) return prev;

                    return [...prev, imageFile];
                  });

                  resolve(url);
                };
                reader.readAsDataURL(currentBlob);
              });

              return objectUrl;
            } catch (error) {
              console.error("Image upload failed:", error);
              throw error;
            }
          },
        }}
      />
    );
  }, []);

  const uploadFiles = async (
    files: File[],
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
      "/api/answers/{parentId}/genFiles/{typeCode}",
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
  };

  const onSubmit = async (data: AnswerWriteFormInputs) => {
    try {
      const response = await client.POST(
        "/api/questions/{questionId}/answers",
        {
          body: { content: data.content },
          params: { path: { questionId: Number(id) } },
        }
      );

      if (response.error) {
        toast({
          title: "Error",
          description: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      const answerId = response.data.data.id;

      // 에디터 이미지와 첨부파일 각각 업로드
      if (uploadedImages && uploadedImages.length > 0) {
        await uploadFiles(uploadedImages, answerId, "body");
      }

      if (data.attachment_0 && data.attachment_0.length > 0) {
        await uploadFiles(data.attachment_0, answerId, "attachment");
      }

      toast({ title: response.data.msg });

      router.replace(`/question/${id}`);
    } catch (error) {
      console.error("Submit error:", error);

      toast({
        title: "Error",
        description: "답변 등록 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          답변 등록
        </h2>
      </div>

      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4 w-full"
        >
          <FormField
            control={form.control}
            name="content"
            render={({ field }) => (
              <FormItem>
                <FormControl>{MyEditor}</FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="attachment_0"
            render={({ field: { onChange, ...field } }) => (
              <FormItem className="my-4">
                <FormLabel>
                  첨부파일 추가 (드래그 앤 드롭 가능, 최대 5개)
                </FormLabel>
                <FormControl>
                  <Input
                    type="file"
                    multiple
                    accept={getUplodableInputAccept()}
                    onChange={(e) => {
                      const files = Array.from(e.target.files || []);
                      onChange(files);
                    }}
                    {...field}
                    value={undefined}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="mt-6 flex justify-start gap-2">
            <Button type="submit" disabled={form.formState.isSubmitting}>
              {form.formState.isSubmitting ? "저장 중..." : "답변 등록"}
            </Button>
            <Button
              variant="outline"
              type="button"
              onClick={() => router.back()}
            >
              취소
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
}
