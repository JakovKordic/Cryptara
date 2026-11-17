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

public class AsymmetricPanel extends JPanel {

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
    }
}