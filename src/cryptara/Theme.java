package cryptara;

import java.awt.Color;
import javax.swing.UIManager;

public class Theme {

    public static void applyDark() {
        UIManager.put("control", new Color(45, 45, 48));
        UIManager.put("info", new Color(45, 45, 48));
        UIManager.put("nimbusBase", new Color(30, 30, 30));
        UIManager.put("nimbusLightBackground", new Color(30, 30, 30));
        UIManager.put("nimbusSelectionBackground", new Color(80, 100, 160));
        UIManager.put("nimbusFocus", new Color(110, 140, 220));
        UIManager.put("nimbusDisabledText", new Color(120, 120, 120));
        UIManager.put("text", new Color(230, 230, 230));

        try {
            for (UIManager.LookAndFeelInfo lf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(lf.getName())) {
                    UIManager.setLookAndFeel(lf.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}