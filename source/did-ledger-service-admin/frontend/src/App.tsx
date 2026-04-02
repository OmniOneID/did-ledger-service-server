import { CssBaseline, GlobalStyles } from '@mui/material';
import type { Navigation } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import { useCallback, useMemo, useState } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { ExtendedSession, SessionContext } from './context/SessionContext';
import { PasswordPolicyProvider } from './context/PasswordPolicyContext';
import customTheme from './theme';

const NAVIGATION: Navigation = [
  {
    kind: 'divider',
  },
  {
    segment: 'dids',
    title: 'DID List',
    children: [
      {
        segment: 'did-list',
        title: 'DID List',
      },
      {
        segment: 'did-change-history',
        title: 'DID Change History',
      },
    ],
  },
  {
    segment: 'vc-metadatas',
    title: 'VC Metadata List',
    children: [
      {
        segment: 'vc-metadata-list',
        title: 'VC Metadata List',
      },
      {
        segment: 'vc-change-history',
        title: 'VC Change History',
      },
    ],
  },
  {
    segment: 'schemas',
    title: 'Schema Management',
    children: [
      {
        segment: 'vc-schema-list',
        title: 'VC Schema List',
      },
    ],
  },
  {
    segment: 'apikey-management',
    title: 'API Key Management',
  },
  {
    segment: 'admins',
    title: 'Admin Management',
    children: [
      {
        segment: 'admin-management',
        title: 'Admin Management',
      },
      {
        segment: 'password-policy',
        title: 'Password Policy Settings',
      },
    ],
  },
  {
    segment: 'server-configuration',
    title: 'Server Configuration',
  },
  {
  segment: 'logs',
  title: 'Log Management',
  children: [
    {
      segment: 'api',
      title: 'API Log',
    },
    {
      segment: 'audit',
      title: 'Audit API Log',
    },
  ],
},
  {
    kind: 'divider',
  },
];

function AppContent() {
  const navigate = useNavigate();
  
  const [session, setSessionState] = useState<ExtendedSession | null>(() => {
    // 1. 먼저 sessionStorage의 activeSession 마커 확인
    const activeSession = sessionStorage.getItem('activeSession');
    
    if (!activeSession) {
      // sessionStorage에 activeSession 마커가 없음
      // = 브라우저 재시작 또는 서버 재부팅으로 sessionStorage 클리어됨
      console.log('No active session marker found, clearing localStorage session');
      localStorage.removeItem('session'); // localStorage 세션도 클리어
      return null; // 로그인 페이지로 보내기
    }
    
    // 2. activeSession이 있으면 저장된 세션 데이터 확인
    // sessionStorage 먼저 확인 후 localStorage 확인
    const sessionStorageSession = sessionStorage.getItem('session');
    const localStorageSession = localStorage.getItem('session');
    
    const storedSession = sessionStorageSession || localStorageSession;
    
    if (storedSession) {
      console.log('Session restored from storage');
      return JSON.parse(storedSession);
    }
    
    return null;
  });
  
  const setSession = useCallback((newSession: ExtendedSession | null) => {
    setSessionState(newSession);
    if (newSession) {
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      localStorage.removeItem('session'); 
    }
  }, []);

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(() => {
    setSession(null);
    
    // activeSession 마커도 클리어
    sessionStorage.removeItem('activeSession');
    
    navigate('/sign-in');
  }, [navigate, setSession]);

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  return (
    <SessionContext.Provider value={sessionContextValue}>
      <DialogsProvider>
        <PasswordPolicyProvider>
          <ReactRouterAppProvider
            navigation={NAVIGATION}
            session={session}
            authentication={{ signIn, signOut }}
            theme={customTheme}
          >
            <CssBaseline />
            <Outlet />
          </ReactRouterAppProvider>
        </PasswordPolicyProvider>
      </DialogsProvider>
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <>
      <GlobalStyles styles={{ body: { padding: "10px" } }} />
      <AppContent />
    </>
  );
}
