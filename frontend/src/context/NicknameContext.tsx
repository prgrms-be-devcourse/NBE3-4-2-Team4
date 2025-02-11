import { createContext, useContext, useState, ReactNode } from "react";

export const NicknameContext = createContext<{
    nickname: string | null;
    setNickname: (value: string | null) => void;
}>({
    nickname: null,
    setNickname: () => {},
});

export const useNickname = () => useContext(NicknameContext);

export const NicknameProvider = ({ children }: { children: ReactNode }) => {
    const [nickname, setNickname] = useState<string | null>(null);

    return <NicknameContext.Provider value={{ nickname, setNickname }}>{children}</NicknameContext.Provider>;
};