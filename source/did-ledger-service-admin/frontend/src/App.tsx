import { CssBaseline, GlobalStyles } from '@mui/material';
import type { Navigation } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { useIdleLogout } from './hooks/useIdleLogout';
import { ExtendedSession, SessionContext } from './context/SessionContext';
import { PasswordPolicyProvider } from './context/PasswordPolicyContext';
import customTheme from './theme';
import { tokenStore } from './utils/authTokens';
import { restoreSessionFromToken } from './utils/authSync';
import FullscreenLoader from './components/loading/FullscreenLoader';
import { ADMIN_LOGOUT_EVENT, logoutAdmin } from './utils/logout';

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
      {
        segment: 'zkp-credential-schema-list',
        title: 'ZKP Credential Schema List',
      },
      {
        segment: 'zkp-credential-definition-list',
        title: 'ZKP Credential Definition List',
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
    const storedSession = sessionStorage.getItem('session') ?? localStorage.getItem('session');
    return storedSession && tokenStore.getRefresh() ? JSON.parse(storedSession) : null;
  });
  const [isRestoring, setIsRestoring] = useState(() => !session && !!tokenStore.getRefresh());
  
  const setSession = useCallback((newSession: ExtendedSession | null) => {
    setSessionState(newSession);
    if (newSession) {
      sessionStorage.setItem('session', JSON.stringify(newSession));
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      sessionStorage.removeItem('session');
      localStorage.removeItem('session'); 
    }
  }, []);

  useEffect(() => {
    if (session || !tokenStore.getRefresh()) return;
    void restoreSessionFromToken('/lss/admin/v1')
      .then((restored) => { if (restored) setSessionState(restored); })
      .finally(() => setIsRestoring(false));
  }, []);

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(async () => {
    if (tokenStore.isPendingLogout()) {
      sessionStorage.removeItem('session');
      sessionStorage.removeItem('activeSession');
      window.location.replace('/sign-in');
      return;
    }
    await logoutAdmin('/lss/admin/v1');
    setSession(null);
    navigate('/sign-in');
  }, [navigate, setSession]);

  useEffect(() => {
    const onStorage = (event: StorageEvent) => {
      if (event.key === ADMIN_LOGOUT_EVENT && event.newValue) {
        tokenStore.markRemoteLogout(event.newValue);
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, [navigate, setSession]);

  useIdleLogout(session !== null, signOut);

  const displaySession = useMemo(() => session ? {
    ...session,
    user: {
      ...session.user,
      name: session.user?.name || session.user?.id || 'Admin',
      email: session.user?.email || session.user?.id || undefined,
    },
  } : null, [session]);

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  if (isRestoring) return <FullscreenLoader open />;

  return (
    <SessionContext.Provider value={sessionContextValue}>
      <DialogsProvider>
        <PasswordPolicyProvider>
          <ReactRouterAppProvider
            navigation={NAVIGATION}
            session={displaySession}
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
