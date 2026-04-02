import React, { useState, useEffect, useMemo } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  IconButton,
  Alert,
  TextField,
  styled,
  Chip,
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { useDialogs } from '@toolpad/core';
import { deactivateApiKey, activateApiKey, renewApiKey, getApiKeyInfo } from '../../apis/apikey-api';
import { formatErrorMessage } from '../../utils/error-handler';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import RenewApiKeyDialog from './RenewApiKeyDialog';

interface ApiKeyDetailDialogProps {
  open: boolean;
  onClose: () => void;
  apiKeyData: {
    id: string | number;
    apiKey: string;
    maskedApiKey: string;
    name: string;
    description: string;
    role: string;
    isActive: boolean;
    lastUsedAt: string;
    expiresAt: string;
    createdAt: string;
    updatedAt: string;
  };
  onDeactivateSuccess: () => void;
  onActivateSuccess: () => void;
  onRenewSuccess: () => void;
}

const ApiKeyDetailDialog: React.FC<ApiKeyDetailDialogProps> = ({
  open,
  onClose,
  apiKeyData,
  onDeactivateSuccess,
  onActivateSuccess,
  onRenewSuccess,
}) => {
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailData, setDetailData] = useState<any>(null);
  const [renewDialogOpen, setRenewDialogOpen] = useState(false);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    maxWidth: 500,
    margin: '0 auto',
    padding: 0,
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
  })), []);

  const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(1),
  })), []);
  
  useEffect(() => {
    if (open && apiKeyData) {
      fetchDetailData();
    }
  }, [open, apiKeyData]);

  const fetchDetailData = async () => {
    setDetailLoading(true);
    try {
      const response = await getApiKeyInfo(apiKeyData.id as number);
      setDetailData(response.data);
    } catch (error) {
      console.error("Failed to fetch API Key detail:", error);
      await dialogs.open(CustomDialog, {
        title: 'Error',
        message: formatErrorMessage(error, 'Failed to retrieve API Key details'),
        isModal: true,
      });
    } finally {
      setDetailLoading(false);
    }
  };

  const getStatusChip = (isActive: boolean) => {
    return (
      <Chip
        label={isActive ? 'Active' : 'Inactive'}
        color={isActive ? 'success' : 'default'}
        size="small"
        sx={{ fontWeight: 500 }}
      />
    );
  };

  const getRoleChip = (role: string) => {
    const roleColors: Record<string, 'primary' | 'secondary' | 'info'> = {
      'TAS': 'primary',
      'ISSUER': 'secondary',
      'READ': 'info'
    };

    return (
      <Chip
        label={role}
        color={roleColors[role] || 'default'}
        size="small"
        variant="outlined"
      />
    );
  };

  const handleDeactivate = async () => {
    const confirmed = await dialogs.open(CustomConfirmDialog, { 
      title: 'Confirm Deactivation',
      message: 'Are you sure you want to deactivate this API key?',
      isModal: true 
    });

    if (!confirmed) return;

    setLoading(true);
    try {
      await deactivateApiKey(apiKeyData.id as number);
      await dialogs.open(CustomDialog, { 
        title: 'Success',
        message: 'API key has been deactivated successfully.',
        isModal: true 
      });
      onDeactivateSuccess();
    } catch (error) {
      await dialogs.open(CustomDialog, { 
        title: 'Error',
        message: formatErrorMessage(error, "Failed to deactivate API key"),
        isModal: true 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleActivate = async () => {
    const confirmed = await dialogs.open(CustomConfirmDialog, { 
      title: 'Confirm Activation',
      message: 'Are you sure you want to activate this API key?',
      isModal: true 
    });

    if (!confirmed) return;

    setLoading(true);
    try {
      await activateApiKey(apiKeyData.id as number);
      await dialogs.open(CustomDialog, { 
        title: 'Success',
        message: 'API key has been activated successfully.',
        isModal: true 
      });
      onActivateSuccess();
    } catch (error) {
      await dialogs.open(CustomDialog, { 
        title: 'Error',
        message: formatErrorMessage(error, "Failed to activate API key"),
        isModal: true 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleRenewClick = () => {
    setRenewDialogOpen(true);
  };

  const handleRenewDialogClose = () => {
    setRenewDialogOpen(false);
  };

  const handleRenewSuccess = async (result: { previousExpiresAt: string; newExpiresAt: string }) => {
    // 갱신 성공 시 상세 정보 다시 가져오기
    void fetchDetailData();
    
    // 성공 메시지 표시
    await dialogs.open(CustomDialog, { 
      title: 'Success',
      message: `API key has been renewed successfully.\n\nPrevious expiration: ${result.previousExpiresAt}\nNew expiration: ${result.newExpiresAt}`,
      isModal: true 
    });
    
    // 부모 컴포넌트에 성공 알림
    onRenewSuccess();
  };

  const displayData = detailData || apiKeyData;

  return (
    <>
      <FullscreenLoader open={detailLoading} />
      <Dialog 
        open={open} 
        onClose={onClose}
        maxWidth="xs" 
        fullWidth
        slotProps={{
          paper: { sx: { borderRadius: 2 } }
        }}
      >
        <DialogTitle sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          pb: 1
        }}>
          <Typography variant="h6" fontWeight={600}>
            API Key Detail Information
          </Typography>
          <IconButton onClick={onClose} size="small">
            <CloseIcon />
          </IconButton>
        </DialogTitle>

        <DialogContent dividers sx={{ p: 0, pt: 2 }}>
          <StyledContainer>
            <StyledInputArea sx={{ p: 3, pt: 0 }}>
              <TextField 
                fullWidth
                label="Name" 
                variant="standard"
                margin="normal" 
                value={displayData?.name || ''} 
                slotProps={{ input: { readOnly: true } }} 
              />

              <TextField 
                fullWidth
                label="API Key" 
                variant="standard"
                margin="normal" 
                value={displayData?.maskedApiKey || ''} 
                slotProps={{ 
                  input: { 
                    readOnly: true,
                    style: { fontFamily: 'monospace', fontSize: '14px' }
                  } 
                }} 
              />

              <Box sx={{ mt: 2, mb: 1 }}>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  Status
                </Typography>
                {getStatusChip(displayData?.isActive)}
              </Box>

              <Box sx={{ mt: 2, mb: 1 }}>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  Role
                </Typography>
                {getRoleChip(displayData?.role)}
              </Box>

              <TextField 
                fullWidth
                label="Description" 
                variant="standard"
                margin="normal" 
                value={displayData?.description || 'No description provided'} 
                slotProps={{ input: { readOnly: true } }} 
              />

              <TextField 
                fullWidth 
                label="Created At" 
                variant="standard" 
                margin="normal" 
                value={displayData?.createdAt || ''} 
                slotProps={{ input: { readOnly: true } }} 
              />

              <TextField 
                fullWidth 
                label="Expires At" 
                variant="standard" 
                margin="normal" 
                value={displayData?.expiresAt || ''} 
                slotProps={{ input: { readOnly: true } }} 
              />

              <TextField 
                fullWidth 
                label="Last Used At" 
                variant="standard" 
                margin="normal" 
                value={displayData?.lastUsedAt || 'Never used'} 
                slotProps={{ input: { readOnly: true } }} 
              />

              {displayData?.updatedAt && (
                <TextField 
                  fullWidth 
                  label="Updated At" 
                  variant="standard" 
                  margin="normal" 
                  value={displayData.updatedAt} 
                  slotProps={{ input: { readOnly: true } }} 
                />
              )}
            </StyledInputArea>
          </StyledContainer>

          {/* Status-based alerts */}
          <Box sx={{ px: 3, pb: 1 }}>
            {displayData?.isActive && (
              <Alert severity="info" sx={{ mt: 1 }}>
                This API key is currently active.
              </Alert>
            )}

            {!displayData?.isActive && (
              <Alert severity="warning" sx={{ mt: 1 }}>
                This API key has been deactivated.
              </Alert>
            )}
          </Box>
        </DialogContent>

        <DialogActions sx={{ px: 3, py: 2 }}>
          {/* AdminDetailPage와 동일한 버튼 정렬 패턴 */}
          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, width: '100%' }}>
            <Button variant="outlined" color="primary" onClick={onClose}>
              Close
            </Button>
            {displayData?.isActive ? (
              <>
                <Button
                  onClick={handleRenewClick}
                  variant="contained"
                  color="primary"
                  disabled={loading}
                >
                  Renew
                </Button>
                <Button
                  onClick={handleDeactivate}
                  variant="contained"
                  color="error"
                  disabled={loading}
                >
                  {loading ? 'Deactivating...' : 'Deactivate'}
                </Button>
              </>
            ) : (
              <Button
                onClick={handleActivate}
                variant="contained"
                color="success"
                disabled={loading}
              >
                {loading ? 'Activating...' : 'Activate'}
              </Button>
            )}
          </Box>
        </DialogActions>
      </Dialog>

      {/* Renew API Key Dialog */}
      <RenewApiKeyDialog
        open={renewDialogOpen}
        onClose={handleRenewDialogClose}
        apiKeyData={{
          id: apiKeyData.id,
          name: displayData?.name || apiKeyData.name,
          expiresAt: displayData?.expiresAt || apiKeyData.expiresAt,
        }}
        onRenewSuccess={handleRenewSuccess}
      />
    </>
  );
};

export default ApiKeyDetailDialog;
