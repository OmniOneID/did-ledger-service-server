import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Box,
    Typography,
    IconButton,
    Alert,
    TextField,
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { renewApiKey } from '../../apis/apikey-api';
import { formatErrorMessage } from '../../utils/error-handler';

interface RenewApiKeyDialogProps {
    open: boolean;
    onClose: () => void;
    apiKeyData: {
        id: string | number;
        name: string;
        expiresAt: string;
    };
    onRenewSuccess: (result: { previousExpiresAt: string; newExpiresAt: string }) => void;
}

interface RenewFormData {
    extensionDays: number;
    extensionMode: 'preset' | 'custom';
    customExtensionDays: string;
}

interface ErrorState {
    extensionDays?: string;
    customExtensionDays?: string;
}

const EXPIRATION_PRESETS = [
    { label: '1 month', value: 30 },
    { label: '3 months', value: 90 },
    { label: '6 months', value: 180 },
    { label: '1 year', value: 365 },
    { label: '2 years', value: 730 },
    { label: 'No Limit', value: 3652425 },
];

const MIN_CUSTOM_DAYS = 30;
const MAX_CUSTOM_DAYS = 36525; // 100 years

const RenewApiKeyDialog: React.FC<RenewApiKeyDialogProps> = ({
    open,
    onClose,
    apiKeyData,
    onRenewSuccess,
}) => {
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState<RenewFormData>({
        extensionDays: 30,
        extensionMode: 'preset',
        customExtensionDays: '',
    });
    const [error, setError] = useState<string>('');
    const [errors, setErrors] = useState<ErrorState>({});
    const [isRenewDisabled, setIsRenewDisabled] = useState(true);

    useEffect(() => {
        if (open) {
            setFormData({ extensionDays: 30, extensionMode: 'preset', customExtensionDays: '' });
            setError('');
            setErrors({});
        }
    }, [open]);

    const handleExtensionModeChange = (event: any) => {
        const value = event.target.value;
        
        if (value === 'custom') {
            setFormData((prev) => ({
                ...prev,
                extensionMode: 'custom',
                customExtensionDays: '',
            }));
        } else {
            const selectedDays = parseInt(value);
            setFormData((prev) => ({
                ...prev,
                extensionMode: 'preset',
                extensionDays: selectedDays,
                customExtensionDays: '',
            }));
        }
        setError('');
        if (errors.extensionDays || errors.customExtensionDays) {
            setErrors((prev) => ({
                ...prev,
                extensionDays: undefined,
                customExtensionDays: undefined,
            }));
        }
    };

    const handleCustomExtensionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        let value = event.target.value;

        if (value === '') {
            setFormData((prev) => ({ ...prev, customExtensionDays: '' }));
        } else {
            const numValue = parseInt(value);

            if (numValue > MAX_CUSTOM_DAYS) {
                value = MAX_CUSTOM_DAYS.toString();
            }
            
            setFormData((prev) => ({ ...prev, customExtensionDays: value }));
        }
        setError('');
        if (errors.customExtensionDays) {
            setErrors((prev) => ({ ...prev, customExtensionDays: undefined }));
        }
    };

    const getCurrentExpiresAt = (): Date => {
        return new Date(apiKeyData.expiresAt);
    };

    const isNoLimitSelected = (): boolean => {
        const isSelected = formData.extensionMode === 'preset' && formData.extensionDays === 3652425;
        console.log('isNoLimitSelected:', isSelected, 'extensionDays:', formData.extensionDays, 'extensionMode:', formData.extensionMode);
        return isSelected;
    };

    const isCustomExceedsNoLimit = (): boolean => {
        if (formData.extensionMode === 'custom' && formData.customExtensionDays) {
            const customDays = parseInt(formData.customExtensionDays);
            return !isNaN(customDays) && customDays > MAX_CUSTOM_DAYS;
        }
        return false;
    };

    const getNewExpiresAt = (): Date | string => {
        if (isNoLimitSelected() || isCustomExceedsNoLimit()) {
            return '9999-12-31 23:59:59';
        }
        
        const currentExpires = getCurrentExpiresAt();
        const newExpires = new Date(currentExpires);
        const extensionDays = formData.extensionMode === 'preset'
            ? formData.extensionDays
            : parseInt(formData.customExtensionDays) || 0;
        newExpires.setDate(currentExpires.getDate() + extensionDays);
        return newExpires;
    };

    const formatDateTime = (date: Date | string): string => {
        if (typeof date === 'string') {
            return date;
        }
        return date.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
        });
    };

    const isYear9999 = (): boolean => {
        return !!(apiKeyData.expiresAt && apiKeyData.expiresAt.startsWith('9999'));
    };

    // Update renew button state
    useEffect(() => {
        let isFormValid: boolean = true;
        
        if (formData.extensionMode === 'preset') {
            isFormValid = isFormValid && formData.extensionDays >= 1;
        } else {
            const customDays = parseInt(formData.customExtensionDays);
            isFormValid = isFormValid && 
                          formData.customExtensionDays.trim() !== '' &&
                          !isNaN(customDays) &&
                          customDays >= MIN_CUSTOM_DAYS &&
                          customDays <= MAX_CUSTOM_DAYS;
        }
        
        setIsRenewDisabled(!isFormValid);
    }, [formData]);

    const validateBeforeSubmit = (): boolean => {
        const tempErrors: ErrorState = {};
        
        if (formData.extensionMode === 'preset') {
            if (!formData.extensionDays || formData.extensionDays < 1) {
                tempErrors.extensionDays = 'Extension period is required.';
            }
        } else {
            // Custom mode validation
            if (!formData.customExtensionDays.trim()) {
                tempErrors.customExtensionDays = 'Custom extension days is required.';
            } else {
                const customDays = parseInt(formData.customExtensionDays);
                if (isNaN(customDays)) {
                    tempErrors.customExtensionDays = 'Please enter a valid number.';
                } else if (customDays < MIN_CUSTOM_DAYS) {
                    tempErrors.customExtensionDays = `Minimum extension days is ${MIN_CUSTOM_DAYS}.`;
                } else if (customDays > MAX_CUSTOM_DAYS) {
                    tempErrors.customExtensionDays = `Maximum extension days is ${MAX_CUSTOM_DAYS}.`;
                }
            }
        }

        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validateBeforeSubmit()) return;

        setLoading(true);
        setError('');

        try {
            const extensionDays = formData.extensionMode === 'preset'
                ? formData.extensionDays
                : parseInt(formData.customExtensionDays);

            const response = await renewApiKey(apiKeyData.id as number, extensionDays);
            
            onRenewSuccess({
                previousExpiresAt: response.data.previousExpiresAt,
                newExpiresAt: response.data.newExpiresAt,
            });
            
            onClose();
        } catch (error) {
            console.error('Failed to renew API key:', error);
            setError(formatErrorMessage(error, 'Failed to renew API key'));
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        if (!loading) {
            onClose();
        }
    };

    const year9999Warning = isYear9999();

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth="sm"
            fullWidth
            PaperProps={{
                sx: { borderRadius: 2 }
            }}
        >
            <DialogTitle sx={{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center',
                pb: 1
            }}>
                <Typography variant="h6" fontWeight={600}>
                    Renew API Key
                </Typography>
                <IconButton onClick={handleClose} size="small" disabled={loading}>
                    <CloseIcon />
                </IconButton>
            </DialogTitle>
            
            <DialogContent dividers>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                    <Box>
                        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                            API Key Name
                        </Typography>
                        <Typography variant="body1" fontWeight={500}>
                            {apiKeyData.name}
                        </Typography>
                    </Box>

                    <Box>
                        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                            Current Expiration
                        </Typography>
                        <Typography variant="body1" fontWeight={500}>
                            {apiKeyData.expiresAt}
                        </Typography>
                    </Box>

                    {year9999Warning && (
                        <Alert severity="info">
                            This API key has no expiration limit (No Limit) and cannot be renewed.
                        </Alert>
                    )}

                    {!year9999Warning && (
                        <>
                            <FormControl fullWidth error={!!errors.extensionDays || !!errors.customExtensionDays}>
                                <InputLabel>Extension Period *</InputLabel>
                                <Select
                                    value={formData.extensionMode === 'custom' ? 'custom' : formData.extensionDays.toString()}
                                    onChange={handleExtensionModeChange}
                                    label="Extension Period *"
                                    disabled={loading}
                                >
                                    {EXPIRATION_PRESETS.map((preset) => (
                                        <MenuItem key={preset.value} value={preset.value.toString()}>
                                            {preset.label}
                                        </MenuItem>
                                    ))}
                                    <MenuItem value="custom">Custom</MenuItem>
                                </Select>
                                {(errors.extensionDays || errors.customExtensionDays) && (
                                    <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5 }}>
                                        {errors.extensionDays || errors.customExtensionDays}
                                    </Typography>
                                )}
                            </FormControl>

                            {formData.extensionMode === 'custom' && (
                                <TextField
                                    fullWidth
                                    label="Custom Extension Days *"
                                    variant="outlined"
                                    type="number"
                                    value={formData.customExtensionDays}
                                    onChange={handleCustomExtensionChange}
                                    error={!!errors.customExtensionDays}
                                    helperText={errors.customExtensionDays || `Enter days between ${MIN_CUSTOM_DAYS} and ${MAX_CUSTOM_DAYS}`}
                                    placeholder="Enter number of days"
                                    inputProps={{ 
                                        min: MIN_CUSTOM_DAYS, 
                                        max: MAX_CUSTOM_DAYS,
                                        step: 1,
                                    }}
                                    disabled={loading}
                                />
                            )}

                            <Box sx={{ 
                                bgcolor: '#f5f5f5', 
                                p: 2, 
                                borderRadius: 1,
                                border: '1px solid #e0e0e0'
                            }}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    New Expiration (Preview)
                                </Typography>
                                <Typography variant="body1" color="primary" fontWeight={600}>
                                    {formatDateTime(getNewExpiresAt())}
                                </Typography>
                                <Typography variant="caption" color="success.main" sx={{ mt: 0.5, display: 'block' }}>
                                    Extended by {formData.extensionMode === 'preset' ? formData.extensionDays : formData.customExtensionDays} days
                                </Typography>
                            </Box>
                        </>
                    )}

                    {error && (
                        <Alert severity="error">
                            {error}
                        </Alert>
                    )}
                </Box>
            </DialogContent>

            <DialogActions sx={{ px: 3, py: 2, display: 'flex', justifyContent: 'center', gap: 2 }}>
                <Button
                    variant="outlined"
                    color="primary"
                    onClick={handleClose}
                    disabled={loading}
                >
                    Close
                </Button>
                {!year9999Warning && (
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSubmit}
                        disabled={isRenewDisabled || loading}
                    >
                        {loading ? 'Renewing...' : 'Confirm Renew'}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default RenewApiKeyDialog;
