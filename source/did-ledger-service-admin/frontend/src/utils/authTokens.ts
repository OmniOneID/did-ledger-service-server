const ACCESS_KEY = 'accessToken';
const REFRESH_KEY = 'refreshToken';

export type TokenStorage = Storage;

export const tokenStore = {
  save(access: string, refresh: string, remember: boolean) {
    const store: TokenStorage = remember ? localStorage : sessionStorage;
    store.setItem(ACCESS_KEY, access);
    store.setItem(REFRESH_KEY, refresh);

    // 반대편 스토리지에서 토큰 제거
    (remember ? sessionStorage : localStorage).removeItem(ACCESS_KEY);
    (remember ? sessionStorage : localStorage).removeItem(REFRESH_KEY);
  },
  
  getAccess(): string | null {
    return sessionStorage.getItem(ACCESS_KEY) ?? localStorage.getItem(ACCESS_KEY);
  },
  
  getRefresh(): string | null {
    return sessionStorage.getItem(REFRESH_KEY) ?? localStorage.getItem(REFRESH_KEY);
  },
  
  clear() {
    [localStorage, sessionStorage].forEach((storage) => {
      storage.removeItem(ACCESS_KEY);
      storage.removeItem(REFRESH_KEY);
    });
  },
  
  writeAccess(newAccess: string) {
    // 리프레시 토큰이 있는 스토리지에 액세스 토큰도 저장
    if (sessionStorage.getItem(REFRESH_KEY)) {
      sessionStorage.setItem(ACCESS_KEY, newAccess);
    } else {
      localStorage.setItem(ACCESS_KEY, newAccess);
    }
  },
};
