import React, { createContext, useContext, useState, ReactNode, useCallback } from 'react';

// 단순한 로딩 상태 관리만 유지
interface LoadingContextType {
  isLoading: boolean;
  setIsLoading: (loading: boolean, message?: string) => void;
  loadingMessage: string;
}

export const LoadingContext = createContext<LoadingContextType>({
  isLoading: false,
  setIsLoading: () => {},
  loadingMessage: '',
});

export const LoadingProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isLoading, setIsLoadingState] = useState<boolean>(false);
  const [loadingMessage, setLoadingMessage] = useState<string>('');

  const setIsLoading = useCallback((loading: boolean, message?: string) => {
    setIsLoadingState(loading);
    setLoadingMessage(message ?? '처리 중입니다...');
  }, []);

  return (
    <LoadingContext.Provider 
      value={{ 
        isLoading, 
        setIsLoading, 
        loadingMessage,
      }}
    >
      {children}
    </LoadingContext.Provider>
  );
};

export const useLoading = () => useContext(LoadingContext);
