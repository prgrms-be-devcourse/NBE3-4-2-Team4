import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

interface RejectDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  message: string;
}

const RejectDialog = ({ open, onOpenChange, message }: RejectDialogProps) => {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>채팅 거절됨</DialogTitle>
        </DialogHeader>
        <DialogDescription>{message}</DialogDescription>
        <DialogFooter className="flex justify-end mt-4">
          <Button onClick={() => onOpenChange(false)}>닫기</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default RejectDialog;
