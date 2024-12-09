import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * The GarageGUI class is a graphical user interface for adding, displaying, 
 * and removing vehicles from a garage inventory. It extends JFrame and uses 
 * Swing components to create an interactive UI.
 */
public class StudyTrackerGUI extends JFrame {
    private final int windowWidth, windowHeight;
    private final JTextField subjectField, taskField;
    private final DefaultListModel<Subject> listModel;
    private final JList<Subject> subjectList;

    private boolean timer = false;
    private long start;
    private long stop;

    /**
     * Constructor for the Main class.
     * Sets up the main window, initializes components, and configures layout and behavior.
     */
    public StudyTrackerGUI() {
        // Set window dimensions
        windowWidth = 800;
        windowHeight = 600;

        // Initialize list model and JList for subject display
        listModel = new DefaultListModel<>();
        subjectList = new JList<>(listModel);

        // Apply a custom cell renderer to display vehicle details
        //subjectList.setCellRenderer(new VehicleListCellRenderer());

        // Configure JFrame properties
        setTitle("Study Tracker");
        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 5, 0));

        // Panel for displaying the list of vehicles
        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.add(new JScrollPane(subjectList)); // Add scrollable list

        // Panel for remove subject & task buttons
        JPanel subjButtonPanel = new JPanel(new GridLayout(1, 2, 0, 5));

        // Button for removing a selected subject
        JButton subjRemoveButton = new JButton("Remove Subject");
        subjRemoveButton.addActionListener((ActionEvent e) -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        subjButtonPanel.add(subjRemoveButton);

        // Button for removing a selected task
        JButton taskRemoveButton = new JButton("Remove Task");
        taskRemoveButton.addActionListener((ActionEvent e) -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex != -1) {
                removeTask();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete a task from.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        subjButtonPanel.add(taskRemoveButton);

        // Add buttons to subjectPanel
        subjectPanel.add(subjButtonPanel, BorderLayout.SOUTH);

        // Create secondary panel for interacting with program
        JPanel secondaryPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        JPanel secondButtonPanel = new JPanel(new GridLayout(1, 2, 10, 5));

        // Text field for subject input
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        // Button for adding a subject
        JButton subjAddButton = new JButton("Add Subject");
        subjAddButton.addActionListener((ActionEvent e) -> {
            addSubject();
        });
        inputPanel.add(subjAddButton);

        // Text field for task input
        taskField = new JTextField();
        inputPanel.add(taskField);

        // Button for adding a task
        JButton taskAddButton = new JButton("Add Task");
        taskAddButton.addActionListener((ActionEvent e) -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex != -1) {
                addTask();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a subject to add a task to.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(taskAddButton);

        // Add input panel to secondary panel
        secondaryPanel.add(inputPanel, BorderLayout.NORTH);

        // Button to start and stop time
        JButton startStopButton = new JButton("Start Timer");
        startStopButton.addActionListener((ActionEvent e) -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex != -1) {
                if (!timer) {
                    start = System.currentTimeMillis();
                    startStopButton.setText("Stop Timer");
                    timer = true;
                } else {
                    stop = System.currentTimeMillis();
                    long elapsed = stop - start;
                    listModel.elementAt(selectedIndex).addTime(elapsed);
                    startStopButton.setText("Start Timer");
                    timer = false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a subject to start timing.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        secondButtonPanel.add(startStopButton);

        // Button to save current information to a .csv file
        JButton saveButton = new JButton("Save Data");
        saveButton.addActionListener((ActionEvent e) -> {
            saveData();
        });
        secondButtonPanel.add(saveButton);

        // Add secondButtonPanel to secondaryPanel
        secondaryPanel.add(secondButtonPanel, BorderLayout.SOUTH);

        // Set borders for panels
        subjectPanel.setBorder(BorderFactory.createTitledBorder("Subjects"));
        add(subjectPanel);
        secondaryPanel.setBorder(BorderFactory.createTitledBorder("..."));
        add(secondaryPanel);

        // Make the frame visible
        setVisible(true);
    }

    /**
     * 
     */
    private void removeTask() {

    }

    /**
     * Adds a new vehicle to the list model based on user input from the form fields.
     * Displays error messages if any input validation fails.
     */
    private void addSubject() {
/*         String vehicle = (String) vehicleComboBox.getSelectedItem();
        String make = makeField.getText();
        String model = modelField.getText();
        String year = yearField.getText();
        String mileageString = mileageField.getText();
        int mileage;

        // Validate that all fields are filled
        if (make.isEmpty() || model.isEmpty() || year.isEmpty() || mileageString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate that mileage is a positive integer
        try {
            mileage = Integer.parseInt(mileageString);
            if (mileage < 0) {
                JOptionPane.showMessageDialog(this, "Please enter an integer greater than 0 for mileage.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter an integer greater than 0 for mileage.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add the appropriate vehicle type to the list
        switch (vehicle) {
            case "Car" -> {
                Car car = new Car(make, model, year, mileage);
                listModel.addElement(car);
                JOptionPane.showMessageDialog(this, "Car added successfully.");
            }
            case "Bike" -> {
                Bike bike = new Bike(make, model, year, mileage);
                listModel.addElement(bike);
                JOptionPane.showMessageDialog(this, "Bike added successfully.");
            }
            case "Truck" -> {
                Truck truck = new Truck(make, model, year, mileage);
                listModel.addElement(truck);
                JOptionPane.showMessageDialog(this, "Truck added successfully.");
            }
        } */
    }

    /**
     * 
     */
    private void addTask() {

    }

    /**
     * 
     */
    private void saveData() {

    }

    /**
     * Main method to run the GarageGUI application.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
