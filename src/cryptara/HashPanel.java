package cryptara;

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
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import cryptara.util.PathChecks;

public class HashPanel extends JPanel {
    private static final String HASH_EXTENSION = ".sha256";
    private final PathSelector inputSelector = new PathSelector("Input file:", "", false);
    private final PathSelector hashFileSelector = new PathSelector("Hash file:", "", false);
    private final JButton computeButton = new JButton("Compute hash");
    private final JButton verifyButton = new JButton("Verify hash");
    private final JTextArea logArea = new JTextArea();

    public HashPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.add(computeButton);
        buttonRow.add(verifyButton);

        JPanel topArea = new JPanel(new GridLayout(0, 1, 0, 8));
        topArea.add(inputSelector);
        topArea.add(hashFileSelector);
        topArea.add(buttonRow);

        logArea.setEditable(false);

        add(topArea, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        computeButton.addActionListener(event -> run(true));
        verifyButton.addActionListener(event -> run(false));
    }
    private void run(boolean compute) {
        if (inputSelector.isEmpty()) {
            log("Error: choose an input file.");
            return;
        }

        Path input;
        Path hashFile;
        try {
            input = inputSelector.getPath();
            hashFile = hashFileSelector.isEmpty() ? defaultHashFileFor(input) : hashFileSelector.getPath();
        } catch (InvalidPathException e) {
            log("Error: invalid path.");
            return;
        }
        if (input.getFileName() == null) {
            log("Error: choose a file, not a folder root.");
            return;
        }
        if (compute && Files.exists(hashFile) && !confirmOverwrite(hashFile)) {
            return;
        }

        setButtonsEnabled(false);
        log((compute ? "Computing hash of " : "Verifying hash of ") + input + " ...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                if (compute) {
                    PathChecks.checkDifferent(input, hashFile);
                    String hash = FileHash.save(input, hashFile);
                    return "SHA-256: " + hash + "\nSaved to " + hashFile.toAbsolutePath();
                }
                if (FileHash.verify(input, hashFile)) {
                    return "OK: the file matches the stored hash.";
                }
                return "MISMATCH: the file or the hash file has been changed.";
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

    private static Path defaultHashFileFor(Path input) {
        return input.resolveSibling(input.getFileName() + HASH_EXTENSION);
    }

    private boolean confirmOverwrite(Path output) {
        int answer = JOptionPane.showConfirmDialog(this,
                output.getFileName() + " already exists. Overwrite it?",
                "Overwrite file", JOptionPane.YES_NO_OPTION);
        return answer == JOptionPane.YES_OPTION;
    }

    private void setButtonsEnabled(boolean enabled) {
        computeButton.setEnabled(enabled);
        verifyButton.setEnabled(enabled);
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }
}