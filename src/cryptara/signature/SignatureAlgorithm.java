package cryptara.signature;

import cryptara.keys.KeyManager;

public enum SignatureAlgorithm {

    RSA_PSS("RSA-PSS (3072-bit)", ".rsa.sig", KeyManager.RSA_PSS, "RSA"),
    ML_DSA("ML-DSA-65 (post-quantum)", ".mldsa.sig", KeyManager.ML_DSA_65, "ML-DSA");

    private final String displayName;
    private final String fileExtension;
    private final String keyLabel;
    private final String keyAlgorithm;

    SignatureAlgorithm(String displayName, String fileExtension, String keyLabel, String keyAlgorithm) {
        this.displayName = displayName;
        this.fileExtension = fileExtension;
        this.keyLabel = keyLabel;
        this.keyAlgorithm = keyAlgorithm;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    @Override
    public String toString() {
        return displayName;
    }
}