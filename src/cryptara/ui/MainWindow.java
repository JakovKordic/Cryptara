package cryptara.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainWindow extends JFrame {

    public MainWindow() {
        super("Cryptara");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Keys", new KeysPanel());
        tabs.addTab("Symmetric", new SymmetricPanel());
        tabs.addTab("Asymmetric", new AsymmetricPanel());
        tabs.addTab("Hash", new HashPanel());
        tabs.addTab("Signature", new SignaturePanel());

        add(tabs, BorderLayout.CENTER);
    }
}