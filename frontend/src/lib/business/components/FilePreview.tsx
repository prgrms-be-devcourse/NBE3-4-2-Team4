import Image from "next/image";
import imageLoader from "@/utils/imageLoader";
import { components } from "@/lib/backend/apiV1/schema";

type FilePreviewProps = {
  genFile: components["schemas"]["GenFileDto"];
  className?: string;
};

export function FilePreview({ genFile, className }: FilePreviewProps) {
  return (
    <div className={`flex justify-center ${className}`}>
      {genFile.fileExtTypeCode == "img" && (
        <Image
          loader={imageLoader}
          src={genFile.publicUrl}
          alt={genFile.originalFileName}
          width={100}
          height={100}
          className="max-w-[80dvh] max-h-[70dvh] w-full"
        />
      )}
      {genFile.fileExtTypeCode == "audio" && (
        <audio
          src={genFile.publicUrl}
          controls
          className="max-w-[80dvh] max-h-[70dvh] w-full"
        />
      )}
      {genFile.fileExtTypeCode == "video" && (
        <video
          src={genFile.publicUrl}
          controls
          className="max-w-[80dvh] max-h-[70dvh] w-full"
        />
      )}
    </div>
  );
}
