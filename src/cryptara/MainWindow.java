package cryptara;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class MainWindow extends JFrame {

    public MainWindow() {
        super("Cryptara");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Keys", createPlaceholder("Key generation"));
        tabs.addTab("Symmetric", createPlaceholder("Symmetric encryption"));
        tabs.addTab("Asymmetric", createPlaceholder("Asymmetric encryption"));
        tabs.addTab("Hash", createPlaceholder("Message digest"));
        tabs.addTab("Signature", createPlaceholder("Digital signature"));

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}