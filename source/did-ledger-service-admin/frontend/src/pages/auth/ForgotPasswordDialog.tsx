import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField,
    Box,
    Typography,
    IconButton,
    Collapse,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Alert,
    CircularProgress,
    Link,
    Stepper,
    Step,
    StepLabel
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { usePasswordPolicy } from "../../hooks/usePasswordPolicy";
import { ValidationRuleResult } from "../../constants/password-policy";
import { Visibility, VisibilityOff, Check, Close, ExpandMore, ExpandLess, Info, Email, VpnKey } from "@mui/icons-material";
import { requestPasswordResetOtp, verifyOtpAndResetPassword } from "../../apis/admin-api";
import { sha256Hash } from "../../utils/sha256-hash";
import {useDialogs} from '@toolpad/core/useDialogs';
import CustomDialog from "../../components/dialog/CustomDialog";

interface ForgotPasswordDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

interface ErrorState {
    loginId?: string;
    otp?: string;
    newPassword?: string;
    confirmPassword?: string;
}

type Step = 'request-otp' | 'verify-otp';

const ForgotPasswordDialog: React.FC<ForgotPasswordDialogProps> = ({ 
    open, 
    onClose, 
    onSuccess 
}) => {
    // Step management
    const [currentStep, setCurrentStep] = useState<Step>('request-otp');
    const [activeStepIndex, setActiveStepIndex] = useState(0);

    // Form data
    const [loginId, setLoginId] = useState("");
    const [otp, setOtp] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    
    // UI states
    const [errors, setErrors] = useState<ErrorState>({});
    const [isLoading, setIsLoading] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [showRequirements, setShowRequirements] = useState(false);

    // OTP resend timer
    const [resendTimer, setResendTimer] = useState(0);
    const [canResend, setCanResend] = useState(false);

    // Touch states for better UX
    const [loginIdTouched, setLoginIdTouched] = useState(false);
    const [otpTouched, setOtpTouched] = useState(false);
    const [newPasswordTouched, setNewPasswordTouched] = useState(false);
    const [confirmPasswordTouched, setConfirmPasswordTouched] = useState(false);

    const dialogs = useDialogs();

    // Success/Error messages
    const [alertMessage, setAlertMessage] = useState<{ type: 'success' | 'error' | 'info', message: string } | null>(null);

    // Password policy hook
    const {
        policy,
        isLoading: isPolicyLoading,
        validatePassword,
        getValidationResults,
        getValidationSummary,
        loadPolicy
    } = usePasswordPolicy();

    // Real-time password validation results
    const [validationResults, setValidationResults] = useState<ValidationRuleResult[]>([]);
    const [validationSummary, setValidationSummary] = useState({
        isValid: false,
        passedCount: 0,
        totalCount: 0,
        failedRules: [] as string[]
    });

    const steps = ['Request OTP', 'Verify OTP & Reset Password'];

    // Timer effect for OTP resend
    useEffect(() => {
        if (resendTimer > 0) {
            const timer = setTimeout(() => {
                setResendTimer(resendTimer - 1);
            }, 1000);
            return () => clearTimeout(timer);
        } else if (resendTimer === 0 && currentStep === 'verify-otp') {
            setCanResend(true);
        }
    }, [resendTimer, currentStep]);

    const handleRequestOtp = async () => {
        if (!validateStepOne()) return;

        setIsLoading(true);
        setAlertMessage(null);
        
        try {
            await requestPasswordResetOtp({ loginId });
            setCurrentStep('verify-otp');
            setActiveStepIndex(1);
            setResendTimer(60); // 1 minutes
            setCanResend(false);
            setAlertMessage({
                type: 'success',
                message: 'OTP has been sent. Please check your email.'
            });
        } catch (error: any) {
            setAlertMessage({
                type: 'error',
                message: error.message || 'Failed to send OTP. Please check your login ID.'
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleResendOtp = async () => {
        if (!canResend || isLoading) return;

        setIsLoading(true);
        setAlertMessage(null);
        
        try {
            await requestPasswordResetOtp({ loginId });
            setResendTimer(60); // 1 minutes
            setCanResend(false);
            setAlertMessage({
                type: 'success',
                message: 'OTP has been resent.'
            });
        } catch (error: any) {
            setAlertMessage({
                type: 'error',
                message: error.message || 'Failed to resend OTP.'
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleVerifyOtpAndResetPassword = async () => {
        if (!validateStepTwo()) return;

        setIsLoading(true);
        setAlertMessage(null);
        
        try {
            const hashedPassword = await sha256Hash(newPassword);
            await verifyOtpAndResetPassword({
                loginId,
                otp,
                hashedPassword
            });
            
            
            await dialogs.open(CustomDialog, {
                title: 'Success',
                message: 'Password has been reset successfully.',
                isModal: true,
            }, {
                onClose: async (result) => onSuccess()
            });

        } catch (error: any) {
            setAlertMessage({
                type: 'error',
                message: error.message || 'Failed to verify OTP or reset password.'
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleNewPasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const password = event.target.value;
        setNewPassword(password);

        // Update real-time validation when policy is available
        if (policy && password) {
            const results = getValidationResults(password);
            const summary = getValidationSummary(password);
            setValidationResults(results);
            setValidationSummary(summary);

            // Auto-show requirements when user starts typing
            if (!showRequirements) {
                setShowRequirements(true);
            }

            // Auto-hide requirements when password becomes valid and is not focused
            if (summary.isValid && document.activeElement?.id !== 'new-password-forgot') {
                setTimeout(() => {
                    setShowRequirements(false);
                }, 1500);
            }
        } else if (!policy && password) {
            // Show basic requirements when policy is not available
            const basicValidation = [
                { 
                    rule: 'minLength',
                    isValid: password.length >= 8, 
                    message: 'At least 8 characters' 
                },
                { 
                    rule: 'maxLength',
                    isValid: password.length <= 64, 
                    message: 'Maximum 64 characters' 
                },
                { 
                    rule: 'uppercase',
                    isValid: /[A-Z]/.test(password), 
                    message: 'At least one uppercase letter' 
                },
                { 
                    rule: 'lowercase',
                    isValid: /[a-z]/.test(password), 
                    message: 'At least one lowercase letter' 
                },
                { 
                    rule: 'number',
                    isValid: /\d/.test(password), 
                    message: 'At least one number' 
                },
                { 
                    rule: 'special',
                    isValid: /[!@#$%^&*(),.?":{}|<>]/.test(password), 
                    message: 'At least one special character' 
                }
            ];
            
            const passedCount = basicValidation.filter(rule => rule.isValid).length;
            const isValid = passedCount === basicValidation.length;
            
            setValidationResults(basicValidation);
            setValidationSummary({
                isValid,
                passedCount,
                totalCount: basicValidation.length,
                failedRules: basicValidation.filter(rule => !rule.isValid).map(rule => rule.message)
            });

            // Auto-show requirements when user starts typing
            if (!showRequirements) {
                setShowRequirements(true);
            }

            // Auto-hide requirements when password becomes valid
            if (isValid && document.activeElement?.id !== 'new-password-forgot') {
                setTimeout(() => {
                    setShowRequirements(false);
                }, 1500);
            }
        } else {
            setValidationResults([]);
            setValidationSummary({
                isValid: false,
                passedCount: 0,
                totalCount: 0,
                failedRules: []
            });
        }
    };

    const validateStepOne = () => {
        let tempErrors: ErrorState = {};

        if (!loginId.trim() && loginIdTouched) {
            tempErrors.loginId = "Please enter your login ID.";
        }

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const validateStepTwo = () => {
        let tempErrors: ErrorState = {};

        // Validate OTP
        if (!otp.trim() && otpTouched) {
            tempErrors.otp = "Please enter the OTP.";
        } else if (otpTouched && otp.trim() && !/^\d{6}$/.test(otp.trim())) {
            tempErrors.otp = "OTP must be 6 digits.";
        }

        // Validate new password using policy - similar to PasswordResetDialog
        if (!newPassword.trim() && newPasswordTouched) {
            tempErrors.newPassword = "Please enter a new password.";
        } else if (policy && newPasswordTouched && newPassword.trim() && !validatePassword(newPassword)) {
            tempErrors.newPassword = "Password does not meet policy requirements.";
        } else if (!policy && newPasswordTouched && newPassword.trim() && (newPassword.length < 8 || newPassword.length > 64)) {
            // Fallback validation if policy is not available
            tempErrors.newPassword = "Password must be between 8 and 64 characters.";
        }

        // Validate password confirmation
        if (!confirmPassword.trim() && confirmPasswordTouched) {
            tempErrors.confirmPassword = "Please confirm your new password.";
        } else if (confirmPasswordTouched && confirmPassword.trim() && confirmPassword !== newPassword) {
            tempErrors.confirmPassword = "Passwords do not match.";
        }

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const handleSuccessConfirm = () => {
        onSuccess();
    };

    const handleClose = (event?: {}, reason?: 'backdropClick' | 'escapeKeyDown') => {
        // 백드롭 클릭으로는 닫히지 않도록 방지
        if (reason === 'backdropClick') {
            return;
        }
        onClose();
    };

    const handleBackToStepOne = () => {
        setCurrentStep('request-otp');
        setActiveStepIndex(0);
        setOtp("");
        setNewPassword("");
        setConfirmPassword("");
        setErrors({});
        setAlertMessage(null);
        setResendTimer(0);
        setCanResend(false);
        // Reset touched states for step two
        setOtpTouched(false);
        setNewPasswordTouched(false);
        setConfirmPasswordTouched(false);
        setShowNewPassword(false);
        setShowConfirmPassword(false);
        setShowRequirements(false);
        setValidationResults([]);
        setValidationSummary({
            isValid: false,
            passedCount: 0,
            totalCount: 0,
            failedRules: []
        });
    };

    const formatTime = (seconds: number) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    // Reset form when dialog opens/closes
    useEffect(() => {
        if (open) {
            // Load password policy when dialog opens
            if (!policy && !isPolicyLoading) {
                loadPolicy();
            }
            
            // Reset all states
            setCurrentStep('request-otp');
            setActiveStepIndex(0);
            setLoginId("");
            setOtp("");
            setNewPassword("");
            setConfirmPassword("");
            setErrors({});
            setIsLoading(false);
            setShowNewPassword(false);
            setShowConfirmPassword(false);
            setShowRequirements(false);
            setLoginIdTouched(false);
            setOtpTouched(false);
            setNewPasswordTouched(false);
            setConfirmPasswordTouched(false);
            setAlertMessage(null);
            setResendTimer(0);
            setCanResend(false);
            setValidationResults([]);
            setValidationSummary({
                isValid: false,
                passedCount: 0,
                totalCount: 0,
                failedRules: []
            });
        }
    }, [open, policy, isPolicyLoading, loadPolicy]);

    // Update button state based on validation
    useEffect(() => {
        const hasBasicInput = newPassword.trim() && confirmPassword.trim();
        const isPasswordValid = policy ? validatePassword(newPassword) : newPassword.length >= 8;
        const passwordsMatch = newPassword === confirmPassword;

        // This effect tracks button state for step 2
    }, [newPassword, confirmPassword, policy, validatePassword]);

    // Validate form on changes - only when fields are touched
    useEffect(() => {
        if (currentStep === 'request-otp' && loginIdTouched) {
            validateStepOne();
        } else if (currentStep === 'verify-otp' && (otpTouched || newPasswordTouched || confirmPasswordTouched)) {
            validateStepTwo();
        }
    }, [loginId, otp, newPassword, confirmPassword, loginIdTouched, otpTouched, newPasswordTouched, confirmPasswordTouched, currentStep, policy, validatePassword]);

    // Check if current step is valid for button enabling
    const isStepOneValid = loginId.trim() && !isLoading;
    const isStepTwoValid = 
        otp.trim() && 
        /^\d{6}$/.test(otp.trim()) && 
        newPassword.trim() && 
        confirmPassword.trim() && 
        newPassword === confirmPassword &&
        (policy ? validatePassword(newPassword) : newPassword.length >= 8) &&
        !isLoading;

    if (isPolicyLoading) {
        return (
            <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm" sx={{ maxWidth: 500, margin: "0 auto" }}>
                <DialogContent sx={{ textAlign: 'center', py: 4 }}>
                    <CircularProgress />
                    <Typography sx={{ mt: 2 }}>Loading password policy...</Typography>
                </DialogContent>
            </Dialog>
        );
    }

    return (
        <Dialog 
            open={open} 
            onClose={handleClose} 
            fullWidth 
            maxWidth="sm" 
            sx={{ maxWidth: 500, margin: "0 auto" }}
            disableEscapeKeyDown={false}
            aria-labelledby="forgot-password-dialog-title"
            aria-describedby="forgot-password-dialog-description"
        >
            <Box sx={{ px: 2 }}>
                <DialogTitle 
                    id="forgot-password-dialog-title"
                    sx={{ p: 0, pt: 2, fontWeight: 700 }}
                >
                    Forgot Password
                </DialogTitle>
                <Box sx={{ height: "1px", backgroundColor: "var(--G40, #BFBFBF)", width: "100%", mt: 1 }} />
            </Box>

            <DialogContent sx={{ px: 2, pt: 3 }}>
                {/* Progress Stepper */}
                <Stepper activeStep={activeStepIndex} sx={{ mb: 3 }}>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>

                {/* Alert Message */}
                {alertMessage && (
                    <Alert 
                        severity={alertMessage.type} 
                        sx={{ mb: 2 }}
                        variant="outlined"
                        icon={
                            alertMessage.type === 'success' ? <Check /> :
                            alertMessage.type === 'error' ? <Close /> : <Info />
                        }
                    >
                        {alertMessage.message}
                    </Alert>
                )}

                {/* Step 1: Request OTP */}
                {currentStep === 'request-otp' && (
                    <Box>
                        <Alert 
                            severity="info" 
                            sx={{ mb: 2 }}
                            variant="outlined"
                            icon={<Email />}
                        >
                            Enter your login ID to receive an OTP.
                        </Alert>

                        <TextField
                            fullWidth
                            label="Login ID *"
                            variant="outlined"
                            margin="normal"
                            value={loginId}
                            onChange={(e) => setLoginId(e.target.value)}
                            onBlur={() => setLoginIdTouched(true)}
                            error={!!errors.loginId}
                            helperText={errors.loginId}
                            disabled={isLoading}
                        />
                    </Box>
                )}

                {/* Step 2: Verify OTP and Reset Password */}
                {currentStep === 'verify-otp' && (
                    <Box>
                        {/* OTP Input */}
                        <TextField
                            fullWidth
                            label="OTP (6 digits) *"
                            variant="outlined"
                            margin="normal"
                            value={otp}
                            onChange={(e) => {
                                const value = e.target.value.replace(/\D/g, '').slice(0, 6);
                                setOtp(value);
                            }}
                            onBlur={() => setOtpTouched(true)}
                            error={!!errors.otp}
                            helperText={errors.otp}
                            disabled={isLoading}
                            inputProps={{
                                maxLength: 6,
                                style: { textAlign: 'center', fontSize: '1.2rem', letterSpacing: '0.5rem' }
                            }}
                        />

                        {/* OTP Resend */}
                        <Box sx={{ textAlign: 'center', mb: 2 }}>
                            {resendTimer > 0 ? (
                                <Typography variant="body2" color="text.secondary">
                                    Resend available in: {formatTime(resendTimer)}
                                </Typography>
                            ) : (
                                <Link
                                    component="button"
                                    type="button"
                                    onClick={handleResendOtp}
                                    disabled={!canResend || isLoading}
                                    sx={{ fontSize: '0.875rem' }}
                                >
                                    Resend OTP
                                </Link>
                            )}
                        </Box>

                        {/* New Password */}
                        <TextField
                            id="new-password-forgot"
                            fullWidth
                            label="New Password *"
                            type={showNewPassword ? "text" : "password"}
                            variant="outlined"
                            margin="normal"
                            value={newPassword}
                            onChange={handleNewPasswordChange}
                            onBlur={() => setNewPasswordTouched(true)}
                            error={!!errors.newPassword}
                            helperText={errors.newPassword}
                            disabled={isLoading}
                            InputProps={{
                                endAdornment: (
                                    <IconButton
                                        aria-label="toggle password visibility"
                                        onClick={() => setShowNewPassword(!showNewPassword)}
                                        edge="end"
                                        disabled={isLoading}
                                    >
                                        {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                    </IconButton>
                                ),
                            }}
                        />

                        {/* Password Requirements Toggle */}
                        {newPassword && (policy || !isPolicyLoading) && (
                            <Box sx={{ mt: 1 }}>
                                <Button
                                    size="small"
                                    variant="text"
                                    color="inherit"
                                    startIcon={<Info fontSize="small" />}
                                    endIcon={showRequirements ? <ExpandLess /> : <ExpandMore />}
                                    onClick={() => setShowRequirements(!showRequirements)}
                                    sx={{ 
                                        textTransform: 'none', 
                                        fontSize: '0.875rem',
                                        color: 'text.secondary',
                                        p: 0.5,
                                        minWidth: 'auto'
                                    }}
                                    disabled={isLoading}
                                >
                                    Requirements ({validationSummary.passedCount}/{validationSummary.totalCount})
                                </Button>

                                {/* Password Requirements List */}
                                <Collapse in={showRequirements}>
                                    <Box sx={{ 
                                        mt: 1, 
                                        backgroundColor: 'rgba(0,0,0,0.02)', 
                                        borderRadius: 1,
                                        border: '1px solid rgba(0,0,0,0.1)'
                                    }}>
                                        <List dense sx={{ py: 1 }}>
                                            {validationResults.map((result, index) => (
                                                <ListItem key={index} sx={{ py: 0.25, px: 2 }}>
                                                    <ListItemIcon sx={{ minWidth: 32 }}>
                                                        {result.isValid ? (
                                                            <Check 
                                                                fontSize="small" 
                                                                sx={{ color: 'success.main' }}
                                                            />
                                                        ) : (
                                                            <Close 
                                                                fontSize="small" 
                                                                sx={{ color: 'text.disabled' }}
                                                            />
                                                        )}
                                                    </ListItemIcon>
                                                    <ListItemText 
                                                        primary={result.message}
                                                        slotProps={{
                                                            primary: {
                                                                variant: 'body2',
                                                                sx: {
                                                                    color: result.isValid ? 'success.main' : 'text.secondary',
                                                                    fontSize: '0.875rem'
                                                                }
                                                            }
                                                        }}
                                                    />
                                                </ListItem>
                                            ))}
                                        </List>
                                    </Box>
                                </Collapse>
                            </Box>
                        )}

                        {/* Confirm Password */}
                        <TextField
                            fullWidth
                            label="Confirm Password *"
                            type={showConfirmPassword ? "text" : "password"}
                            variant="outlined"
                            margin="normal"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            onBlur={() => setConfirmPasswordTouched(true)}
                            error={!!errors.confirmPassword}
                            helperText={errors.confirmPassword}
                            disabled={isLoading}
                            InputProps={{
                                endAdornment: (
                                    <IconButton
                                        aria-label="toggle password visibility"
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                        edge="end"
                                        disabled={isLoading}
                                    >
                                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                                    </IconButton>
                                ),
                            }}
                        />
                    </Box>
                )}
            </DialogContent>

            <DialogActions sx={{ px: 2, pt: 0, display: "flex", gap: 2, mt: 0 }}>
                {currentStep === 'request-otp' ? (
                    <>
                        <Button 
                            variant="outlined" 
                            onClick={handleClose} 
                            color="primary" 
                            sx={{ flexGrow: 1, height: "48px" }}
                            disabled={isLoading}
                        >
                            Cancel
                        </Button>
                        <Button 
                            variant="contained" 
                            onClick={handleRequestOtp} 
                            color="primary" 
                            disabled={!isStepOneValid}
                            sx={{ flexGrow: 1, height: "48px" }}
                        >
                            {isLoading ? <CircularProgress size={24} /> : 'Send OTP'}
                        </Button>
                    </>
                ) : (
                    <>
                        <Button 
                            variant="outlined" 
                            onClick={handleBackToStepOne} 
                            color="primary" 
                            sx={{ flexGrow: 0.5, height: "48px" }}
                            disabled={isLoading}
                        >
                            Back
                        </Button>
                        <Button 
                            variant="contained" 
                            onClick={handleVerifyOtpAndResetPassword} 
                            color="primary" 
                            disabled={!isStepTwoValid}
                            sx={{ flexGrow: 1, height: "48px" }}
                        >
                            {isLoading ? <CircularProgress size={24} /> : 'Reset Password'}
                        </Button>
                    </>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default ForgotPasswordDialog;