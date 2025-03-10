import { createContext, useContext, useState, ReactNode } from "react";

export const UsernameContext = createContext<{
  username: string | null;
  setUsername: (value: string | null) => void;
}>({
  username: null,
  setUsername: () => {},
});

export const useUsername = () => useContext(UsernameContext);

export const UsernameProvider = ({ children }: { children: ReactNode }) => {
  const [username, setUsername] = useState<string | null>(null);

  return (
    <UsernameContext.Provider value={{ username, setUsername }}>
      {children}
    </UsernameContext.Provider>
  );
};
