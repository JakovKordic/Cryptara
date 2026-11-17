package cryptara.asymmetric;

import cryptara.keys.KeyManager;

public enum AsymmetricAlgorithm {

    RSA_OAEP("RSA-OAEP (3072-bit)", ".rsa", KeyManager.RSA_OAEP, "RSA"),
    ML_KEM("ML-KEM-768 (post-quantum)", ".kem", KeyManager.ML_KEM_768, "ML-KEM");

    private final String displayName;
    private final String fileExtension;
    private final String keyLabel;
    private final String keyAlgorithm;

    AsymmetricAlgorithm(String displayName, String fileExtension, String keyLabel, String keyAlgorithm) {
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