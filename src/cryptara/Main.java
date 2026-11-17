package cryptara;

import javax.swing.SwingUtilities;

import cryptara.ui.MainWindow;
import cryptara.ui.Theme;

public class Main {

    public static void main(String[] args) {
        Theme.applyDark();

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}