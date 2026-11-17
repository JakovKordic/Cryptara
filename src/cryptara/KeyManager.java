package cryptara;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.SecretKey;

public class KeyManager {

    public static final String SECRET_FILE = "tajni_kljuc.txt";
    public static final String PUBLIC_FILE = "javni_kljuc.txt";
    public static final String PRIVATE_FILE = "privatni_kljuc.txt";

    public static final String AES_256 = "AES-256";
    public static final String RSA_OAEP = "RSA-OAEP";
    public static final String ML_KEM_768 = "ML-KEM-768";
    public static final String RSA_PSS = "RSA-PSS";
    public static final String ML_DSA_65 = "ML-DSA-65";

    public static void generateAndSave(Path directory) throws IOException, NoSuchAlgorithmException {
        SecretKey aesKey = KeyGeneration.generateAesKey();
        KeyPair rsaOaepKeys = KeyGeneration.generateRsaKeyPair();
        KeyPair mlKemKeys = KeyGeneration.generateMlKemKeyPair();
        KeyPair rsaPssKeys = KeyGeneration.generateRsaKeyPair();
        KeyPair mlDsaKeys = KeyGeneration.generateMlDsaKeyPair();

        Map<String, byte[]> secretEntries = new LinkedHashMap<>();
        secretEntries.put(AES_256, aesKey.getEncoded());

        Map<String, byte[]> publicEntries = new LinkedHashMap<>();
        publicEntries.put(RSA_OAEP, rsaOaepKeys.getPublic().getEncoded());
        publicEntries.put(ML_KEM_768, mlKemKeys.getPublic().getEncoded());
        publicEntries.put(RSA_PSS, rsaPssKeys.getPublic().getEncoded());
        publicEntries.put(ML_DSA_65, mlDsaKeys.getPublic().getEncoded());

        Map<String, byte[]> privateEntries = new LinkedHashMap<>();
        privateEntries.put(RSA_OAEP, rsaOaepKeys.getPrivate().getEncoded());
        privateEntries.put(ML_KEM_768, mlKemKeys.getPrivate().getEncoded());
        privateEntries.put(RSA_PSS, rsaPssKeys.getPrivate().getEncoded());
        privateEntries.put(ML_DSA_65, mlDsaKeys.getPrivate().getEncoded());

        Files.createDirectories(directory);
        KeyFiles.write(directory.resolve(SECRET_FILE), secretEntries);
        KeyFiles.write(directory.resolve(PUBLIC_FILE), publicEntries);
        KeyFiles.write(directory.resolve(PRIVATE_FILE), privateEntries);
    }
}