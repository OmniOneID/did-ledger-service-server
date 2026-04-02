import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Box,
    Typography,
    SelectChangeEvent,
    Alert,
    IconButton,
    Snackbar,
} from '@mui/material';
import { Close as CloseIcon, ContentCopy as CopyIcon } from '@mui/icons-material';
import { createApiKey } from '../../apis/apikey-api';
import { formatErrorMessage } from '../../utils/error-handler';

interface CreateApiKeyDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

interface ApiKeyFormData {
    name: string;
    description: string;
    role: string;
    expirationDays: number;
    expirationMode: 'preset' | 'custom';
    customExpirationDays: string;
}

interface CreatedApiKeyData {
    apiKey: string;
    name: string;
    expiresAt: string;
}

interface ErrorState {
    name?: string;
    description?: string;
    role?: string;
    expirationDays?: string;
    customExpirationDays?: string;
    expirationMode?: string;
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

const CreateApiKeyDialog: React.FC<CreateApiKeyDialogProps> = ({
    open,
    onClose,
    onSuccess,
}) => {
    const [loading, setLoading] = useState(false);
    const [createdApiKey, setCreatedApiKey] = useState<CreatedApiKeyData | null>(null);
    const [copySuccess, setCopySuccess] = useState(false);
    
    const [formData, setFormData] = useState<ApiKeyFormData>({
        name: '',
        description: '',
        role: 'READ',
        expirationDays: 3652425,
        expirationMode: 'preset',
        customExpirationDays: '',
    });

    const [errors, setErrors] = useState<ErrorState>({});
    const [isSubmitDisabled, setIsSubmitDisabled] = useState(true);

    // Reset form when dialog opens
    useEffect(() => {
        if (open) {
            handleReset();
        }
    }, [open]);

    const handleChange = (field: keyof ApiKeyFormData) =>
        (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
            let newValue: string | number = event.target.value;
            
            // Convert to number for expirationDays field
            if (field === 'expirationDays') {
                newValue = parseInt(event.target.value) || 1;
            }
            
            setFormData((prev) => ({ ...prev, [field]: newValue }));
            
            // Clear error for the field being changed
            if (errors[field]) {
                setErrors((prev) => ({ ...prev, [field]: undefined }));
            }
        };

    const handleExpirationModeChange = (event: SelectChangeEvent<string>) => {
        const value = event.target.value;
        
        if (value === 'custom') {
            setFormData((prev) => ({
                ...prev,
                expirationMode: 'custom',
                customExpirationDays: '',
            }));
        } else {
            const selectedDays = parseInt(value);
            setFormData((prev) => ({
                ...prev,
                expirationMode: 'preset',
                expirationDays: selectedDays,
                customExpirationDays: '',
            }));
        }
        
        // Clear expiration errors
        if (errors.expirationDays || errors.customExpirationDays) {
            setErrors((prev) => ({
                ...prev,
                expirationDays: undefined,
                customExpirationDays: undefined,
            }));
        }
    };

    const handleCustomExpirationChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        let value = event.target.value;

        if (value === '') {
            setFormData((prev) => ({ ...prev, customExpirationDays: '' }));
        } else {
            const numValue = parseInt(value);

            if (numValue > MAX_CUSTOM_DAYS) {
                value = MAX_CUSTOM_DAYS.toString();
            }
            
            setFormData((prev) => ({ ...prev, customExpirationDays: value }));
        }
        
        // Clear error when user starts typing
        if (errors.customExpirationDays) {
            setErrors((prev) => ({ ...prev, customExpirationDays: undefined }));
        }
    };

    const handleReset = () => {
        setFormData({
            name: '',
            description: '',
            role: 'READ',
            expirationDays: 3652425,
            expirationMode: 'preset',
            customExpirationDays: '',
        });
        setErrors({});
        setCreatedApiKey(null);
        setCopySuccess(false);
        setIsSubmitDisabled(true);
    };

    const validate = (): boolean => {
        const tempErrors: ErrorState = {};
        
        if (!formData.name.trim()) {
            tempErrors.name = 'API Key name is required.';
        } else if (formData.name.length > 100) {
            tempErrors.name = 'API Key name must not exceed 100 characters.';
        }

        if (formData.description && formData.description.length > 500) {
            tempErrors.description = 'Description must not exceed 500 characters.';
        }

        if (!formData.role) {
            tempErrors.role = 'Role is required.';
        }

        // Validate expiration
        if (formData.expirationMode === 'preset') {
            if (!formData.expirationDays || formData.expirationDays < 1) {
                tempErrors.expirationDays = 'Expiration period is required.';
            }
        } else {
            // Custom mode validation
            if (!formData.customExpirationDays.trim()) {
                tempErrors.customExpirationDays = 'Custom expiration days is required.';
            } else {
                const customDays = parseInt(formData.customExpirationDays);
                if (isNaN(customDays)) {
                    tempErrors.customExpirationDays = 'Please enter a valid number.';
                } else if (customDays < MIN_CUSTOM_DAYS) {
                    tempErrors.customExpirationDays = `Minimum expiration days is ${MIN_CUSTOM_DAYS}.`;
                } else if (customDays > MAX_CUSTOM_DAYS) {
                    tempErrors.customExpirationDays = `Maximum expiration days is ${MAX_CUSTOM_DAYS}.`;
                }
            }
        }

        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        setLoading(true);
        try {
            const expirationDays = formData.expirationMode === 'preset'
                ? formData.expirationDays
                : parseInt(formData.customExpirationDays);

            const response = await createApiKey({
                name: formData.name.trim(),
                description: formData.description.trim() || undefined,
                role: formData.role,
                expirationDays: expirationDays,
            });

            // Set the created API key data to show success screen
            setCreatedApiKey({
                apiKey: response.data.apiKey,
                name: formData.name.trim(),
                expiresAt: new Date(response.data.expiresAt).toLocaleString(),
            });
        } catch (error) {
            console.error('Failed to create API key:', error);
            setErrors({ name: formatErrorMessage(error, 'Failed to create API key') });
        } finally {
            setLoading(false);
        }
    };

    const handleCopyApiKey = async () => {
        if (createdApiKey?.apiKey) {
            try {
                await navigator.clipboard.writeText(createdApiKey.apiKey);
                setCopySuccess(true);
            } catch (error) {
                console.error('Failed to copy API key:', error);
            }
        }
    };

    const handleClose = () => {
        if (createdApiKey) {
            onSuccess();
        }
        handleReset();
        onClose();
    };

    // Update submit button state
    useEffect(() => {
        let isFormValid: boolean = !!(formData.name.trim() && formData.role);
        
        if (formData.expirationMode === 'preset') {
            isFormValid = isFormValid && formData.expirationDays >= 1;
        } else {
            const customDays = parseInt(formData.customExpirationDays);
            isFormValid = isFormValid && 
                          formData.customExpirationDays.trim() !== '' &&
                          !isNaN(customDays) &&
                          customDays >= MIN_CUSTOM_DAYS &&
                          customDays <= MAX_CUSTOM_DAYS;
        }
        
        setIsSubmitDisabled(!isFormValid);
    }, [formData]);

    return (
        <>
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
                        {createdApiKey ? 'API Key Created Successfully' : 'Create New API Key'}
                    </Typography>
                    <IconButton onClick={handleClose} size="small">
                        <CloseIcon />
                    </IconButton>
                </DialogTitle>
                
                <DialogContent dividers>
                    {!createdApiKey ? (
                        // 생성 폼
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                            <TextField
                                fullWidth
                                label="Name *"
                                variant="outlined"
                                value={formData.name}
                                onChange={handleChange('name')}
                                error={!!errors.name}
                                helperText={errors.name || "A descriptive name for this API key"}
                                placeholder="Enter API key name"
                            />

                            <TextField
                                fullWidth
                                label="Description"
                                variant="outlined"
                                multiline
                                rows={3}
                                value={formData.description}
                                onChange={handleChange('description')}
                                error={!!errors.description}
                                helperText={errors.description || "Optional description for this API key"}
                                placeholder="Enter description (optional)"
                            />

                            <FormControl fullWidth error={!!errors.role}>
                                <InputLabel>Role *</InputLabel>
                                <Select
                                    value={formData.role}
                                    onChange={handleChange('role')}
                                    label="Role *"
                                >
                                    <MenuItem value="READ">READ</MenuItem>
                                    <MenuItem value="ISSUER">ISSUER</MenuItem>
                                    <MenuItem value="TAS">TAS</MenuItem>
                                </Select>
                                {errors.role && (
                                    <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5 }}>
                                        {errors.role}
                                    </Typography>
                                )}
                            </FormControl>

                            <FormControl fullWidth error={!!errors.expirationDays || !!errors.customExpirationDays}>
                                <InputLabel>Expiration Period *</InputLabel>
                                <Select
                                    value={formData.expirationMode === 'custom' ? 'custom' : formData.expirationDays.toString()}
                                    onChange={handleExpirationModeChange}
                                    label="Expiration Period *"
                                >
                                    {EXPIRATION_PRESETS.map((preset) => (
                                        <MenuItem key={preset.value} value={preset.value.toString()}>
                                            {preset.label}
                                        </MenuItem>
                                    ))}
                                    <MenuItem value="custom">Custom</MenuItem>
                                </Select>
                                {(errors.expirationDays || errors.customExpirationDays) && (
                                    <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5 }}>
                                        {errors.expirationDays || errors.customExpirationDays}
                                    </Typography>
                                )}
                            </FormControl>

                            {formData.expirationMode === 'custom' && (
                                <TextField
                                    fullWidth
                                    label="Custom Expiration Days *"
                                    variant="outlined"
                                    type="number"
                                    value={formData.customExpirationDays}
                                    onChange={handleCustomExpirationChange}
                                    error={!!errors.customExpirationDays}
                                    helperText={errors.customExpirationDays || `Enter days between ${MIN_CUSTOM_DAYS} and ${MAX_CUSTOM_DAYS}`}
                                    placeholder="Enter number of days"
                                    inputProps={{ 
                                        min: MIN_CUSTOM_DAYS, 
                                        max: MAX_CUSTOM_DAYS,
                                        step: 1,
                                    }}
                                />
                            )}
                        </Box>
                    ) : (
                        // 생성 결과
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                            <Alert severity="success" sx={{ mb: 2 }}>
                                API Key has been created successfully!
                            </Alert>

                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    API Key Name
                                </Typography>
                                <Typography variant="body1" fontWeight={500}>
                                    {createdApiKey.name}
                                </Typography>
                            </Box>

                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    API Key
                                </Typography>
                                <Box sx={{ 
                                    display: 'flex', 
                                    alignItems: 'center', 
                                    gap: 1,
                                    p: 2,
                                    bgcolor: '#f5f5f5',
                                    borderRadius: 1,
                                    border: '1px solid #e0e0e0'
                                }}>
                                    <Typography 
                                        variant="body2" 
                                        sx={{ 
                                            fontFamily: 'monospace',
                                            wordBreak: 'break-all',
                                            flex: 1,
                                            fontSize: '14px'
                                        }}
                                    >
                                        {createdApiKey.apiKey}
                                    </Typography>
                                    <IconButton 
                                        onClick={handleCopyApiKey}
                                        size="small"
                                        color="primary"
                                    >
                                        <CopyIcon />
                                    </IconButton>
                                </Box>
                            </Box>

                            <Box>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Expires At
                                </Typography>
                                <Typography variant="body1">
                                    {createdApiKey.expiresAt}
                                </Typography>
                            </Box>

                            <Alert severity="warning">
                                <Typography variant="body2">
                                    <strong>Important:</strong> Please copy and store this API key securely. 
                                    You won't be able to see the full API key again after closing this dialog.
                                </Typography>
                            </Alert>
                        </Box>
                    )}
                </DialogContent>

                <DialogActions sx={{ px: 3, py: 2, display: 'flex', justifyContent: 'center', gap: 2 }}>
                    {!createdApiKey ? (
                        <>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleSubmit}
                                disabled={isSubmitDisabled || loading}
                            >
                                {loading ? 'Creating...' : 'Create'}
                            </Button>
                            <Button
                                variant="contained"
                                color="secondary"
                                onClick={handleReset}
                                disabled={loading}
                            >
                                Reset
                            </Button>
                            <Button
                                variant="outlined"
                                color="primary"
                                onClick={handleClose}
                                disabled={loading}
                            >
                                Cancel
                            </Button>
                        </>
                    ) : (
                        <Button onClick={handleClose} variant="contained" color="primary">
                            Close
                        </Button>
                    )}
                </DialogActions>
            </Dialog>

            <Snackbar
                open={copySuccess}
                autoHideDuration={3000}
                onClose={() => setCopySuccess(false)}
                message="API Key copied to clipboard!"
            />
        </>
    );
};

export default CreateApiKeyDialog;
