import { Box, Link, styled, Tooltip, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchZkpCredentialSchemaList } from '../../../apis/zkp-schema-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type ZkpCredentialSchemaRow = {
    id: string | number;
    schemaId: string;
    name: string;
    version: string;
    tag: string;
    createdAt: string;
    updatedAt: string;
};

const StyledContainer = styled(Box)(({ theme }) => ({
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
}));

const StyledSubTitle = styled(Typography)({
    textAlign: 'left',
    fontSize: '24px',
    fontWeight: 700,
});

const truncateString = (str: string, maxLength: number) => {
    if (str.length <= maxLength) return str;
    return str.substring(0, maxLength) + '...';
};

const renderCellWithTooltip = (value: string, maxLength: number, onClick?: () => void) => {
    if (!value) return null;

    const isTruncated = value.length > maxLength;
    const displayValue = isTruncated ? truncateString(value, maxLength) : value;
    const content = onClick ? (
        <Link
            component="button"
            variant="body2"
            onClick={onClick}
            sx={{ cursor: 'pointer', color: 'primary.main', textAlign: 'left', display: 'inline-block', maxWidth: '100%' }}
        >
            {displayValue}
        </Link>
    ) : (
        <span style={{ display: 'inline-block', maxWidth: '100%' }}>{displayValue}</span>
    );

    return isTruncated ? (
        <Tooltip title={value} arrow placement="top">
            {content}
        </Tooltip>
    ) : content;
};

const ZkpCredentialSchemaListPage = () => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<ZkpCredentialSchemaRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('schemaId');
    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchZkpCredentialSchemaList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error('Failed to fetch ZKP Credential Schema List', err);
            navigate('/error', { state: { message: formatErrorMessage(err, 'Failed to retrieve ZKP Credential Schema List') } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const handleRefresh = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchZkpCredentialSchemaList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error('Failed to fetch ZKP Credential Schema List', err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve ZKP Credential Schema List'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
        // dialogs omitted: useDialogs() returns a new reference whenever a dialog opens or closes.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [paginationModel.pageSize, selectedSearch, searchText]);

    const handleSearch = useCallback(async (field: string, text: string) => {
        const trimmed = text.trim();
        if (!trimmed) return;

        setSelectedSearch(field);
        setSearchText(trimmed);
        setPaginationModel((prev) => ({ ...prev, page: 0 }));
    }, []);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return (
        <>
            <FullscreenLoader open={loading} />
            <StyledContainer>
                <StyledSubTitle>ZKP Credential Schema List</StyledSubTitle>
                <CustomDataGrid
                    rows={rows}
                    columns={[
                        {
                            field: 'schemaId',
                            headerName: 'Schema ID',
                            width: 350,
                            renderCell: (params) => renderCellWithTooltip(params.value, 50, () => navigate(`/schemas/zkp-credential-schema-list/${params.row.id}`)),
                        },
                        {
                            field: 'name',
                            headerName: 'Name',
                            width: 200,
                            renderCell: (params) => renderCellWithTooltip(params.value, 25),
                        },
                        { field: 'version', headerName: 'Version', width: 100, align: 'center', headerAlign: 'center' },
                        {
                            field: 'tag',
                            headerName: 'Tag',
                            width: 150,
                            renderCell: (params) => renderCellWithTooltip(params.value, 20),
                        },
                        { field: 'createdAt', headerName: 'Created At', width: 170, align: 'center', headerAlign: 'center' },
                    ]}
                    selectedRow={selectedRow}
                    setSelectedRow={setSelectedRow}
                    paginationMode="server"
                    totalRows={totalRows}
                    paginationModel={paginationModel}
                    setPaginationModel={setPaginationModel}
                    enableSearch={true}
                    searchText={searchText}
                    setSearchText={setSearchText}
                    selectedSearch={selectedSearch}
                    setSelectedSearch={setSelectedSearch}
                    searchOptions={[
                        { value: 'schemaId', label: 'Schema ID' },
                        { value: 'name', label: 'Name' },
                        { value: 'version', label: 'Version' },
                        { value: 'tag', label: 'Tag' },
                    ]}
                    onSearch={handleSearch}
                    onRefresh={handleRefresh}
                />
            </StyledContainer>
        </>
    );
};

export default ZkpCredentialSchemaListPage;
