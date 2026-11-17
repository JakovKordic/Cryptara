package cryptara;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AsymmetricFiles {

    public static void encryptWithRsa(Path input, Path output, PublicKey publicKey)
            throws IOException, GeneralSecurityException {
        SymmetricFiles.checkDifferent(input, output);
        byte[] plaintext = Files.readAllBytes(input);

        SecretKey sessionKey = KeyGeneration.generateAesKey();
        byte[] encryptedData = SymmetricCrypto.encrypt(plaintext, sessionKey);
        byte[] encryptedKey = RsaOaep.encrypt(sessionKey.getEncoded(), publicKey);

        Files.write(output, Envelope.pack(encryptedKey, encryptedData));
    }

    public static void decryptWithRsa(Path input, Path output, PrivateKey privateKey)
            throws IOException, GeneralSecurityException {
        SymmetricFiles.checkDifferent(input, output);
        Envelope.Parts parts = Envelope.unpack(Files.readAllBytes(input));

        byte[] keyBytes = RsaOaep.decrypt(parts.keyMaterial(), privateKey);
        SecretKey sessionKey = new SecretKeySpec(keyBytes, "AES");
        byte[] plaintext = SymmetricCrypto.decrypt(parts.encryptedData(), sessionKey);

        Files.write(output, plaintext);
    }

    public static void encryptWithMlKem(Path input, Path output, PublicKey publicKey)
            throws IOException, GeneralSecurityException {
        SymmetricFiles.checkDifferent(input, output);
        byte[] plaintext = Files.readAllBytes(input);

        MlKem.Result kem = MlKem.encapsulate(publicKey);
        byte[] encryptedData = SymmetricCrypto.encrypt(plaintext, kem.sessionKey());

        Files.write(output, Envelope.pack(kem.encapsulation(), encryptedData));
    }

    public static void decryptWithMlKem(Path input, Path output, PrivateKey privateKey)
            throws IOException, GeneralSecurityException {
        SymmetricFiles.checkDifferent(input, output);
        Envelope.Parts parts = Envelope.unpack(Files.readAllBytes(input));

        SecretKey sessionKey = MlKem.decapsulate(parts.keyMaterial(), privateKey);
        byte[] plaintext = SymmetricCrypto.decrypt(parts.encryptedData(), sessionKey);

        Files.write(output, plaintext);
    }
}