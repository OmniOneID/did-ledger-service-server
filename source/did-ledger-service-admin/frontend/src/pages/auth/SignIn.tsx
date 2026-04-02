import { AuthProvider, AuthResponse, SignInPage } from '@toolpad/core/SignInPage';
import { useState } from 'react';
import { useNavigate } from 'react-router';
import { requestLogin, requestPasswordReset, requestChangeAdminIdAndPassword } from '../../apis/admin-api';
import { useSession } from '../../context/SessionContext';
import { sha256Hash } from '../../utils/sha256-hash';
import { tokenStore } from '../../utils/authTokens';
import PasswordResetDialog from './PasswordResetDialog';
import ChangeIdAndPasswordDialog from './ChangeIdAndPasswordDialog';
import ForgotPasswordDialog from './ForgotPasswordDialog';

// Types for password reset reasons
type PasswordResetReason = 'FIRST_LOGIN' | 'EXPIRED' | 'ADMIN_FORCED';

interface LoginResponse {
  admin: {
    id: number;
    loginId: string;
    name: string;
    email: string;
    emailVerified: boolean;
    requirePasswordReset: boolean;
    role: string;
    createdBy: string;
    createdAt: string;
    updatedAt: string;
    lastPasswordChangedAt: string;
    passwordResetReason: PasswordResetReason | null;
    isPasswordExpired: boolean;
  };
  accessToken: string;
  refreshToken: string;
}

interface PasswordResetData {
  email: string;
  hashedPassword: string;
  reason: PasswordResetReason | null;
  isExpired: boolean;
}

export default function SignIn() {
  const { setSession } = useSession();
  const navigate = useNavigate();
  const [requirePasswordReset, setRequirePasswordReset] = useState(false);
  const [requireChangeIdAndPassword, setRequireChangeIdAndPassword] = useState(false);
  const [passwordResetData, setPasswordResetData] = useState<PasswordResetData | null>(null);
  const [changeIdAndPasswordData, setChangeIdAndPasswordData] = useState<{
    oldLoginId: string;
    hashedPassword: string;
  } | null>(null);
  const [forgotPasswordOpen, setForgotPasswordOpen] = useState(false);
  const [rememberMe, setRememberMe] = useState<boolean>(() => {
    return localStorage.getItem('rememberMe') === 'true';
  });

  const handleSignIn = async (
      provider: AuthProvider,
      formData?: FormData,
      callbackUrl?: string
  ): Promise<AuthResponse> => {
    try {
      const email = formData?.get('email') as string;
      const password = formData?.get('password') as string;
      const hashedPassword = await sha256Hash(password);

      const { data }: { data: LoginResponse } = await requestLogin({
        loginId: email,
        loginPassword: hashedPassword,
      });

      // 토큰 저장
      tokenStore.save(data.accessToken, data.refreshToken, rememberMe);

      // Check if root account on first login - show ID and password change dialog
      if (data.admin.requirePasswordReset && 
          data.admin.role === 'ROOT' && 
          data.admin.passwordResetReason === 'FIRST_LOGIN') {
        setRequireChangeIdAndPassword(true);
        setChangeIdAndPasswordData({
          oldLoginId: email,
          hashedPassword,
        });
        return {};
      }

      // Otherwise, check if password reset is needed
      if (data.admin.requirePasswordReset) {
        setRequirePasswordReset(true);
        setPasswordResetData({
          email,
          hashedPassword,
          reason: data.admin.passwordResetReason,
          isExpired: data.admin.isPasswordExpired
        });
        return {};
      }

      const session = {
        user: {
          id: data.admin.loginId,
          role: data.admin.role,
        },
      };
      setSession(session);

      const storage = rememberMe ? localStorage : sessionStorage;
      storage.setItem('session', JSON.stringify(session));
      localStorage.setItem('rememberMe', rememberMe.toString());
      
      // rememberMe와 관계없이 ALWAYS sessionStorage에 활성 세션 마커 저장
      sessionStorage.setItem('activeSession', 'true');
      
      if (rememberMe) {
        localStorage.setItem('email', email);
      } else {
        localStorage.removeItem('email');
      }

      navigate(callbackUrl ?? '/dids', { replace: true });
      return {};
    } catch (error) {
      return { error: 'Invalid username or password.' };
    }
  };

  const handlePasswordReset = async (newPassword: string) => {
    if (!passwordResetData) return;

    try {
      const newHashedPassword = await sha256Hash(newPassword);
      const response = await requestPasswordReset({
        loginId: passwordResetData.email,
        oldPassword: passwordResetData.hashedPassword,
        newPassword: newHashedPassword,
      });

      // Get user role from the response or make another API call to get user info
      const { data } = await requestLogin({
        loginId: passwordResetData.email,
        loginPassword: newHashedPassword,
      });

      // 토큰 저장
      tokenStore.save(data.accessToken, data.refreshToken, rememberMe);

      const session = {
        user: {
          id: data.admin.loginId,
          role: data.admin.role
        }
      };
      setSession(session);

      // Store session with role information
      const storage = rememberMe ? localStorage : sessionStorage;
      storage.setItem('session', JSON.stringify(session));
      
      // activeSession 마커도 설정
      sessionStorage.setItem('activeSession', 'true');

      navigate('/dids', { replace: true });
    } catch (error) {
      console.error('Failed to reset password:', error);
    } finally {
      setRequirePasswordReset(false);
      setPasswordResetData(null);
    }
  };

  const handlePasswordResetDialogClose = () => {
    setRequirePasswordReset(false);
    setPasswordResetData(null);
  };

  const handleChangeIdAndPassword = async (
    oldLoginId: string,
    newLoginId: string,
    oldPassword: string,
    newPassword: string
  ) => {
    if (!changeIdAndPasswordData) return;

    try {
      const newHashedPassword = await sha256Hash(newPassword);
      const oldHashedPassword = await sha256Hash(oldPassword);

      // Call the change ID and password API
      await requestChangeAdminIdAndPassword({
        oldLoginId,
        newLoginId,
        oldPassword: oldHashedPassword,
        newPassword: newHashedPassword,
      });

      // Login with new credentials
      const { data } = await requestLogin({
        loginId: newLoginId,
        loginPassword: newHashedPassword,
      });

      // 토큰 저장
      tokenStore.save(data.accessToken, data.refreshToken, rememberMe);

      const session = {
        user: {
          id: newLoginId,
          role: data.admin.role
        }
      };
      setSession(session);

      // Store session
      const storage = rememberMe ? localStorage : sessionStorage;
      storage.setItem('session', JSON.stringify(session));

      // Set activeSession marker
      sessionStorage.setItem('activeSession', 'true');

      if (rememberMe) localStorage.setItem('email', newLoginId);
      else localStorage.removeItem('email');

      setRequireChangeIdAndPassword(false);
      setChangeIdAndPasswordData(null);
      navigate('/dids', { replace: true });
    } catch (error: any) {
      console.error('Failed to change ID and password:', error);
      throw error;
    }
  };

  const handleChangeIdAndPasswordDialogClose = () => {
    setRequireChangeIdAndPassword(false);
    setChangeIdAndPasswordData(null);
  };

  const Title = () => <p style={{ fontWeight: 700, fontSize: '32px', lineHeight: '150%', margin: 0 }}>LS Admin Login</p>;
  const SubTitle = () => <p style={{ fontSize: '14px', marginBottom: 16, marginTop: 8 }}>Welcome, please sign in to continue</p>;

  return (
      <>
        <SignInPage
            providers={[{ id: 'credentials', name: 'Credentials' }]}
            signIn={handleSignIn}
            slots={{
              title: Title,
              subtitle: SubTitle,
              forgotPasswordLink: () => (
                  <div style={{ textAlign: 'center', marginTop: '16px' }}>
              <span
                  onClick={() => setForgotPasswordOpen(true)}
                  style={{
                    color: '#FF8400',
                    fontSize: '14px',
                    cursor: 'pointer',
                    textDecoration: 'underline'
                  }}
              >
                Forgot your password?
              </span>
                  </div>
              )
            }}
            slotProps={{
              emailField: {
                defaultValue: rememberMe ? localStorage.getItem('email') ?? '' : '',
                sx: { '& .MuiOutlinedInput-root': { height: 48 } },
              },
              passwordField: { sx: { '& .MuiOutlinedInput-root': { height: 48 } } },
              rememberMe: {
                checked: rememberMe,
                onChange: (_e, checked) => setRememberMe(checked),
                sx: { '& .MuiFormControlLabel-label': { color: '#000000' } },
              },
              submitButton: {
                sx: {
                  height: '60px',
                  backgroundColor: '#FF8400',
                  borderRadius: '8px',
                  color: 'white',
                  marginTop: '16px',
                  padding: '10px 32px',
                  '&:hover': { backgroundColor: '#E67300' },
                },
              },
            }}
            sx={{
              '& .MuiContainer-root': { maxWidth: 'none', width: 540, height: 500 },
              '& .MuiContainer-root > .MuiBox-root:first-of-type': {
                height: 410,
                borderRadius: '4px',
                boxShadow: 'none',
                backgroundColor: 'white',
                border: '#FFFFFF',
              },
            }}
        />
        <PasswordResetDialog
            open={requirePasswordReset}
            onClose={handlePasswordResetDialogClose}
            onSubmit={handlePasswordReset}
            passwordResetReason={passwordResetData?.reason}
            isPasswordExpired={passwordResetData?.isExpired}
        />
        <ChangeIdAndPasswordDialog
            open={requireChangeIdAndPassword}
            onClose={handleChangeIdAndPasswordDialogClose}
            onSubmit={handleChangeIdAndPassword}
            oldLoginId={changeIdAndPasswordData?.oldLoginId}
        />
        <ForgotPasswordDialog
            open={forgotPasswordOpen}
            onClose={() => setForgotPasswordOpen(false)}
            onSuccess={() => {
              setForgotPasswordOpen(false);
            }}
        />
      </>
  );
}