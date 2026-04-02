import {Box, Chip, styled, Typography} from '@mui/material';
import {GridPaginationModel} from '@mui/x-data-grid';
import {useDialogs} from '@toolpad/core';
import React, {useCallback, useEffect, useMemo, useState} from 'react'
import {useNavigate} from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import {fetchVcStatusHistoryList} from '../../../apis/vc-metadata-api';
import {formatErrorMessage} from '../../../utils/error-handler';

type Props = {}

type VcStatusHistoryRow = {
    id: string | number;
    vcId: string;
    fromStatus: string | null;
    toStatus: string;
    changedAt: string;
    createdAt: string;
    updatedAt: string;
};

const VcChangeHistoryPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<VcStatusHistoryRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('vcId');

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchVcStatusHistoryList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch VC Status History ", err);
            navigate('/error', {state: {message: formatErrorMessage(err, "Failed to retrieve VC Status History")}});
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchVcStatusHistoryList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({...prev, page: 0}));
        } catch (err) {
            console.error("Failed to fetch VC Status History ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve VC Status History'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const handleSearch = useCallback(
        async (field: string, text: string) => {
            const trimmed = text.trim();
            console.log(trimmed);
            if (!trimmed) return;

            setSelectedSearch(field);
            setSearchText(trimmed);
            setPaginationModel((prev) => ({...prev, page: 0}));
        }, []
    );

    // Get status color for VC Status (based on VcMetadataListPage)
    const getVcStatusColor = (status: string) => {
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

    // Get custom chip style for VC status colors (based on VcMetadataListPage)
    const getVcStatusChipStyle = (status: string) => {
        switch (status) {
            case 'ACTIVE':
                return {backgroundColor: '#4caf50', color: 'white'};
            case 'INACTIVE':
                return {backgroundColor: '#ffeb3b', color: 'black'};
            case 'REVOKED':
                return {backgroundColor: '#f44336', color: 'white'};
            case 'EXPIRED':
                return {backgroundColor: '#9e9e9e', color: 'white'};
            default:
                return {};
        }
    };

    // Get localized VC status label (based on VcMetadataListPage)
    const getVcStatusLabel = (status: string) => {
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

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const StyledContainer = useMemo(() => styled(Box)(({theme}) => ({
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

    return (
        <>
            <FullscreenLoader open={loading}/>
            <StyledContainer>
                <StyledSubTitle>VC Status History</StyledSubTitle>
                <CustomDataGrid
                    rows={rows}
                    columns={[
                        {
                            field: 'vcId',
                            headerName: "VC ID",
                            width: 350,
                            renderCell: (params) => (
                                <div style={{
                                    textAlign: 'left',
                                    whiteSpace: 'nowrap',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis'
                                }}>
                                    {params.value}
                                </div>
                            )
                        },
                        {
                            field: 'fromStatus',
                            headerName: "From Status",
                            width: 150,
                            align: 'center',
                            headerAlign: 'center',
                            renderCell: (params) => (
                                params.value ? (
                                    <Chip
                                        label={getVcStatusLabel(params.value)}
                                        size="small"
                                        variant="filled"
                                        sx={getVcStatusChipStyle(params.value)}
                                    />
                                ) : '-'
                            )
                        },
                        {
                            field: 'toStatus',
                            headerName: "To Status",
                            width: 150,
                            align: 'center',
                            headerAlign: 'center',
                            renderCell: (params) => (
                                <Chip
                                    label={getVcStatusLabel(params.value)}
                                    size="small"
                                    variant="filled"
                                    sx={getVcStatusChipStyle(params.value)}
                                />
                            )
                        },
                        {
                            field: 'changedAt',
                            headerName: "Changed At",
                            width: 180,
                            align: 'center',
                            headerAlign: 'center'
                        },
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
                        {value: 'vcId', label: 'VC ID'},
                        {value: 'fromStatus', label: 'From Status'},
                        {value: 'toStatus', label: 'To Status'},
                    ]}
                    selectableFields={[
                        {
                            field: 'fromStatus',
                            options: [
                                {value: 'ACTIVE', label: 'Active'},
                                {value: 'INACTIVE', label: 'Inactive'},
                                {value: 'REVOKED', label: 'Revoked'},
                                {value: 'EXPIRED', label: 'Expired'},
                            ]
                        },
                        {
                            field: 'toStatus',
                            options: [
                                {value: 'ACTIVE', label: 'Active'},
                                {value: 'INACTIVE', label: 'Inactive'},
                                {value: 'REVOKED', label: 'Revoked'},
                                {value: 'EXPIRED', label: 'Expired'},
                            ]
                        },
                    ]}
                    onSearch={handleSearch}
                    onRefresh={getData}
                />
            </StyledContainer>
        </>

    )
}

export default VcChangeHistoryPage