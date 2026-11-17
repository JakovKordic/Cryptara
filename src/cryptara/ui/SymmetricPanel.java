package cryptara.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import cryptara.keys.KeyLoader;
import cryptara.symmetric.SymmetricFiles;

public class SymmetricPanel extends JPanel {
    private static final String ENCRYPTED_EXTENSION = ".aes";
    private static final String DECRYPTED_PREFIX = "decrypted_";
    private final PathSelector keysSelector = new PathSelector("Keys folder:", "keys", true);
    private final PathSelector inputSelector = new PathSelector("Input file:", "", false);
    private final JButton encryptButton = new JButton("Encrypt");
    private final JButton decryptButton = new JButton("Decrypt");
    private final JTextArea logArea = new JTextArea();

    public SymmetricPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.add(encryptButton);
        buttonRow.add(decryptButton);

        JPanel topArea = new JPanel(new GridLayout(0, 1, 0, 8));
        topArea.add(keysSelector);
        topArea.add(inputSelector);
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

        Path output = encrypt ? encryptedPathFor(input) : decryptedPathFor(input);
        if (Files.exists(output) && !confirmOverwrite(output)) {
            return;
        }

        setButtonsEnabled(false);
        log((encrypt ? "Encrypting " : "Decrypting ") + input + " ...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                SecretKey key = KeyLoader.loadAesKey(keysDirectory);
                if (encrypt) {
                    SymmetricFiles.encryptFile(input, output, key);
                } else {
                    SymmetricFiles.decryptFile(input, output, key);
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

    private static Path encryptedPathFor(Path input) {
        return input.resolveSibling(input.getFileName() + ENCRYPTED_EXTENSION);
    }

    private static Path decryptedPathFor(Path input) {
        String name = input.getFileName().toString();
        if (name.endsWith(ENCRYPTED_EXTENSION)) {
            name = name.substring(0, name.length() - ENCRYPTED_EXTENSION.length());
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
        if (cause instanceof AEADBadTagException) {
            return "Error: decryption failed. Wrong key or the file was modified.";
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