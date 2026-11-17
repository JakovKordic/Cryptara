package cryptara.signature;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

public class FileSigner {

    private static final int BUFFER_SIZE = 8192;

    public static byte[] sign(Path file, PrivateKey privateKey, SignatureAlgorithm algorithm)
            throws IOException, GeneralSecurityException {
        Signature signature = createSignature(algorithm);
        signature.initSign(privateKey);
        feed(signature, file);
        return signature.sign();
    }

    public static boolean verify(Path file, byte[] signatureBytes, PublicKey publicKey, SignatureAlgorithm algorithm)
            throws IOException, GeneralSecurityException {
        Signature signature = createSignature(algorithm);
        signature.initVerify(publicKey);
        feed(signature, file);
        try {
            return signature.verify(signatureBytes);
        } catch (SignatureException e) {
            return false;
        }
    }

    private static Signature createSignature(SignatureAlgorithm algorithm) throws GeneralSecurityException {
        return switch (algorithm) {
            case RSA_PSS -> {
                Signature signature = Signature.getInstance("RSASSA-PSS");
                signature.setParameter(new PSSParameterSpec(
                        "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, PSSParameterSpec.TRAILER_FIELD_BC));
                yield signature;
            }
            case ML_DSA -> Signature.getInstance("ML-DSA");
        };
    }

    private static void feed(Signature signature, Path file) throws IOException, SignatureException {
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                signature.update(buffer, 0, read);
            }
        }
    }
}