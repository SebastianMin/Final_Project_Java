import java.awt.*;
import javax.swing.*;

/**
 * SubjectListCellRenderer is the same as the DefaultListCellRenderer except its text also includes the index in the JList of the value being rendered in the cell.
 */
public class SubjectListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the superclass method to ensure default rendering behavior
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Cast the value to Vehicle
        Subject subject = (Subject) value;

        // Set the text to include the index and the vehicle's details
        setText((index + 1) + ". " + subject.toString());

        return this;
    }
}
