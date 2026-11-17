package cryptara;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class SignaturePanel extends JPanel {

    private final PathSelector keysSelector = new PathSelector("Keys folder:", "keys", true);
    private final PathSelector inputSelector = new PathSelector("Input file:", "", false);
    private final PathSelector signatureSelector = new PathSelector("Signature file:", "", false);
    private final JComboBox<SignatureAlgorithm> algorithmBox = new JComboBox<>(SignatureAlgorithm.values());
    private final JButton signButton = new JButton("Sign");
    private final JButton verifyButton = new JButton("Verify");
    private final JTextArea logArea = new JTextArea();

    public SignaturePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setPreferredSize(new Dimension(PathSelector.LABEL_WIDTH, algorithmLabel.getPreferredSize().height));

        JPanel algorithmRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        algorithmRow.add(algorithmLabel);
        algorithmRow.add(algorithmBox);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.add(signButton);
        buttonRow.add(verifyButton);

        JPanel topArea = new JPanel(new GridLayout(0, 1, 0, 8));
        topArea.add(keysSelector);
        topArea.add(inputSelector);
        topArea.add(signatureSelector);
        topArea.add(algorithmRow);
        topArea.add(buttonRow);

        logArea.setEditable(false);

        add(topArea, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        signButton.addActionListener(event -> run(true));
        verifyButton.addActionListener(event -> run(false));
    }
    private void run(boolean sign) {
        if (inputSelector.isEmpty()) {
            log("Error: choose an input file.");
            return;
        }

        SignatureAlgorithm algorithm = (SignatureAlgorithm) algorithmBox.getSelectedItem();
        Path keysDirectory;
        Path input;
        Path signatureFile;
        try {
            keysDirectory = keysSelector.getPath();
            input = inputSelector.getPath();
            signatureFile = signatureSelector.isEmpty()
                    ? defaultSignatureFileFor(input, algorithm)
                    : signatureSelector.getPath();
        } catch (InvalidPathException e) {
            log("Error: invalid path.");
            return;
        }
        if (input.getFileName() == null) {
            log("Error: choose a file, not a folder root.");
            return;
        }
        if (sign && Files.exists(signatureFile) && !confirmOverwrite(signatureFile)) {
            return;
        }

        setButtonsEnabled(false);
        log((sign ? "Signing " : "Verifying ") + input + " with " + algorithm + " ...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                if (sign) {
                    return signFile(algorithm, keysDirectory, input, signatureFile);
                }
                return verifyFile(algorithm, keysDirectory, input, signatureFile);
            }

            @Override
            protected void done() {
                try {
                    log(get());
                } catch (ExecutionException e) {
                    log("Error: " + e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                setButtonsEnabled(true);
            }
        }.execute();
    }

    private static String signFile(SignatureAlgorithm algorithm, Path keysDirectory, Path input, Path signatureFile)
            throws IOException, GeneralSecurityException {
        SymmetricFiles.checkDifferent(input, signatureFile);
        PrivateKey privateKey = KeyLoader.loadPrivateKey(
                keysDirectory, algorithm.getKeyLabel(), algorithm.getKeyAlgorithm());
        byte[] signature = FileSigner.sign(input, privateKey, algorithm);
        SignatureFiles.save(signatureFile, algorithm, signature);
        return "Signed. Signature saved to " + signatureFile.toAbsolutePath();
    }

    private static String verifyFile(SignatureAlgorithm algorithm, Path keysDirectory, Path input, Path signatureFile)
            throws IOException, GeneralSecurityException {
        PublicKey publicKey = KeyLoader.loadPublicKey(
                keysDirectory, algorithm.getKeyLabel(), algorithm.getKeyAlgorithm());

        byte[] signature;
        try {
            signature = SignatureFiles.load(signatureFile, algorithm);
        } catch (NoSuchFileException e) {
            throw e;
        } catch (IOException e) {
            return "NOT VALID: the signature file is damaged.";
        }

        if (FileSigner.verify(input, signature, publicKey, algorithm)) {
            return "VALID: the file is unchanged and the signature matches the public key.";
        }
        return "NOT VALID: the file or the signature has been changed, or it was made with another key.";
    }

    private static Path defaultSignatureFileFor(Path input, SignatureAlgorithm algorithm) {
        return input.resolveSibling(input.getFileName() + algorithm.getFileExtension());
    }

    private boolean confirmOverwrite(Path output) {
        int answer = JOptionPane.showConfirmDialog(this,
                output.getFileName() + " already exists. Overwrite it?",
                "Overwrite file", JOptionPane.YES_NO_OPTION);
        return answer == JOptionPane.YES_OPTION;
    }

    private void setButtonsEnabled(boolean enabled) {
        signButton.setEnabled(enabled);
        verifyButton.setEnabled(enabled);
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }
}