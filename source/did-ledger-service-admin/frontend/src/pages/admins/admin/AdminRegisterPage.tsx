import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import {
    Box,
    Button,
    FormControl,
    FormHelperText,
    IconButton,
    InputLabel,
    Paper,
    Select,
    SelectChangeEvent,
    styled,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
    Collapse,
    List,
    ListItem,
    ListItemIcon,
    ListItemText
} from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import {
    Visibility,
    VisibilityOff,
    Check,
    Close,
    ExpandMore,
    ExpandLess,
    Info
} from '@mui/icons-material';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { verifyAdminIdUnique, registerAdmin } from '../../../apis/admin-api';
import { emailRegex } from '../../../utils/regex';
import { sha256Hash } from '../../../utils/sha256-hash';
import { usePasswordPolicy } from '../../../hooks/usePasswordPolicy';
import { ValidationRuleResult } from '../../../constants/password-policy';

type Props = {}

type AdminFormData = {
    loginId: string;
    role: string;
    loginPassword: string;
    confirmPassword: string;
};

interface ErrorState {
    loginId?: string;
    role?: string[];
    loginPassword?: string;
    confirmPassword?: string;
}

const AdminRegisterPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const [formData, setFormData] = useState<AdminFormData>({
        loginId: '',
        role: 'NORMAL',
        loginPassword: '',
        confirmPassword: '',
    });

    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [isLoginIdIsValid, setIsLoginIdIsValid] = useState(false);
    const [loginIdCheckMessage, setLoginIdCheckMessage] = useState<string>('');
    const [loginIdCheckStatus, setLoginIdCheckStatus] = useState<'success' | 'error' | ''>('');
    const [showInitialDuplicateCheckMessage, setShowInitialDuplicateCheckMessage] = useState(true);

    // Password visibility states
    const [showLoginPassword, setShowLoginPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    // Requirements visibility
    const [showRequirements, setShowRequirements] = useState(false);

    // Focus and touch states for better UX
    const [loginIdTouched, setLoginIdTouched] = useState(false);
    const [passwordTouched, setPasswordTouched] = useState(false);
    const [confirmPasswordTouched, setConfirmPasswordTouched] = useState(false);

    // Password policy hook
    const {
        policy,
        isLoading: isPolicyLoading,
        validatePassword,
        getValidationResults,
        getValidationSummary
    } = usePasswordPolicy();

    // Real-time password validation results
    const [validationResults, setValidationResults] = useState<ValidationRuleResult[]>([]);
    const [validationSummary, setValidationSummary] = useState({
        isValid: false,
        passedCount: 0,
        totalCount: 0,
        failedRules: [] as string[]
    });

    const handleChange = (field: keyof AdminFormData) =>
        (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
            const newValue = event.target.value;
            setFormData((prev) => ({ ...prev, [field]: newValue }));

            if (field === 'loginId') {
                setIsLoginIdIsValid(false);
                setErrors((prev) => ({ ...prev, loginId: undefined }));
                setLoginIdCheckMessage('');
                setLoginIdCheckStatus('');
                setShowInitialDuplicateCheckMessage(true);
            }

            if (field === 'confirmPassword') {
                if (confirmPasswordTouched) {
                    setConfirmPasswordTouched(true);
                }
            }
        };

    const handleLoginIdBlur = () => {
        setLoginIdTouched(true);
    };

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const password = event.target.value;
        setFormData((prev) => ({ ...prev, loginPassword: password }));

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
            if (summary.isValid && document.activeElement?.id !== 'admin-password') {
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

    const handlePasswordBlur = () => {
        setPasswordTouched(true);
    };

    const handleConfirmPasswordBlur = () => {
        setConfirmPasswordTouched(true);
    };

    const handleReset = () => {
        setErrors({});
        setIsButtonDisabled(true);
        setFormData({ loginId: '', role: 'NORMAL', loginPassword: '', confirmPassword: '' });
        setIsLoginIdIsValid(false);
        setLoginIdCheckMessage('');
        setLoginIdCheckStatus('');
        setShowInitialDuplicateCheckMessage(true);
        setValidationResults([]);
        setValidationSummary({
            isValid: false,
            passedCount: 0,
            totalCount: 0,
            failedRules: []
        });
        setShowLoginPassword(false);
        setShowConfirmPassword(false);
        setShowRequirements(false);
        setLoginIdTouched(false);
        setPasswordTouched(false);
        setConfirmPasswordTouched(false);
    };

    const validate = () => {
        let tempErrors: ErrorState = {};
        tempErrors.loginId = validateLoginId(formData.loginId);
        tempErrors.loginPassword = validateLoginPassword(formData.loginPassword);
        tempErrors.confirmPassword = validateConfirmPassword(formData.confirmPassword);

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const validateLoginId = (loginId?: string): string | undefined => {
        if (!loginId) {
            // 로그인 ID가 비어있고 아직 건드리지 않았다면 에러 표시 안함
            return loginIdTouched ? 'Please enter a Login ID.' : undefined;
        }
        if (loginId.length < 3 || loginId.length > 30) return 'Login ID must be between 3 and 30 characters.';
        if (!emailRegex.test(loginId)) return 'Please enter a valid email address.';
        if (loginIdTouched && !isLoginIdIsValid) return 'Please check for duplicate Login ID.';
        return undefined;
    };

    const validateLoginPassword = (loginPassword?: string): string | undefined => {
        if (!loginPassword) {
            // 비밀번호가 비어있고 아직 건드리지 않았다면 에러 표시 안함
            return passwordTouched ? 'Please enter a Login Password.' : undefined;
        }

        // Use password policy validation if available
        if (policy && passwordTouched && !validatePassword(loginPassword)) {
            return "Password does not meet policy requirements.";
        }

        // Fallback validation if policy is not available
        if (loginPassword.length < 8 || loginPassword.length > 30) {
            return 'Password must be between 8 and 30 characters.';
        }

        return undefined;
    };

    const validateConfirmPassword = (confirmPassword?: string): string | undefined => {
        if (!confirmPassword) {
            // 확인 비밀번호가 비어있고 아직 건드리지 않았다면 에러 표시 안함
            return confirmPasswordTouched ? 'Please re-enter the Login Password.' : undefined;
        }
        if (confirmPasswordTouched && confirmPassword !== formData.loginPassword) return 'Passwords do not match.';
        return undefined;
    };



    const handleSubmit = async () => {
        if (!validate()) return;

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to register Admin?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);

            const hashedPassword = await sha256Hash(formData.loginPassword);
            let requestObject = {
                loginId: formData.loginId,
                loginPassword: hashedPassword,
                role: formData.role,
            }

            await registerAdmin(requestObject).then((response) => {
                setIsLoading(false);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Admin registration completed.',
                    isModal: true,
                },{
                    onClose: async (result) =>  navigate('/admins/admin-management'),
                });

            }).catch((error) => {
                setIsLoading(false);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register Admin: ${error}`,
                    isModal: true,
                });
            });
        }
    };

    const validateOnlyLoginId = () => {
        let tempErrors: ErrorState = {};

        if (!formData.loginId)  tempErrors.loginId = 'Please enter a Login ID.';
        if (formData.loginId.length < 3 || formData.loginId.length > 30) tempErrors.loginId = 'Login ID must be between 3 and 30 characters.';
        if (!emailRegex.test(formData.loginId)) tempErrors.loginId = 'Please enter a valid email address.';

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const handleCheckDuplicateLoginId = () => {
        if (!validateOnlyLoginId()) return;

        if (!formData.loginId.trim()) {
            setLoginIdCheckMessage('Please enter a login ID first.');
            setLoginIdCheckStatus('error');
            return;
        }

        verifyAdminIdUnique(formData.loginId as string)
            .then((response) => {
                if (response.data.unique === false) {
                    setErrors((prev) => ({ ...prev, loginId: 'Login ID already exists.' }));
                    setIsLoginIdIsValid(false);
                    setShowInitialDuplicateCheckMessage(false);
                    setLoginIdCheckMessage('This login ID is already in use. Please choose a different one.');
                    setLoginIdCheckStatus('error');
                } else {
                    setIsLoginIdIsValid(true);
                    setErrors((prev) => ({ ...prev, loginId: undefined }));
                    setShowInitialDuplicateCheckMessage(false);
                    setLoginIdCheckMessage('This login ID is available for use.');
                    setLoginIdCheckStatus('success');
                }
            })
            .catch((error) => {
                setIsLoginIdIsValid(false);
                setShowInitialDuplicateCheckMessage(false);
                setLoginIdCheckMessage('Failed to check login ID availability. Please try again.');
                setLoginIdCheckStatus('error');
            });
    };

    const handleCancel = async () => {
        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to cancel admin registration?',
            isModal: true,
        });

        if (result) {
            navigate('/admins/admin-management');
        }
    };

    useEffect(() => {
        const isModified = Object.entries(formData).some(([key, value]) => {
            if (key === 'role') return false;
            if (Array.isArray(value)) return value.length > 0;
            return value !== '' && value !== undefined;
        });

        // Also check password policy validation
        const isPasswordValid = policy ? validatePassword(formData.loginPassword) : formData.loginPassword.length >= 8;
        const passwordsMatch = formData.loginPassword === formData.confirmPassword;

        setIsButtonDisabled(!isModified || !isLoginIdIsValid || !isPasswordValid || !passwordsMatch);
    }, [formData, isLoginIdIsValid, policy, validatePassword]);

    // Touch 상태가 변경되었을 때만 검증 실행
    useEffect(() => {
        // 아무것도 건드리지 않았다면 검증하지 않음
        if (!loginIdTouched && !passwordTouched && !confirmPasswordTouched && !formData.loginId) {
            return;
        }
        validate();
    }, [formData, loginIdTouched, passwordTouched, confirmPasswordTouched]);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        width: 500,
        margin: 'auto',
        marginTop: theme.spacing(3),
        padding: theme.spacing(3),
        border: 'none',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: '#ffffff',
        boxShadow: '0px 4px 8px 0px #0000001A',
    })), []);

    const StyledSubTitle = useMemo(() => styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
    }), []);

    const StyledDescription = useMemo(() => styled(Box)(({ theme }) => ({
        maxWidth: 500,
        marginTop: theme.spacing(1),
        padding: theme.spacing(0),
    })), []);

    const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
        marginTop: theme.spacing(2),
    })), []);

    // Show loading if policy is being loaded
    if (isPolicyLoading) {
        return <FullscreenLoader open={true} />;
    }

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">Admin Management</Typography>
            <StyledContainer>
                <StyledSubTitle>Admin Registration</StyledSubTitle>
                <StyledInputArea>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField
                            fullWidth
                            label="ID *"
                            variant="outlined"
                            margin="normal"
                            size="medium"
                            type='email'
                            value={formData.loginId}
                            onChange={handleChange('loginId')}
                            onBlur={handleLoginIdBlur}
                            error={!!errors.loginId}
                            helperText={
                                errors.loginId ||
                                (showInitialDuplicateCheckMessage && !isLoginIdIsValid ? 'Please check ID availability' : '') ||
                                loginIdCheckMessage
                            }
                            sx={{
                                minWidth: 250,
                                '& .MuiFormHelperText-root': {
                                    color: loginIdCheckStatus === 'success' ? 'green' :
                                        loginIdCheckStatus === 'error' ? 'red' : 'inherit',
                                    fontWeight: loginIdCheckStatus ? 500 : 'inherit'
                                }
                            }}
                        />
                        <Button
                            variant="outlined"
                            onClick={handleCheckDuplicateLoginId}
                            disabled={!formData.loginId}
                            sx={{
                                minWidth: 150,
                                whiteSpace: 'nowrap',
                                textTransform: 'none'
                            }}
                        >
                            Check Availability
                        </Button>
                    </Box>

                    <FormControl fullWidth margin="normal">
                        <InputLabel>Role *</InputLabel>
                        <Select
                            value={formData.role}
                            onChange={() => {}}
                            label="Role"
                            slotProps={{ input: { readOnly: true } }}
                        >
                            <option value="NORMAL">Normal Admin</option>
                        </Select>
                    </FormControl>

                    <TextField
                        id="admin-password"
                        fullWidth
                        label="Password *"
                        type={showLoginPassword ? "text" : "password"}
                        variant="outlined"
                        margin="normal"
                        size="medium"
                        value={formData.loginPassword}
                        onChange={handlePasswordChange}
                        onBlur={handlePasswordBlur}
                        error={!!errors.loginPassword}
                        helperText={errors.loginPassword}
                        InputProps={{
                            endAdornment: (
                                <IconButton
                                    aria-label="toggle password visibility"
                                    onClick={() => setShowLoginPassword(!showLoginPassword)}
                                    edge="end"
                                >
                                    {showLoginPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            ),
                        }}
                    />



                    {/* Password Requirements Toggle */}
                    {formData.loginPassword && policy && (
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

                    <TextField
                        fullWidth
                        label="Re-enter Password *"
                        type={showConfirmPassword ? "text" : "password"}
                        variant="outlined"
                        margin="normal"
                        size="medium"
                        value={formData.confirmPassword}
                        onChange={handleChange('confirmPassword')}
                        onBlur={handleConfirmPasswordBlur}
                        error={!!errors.confirmPassword}
                        helperText={errors.confirmPassword}
                        InputProps={{
                            endAdornment: (
                                <IconButton
                                    aria-label="toggle password visibility"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    edge="end"
                                >
                                    {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            ),
                        }}
                    />

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
                        <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
                        <Button variant="outlined" color="primary" onClick={handleCancel}>Cancel</Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    )
}

export default AdminRegisterPage