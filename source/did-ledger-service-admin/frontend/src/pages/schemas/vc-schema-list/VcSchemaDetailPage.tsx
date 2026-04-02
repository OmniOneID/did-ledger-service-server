import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router';
import { 
    Box, 
    Button, 
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
    DataObject,
    Visibility,
    Close
} from '@mui/icons-material';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { getVcSchemaDetail } from '../../../apis/vc-schema-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type VcSchemaBasicInfo = {
    id: number;
    schemaId: string;
    title: string;
    version: string;
    description: string;
    schema: string;
    did: string;
    createdAt: string;
    updatedAt: string;
};

type VcSchemaDetailData = {
    basicInfo: VcSchemaBasicInfo;
    parsedSchema: string;
};

const VcSchemaDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [vcSchemaData, setVcSchemaData] = useState<VcSchemaDetailData | null>(null);
    const [schemaDialogOpen, setSchemaDialogOpen] = useState<boolean>(false);

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async () => navigate('/schemas/vc-schema-list', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getVcSchemaDetail(numericId);
                setVcSchemaData(data);
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch VC Schema detail information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch VC Schema Detail") } });
            }
        };

        fetchData();
    }, [numericId, navigate, dialogs]);

    const handleViewSchema = () => {
        setSchemaDialogOpen(true);
    };

    const handleCloseSchemaDialog = () => {
        setSchemaDialogOpen(false);
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
            <Typography variant="h4">VC Schema List</Typography>
            <StyledContainer>
                <StyledSubTitle>VC Schema Detail Information</StyledSubTitle>
                
                {vcSchemaData && (
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
                                        label="Schema ID" 
                                        variant="outlined"
                                        value={vcSchemaData.basicInfo.schemaId || ''} 
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
                                        label="Title" 
                                        variant="outlined"
                                        value={vcSchemaData.basicInfo.title || ''} 
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
                                        value={vcSchemaData.basicInfo.version || ''} 
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
                                        label="Description" 
                                        variant="outlined"
                                        multiline
                                        rows={3}
                                        value={vcSchemaData.basicInfo.description || ''} 
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
                                        label="Publisher DID" 
                                        variant="outlined"
                                        value={vcSchemaData.basicInfo.did || ''} 
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
                                        value={vcSchemaData.basicInfo.createdAt || ''} 
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
                                        label="Updated At" 
                                        variant="outlined"
                                        value={vcSchemaData.basicInfo.updatedAt || ''} 
                                        slotProps={{ input: { readOnly: true } }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
                                                backgroundColor: '#f8f9fa',
                                            }
                                        }}
                                    />
                                </Grid2>
                            </StyledInfoGrid>

                            {/* View VC Schema JSON Button */}
                            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                                <Button
                                    variant="outlined"
                                    startIcon={<Visibility />}
                                    size="large"
                                    onClick={handleViewSchema}
                                    sx={{ fontWeight: 500 }}
                                >
                                    View VC Schema JSON
                                </Button>
                            </Box>
                        </StyledSection>

                        {/* Action Buttons */}
                        <Box sx={{ display: 'flex', justifyContent: 'center', pt: 2 }}>
                            <Button
                                variant="outlined"
                                color="primary"
                                size="large"
                                onClick={() => navigate('/schemas/vc-schema-list')}
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

                {/* VC Schema JSON Popup Dialog */}
                <Dialog
                    open={schemaDialogOpen}
                    onClose={handleCloseSchemaDialog}
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
                                VC Schema JSON
                            </Typography>
                        </Box>
                        <IconButton onClick={handleCloseSchemaDialog} size="small">
                            <Close />
                        </IconButton>
                    </DialogTitle>
                    
                    <DialogContent sx={{ pt: 3 }}>
                        {vcSchemaData && (
                            <Box>
                                <Typography variant="subtitle1" sx={{ mt: 2, mb: 2, fontWeight: 600 }}>
                                    Formatted VC Schema
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
                                        {vcSchemaData.parsedSchema}
                                    </pre>
                                </Paper>
                            </Box>
                        )}
                    </DialogContent>
                    
                    <DialogActions sx={{ p: 2, borderTop: '1px solid #e0e0e0', justifyContent: 'center' }}>
                        <Button 
                            onClick={handleCloseSchemaDialog} 
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

export default VcSchemaDetailPage
