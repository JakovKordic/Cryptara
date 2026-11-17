package cryptara;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame prozor = new JFrame("Cryptara");
            prozor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            prozor.setSize(800, 600);
            prozor.setLocationRelativeTo(null);
            prozor.setVisible(true);
        });
    }
}