import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import ChatWindow from "./ChatWindow";

const AlertDialog = ({
  message,
  senderName,
  senderUsername,
  open,
  onClose,
}: {
  message: string;
  senderName: string;
  senderUsername: string;
  open: boolean;
  onClose: () => void;
}) => {
  const [showChat, setShowChat] = useState(false);

  const onClick = () => {
    setShowChat(true);
    onClose();
  };

  return (
    <>
      <Dialog open={open} onOpenChange={onClose}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>알림</DialogTitle>
          </DialogHeader>
          <DialogDescription>{message}</DialogDescription>
          <DialogFooter>
            <Button onClick={onClick}>채팅하기</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      {showChat && (
        <ChatWindow
          onClose={() => setShowChat(false)}
          senderName={senderName}
          senderUsername={senderUsername}
        />
      )}
    </>
  );
};

export default AlertDialog;
