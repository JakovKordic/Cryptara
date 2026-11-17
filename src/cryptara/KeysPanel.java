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
    }
}