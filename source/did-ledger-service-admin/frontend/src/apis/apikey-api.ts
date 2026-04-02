import { postData, getData, deleteData, putData } from "../utils/api";

const API_BASE_URL = "/lss/admin/v1";

export const getApiKeyInfo = async (id: number) => {
    return getData(API_BASE_URL, `api-keys?id=${id}`);
}

export const fetchApiKeyList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `api-keys/list?${params.toString()}`);
}

export const createApiKey = async (data: { name: string; description?: string; role: string; expirationDays: number }) => {
    return postData(API_BASE_URL, 'api-keys', data);
}

export const deactivateApiKey = async (id: number) => {
    return putData(API_BASE_URL, `api-keys/${id}/deactivate`, {});
}

export const renewApiKey = async (id: number, extensionDays?: number) => {
    const requestData = extensionDays ? { extensionDays } : {};
    return putData(API_BASE_URL, `api-keys/${id}/renew`, requestData);
}

export const activateApiKey = async (id: number) => {
    return putData(API_BASE_URL, `api-keys/${id}/activate`, {});
}