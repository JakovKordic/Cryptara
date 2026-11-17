package cryptara;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Theme.applyDark();

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}