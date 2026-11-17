package cryptara;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SymmetricPanel extends JPanel {

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
    }
}