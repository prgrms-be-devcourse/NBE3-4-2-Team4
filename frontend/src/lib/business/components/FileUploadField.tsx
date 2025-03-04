import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import { Control } from "react-hook-form";

interface FileUploadFieldProps {
  control: Control<any>;
  name: string;
  label?: string;
  maxFiles?: number;
}

export function FileUploadField({ control, name }: FileUploadFieldProps) {
  return (
    <FormField
      control={control}
      name={name}
      render={({ field: { onChange, ...field } }) => (
        <FormItem>
          <FormLabel>첨부파일 추가 (드래그 앤 드롭 가능, 최대 5개)</FormLabel>
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
  );
}
