import { Close, DataObject, Visibility } from '@mui/icons-material';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid2,
    IconButton,
    Paper,
    Stack,
    styled,
    TextField,
    Typography,
} from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { getZkpCredentialSchemaDetail } from '../../../apis/zkp-schema-api';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type BasicInfo = {
    id: number;
    schemaId: string;
    name: string;
    version: string;
    tag: string;
    attrNames: string;
    attrTypes: string;
    createdAt: string;
    updatedAt: string;
};

type DetailData = {
    basicInfo: BasicInfo;
    parsedSchema: string;
};

const LIST_PATH = '/schemas/zkp-credential-schema-list';

const StyledContainer = styled(Box)(({ theme }) => ({
    maxWidth: 1000,
    margin: 'auto',
    marginTop: theme.spacing(2),
    padding: theme.spacing(3),
    [theme.breakpoints.down('md')]: {
        padding: theme.spacing(2),
        marginTop: theme.spacing(1),
    },
}));

const StyledSubTitle = styled(Typography)(({ theme }) => ({
    fontSize: '24px',
    fontWeight: 600,
    marginBottom: theme.spacing(4),
    color: theme.palette.text.primary,
    [theme.breakpoints.down('md')]: {
        fontSize: '20px',
        marginBottom: theme.spacing(3),
    },
}));

const StyledSectionTitle = styled(Box)(({ theme }) => ({
    display: 'flex',
    alignItems: 'center',
    gap: theme.spacing(1),
    marginBottom: theme.spacing(3),
    paddingBottom: theme.spacing(1),
    borderBottom: '2px solid #1976d2',
}));

const StyledSection = styled(Box)(({ theme }) => ({
    marginBottom: theme.spacing(5),
    backgroundColor: '#ffffff',
    borderRadius: theme.spacing(1),
    padding: theme.spacing(3),
    boxShadow: '0px 2px 8px 0px rgba(0, 0, 0, 0.1)',
    border: '1px solid #e0e0e0',
}));

const formatAttributeNames = (attrNames?: string | null) => {
    if (!attrNames) return '';
    try {
        const parsed = JSON.parse(attrNames);
        return Array.isArray(parsed) ? parsed.join(', ') : attrNames;
    } catch {
        return attrNames;
    }
};

const ReadOnlyField = ({ label, value, multiline }: { label: string; value?: string | null; multiline?: boolean }) => (
    <TextField
        fullWidth
        label={label}
        variant="outlined"
        multiline={multiline}
        value={value || ''}
        slotProps={{ input: { readOnly: true } }}
        sx={{ '& .MuiOutlinedInput-root': { backgroundColor: '#f8f9fa' } }}
    />
);

const ZkpCredentialSchemaDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [detail, setDetail] = useState<DetailData | null>(null);
    const [jsonDialogOpen, setJsonDialogOpen] = useState<boolean>(false);

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Invalid Path.',
                    isModal: true,
                }, {
                    onClose: async () => navigate(LIST_PATH, { replace: true }),
                });
                return;
            }

            setIsLoading(true);
            try {
                const { data } = await getZkpCredentialSchemaDetail(numericId);
                setDetail(data);
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch ZKP Credential Schema detail information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, 'Failed to fetch ZKP Credential Schema Detail') } });
            }
        };

        fetchData();
        // dialogs omitted: useDialogs() returns a new reference whenever a dialog opens or closes.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [numericId, navigate]);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">ZKP Credential Schema List</Typography>
            <StyledContainer>
                <StyledSubTitle>ZKP Credential Schema Detail Information</StyledSubTitle>

                {detail && (
                    <Stack spacing={5}>
                        <StyledSection>
                            <StyledSectionTitle>
                                <Typography variant="h6" fontWeight="600" color="primary">
                                    Basic Information
                                </Typography>
                            </StyledSectionTitle>

                            <Grid2 container spacing={2}>
                                <Grid2 size={12}>
                                    <ReadOnlyField label="Schema ID" value={detail.basicInfo.schemaId} />
                                </Grid2>
                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <ReadOnlyField label="Name" value={detail.basicInfo.name} />
                                </Grid2>
                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <ReadOnlyField label="Version" value={detail.basicInfo.version} />
                                </Grid2>
                                <Grid2 size={12}>
                                    <ReadOnlyField label="Tag" value={detail.basicInfo.tag} />
                                </Grid2>
                                <Grid2 size={12}>
                                    <ReadOnlyField label="Attribute Names" value={formatAttributeNames(detail.basicInfo.attrNames)} multiline />
                                </Grid2>
                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <ReadOnlyField label="Created At" value={detail.basicInfo.createdAt} />
                                </Grid2>
                                <Grid2 size={{ xs: 12, sm: 6 }}>
                                    <ReadOnlyField label="Updated At" value={detail.basicInfo.updatedAt} />
                                </Grid2>
                            </Grid2>

                            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                                <Button
                                    variant="outlined"
                                    startIcon={<Visibility />}
                                    size="large"
                                    onClick={() => setJsonDialogOpen(true)}
                                    sx={{ fontWeight: 500 }}
                                >
                                    View ZKP Credential Schema JSON
                                </Button>
                            </Box>
                        </StyledSection>

                        <Box sx={{ display: 'flex', justifyContent: 'center', pt: 2 }}>
                            <Button
                                variant="outlined"
                                color="primary"
                                size="large"
                                onClick={() => navigate(LIST_PATH)}
                                sx={{ minWidth: 120, height: 48, borderRadius: 2, fontWeight: 600 }}
                            >
                                Back
                            </Button>
                        </Box>
                    </Stack>
                )}

                <Dialog
                    open={jsonDialogOpen}
                    onClose={() => setJsonDialogOpen(false)}
                    maxWidth="md"
                    fullWidth
                    slotProps={{ paper: { sx: { borderRadius: 2, maxHeight: '90vh' } } }}
                >
                    <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', pb: 1, borderBottom: '1px solid #e0e0e0' }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                            <DataObject color="primary" />
                            <Typography variant="h6" fontWeight="600">
                                ZKP Credential Schema JSON
                            </Typography>
                        </Box>
                        <IconButton onClick={() => setJsonDialogOpen(false)} size="small">
                            <Close />
                        </IconButton>
                    </DialogTitle>

                    <DialogContent sx={{ pt: 3 }}>
                        {detail && (
                            <Paper sx={{ mt: 2, p: 2, backgroundColor: '#f8f9fa', maxHeight: 400, overflow: 'auto', border: '1px solid #e0e0e0' }}>
                                <pre style={{ fontSize: '12px', whiteSpace: 'pre-wrap', wordBreak: 'break-all', margin: 0, fontFamily: 'Consolas, Monaco, "Courier New", monospace', lineHeight: 1.5, color: '#333' }}>
                                    {detail.parsedSchema}
                                </pre>
                            </Paper>
                        )}
                    </DialogContent>

                    <DialogActions sx={{ p: 2, borderTop: '1px solid #e0e0e0', justifyContent: 'center' }}>
                        <Button onClick={() => setJsonDialogOpen(false)} variant="contained" sx={{ fontWeight: 600 }}>
                            Close
                        </Button>
                    </DialogActions>
                </Dialog>
            </StyledContainer>
        </>
    );
};

export default ZkpCredentialSchemaDetailPage;
