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
  const [username, setUsername] = useState<string | null>(
    () => localStorage.getItem("username") // 초기값으로 localStorage에서 가져옴
  );

  const handleSetUsername = (value: string | null) => {
    setUsername(value);
    if (value) {
      localStorage.setItem("username", value);
    } else {
      localStorage.removeItem("username");
    }
  };

  return (
    <UsernameContext.Provider
      value={{ username, setUsername: handleSetUsername }}
    >
      {children}
    </UsernameContext.Provider>
  );
};
