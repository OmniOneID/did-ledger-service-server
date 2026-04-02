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
    AccountTree,
    Visibility,
    Close
} from '@mui/icons-material';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { getDidDetail } from '../../../apis/did-api';
import { formatErrorMessage } from '../../../utils/error-handler';
import { roles } from '../../../constants/roles';

type DidBasicInfo = {
    id: number;
    did: string;
    version: number;
    role: string;
    status: string;
    terminatedTime: string | null;
    createdAt: string;
    updatedAt: string;
};

type DidDocumentHistory = {
    id: number;
    version: number;
    document: string;
    controller: string;
    deactivated: boolean;
    isRevoked: boolean;
    createdAt: string;
    updatedAt: string;
    revokedAt: string | null;
};

type DidStatusHistory = {
    id: number;
    version: number;
    fromStatus: string | null;
    toStatus: string;
    reason: string | null;
    changedAt: string;
    createdAt: string;
    updatedAt: string;
};

type DidDetailData = {
    basicInfo: DidBasicInfo;
    documentHistory: DidDocumentHistory[];
    statusHistory: DidStatusHistory[];
};

const DidDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [didData, setDidData] = useState<DidDetailData | null>(null);
    const [documentDialogOpen, setDocumentDialogOpen] = useState<boolean>(false);
    const [selectedDocument, setSelectedDocument] = useState<DidDocumentHistory | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async () => navigate('/dids/did-list', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getDidDetail(numericId);
                setDidData(data);
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch DID detail information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch DID Detail") } });
            }
        };

        fetchData();
    }, [numericId, navigate, dialogs]);

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'ACTIVATED':
                return 'success';
            case 'DEACTIVATED':
                return 'warning';
            case 'REVOKED':
                return 'warning';
            case 'TERMINATED':
                return 'error';
            default:
                return 'default';
        }
    };

    const getChipStyle = (status: string) => {
        switch (status) {
            case 'ACTIVATED':
                return { backgroundColor: '#4caf50', color: 'white' };
            case 'DEACTIVATED':
                return { backgroundColor: '#ffeb3b', color: 'black' };
            case 'REVOKED':
                return { backgroundColor: '#ff9800', color: 'white' };
            case 'TERMINATED':
                return { backgroundColor: '#f44336', color: 'white' };
            default:
                return {};
        }
    };

    const getStatusLabel = (status: string) => {
        switch (status) {
            case 'ACTIVATED':
                return 'Activated';
            case 'DEACTIVATED':
                return 'Deactivated';
            case 'REVOKED':
                return 'Revoked';
            case 'TERMINATED':
                return 'Terminated';
            default:
                return status;
        }
    };

    const getRoleLabel = (role: string) => {
        const roleInfo = roles.find(r => r.value === role);
        return roleInfo ? roleInfo.label : role;
    };

    const handleViewDocument = (document: DidDocumentHistory) => {
        setSelectedDocument(document);
        setDocumentDialogOpen(true);
    };

    const handleCloseDocumentDialog = () => {
        setDocumentDialogOpen(false);
        setSelectedDocument(null);
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
            <Typography variant="h4">DID Management</Typography>
            <StyledContainer>
                <StyledSubTitle>DID Detail Information</StyledSubTitle>
                
                {didData && (
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
                                        label="DID" 
                                        variant="outlined"
                                        value={didData.basicInfo.did || ''} 
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
                                        label="Version" 
                                        variant="outlined"
                                        value={didData.basicInfo.version || ''} 
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
                                        label="Role" 
                                        variant="outlined"
                                        value={getRoleLabel(didData.basicInfo.role) || ''} 
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
                                            label={getStatusLabel(didData.basicInfo.status)}
                                            variant="filled"
                                            size="medium"
                                            sx={{ 
                                                fontWeight: 600,
                                                minWidth: 80,
                                                height: 32,
                                                ...getChipStyle(didData.basicInfo.status)
                                            }}
                                        />
                                    </Box>
                                </Grid2>

                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <TextField 
                                        fullWidth 
                                        label="Created At" 
                                        variant="outlined"
                                        value={didData.basicInfo.createdAt || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>

                                {didData.basicInfo.terminatedTime && (
                                    <Grid2 size={12}>
                                        <TextField 
                                            fullWidth 
                                            label="Terminated At" 
                                            variant="outlined"
                                            value={didData.basicInfo.terminatedTime} 
                                            slotProps={{ input: { readOnly: true } }}
                                            sx={{
                                                '& .MuiOutlinedInput-root': {
                                                    backgroundColor: '#ffebee',
                                                }
                                            }}
                                        />
                                    </Grid2>
                                )}
                            </StyledInfoGrid>
                        </StyledSection>

                        {/* DID Document History Section */}
                        <StyledSection>
                            <StyledSectionTitle>
                                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                                    <Typography variant="h6" fontWeight="600" color="primary">
                                        DID Document History
                                    </Typography>
                                    <Chip 
                                        label={`${didData.documentHistory.length} Version${didData.documentHistory.length !== 1 ? 's' : ''}`}
                                        color="info"
                                        size="small"
                                        variant="outlined"
                                    />
                                </Box>
                            </StyledSectionTitle>
                            
                            {didData.documentHistory.length > 0 ? (
                                <Stack spacing={2}>
                                    {didData.documentHistory.map((doc) => (
                                        <Paper 
                                            key={doc.id} 
                                            sx={{ 
                                                border: '2px solid #e0e0e0',
                                                borderRadius: 2,
                                                overflow: 'hidden',
                                                boxShadow: '0px 2px 8px 0px rgba(0, 0, 0, 0.08)',
                                            }}
                                        >
                                            <Box sx={{ 
                                                p: 3,
                                                backgroundColor: doc.isRevoked ? '#fff3e0' : '#f3f9ff',
                                                borderBottom: '1px solid #e0e0e0',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'space-between'
                                            }}>
                                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                                    <Typography variant="h6" fontWeight="600" color="primary">
                                                        Version {doc.version}
                                                    </Typography>
                                                    <Chip 
                                                        label={doc.isRevoked ? 'Revoked' : doc.deactivated ? 'Deactivated' : 'Activated'}
                                                        size="small"
                                                        variant="filled"
                                                        sx={doc.isRevoked ? getChipStyle('REVOKED') : doc.deactivated ? getChipStyle('DEACTIVATED') : getChipStyle('ACTIVATED')}
                                                    />
                                                </Box>
                                            </Box>
                                            
                                            <Box sx={{ p: 3, backgroundColor: '#fafafa' }}>
                                                <Paper sx={{ 
                                                    p: 2, 
                                                    backgroundColor: '#fff',
                                                    border: '1px solid #e0e0e0',
                                                    borderRadius: 1,
                                                    width: '100%',
                                                    mb: 3
                                                }}>
                                                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
                                                        <Box sx={{ flex: '1 1 auto', minWidth: '200px' }}>
                                                            <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, mb: 0.5 }}>
                                                                Created At
                                                            </Typography>
                                                            <Typography variant="body2" color="text.primary" sx={{ fontWeight: 600 }}>
                                                                {doc.createdAt}
                                                            </Typography>
                                                        </Box>
                                                        {doc.isRevoked && doc.revokedAt && (
                                                            <Box sx={{ flex: '1 1 auto', minWidth: '200px' }}>
                                                                <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, mb: 0.5 }}>
                                                                    Revoked At
                                                                </Typography>
                                                                <Typography variant="body2" color="warning.main" sx={{ fontWeight: 600 }}>
                                                                    {doc.revokedAt}
                                                                </Typography>
                                                            </Box>
                                                        )}
                                                    </Box>
                                                </Paper>
                                                
                                                <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                                                    <Button
                                                        variant="outlined"
                                                        startIcon={<Visibility />}
                                                        size="medium"
                                                        onClick={() => handleViewDocument(doc)}
                                                        sx={{ fontWeight: 500 }}
                                                    >
                                                        View Document
                                                    </Button>
                                                </Box>
                                            </Box>
                                        </Paper>
                                    ))}
                                </Stack>
                            ) : (
                                <Paper sx={{ p: 4, textAlign: 'center', backgroundColor: '#f8f9fa' }}>
                                    <AccountTree sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
                                    <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 500 }}>
                                        No DID document history available
                                    </Typography>
                                </Paper>
                            )}
                        </StyledSection>

                        {/* Status Change History Section */}
                        <StyledSection>
                            <StyledSectionTitle>
                                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                                    <Typography variant="h6" fontWeight="600" color="primary">
                                        Status Change History
                                    </Typography>
                                    <Chip 
                                        label={`${didData.statusHistory.length} Change${didData.statusHistory.length !== 1 ? 's' : ''}`}
                                        color="info"
                                        size="small"
                                        variant="outlined"
                                    />
                                </Box>
                            </StyledSectionTitle>
                            
                            {didData.statusHistory.length > 0 ? (
                                <Stack spacing={1.5}>
                                    {didData.statusHistory.map((history) => {
                                        const getStatusColorIcon = (status: string) => {
                                            switch (status) {
                                                case 'ACTIVATED':
                                                    return '🟢';
                                                case 'DEACTIVATED':
                                                    return '🟡';
                                                case 'REVOKED':
                                                    return '🟠';
                                                case 'TERMINATED':
                                                    return '🔴';
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
                                                        {getStatusColorIcon(history.toStatus)} Version {history.version}:
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
                                                
                                                {history.reason && (
                                                    <Box sx={{ mt: 1.5, pl: 2, borderLeft: '3px solid #e0e0e0' }}>
                                                        <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
                                                            Reason: {history.reason}
                                                        </Typography>
                                                    </Box>
                                                )}
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
                                onClick={() => navigate('/dids/did-list')}
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

                {/* DID Document Popup Dialog */}
                <Dialog
                    open={documentDialogOpen}
                    onClose={handleCloseDocumentDialog}
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
                            <AccountTree color="primary" />
                            <Typography variant="h6" fontWeight="600">
                                DID Document - Version {selectedDocument?.version}
                            </Typography>
                        </Box>
                        <IconButton onClick={handleCloseDocumentDialog} size="small">
                            <Close />
                        </IconButton>
                    </DialogTitle>
                    
                    <DialogContent sx={{ pt: 3 }}>
                        {selectedDocument && (
                            <Box>
                                <Typography variant="subtitle1" sx={{ mt: 2, mb: 2, fontWeight: 600 }}>
                                    Document Information
                                </Typography>
                                <Box sx={{ mb: 3, p: 2, backgroundColor: '#f8f9fa', borderRadius: 1 }}>
                                    <Grid2 container spacing={2}>
                                        <Grid2 size={{ xs: 12, sm: 6 }}>
                                            <Typography variant="body2">
                                                <strong>Version:</strong> {selectedDocument.version}
                                            </Typography>
                                        </Grid2>
                                        <Grid2 size={{ xs: 12, sm: 6 }}>
                                            <Typography variant="body2">
                                                <strong>Created:</strong> {selectedDocument.createdAt}
                                            </Typography>
                                        </Grid2>
                                        <Grid2 size={{ xs: 12, sm: 6 }}>
                                            <Typography variant="body2">
                                                <strong>Status:</strong> {selectedDocument.isRevoked ? 'Revoked' : selectedDocument.deactivated ? 'Deactivated' : 'Activated'}
                                            </Typography>
                                        </Grid2>
                                        <Grid2 size={{ xs: 12, sm: 6 }}>
                                            <Typography variant="body2">
                                                <strong>Deactivated:</strong> {selectedDocument.deactivated ? 'Yes' : 'No'}
                                            </Typography>
                                        </Grid2>
                                        {selectedDocument.isRevoked && selectedDocument.revokedAt && (
                                            <Grid2 size={{ xs: 12, sm: 6 }}>
                                                <Typography variant="body2">
                                                    <strong>Revoked At:</strong> {selectedDocument.revokedAt}
                                                </Typography>
                                            </Grid2>
                                        )}
                                    </Grid2>
                                </Box>

                                <Typography variant="subtitle1" sx={{ mb: 2, fontWeight: 600 }}>
                                    DID Document (JSON)
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
                                        {JSON.stringify(JSON.parse(selectedDocument.document), null, 2)}
                                    </pre>
                                </Paper>
                            </Box>
                        )}
                    </DialogContent>
                    
                    <DialogActions sx={{ p: 2, borderTop: '1px solid #e0e0e0', justifyContent: 'center' }}>
                        <Button 
                            onClick={handleCloseDocumentDialog} 
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

export default DidDetailPage