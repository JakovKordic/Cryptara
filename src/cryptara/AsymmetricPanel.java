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
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;
import javax.crypto.BadPaddingException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class AsymmetricPanel extends JPanel {
    private static final String DECRYPTED_PREFIX = "decrypted_";
    private final PathSelector keysSelector = new PathSelector("Keys folder:", "keys", true);
    private final PathSelector inputSelector = new PathSelector("Input file:", "", false);
    private final JComboBox<AsymmetricAlgorithm> algorithmBox = new JComboBox<>(AsymmetricAlgorithm.values());
    private final JButton encryptButton = new JButton("Encrypt");
    private final JButton decryptButton = new JButton("Decrypt");
    private final JTextArea logArea = new JTextArea();

    public AsymmetricPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setPreferredSize(new Dimension(PathSelector.LABEL_WIDTH, algorithmLabel.getPreferredSize().height));

        JPanel algorithmRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        algorithmRow.add(algorithmLabel);
        algorithmRow.add(algorithmBox);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.add(encryptButton);
        buttonRow.add(decryptButton);

        JPanel topArea = new JPanel(new GridLayout(0, 1, 0, 8));
        topArea.add(keysSelector);
        topArea.add(inputSelector);
        topArea.add(algorithmRow);
        topArea.add(buttonRow);

        logArea.setEditable(false);

        add(topArea, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        encryptButton.addActionListener(event -> run(true));
        decryptButton.addActionListener(event -> run(false));
    }
        private void run(boolean encrypt) {
        if (inputSelector.isEmpty()) {
            log("Error: choose an input file.");
            return;
        }

        AsymmetricAlgorithm algorithm = (AsymmetricAlgorithm) algorithmBox.getSelectedItem();
        Path keysDirectory;
        Path input;
        try {
            keysDirectory = keysSelector.getPath();
            input = inputSelector.getPath();
        } catch (InvalidPathException e) {
            log("Error: invalid path.");
            return;
        }
        if (input.getFileName() == null) {
            log("Error: choose a file, not a folder root.");
            return;
        }

        Path output = encrypt ? encryptedPathFor(input, algorithm) : decryptedPathFor(input, algorithm);
        if (Files.exists(output) && !confirmOverwrite(output)) {
            return;
        }

        setButtonsEnabled(false);
        log((encrypt ? "Encrypting " : "Decrypting ") + input + " with " + algorithm + " ...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (encrypt) {
                    encryptFile(algorithm, keysDirectory, input, output);
                } else {
                    decryptFile(algorithm, keysDirectory, input, output);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    log("Saved to " + output.toAbsolutePath());
                } catch (ExecutionException e) {
                    log(describe(e.getCause()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                setButtonsEnabled(true);
            }
        }.execute();
    }

    private static void encryptFile(AsymmetricAlgorithm algorithm, Path keysDirectory, Path input, Path output)
            throws IOException, GeneralSecurityException {
        PublicKey publicKey = KeyLoader.loadPublicKey(
                keysDirectory, algorithm.getKeyLabel(), algorithm.getKeyAlgorithm());
        switch (algorithm) {
            case RSA_OAEP -> AsymmetricFiles.encryptWithRsa(input, output, publicKey);
            case ML_KEM -> AsymmetricFiles.encryptWithMlKem(input, output, publicKey);
        }
    }

    private static void decryptFile(AsymmetricAlgorithm algorithm, Path keysDirectory, Path input, Path output)
            throws IOException, GeneralSecurityException {
        PrivateKey privateKey = KeyLoader.loadPrivateKey(
                keysDirectory, algorithm.getKeyLabel(), algorithm.getKeyAlgorithm());
        switch (algorithm) {
            case RSA_OAEP -> AsymmetricFiles.decryptWithRsa(input, output, privateKey);
            case ML_KEM -> AsymmetricFiles.decryptWithMlKem(input, output, privateKey);
        }
    }

    private static Path encryptedPathFor(Path input, AsymmetricAlgorithm algorithm) {
        return input.resolveSibling(input.getFileName() + algorithm.getFileExtension());
    }

    private static Path decryptedPathFor(Path input, AsymmetricAlgorithm algorithm) {
        String name = input.getFileName().toString();
        String extension = algorithm.getFileExtension();
        if (name.endsWith(extension)) {
            name = name.substring(0, name.length() - extension.length());
        }
        return input.resolveSibling(DECRYPTED_PREFIX + name);
    }

    private boolean confirmOverwrite(Path output) {
        int answer = JOptionPane.showConfirmDialog(this,
                output.getFileName() + " already exists. Overwrite it?",
                "Overwrite file", JOptionPane.YES_NO_OPTION);
        return answer == JOptionPane.YES_OPTION;
    }

    private static String describe(Throwable cause) {
        if (cause instanceof BadPaddingException) {
            return "Error: decryption failed. Wrong key, wrong algorithm, or the file was modified.";
        }
        return "Error: " + cause;
    }

    private void setButtonsEnabled(boolean enabled) {
        encryptButton.setEnabled(enabled);
        decryptButton.setEnabled(enabled);
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }
}