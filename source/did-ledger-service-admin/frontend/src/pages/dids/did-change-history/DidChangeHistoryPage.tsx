import { Box, Chip, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { fetchDidDocumentStatusHistoryList } from '../../../apis/did-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type DidChangeHistoryRow = {
  id: string | number;
  did: string;
  version: number;
  fromStatus: string | null;
  toStatus: string;
  reason: string | null;
  changedAt: string;
  createdAt: string;
  updatedAt: string;
};

const DidChangeHistoryPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<DidChangeHistoryRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('did');

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
            const response = await fetchDidDocumentStatusHistoryList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch DID Change History ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve DID Change History") } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchDidDocumentStatusHistoryList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error("Failed to fetch DID Change History ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve DID Change History'),
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
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        }, []
    );

    // Get status color for Chip component
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

    // Get custom chip style for specific status colors
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

    // Get localized status label
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

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
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
        <FullscreenLoader open={loading} />
        <StyledContainer>
          <StyledSubTitle>DID Change History</StyledSubTitle>
          <CustomDataGrid 
              rows={rows} 
              columns={[
                  { 
                      field: 'did', 
                      headerName: "DID", 
                      width: 300,
                      renderCell: (params) => (
                          <div style={{ textAlign: 'left', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                              {params.value}
                          </div>
                      )
                  },
                  { 
                      field: 'version', 
                      headerName: "Version", 
                      width: 100,
                      align: 'center',
                      headerAlign: 'center'
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
                                  label={getStatusLabel(params.value)}
                                  size="small"
                                  variant="filled"
                                  sx={getChipStyle(params.value)}
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
                              label={getStatusLabel(params.value)}
                              size="small"
                              variant="filled"
                              sx={getChipStyle(params.value)}
                          />
                      )
                  },
                  { 
                      field: 'reason', 
                      headerName: "Reason", 
                      width: 200,
                      renderCell: (params) => params.value || '-'
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
                  { value: 'did', label: 'DID' },
                  { value: 'version', label: 'Version' },
                  { value: 'fromStatus', label: 'From Status' },
                  { value: 'toStatus', label: 'To Status' },
                  { value: 'reason', label: 'Reason' },
              ]}
              selectableFields={[
                  {
                      field: 'fromStatus',
                      options: [
                          { value: 'ACTIVATED', label: 'Activated' },
                          { value: 'DEACTIVATED', label: 'Deactivated' },
                          { value: 'REVOKED', label: 'Revoked' },
                          { value: 'TERMINATED', label: 'Terminated' },
                      ]
                  },
                  {
                      field: 'toStatus',
                      options: [
                          { value: 'ACTIVATED', label: 'Activated' },
                          { value: 'DEACTIVATED', label: 'Deactivated' },
                          { value: 'REVOKED', label: 'Revoked' },
                          { value: 'TERMINATED', label: 'Terminated' },
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

export default DidChangeHistoryPage