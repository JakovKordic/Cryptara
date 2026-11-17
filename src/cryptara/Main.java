package cryptara;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Theme.applyDark();

        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Cryptara");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(800, 600);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}