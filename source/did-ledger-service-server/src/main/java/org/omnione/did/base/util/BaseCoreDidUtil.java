package org.omnione.did.base.util;

import org.omnione.did.core.data.rest.SignatureParams;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;

import java.util.List;

public class BaseCoreDidUtil {

    /**
     * Parse DID document
     *
     * @author        : yklee0911
     * @since         : 2024/07/03
     */
    public static DidManager parseDidDoc(String didDoc) {
        DidManager didManager = new DidManager();
        didManager.parse(didDoc);

        return didManager;
    }

    /**
     * Parse DID document
     *
     * @author        : yklee0911
     * @since         : 2024/07/15
     */
    public static DidManager parseDidDoc(DidDocument didDocument) {
        DidManager didManager = new DidManager();
        didManager.parse(didDocument.toJson());

        return didManager;
    }

    /**
     * Load DID document
     *
     * @author        : yklee0911
     * @since         : 2024/07/03
     */
    public static DidManager loadDidDoc(String didDocPath) {
        DidManager didManager = new DidManager();
        didManager.load(didDocPath);

        return didManager;
    }

    /**
     * Get all sign key ids
     *
     * @author        : yklee0911
     * @since         : 2024/07/03
     */
    public static List<String> getAllSignKeyIdList(DidManager didmanager) {
        try {
            return didmanager.getAllSignKeyIdList();
        } catch (Exception e) {
            System.out.println("Failed to get sign key ids: " + e.getMessage());
            throw new RuntimeException("Failed to get sign key ids: " + e.getMessage());
        }
    }

    /**
     * Get all sign key ids
     *
     * @author        : yklee0911
     * @since         : 2024/07/15
     */
    public static List<String> getAllSignKeyIdList(DidDocument didDocument) {
        try {
            DidManager didManager = parseDidDoc(didDocument);
            return didManager.getAllSignKeyIdList();
        } catch (Exception e) {
            System.out.println("Failed to get sign key ids: " + e.getMessage());
            throw new RuntimeException("Failed to get sign key ids: " + e.getMessage());
        }
    }

    /**
     * Get all sign data list
     *
     * @author        : yklee0911
     * @since         : 2024/07/03
     */
    public static List<SignatureParams> getAllSignDataList(DidManager didManager, List<String> keyIdList) {
        try {
            return didManager.getOriginDataForSign(keyIdList);
        } catch (Exception e) {
            System.out.println("Failed to get sign data: " + e.getMessage());
            throw new RuntimeException("Failed to get sign data: " + e.getMessage());
        }
    }

    /**
     * Get all sign data list
     *
     * @author        : yklee0911
     * @since         : 2024/07/15
     */
    public static List<SignatureParams> getAllSignDataList(DidDocument ownerDidDoc, List<String> keyIdList) {
        try {
            DidManager didManager = parseDidDoc(ownerDidDoc.toJson());
            return didManager.getOriginDataForSign(keyIdList);
        } catch (Exception e) {
            System.out.println("Failed to get sign data: " + e.getMessage());
            throw new RuntimeException("Failed to get sign data: " + e.getMessage());
        }
    }

    /**
     * Get all sign data list
     *
     * @author        : yklee0911
     * @since         : 2024/07/03
     */
    public static List<SignatureParams> getAllSignDataList(DidManager didManager) {
        try {
            List<String> allSignKeyIdList = getAllSignKeyIdList(didManager);
            return getAllSignDataList(didManager, allSignKeyIdList);
        }  catch (Exception e) {
            System.out.println("Failed to get sign data: " + e.getMessage());
            throw new RuntimeException("Failed to get sign data: " + e.getMessage());
        }
    }

    /**
     * Get all sign data list
     *
     * @author        : yklee0911
     * @since         : 2024/07/16
     */
    public static List<SignatureParams> getAllSignDataList(DidDocument ownerDidDoc) {
        try {
            DidManager didManager = parseDidDoc(ownerDidDoc.toJson());
            List<String> allSignKeyIdList = getAllSignKeyIdList(didManager);
            return getAllSignDataList(didManager, allSignKeyIdList);
        }  catch (Exception e) {
            System.out.println("Failed to get sign data: " + e.getMessage());
            throw new RuntimeException("Failed to get sign data: " + e.getMessage());
        }
    }

    /**
     * Add proofs to DID document
     *
     * @author        : yklee0911
     * @since         : 2024/07/03
     */
    public static void addProofsToDidDoc(DidManager didManager, List<SignatureParams> signatureParamsList) {
        try {
            didManager.addProof(signatureParamsList);
        } catch (Exception e) {
            System.out.println("Failed to add proof to DID document: " + e.getMessage());
            throw new RuntimeException("Failed to add proof to DID document: " + e.getMessage());
        }
    }

    /**
     * Get verification method
     *
     * @author        : yklee0911
     * @since         : 2024/07/15
     */
    public static VerificationMethod getVerificationMethod(DidManager didManager, String keyId) {
        try {
            return didManager.getPublicKeyByKeyId(keyId);
        } catch (Exception e) {
            System.out.println("Failed to get verification method: " + e.getMessage());
            throw new RuntimeException("Failed to get verification method: " + e.getMessage());
        }
    }

    /**
     * Get verification method
     *
     * @author        : yklee0911
     * @since         : 2024/07/16
     */
    public static VerificationMethod getVerificationMethod(DidDocument didDocument, String keyId) {
        try {
            DidManager didManager = parseDidDoc(didDocument);
            return didManager.getPublicKeyByKeyId(keyId);
        } catch (Exception e) {
            System.out.println("Failed to get verification method: " + e.getMessage());
            throw new RuntimeException("Failed to get verification method: " + e.getMessage());
        }
    }

    /**
     * Get public key
     *
     * @author        : yklee0911
     * @since         : 2024/07/15
     */
    public static String getPublicKey(DidManager didManager, String keyId) {
        try {
            VerificationMethod verificationMethod = didManager.getPublicKeyByKeyId(keyId);
            return verificationMethod.getPublicKeyMultibase();
        } catch (Exception e) {
            System.out.println("Failed to get public key: " + e.getMessage());
            throw new RuntimeException("Failed to get public key: " + e.getMessage());
        }
    }

    /**
     * Get public key
     *
     * @author        : yklee0911
     * @since         : 2024/07/15
     */
    public static String getPublicKey(DidDocument didDocument, String keyId) {
        try {
            DidManager didManager = parseDidDoc(didDocument);

            VerificationMethod verificationMethod = didManager.getPublicKeyByKeyId(keyId);
            return verificationMethod.getPublicKeyMultibase();
        } catch (Exception e) {
            System.out.println("Failed to get public key: " + e.getMessage());
            throw new RuntimeException("Failed to get public key: " + e.getMessage());
        }
    }

    /**
     * Verify DID document key proofs
     *
     * @author        : yklee0911
     * @since         : 2024/07/22
     */
    public static void verifyDidDocKeyProofs(DidManager didManager) {
        try {
            didManager.verifyDidDocSignature();
        }  catch (Exception e) {
            System.out.println("Failed to get verify data: " + e.getMessage());
            throw new RuntimeException("Failed to get verify data: " + e.getMessage());
        }
    }

    /**
     * Verify DID document key proofs
     *
     * @author        : yklee0911
     * @since         : 2024/07/22
     */
    public static void verifyDidDocKeyProofs(DidDocument didDocument) {
        try {
            DidManager didManager = parseDidDoc(didDocument);
            didManager.verifyDidDocSignature();
        }  catch (Exception e) {
            System.out.println("Failed to get verify data: " + e.getMessage());
            throw new RuntimeException("Failed to get verify data: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        String testJson = """
         {
              "@context": ["https://www.w3.org/ns/did/v1"],
              "id": "did:omn:user1",
              "controller": "did:omn:user1",
              "created": "2024-01-01T09:00:00Z",
              "updated": "2024-01-01T09:00:00Z",
              "versionId": "1",
              "deactivated": false,
              "verificationMethod": [
                {
                  "id": "pin",
                  "type": "Secp256r1VerificationKey2018",
                  "controller": "did:omn:user1",
                  "publicKeyMultibase": "z2D6BhWy1Yk8qF6GGHKUDtfRzreZLJphVyTR8pTPPqGMogwzxPeNtmwGE3pFAEQpQMCxVoEJFrgwqhjDADWBUGUcctHFV1NJzKTYGY2tQctY4y3iYkEkgY8TRd3ks3pTN26jYLW9PzDdqxz3d2fbHv1N2hV7mrzUfqPNBJsEkVJ",
                  "authType": 1
                },
                {
                  "id": "bio",
                  "type": "Secp256r1VerificationKey2018",
                  "controller": "did:omn:user1",
                  "publicKeyMultibase": "z2D6BhWy1Yk8qF6GGHKUDtfRzreZLJphVyTR8pTPPqGMogwzxPekpwWkwUsdwuBULXG5P5mH3NxSEyBZLQZ6Bhvj2ytmxvqi5s6FpvuhAnd7BMbUHS4iEveLK8hmwNCPG8wJ4rZTShX6hERqft5tGZs1dM9rZccfm51YH9otLUU",
                  "authType": 1
                }
              ],
              "assertionMethod": ["pin", "bio"],
              "authentication": ["pin", "bio"]
            }
         """;

        // Parse DID document
        System.out.println("=========== 1. Parse DID document ===========");
        DidManager didManager = parseDidDoc(testJson);

        // Load DID document
        //@TODO: 부모 경로만 입력하고 안에 있는 .did 확장자 파일을 찾고 있음. 풀 경로를 요청 파라미터로 받도록 수정 요청 함.
        System.out.println("=========== 2. Load DID document ===========");
        loadDidDoc("/Users/yundabal/wallet/tas-diddocument");

        // Get all sign key ids
        System.out.println("=========== 3. Get all sign key ids ===========");
        List<String> allSignKeyIdList = getAllSignKeyIdList(didManager);
        System.out.println("All sign key ids: " + allSignKeyIdList);

        // Get all sign data list
        System.out.println("=========== 4. Get all sign data list ===========");
        List<SignatureParams> allSignDataList = getAllSignDataList(didManager, allSignKeyIdList);
        for (SignatureParams signData : allSignDataList) {
            System.out.println("Sign data: " + signData.getOriginData());
        }

        // Add proof to DID document
        System.out.println("=========== 5. Add key proof to DID document ===========");
        for (SignatureParams signData : allSignDataList) {
            signData.setSignatureValue("signatureValue..");
        }
        addProofsToDidDoc(didManager, allSignDataList);
        System.out.println(didManager.getDidDocument().toJson());
    }
}
