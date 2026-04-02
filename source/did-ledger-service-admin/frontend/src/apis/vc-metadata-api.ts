import { getData } from "../utils/api";

const API_BASE_URL = "/lss/admin/v1";

export const fetchVcMetadataList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `vc-metadata/list?${params.toString()}`);
}

export const getVcMetadataInfo = async (id: number) => {
    return getData(API_BASE_URL, `vc-metadata?id=${id}`);
}

export const getVcMetadataDetail = async (id: number) => {
    return getData(API_BASE_URL, `vc-metadata/detail?id=${id}`);
}

export const fetchVcStatusHistoryList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `vc-status-history/list?${params.toString()}`);
}

export const getVcStatusHistoryInfo = async (id: number) => {
    return getData(API_BASE_URL, `vc-status-history?id=${id}`);
}
