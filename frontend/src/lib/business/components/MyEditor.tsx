import { Editor as TinyMCEEditor } from "@tinymce/tinymce-react";
import React from "react";

interface EnhancedFile extends File {
  uploadedUrl?: string;
  blobId?: string;
}

interface MyEditorProps {
  form: any;
  name?: string;
  uploadedImages: EnhancedFile[];
  onUploadedImagesChange: (images: EnhancedFile[]) => void;
}

export default function MyEditor({
  form,
  name = "content",
  uploadedImages,
  onUploadedImagesChange,
}: MyEditorProps) {
  const [internalImages, setInternalImages] = React.useState<EnhancedFile[]>(
    []
  );

  // 내부 상태가 변경될 때마다 부모에게 알림
  React.useEffect(() => {
    onUploadedImagesChange(internalImages);
  }, [internalImages, onUploadedImagesChange]);

  return React.useMemo(() => {
    return (
      <TinyMCEEditor
        apiKey={process.env.NEXT_PUBLIC_TINYMCE_API_KEY}
        initialValue={form.getValues(name)}
        onEditorChange={(content, editor) => {
          form.setValue(name, content);

          // 현재 에디터 이미지들 중 임시 이미지만 추출(이게 없으면 에디터에 올렸다가 지운 이미지까지 업로드 됨)
          const currentImages = editor.dom
            .select("img")
            .map((img) => img.src)
            .filter((src) => src.startsWith("blob:"));

          // 업로드할 이미지들 중 현재 에디터에 있는 이미지들만 필터링
          setInternalImages((prev) => {
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

                  setInternalImages((prev) => {
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
}

// 파일을 DataURL로 읽는 헬퍼 함수
const readFileAsDataURL = (blob: Blob): Promise<string> => {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onloadend = () => resolve(reader.result as string);
    reader.readAsDataURL(blob);
  });
};
