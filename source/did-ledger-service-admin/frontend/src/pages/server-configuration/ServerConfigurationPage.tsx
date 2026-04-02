import React, { useState, useEffect, useMemo } from 'react';
import {
  Box,
  Button,
  Typography,
  FormControl,
  InputLabel,
  OutlinedInput,
  Switch,
  styled,
} from '@mui/material';
import { useDialogs } from '@toolpad/core';
import CustomDialog from '../../components/dialog/CustomDialog';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { getAllServerConfigs, updateServerConfigs, ServerConfigDto, UpdateServerConfigReqDto } from '../../apis/server-api';
import { formatErrorMessage } from '../../utils/error-handler';

interface FormData {
  API_KEY_VALIDATION_ENABLED?: boolean;
}

interface ErrorState {
  API_KEY_VALIDATION_ENABLED?: string;
}

const ServerConfigurationPage: React.FC = () => {
  const dialogs = useDialogs();
  const [formData, setFormData] = useState<FormData>({
    API_KEY_VALIDATION_ENABLED: undefined,
  });
  const [initialData, setInitialData] = useState<FormData>({
    API_KEY_VALIDATION_ENABLED: undefined,
  });
  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [configDescriptions, setConfigDescriptions] = useState<Record<string, string>>({});

  // Load server configuration on component mount
  useEffect(() => {
    loadServerConfigs();
  }, []);

  // Check for changes to enable/disable buttons
  useEffect(() => {
    const isModified = Object.keys(formData).some(
      (key) => formData[key as keyof FormData] !== initialData[key as keyof FormData]
    );
    setIsButtonDisabled(!isModified);
  }, [formData, initialData]);

  const loadServerConfigs = async () => {
    setIsLoading(true);
    try {
      const response = await getAllServerConfigs();
      const configs = response.data;
      
      // Convert array of configs to form data
      const formDataFromConfigs: FormData = {};
      const descriptions: Record<string, string> = {};
      
      configs.forEach((config: ServerConfigDto) => {
        if (config.configKey in formData) {
          formDataFromConfigs[config.configKey as keyof FormData] = config.configValue === 'true';
          descriptions[config.configKey] = config.description || '';
        }
      });
      
      setFormData(formDataFromConfigs);
      setInitialData(formDataFromConfigs);
      setConfigDescriptions(descriptions);
    } catch (error) {
      console.error('Failed to load server configuration:', error);
      await dialogs.open(CustomDialog, {
        title: 'Error',
        message: `Failed to load server configuration: ${formatErrorMessage(error, 'Load failed')}`,
        isModal: true,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSwitchChange = (field: keyof FormData) =>
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setFormData((prev) => ({ ...prev, [field]: event.target.checked }));
    };

  const handleReset = () => {
    setFormData(initialData);
    setIsButtonDisabled(true);
    setErrors({});
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    // Validate that all fields have values
    Object.keys(formData).forEach((key) => {
      const typedKey = key as keyof FormData;
      if (formData[typedKey] === undefined) {
        tempErrors[typedKey] = 'Please select a value.';
      }
    });

    setErrors(tempErrors);
    return Object.values(tempErrors).every((error) => !error);
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to update Server Configuration Settings?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        // Convert form data to API request format
        const updateRequest: UpdateServerConfigReqDto = {
          configs: Object.entries(formData)
            .filter(([_, value]) => value !== undefined)
            .map(([key, value]) => ({
              configKey: key,
              configValue: value ? 'true' : 'false',
              description: configDescriptions[key],
            }))
        };

        const response = await updateServerConfigs(updateRequest);

        if (response.data) {
          setInitialData(formData);
        }

        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed server configuration update.',
          isModal: true,
        });

      } catch (error) {
        console.error('Server configuration update failed:', error);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to update Server Configuration: ${formatErrorMessage(error, 'Update failed')}`,
          isModal: true,
        });
      } finally {
        setIsLoading(false);
      }
    }
  };

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 600,
    margin: 'auto',
    marginTop: theme.spacing(1),
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

  const StyledSectionTitle = useMemo(() => styled(Typography)(({ theme }) => ({
    textAlign: 'left',
    fontSize: '18px',
    fontWeight: 600,
    color: '#1976d2',
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(1),
    paddingLeft: theme.spacing(1),
    borderLeft: `4px solid #1976d2`,
  })), []);

  const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(2),
  })), []);

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <StyledContainer>
        <StyledSubTitle>Server Configuration</StyledSubTitle>

        <StyledInputArea>
          {/* API Key Validation Settings Section */}
          <StyledSectionTitle>API Key Validation Settings</StyledSectionTitle>
          
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Configure whether to validate API keys. When disabled, API keys will bypass validation checks.
          </Typography>

          <FormControl fullWidth variant="outlined" sx={{ mt: 2 }} error={!!errors.API_KEY_VALIDATION_ENABLED}>
            <InputLabel shrink>API Key Validation</InputLabel>
            <OutlinedInput
              notched
              label="API Key Validation"
              startAdornment={
                <Switch
                  checked={formData.API_KEY_VALIDATION_ENABLED ?? false}
                  onChange={handleSwitchChange('API_KEY_VALIDATION_ENABLED')}
                  disabled={isLoading}
                  color="primary"
                  sx={{ transform: "scale(1.2)", mr: 1 }}
                />
              }
            />
          </FormControl>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 4 }}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleSubmit}
              disabled={isButtonDisabled || isLoading}
            >
              Update
            </Button>
            <Button 
              variant="contained" 
              color="secondary" 
              onClick={handleReset}
              disabled={isLoading}
            >
              Reset
            </Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
};

export default ServerConfigurationPage;
