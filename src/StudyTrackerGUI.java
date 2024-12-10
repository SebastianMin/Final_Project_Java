import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

/**
 * The StudyTrackerGUI class is a graphical user interface. It extends JFrame and uses 
 * Swing components to create an interactive UI.
 */
public class StudyTrackerGUI extends JFrame {
    private final int windowWidth, windowHeight;
    private final JTextField subjectField, taskField;
    private final DefaultListModel<Subject> listModel;
    private final JList<Subject> subjectList;

    private boolean timer = false;
    private int storedIndex = 0;
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

        // Apply a custom cell renderer to display subjects details
        subjectList.setCellRenderer(new SubjectListCellRenderer());

        // Configure JFrame properties
        setTitle("Study Tracker");
        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 5, 0));

        // Panel for displaying the list of subjects
        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.add(new JScrollPane(subjectList)); // Add scrollable list

        // Populate subjectList from data.csv
        String filePath = "data.csv";
        loadDataFromCSV(filePath);

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
                removeTask(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete a task from.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        subjButtonPanel.add(taskRemoveButton);

        // Add buttons to subjectPanel
        subjectPanel.add(subjButtonPanel, BorderLayout.SOUTH);

        // Create secondary panel for interacting with program
        JPanel secondaryPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
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
                addTask(selectedIndex);
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
                    storedIndex = selectedIndex;
                    start = System.currentTimeMillis();
                    startStopButton.setText("Stop Timer");
                    timer = true;
                } else {
                    stop = System.currentTimeMillis();
                    long elapsed = stop - start;
                    listModel.elementAt(storedIndex).addTime(elapsed);
                    startStopButton.setText("Start Timer");
                    timer = false;
                }
                subjectList.repaint();
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
        secondaryPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        add(secondaryPanel);

        // Make the frame visible
        setVisible(true);
    }

    /**
     * 
     * @param selectedIndex
     */
    private void removeTask(int selectedIndex) {
        Subject selectedSubject = listModel.getElementAt(selectedIndex);
        ArrayList<String> tasks = selectedSubject.getTasks();
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tasks available to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a dialog to select the task to remove
        String[] tasksArray = tasks.toArray(new String[0]);
        String taskToRemove = (String) JOptionPane.showInputDialog(
                this,
                "Select a task to remove:",
                "Remove Task",
                JOptionPane.PLAIN_MESSAGE,
                null,
                tasksArray,
                tasksArray[0]);

        // Check if the user selected a task
        if (taskToRemove != null) {
            tasks.remove(taskToRemove);
            JOptionPane.showMessageDialog(this, "Task removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No task selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        subjectList.repaint();
    }

    /**
     * Adds a new subject to the list model based on user input from the form fields.
     * Displays error messages if any input validation fails.
     */
    private void addSubject() {
        String newSubjectName = subjectField.getText();
        if (newSubjectName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Subject name cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (!checkInput(newSubjectName)) {
            JOptionPane.showMessageDialog(this, "Subject name must be alphanumeric.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else {

            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).getName().equalsIgnoreCase(newSubjectName)) {
                    JOptionPane.showMessageDialog(this, "Subject already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Subject newSubject = new Subject(newSubjectName);
            listModel.addElement(newSubject);
            JOptionPane.showMessageDialog(this, "Subject added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        subjectField.setText("");
    }

    /**
     * 
     * @param selectedIndex
     */
    private void addTask(int selectedIndex) {
        String newTask = taskField.getText();
        if (newTask.isBlank()) {
            JOptionPane.showMessageDialog(this, "Task name cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (!checkInput(newTask)) {
            JOptionPane.showMessageDialog(this, "Task name must be alphanumeric.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            listModel.getElementAt(selectedIndex).addTask(newTask);
            JOptionPane.showMessageDialog(this, "Task added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        subjectList.repaint();
        taskField.setText("");
    }

    /**
     * 
     * @param input
     * @return
     */
    private boolean checkInput(String input) {
        return input.matches("[a-zA-Z0-9 ]+");
    }

    /**
     * 
     */
    private void saveData() {
        File file = new File("data.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Subject Name,Time,Tasks\n");
            for (int i = 0; i < listModel.size(); i++) {
                Subject subject = listModel.getElementAt(i);
                writer.write(subject.getName() + "," + subject.getTime());
                if (subject.getTasks().isEmpty()) {
                    writer.write("\n");
                } else {
                    for (String task : subject.getTasks()) {
                        writer.write("," + task);
                    }
                    writer.write("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Data saved successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 
     * @param filePath
     */
    private void loadDataFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Ignore header line
            while ((line = reader.readLine()) != null) {
                listModel.addElement(parseSubject(line));
            }
        } catch (IOException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 
     * @param input
     * @return
     */
    private static Subject parseSubject(String input) {
        // Remove any leading/trailing whitespace and newlines
        input = input.trim();

        // Split the string by commas
        String[] parts = input.split(",");

        // Ensure the array has the expected number of elements
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid input format");
        }

        // Get the name (trim whitespace)
        String name = parts[0].trim();

        // Get the time (convert to long)
        long time = Long.parseLong(parts[1].trim());

        // Get the tasks (create a list from the remaining parts)
        ArrayList<String> tasks = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) {
            tasks.add(parts[i].trim());
        }

        // Create and return the Student object
        if (tasks.isEmpty()) {
            return new Subject(name, time);
        } else {
            return new Subject(name, time, tasks);
        }
    }

    /**
     * Main method to run the StudyTrackerGUI application.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudyTrackerGUI::new);
    }
}
