package cryptara;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HashPanel extends JPanel {

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
    }
}