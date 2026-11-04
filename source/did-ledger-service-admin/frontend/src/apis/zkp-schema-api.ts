import { getData } from "../utils/api";

const API_BASE_URL = "/lss/admin/v1";

const buildListParams = (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return params.toString();
};

// Fetch ZKP Credential Schema list with pagination and search
export const fetchZkpCredentialSchemaList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    return getData(API_BASE_URL, `zkp-credential-schema/list?${buildListParams(page, size, searchKey, searchValue)}`);
}

// Get ZKP Credential Schema detailed information
export const getZkpCredentialSchemaDetail = async (id: number) => {
    return getData(API_BASE_URL, `zkp-credential-schema/detail?id=${id}`);
}

// Fetch ZKP Credential Definition list with pagination and search
export const fetchZkpCredentialDefinitionList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    return getData(API_BASE_URL, `zkp-credential-definition/list?${buildListParams(page, size, searchKey, searchValue)}`);
}

// Get ZKP Credential Definition detailed information
export const getZkpCredentialDefinitionDetail = async (id: number) => {
    return getData(API_BASE_URL, `zkp-credential-definition/detail?id=${id}`);
}
