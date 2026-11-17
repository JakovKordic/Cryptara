package cryptara;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class KeysPanel extends JPanel {

    private final JTextField directoryField = new JTextField("keys", 30);
    private final JButton browseButton = new JButton("Browse...");
    private final JButton generateButton = new JButton("Generate keys");
    private final JTextArea logArea = new JTextArea();

    public KeysPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.add(new JLabel("Output folder:"));
        topRow.add(directoryField);
        topRow.add(browseButton);
        topRow.add(generateButton);

        logArea.setEditable(false);

        add(topRow, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        browseButton.addActionListener(event -> chooseDirectory());
        generateButton.addActionListener(event -> generateKeys());
    }
        private void chooseDirectory() {
        JFileChooser chooser = new JFileChooser(directoryField.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            directoryField.setText(chooser.getSelectedFile().getPath());
        }
    }

    private void generateKeys() {
        Path directory;
        try {
            directory = Path.of(directoryField.getText().trim());
        } catch (InvalidPathException e) {
            log("Error: invalid folder path.");
            return;
        }

        if (Files.exists(directory.resolve(KeyManager.SECRET_FILE))) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Key files already exist in this folder. Overwrite them?",
                    "Overwrite keys", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        generateButton.setEnabled(false);
        log("Generating keys, please wait...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                KeyManager.generateAndSave(directory);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    log("Keys saved to " + directory.toAbsolutePath());
                } catch (ExecutionException e) {
                    log("Error: " + e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                generateButton.setEnabled(true);
            }
        }.execute();
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }
}