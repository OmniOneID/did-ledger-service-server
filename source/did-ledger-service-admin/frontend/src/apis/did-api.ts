import { getData } from "../utils/api";

const API_BASE_URL = "/lss/admin/v1";

export const fetchDidList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `dids/list?${params.toString()}`);
}

export const getDidInfo = async (id: number) => {
    return getData(API_BASE_URL, `dids?id=${id}`);
}

export const getDidDetail = async (id: number) => {
    return getData(API_BASE_URL, `dids/detail?id=${id}`);
}

export const fetchDidDocumentStatusHistoryList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `did-document-status-histories/list?${params.toString()}`);
}

export const getDidDocumentStatusHistoryInfo = async (id: number) => {
    return getData(API_BASE_URL, `did-document-status-histories?id=${id}`);
}
