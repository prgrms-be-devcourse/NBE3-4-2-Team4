import { createContext, useContext, useState, ReactNode } from "react";

export enum Role {
    ADMIN = "ADMIN",
    USER = "USER"
}

export const RoleContext = createContext<{
    role: Role | null;
    setRole: (value: Role | null) => void;
}>({
    role: null,
    setRole: () => {},
});

export const useRole = () => useContext(RoleContext);

export const RoleProvider = ({ children }: { children: ReactNode }) => {
    const [role, setRole] = useState<Role | null>(null);

    return <RoleContext.Provider value={{ role, setRole }}>{children}</RoleContext.Provider>;
};