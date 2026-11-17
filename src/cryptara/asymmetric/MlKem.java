package cryptara.asymmetric;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.KEM;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MlKem {

    private static final String ALGORITHM = "ML-KEM";
    private static final int SECRET_LENGTH = 32;

    public record Result(SecretKey sessionKey, byte[] encapsulation) {
    }

    public static Result encapsulate(PublicKey publicKey) throws GeneralSecurityException {
        KEM kem = KEM.getInstance(ALGORITHM);
        KEM.Encapsulated encapsulated = kem.newEncapsulator(publicKey).encapsulate();
        return new Result(toAesKey(encapsulated.key()), encapsulated.encapsulation());
    }

    public static SecretKey decapsulate(byte[] encapsulation, PrivateKey privateKey)
            throws GeneralSecurityException {
        KEM kem = KEM.getInstance(ALGORITHM);
        return toAesKey(kem.newDecapsulator(privateKey).decapsulate(encapsulation));
    }

    private static SecretKey toAesKey(SecretKey sharedSecret) throws GeneralSecurityException {
        byte[] bytes = sharedSecret.getEncoded();
        if (bytes == null || bytes.length != SECRET_LENGTH) {
            throw new GeneralSecurityException("Unexpected shared secret from ML-KEM.");
        }
        return new SecretKeySpec(bytes, "AES");
    }
}