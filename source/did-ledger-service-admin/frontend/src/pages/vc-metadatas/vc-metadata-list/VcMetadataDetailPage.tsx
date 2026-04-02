import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router';
import { 
    Box, 
    Button, 
    Chip, 
    styled, 
    TextField, 
    Typography, 
    Paper,
    Grid2,
    Stack,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    IconButton
} from '@mui/material';
import { 
    HistoryOutlined, 
    DataObject,
    Visibility,
    Close
} from '@mui/icons-material';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { getVcMetadataDetail } from '../../../apis/vc-metadata-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type VcMetadataBasicInfo = {
    id: number;
    vcId: string;
    issuerDid: string;
    subjectDid: string;
    vcSchema: string;
    issuanceDate: string;
    validFrom: string;
    validUntil: string;
    formatVersion: string;
    language: string;
    status: string;
    metadata: string;
    createdAt: string;
    updatedAt: string;
};

type VcStatusHistory = {
    id: number;
    vcId: string;
    fromStatus: string | null;
    toStatus: string;
    changedAt: string;
    createdAt: string;
    updatedAt: string;
};

type VcMetadataDetailData = {
    basicInfo: VcMetadataBasicInfo;
    parsedMetadata: string;
    statusHistory: VcStatusHistory[];
};

const VcMetadataDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [vcMetadataData, setVcMetadataData] = useState<VcMetadataDetailData | null>(null);
    const [metadataDialogOpen, setMetadataDialogOpen] = useState<boolean>(false);

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async () => navigate('/vc-metadatas/vc-metadata-list', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getVcMetadataDetail(numericId);
                setVcMetadataData(data);
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch VC Metadata detail information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch VC Metadata Detail") } });
            }
        };

        fetchData();
    }, [numericId, navigate, dialogs]);

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'ACTIVE':
                return 'success';
            case 'INACTIVE':
                return 'warning';
            case 'REVOKED':
                return 'error';
            case 'EXPIRED':
                return 'default';
            default:
                return 'default';
        }
    };

    const getChipStyle = (status: string) => {
        switch (status) {
            case 'ACTIVE':
                return { backgroundColor: '#4caf50', color: 'white' };
            case 'INACTIVE':
                return { backgroundColor: '#ffeb3b', color: 'black' };
            case 'REVOKED':
                return { backgroundColor: '#f44336', color: 'white' };
            case 'EXPIRED':
                return { backgroundColor: '#9e9e9e', color: 'white' };
            default:
                return {};
        }
    };

    const getStatusLabel = (status: string) => {
        switch (status) {
            case 'ACTIVE':
                return 'Active';
            case 'INACTIVE':
                return 'Inactive';
            case 'REVOKED':
                return 'Revoked';
            case 'EXPIRED':
                return 'Expired';
            default:
                return status;
        }
    };

    const handleViewMetadata = () => {
        setMetadataDialogOpen(true);
    };

    const handleCloseMetadataDialog = () => {
        setMetadataDialogOpen(false);
    };

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        maxWidth: 1000,
        margin: 'auto',
        marginTop: theme.spacing(2),
        padding: theme.spacing(3),
        [theme.breakpoints.down('md')]: {
            padding: theme.spacing(2),
            marginTop: theme.spacing(1),
        },
    })), []);

    const StyledSubTitle = useMemo(() => styled(Typography)(({ theme }) => ({
        fontSize: '24px',
        fontWeight: 600,
        marginBottom: theme.spacing(4),
        color: theme.palette.text.primary,
        [theme.breakpoints.down('md')]: {
            fontSize: '20px',
            marginBottom: theme.spacing(3),
        },
    })), []);

    const StyledSectionTitle = useMemo(() => styled(Box)(({ theme }) => ({
        display: 'flex',
        alignItems: 'center',
        gap: theme.spacing(1),
        marginBottom: theme.spacing(3),
        paddingBottom: theme.spacing(1),
        borderBottom: '2px solid #1976d2',
    })), []);

    const StyledInfoGrid = useMemo(() => styled(Grid2)(({ theme }) => ({
        '& .MuiTextField-root': {
            marginBottom: theme.spacing(2),
        },
    })), []);

    const StyledSection = useMemo(() => styled(Box)(({ theme }) => ({
        marginBottom: theme.spacing(5),
        backgroundColor: '#ffffff',
        borderRadius: theme.spacing(1),
        padding: theme.spacing(3),
        boxShadow: '0px 2px 8px 0px rgba(0, 0, 0, 0.1)',
        border: '1px solid #e0e0e0',
    })), []);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">VC Metadata Management</Typography>
            <StyledContainer>
                <StyledSubTitle>VC Metadata Detail Information</StyledSubTitle>
                
                {vcMetadataData && (
                    <Stack spacing={5}>
                        {/* Basic Information Section */}
                        <StyledSection>
                            <StyledSectionTitle>
                                <Typography variant="h6" fontWeight="600" color="primary">
                                    Basic Information
                                </Typography>
                            </StyledSectionTitle>
                            
                            <StyledInfoGrid container spacing={2}>
                                <Grid2 size={12}>
                                    <TextField 
                                        fullWidth
                                        label="VC ID" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.vcId || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={12}>
                                    <TextField 
                                        fullWidth
                                        label="Issuer DID" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.issuerDid || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={12}>
                                    <TextField 
                                        fullWidth
                                        label="Subject DID" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.subjectDid || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={12}>
                                    <TextField 
                                        fullWidth
                                        label="Schema URL" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.vcSchema || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <Box>
                                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1, fontWeight: 500 }}>
                                            Status
                                        </Typography>
                                        <Chip 
                                            label={getStatusLabel(vcMetadataData.basicInfo.status)}
                                            variant="filled"
                                            size="medium"
                                            sx={{ 
                                                fontWeight: 600,
                                                minWidth: 80,
                                                height: 32,
                                                ...getChipStyle(vcMetadataData.basicInfo.status)
                                            }}
                                        />
                                    </Box>
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth
                                        label="Format Version" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.formatVersion || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth
                                        label="Language" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.language || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth 
                                        label="Issuance Date" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.issuanceDate || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth 
                                        label="Valid From" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.validFrom || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth 
                                        label="Valid Until" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.validUntil || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth 
                                        label="Created At" 
                                        variant="outlined"
                                        value={vcMetadataData.basicInfo.createdAt || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>
                            </StyledInfoGrid>

                            {/* View VC Metadata JSON Button */}
                            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                                <Button
                                    variant="outlined"
                                    startIcon={<Visibility />}
                                    size="large"
                                    onClick={handleViewMetadata}
                                    sx={{ fontWeight: 500 }}
                                >
                                    View VC Metadata JSON
                                </Button>
                            </Box>
                        </StyledSection>

                        {/* Status Change History Section */}
                        <StyledSection>
                            <StyledSectionTitle>
                                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                                    <Typography variant="h6" fontWeight="600" color="primary">
                                        Status Change History
                                    </Typography>
                                    <Chip 
                                        label={`${vcMetadataData.statusHistory.length} Change${vcMetadataData.statusHistory.length !== 1 ? 's' : ''}`}
                                        color="info"
                                        size="small"
                                        variant="outlined"
                                    />
                                </Box>
                            </StyledSectionTitle>
                            
                            {vcMetadataData.statusHistory.length > 0 ? (
                                <Stack spacing={1.5}>
                                    {vcMetadataData.statusHistory.map((history) => {
                                        const getStatusColorIcon = (status: string) => {
                                            switch (status) {
                                                case 'ACTIVE':
                                                    return '🟢';
                                                case 'INACTIVE':
                                                    return '🟡';
                                                case 'REVOKED':
                                                    return '🔴';
                                                case 'EXPIRED':
                                                    return '⚫';
                                                default:
                                                    return '⚪';
                                            }
                                        };
                                        
                                        return (
                                            <Paper 
                                                key={history.id} 
                                                sx={{ 
                                                    p: 2.5, 
                                                    border: '1px solid #e0e0e0',
                                                    borderRadius: 1,
                                                    backgroundColor: '#fafafa',
                                                    transition: 'all 0.2s ease',
                                                    '&:hover': {
                                                        backgroundColor: '#f0f0f0',
                                                        boxShadow: '0px 2px 8px 0px rgba(0, 0, 0, 0.08)',
                                                    }
                                                }}
                                            >
                                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                                    <Typography variant="body1" sx={{ fontWeight: 600, color: 'text.primary' }}>
                                                        {getStatusColorIcon(history.toStatus)}
                                                    </Typography>
                                                    <Typography variant="body1" sx={{ color: 'text.primary' }}>
                                                        {history.fromStatus ? (
                                                            `${getStatusLabel(history.fromStatus)} → ${getStatusLabel(history.toStatus)}`
                                                        ) : (
                                                            `${getStatusLabel(history.toStatus)} (Registration)`
                                                        )}
                                                    </Typography>
                                                    <Typography variant="body2" sx={{ color: 'text.secondary', ml: 1 }}>
                                                        ({history.changedAt})
                                                    </Typography>
                                                </Box>
                                            </Paper>
                                        );
                                    })}
                                </Stack>
                            ) : (
                                <Paper sx={{ p: 4, textAlign: 'center', backgroundColor: '#f8f9fa' }}>
                                    <HistoryOutlined sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
                                    <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 500 }}>
                                        No status change history available
                                    </Typography>
                                </Paper>
                            )}
                        </StyledSection>

                        {/* Action Buttons */}
                        <Box sx={{ display: 'flex', justifyContent: 'center', pt: 2 }}>
                            <Button 
                                variant="outlined" 
                                color="primary" 
                                size="large"
                                onClick={() => navigate('/vc-metadatas/vc-metadata-list')}
                                sx={{
                                    minWidth: 120,
                                    height: 48,
                                    borderRadius: 2,
                                    fontWeight: 600
                                }}
                            >
                                Back
                            </Button>
                        </Box>
                    </Stack>
                )}

                {/* VC Metadata Popup Dialog */}
                <Dialog
                    open={metadataDialogOpen}
                    onClose={handleCloseMetadataDialog}
                    maxWidth="md"
                    fullWidth
                    slotProps={{
                        paper: {
                            sx: {
                                borderRadius: 2,
                                maxHeight: '90vh'
                            }
                        }
                    }}
                >
                    <DialogTitle sx={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        justifyContent: 'space-between',
                        pb: 1,
                        borderBottom: '1px solid #e0e0e0'
                    }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                            <DataObject color="primary" />
                            <Typography variant="h6" fontWeight="600">
                                VC Metadata JSON
                            </Typography>
                        </Box>
                        <IconButton onClick={handleCloseMetadataDialog} size="small">
                            <Close />
                        </IconButton>
                    </DialogTitle>
                    
                    <DialogContent sx={{ pt: 3 }}>
                        {vcMetadataData && (
                            <Box>
                                <Typography variant="subtitle1" sx={{ mt: 2, mb: 2, fontWeight: 600 }}>
                                    Formatted VC Metadata
                                </Typography>
                                <Paper sx={{ 
                                    p: 2, 
                                    backgroundColor: '#f8f9fa', 
                                    maxHeight: 400, 
                                    overflow: 'auto',
                                    border: '1px solid #e0e0e0'
                                }}>
                                    <pre style={{ 
                                        fontSize: '12px', 
                                        whiteSpace: 'pre-wrap', 
                                        wordBreak: 'break-all',
                                        margin: 0,
                                        fontFamily: 'Consolas, Monaco, "Courier New", monospace',
                                        lineHeight: 1.5,
                                        color: '#333'
                                    }}>
                                        {vcMetadataData.parsedMetadata}
                                    </pre>
                                </Paper>
                            </Box>
                        )}
                    </DialogContent>
                    
                    <DialogActions sx={{ p: 2, borderTop: '1px solid #e0e0e0', justifyContent: 'center' }}>
                        <Button 
                            onClick={handleCloseMetadataDialog} 
                            variant="contained"
                            sx={{ fontWeight: 600 }}
                        >
                            Close
                        </Button>
                    </DialogActions>
                </Dialog>
            </StyledContainer>
        </>
    )
}

export default VcMetadataDetailPage