package cryptara.symmetric;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SymmetricCrypto {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws GeneralSecurityException {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
        byte[] ciphertext = cipher.doFinal(plaintext);

        byte[] result = new byte[IV_LENGTH + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, IV_LENGTH);
        System.arraycopy(ciphertext, 0, result, IV_LENGTH, ciphertext.length);
        return result;
    }

    public static byte[] decrypt(byte[] data, SecretKey key) throws GeneralSecurityException {
        if (data.length < IV_LENGTH + TAG_LENGTH_BITS / 8) {
            throw new GeneralSecurityException("Encrypted data is too short.");
        }
        byte[] iv = Arrays.copyOfRange(data, 0, IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(data, IV_LENGTH, data.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
        return cipher.doFinal(ciphertext);
    }
}