import { Box, Chip, Link, styled, Typography, Tooltip } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { fetchVcMetadataList } from '../../../apis/vc-metadata-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type VcMetadataRow = {
  id: string | number;
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
  createdAt: string;
  updatedAt: string;
};

const VcMetadataListPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<VcMetadataRow[]>([]);
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
            const response = await fetchVcMetadataList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch VC Metadata List ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve VC Metadata List") } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchVcMetadataList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error("Failed to fetch VC Metadata List ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve VC Metadata List'),
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

    // Truncate long strings for display
    const truncateString = (str: string, maxLength: number) => {
        if (str.length <= maxLength) return str;
        return str.substring(0, maxLength) + '...';
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
          <StyledSubTitle>VC Metadata List</StyledSubTitle>
          <CustomDataGrid 
              rows={rows} 
              columns={[
                  { 
                      field: 'vcId', 
                      headerName: "VC ID", 
                      width: 300,
                      renderCell: (params) => (
                          <Tooltip title={params.value} arrow placement="top">
                              <Link 
                                  component="button"
                                  variant='body2'
                                  onClick={() => navigate(`/vc-metadatas/vc-metadata-list/${params.row.id}`)}
                                  sx={{ 
                                      cursor: 'pointer', 
                                      color: 'primary.main', 
                                      textAlign: 'left',
                                      display: 'block',
                                      overflow: 'hidden',
                                      textOverflow: 'ellipsis',
                                      whiteSpace: 'nowrap',
                                      maxWidth: '100%'
                                  }}
                              >
                                  {truncateString(params.value, 30)}
                              </Link>
                          </Tooltip>
                      ),
                  },
                  { 
                      field: 'status', 
                      headerName: "Status", 
                      width: 120,
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
                      field: 'issuerDid', 
                      headerName: "Issuer DID", 
                      width: 200,
                      renderCell: (params) => (
                          <Tooltip title={params.value} arrow placement="top">
                              <span style={{ 
                                  display: 'block',
                                  overflow: 'hidden',
                                  textOverflow: 'ellipsis',
                                  whiteSpace: 'nowrap',
                                  maxWidth: '100%'
                              }}>
                                  {truncateString(params.value, 20)}
                              </span>
                          </Tooltip>
                      )
                  },
                  { 
                      field: 'subjectDid', 
                      headerName: "Subject DID", 
                      width: 200,
                      renderCell: (params) => (
                          <Tooltip title={params.value} arrow placement="top">
                              <span style={{ 
                                  display: 'block',
                                  overflow: 'hidden',
                                  textOverflow: 'ellipsis',
                                  whiteSpace: 'nowrap',
                                  maxWidth: '100%'
                              }}>
                                  {truncateString(params.value, 20)}
                              </span>
                          </Tooltip>
                      )
                  },
                  { 
                      field: 'vcSchema', 
                      headerName: "Schema ID", 
                      width: 150,
                      renderCell: (params) => (
                          <Tooltip title={params.value} arrow placement="top">
                              <span style={{ 
                                  display: 'block',
                                  overflow: 'hidden',
                                  textOverflow: 'ellipsis',
                                  whiteSpace: 'nowrap',
                                  maxWidth: '100%'
                              }}>
                                  {truncateString(params.value, 15)}
                              </span>
                          </Tooltip>
                      )
                  },
                  { 
                      field: 'issuanceDate', 
                      headerName: "Issuance Date", 
                      width: 180,
                      align: 'center',
                      headerAlign: 'center'
                  },
                  { 
                      field: 'validUntil', 
                      headerName: "Valid Until", 
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
                  { value: 'vcId', label: 'VC ID' },
                  { value: 'issuerDid', label: 'Issuer DID' },
                  { value: 'subjectDid', label: 'Subject DID' },
                  { value: 'vcSchema', label: 'Schema ID' },
                  { value: 'status', label: 'Status' },
              ]}
              selectableFields={[
                  {
                      field: 'status',
                      options: [
                          { value: 'ACTIVE', label: 'Active' },
                          { value: 'INACTIVE', label: 'Inactive' },
                          { value: 'REVOKED', label: 'Revoked' },
                          { value: 'EXPIRED', label: 'Expired' },
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

export default VcMetadataListPage