import { createContext, useContext, useState, ReactNode } from "react";

export const IdContext = createContext<{
    id: number | null;
    setId: (value: number | null) => void;
}>({
    id: null,
    setId: () => {},
});

export const useId = () => useContext(IdContext);

export const IdProvider = ({ children }: { children: ReactNode }) => {
    const [id, setId] = useState<number | null>(null);

    return <IdContext.Provider value={{ id, setId }}>{children}</IdContext.Provider>;
};