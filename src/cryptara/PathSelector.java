package cryptara;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PathSelector extends JPanel {

    static final int LABEL_WIDTH = 110;

    private final JTextField field = new JTextField(35);
    private final boolean directoriesOnly;

    public PathSelector(String labelText, String initialValue, boolean directoriesOnly) {
        super(new FlowLayout(FlowLayout.LEFT, 10, 0));
        this.directoriesOnly = directoriesOnly;
        field.setText(initialValue);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, label.getPreferredSize().height));

        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(event -> browse());

        add(label);
        add(field);
        add(browseButton);
    }

    public Path getPath() throws InvalidPathException {
        return Path.of(field.getText().trim());
    }

    public boolean isEmpty() {
        return field.getText().isBlank();
    }

    public void setText(String text) {
        field.setText(text);
    }

    private void browse() {
        JFileChooser chooser = new JFileChooser(field.getText());
        chooser.setFileSelectionMode(directoriesOnly ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getPath());
        }
    }
}