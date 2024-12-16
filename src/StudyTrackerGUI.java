import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

/**
 * The StudyTrackerGUI class provides a graphical user interface for managing study tasks and subjects.
 * It allows users to add/remove subjects and tasks, track time spent on subjects, and save/load data
 * to/from a CSV file. Analytics such as total study time and averages can also be viewed.
 */
public class StudyTrackerGUI extends JFrame {
    private final int windowWidth, windowHeight;
    private final JTextField subjectField, taskField;
    private final DefaultListModel<Subject> listModel;
    private final JList<Subject> subjectList;

    private boolean timer = false;
    private int storedIndex = -1;
    private long start;
    private long stop;

    /**
     * Constructor for the StudyTrackerGUI class.
     * Initializes the main window, sets up components, and configures layout and behavior.
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 5, 0));

        // Window Listener for asking the user if they want to save before closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, 
                    "Do you want to save before exiting?", 
                    "Exit Confirmation", 
                    JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                    saveData();
                    System.exit(0);
                } else if (choice == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });

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
                if (!timer) {
                    listModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(this, "Please stop timing to delete subjects.", "Error", JOptionPane.ERROR_MESSAGE);
                }                
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
                    storedIndex = -1;
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
        JButton analyticsButton = new JButton("View Analytics");
        analyticsButton.addActionListener((ActionEvent e) -> {
            showAnalytics();
        });
        secondButtonPanel.add(analyticsButton);
    }

    /**
     * Removes a task from the selected subject. Opens a dialog for the user to select which task to remove.
     * Displays an error if no tasks are available or no task is selected.
     *
     * @param selectedIndex The index of the selected subject in the list model.
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
     * Displays error messages if the input is invalid (e.g., blank or duplicate subject names).
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
     * Adds a new task to the specified subject in the list model.
     * Displays error messages if the task name is invalid (e.g., blank or non-alphanumeric).
     *
     * @param selectedIndex The index of the selected subject in the list model.
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
     * Validates user input to ensure it is alphanumeric with optional spaces.
     *
     * @param input The input string to validate.
     * @return True if the input is valid, false otherwise.
     */
    private static boolean checkInput(String input) {
        return input.matches("[a-zA-Z0-9 ]+");
    }

    /**
     * Saves the current list of subjects and tasks to a CSV file.
     * Displays a confirmation message on successful save or an error message on failure.
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
     * Loads data from a CSV file into the list model.
     * Skips invalid lines and displays error messages for each skipped line.
     *
     * @param filePath The path to the CSV file to load.
     */
    private void loadDataFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int count = 1;
            reader.readLine(); // Ignore header line
            while ((line = reader.readLine()) != null) {
                try {
                    listModel.addElement(parseSubject(line));
                    count++;
                } catch (IllegalArgumentException e) {
                    // Tell the user there were errors and continue with next line
                    JOptionPane.showMessageDialog(null, "Invalid data in data.csv\nError parsing line " + ++count + ": " + line + "\nReason: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Parses a single line of CSV data into a Subject object.
     * Validates the data format and throws an IllegalArgumentException for invalid data.
     *
     * @param input The CSV line to parse.
     * @return A Subject object created from the CSV data.
     * @throws IllegalArgumentException If the input data is invalid.
     */
    private static Subject parseSubject(String input) {
        // Remove any leading/trailing whitespace and newlines
        input = input.trim();

        // Split the string by commas
        String[] parts = input.split(",");

        // Ensure the array has the expected number of elements
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid data format");
        }

        // Get the name (trim whitespace)
        String name = parts[0].trim();

        if (!checkInput(name)) {
            throw new IllegalArgumentException("Invalid name value");
        }

        // Get the time (convert to long)
        long time = Long.parseLong(parts[1].trim());

        // Get the tasks (create a list from the remaining parts)
        ArrayList<String> tasks = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) {
            String task = parts[i].trim();
            if (!checkInput(task)) {
                throw new IllegalArgumentException("Invalid task value");
            }
            tasks.add(task);
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
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudyTrackerGUI::new);
    }

    /**
     * Displays analytics for the current list of subjects, including total study time,
     * most/least studied subjects, average time per subject, and task distribution.
     */
    private void showAnalytics() {
        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for analysis.", "Analytics", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Variables
        long totalTime = 0;
        Subject mostStudiedSubject = null;
        Subject leastStudiedSubject = null;
        int totalTasks = 0;
        
        // Analyze data
        for (int i = 0; i < listModel.size(); i++) {
            Subject current = listModel.getElementAt(i);
            totalTime += current.getTime();
            totalTasks += current.getTasks().size();
            
            if (mostStudiedSubject == null || current.getTime() > mostStudiedSubject.getTime()) {
                mostStudiedSubject = current;
            }
            if (leastStudiedSubject == null || current.getTime() < leastStudiedSubject.getTime()) {
                leastStudiedSubject = current;
            }
        }
    
        // Calculate averages
        double avgTimePerSubject = totalTime / (double) listModel.size();
        double avgTasksPerSubject = totalTasks / (double) listModel.size();
    
        // Create analytics report
        StringBuilder report = new StringBuilder();
        report.append("Study Analytics Report\n\n");
        report.append("Total Study Time: ").append(TimeFormatter.formatDuration(totalTime)).append("\n\n");
        report.append("Most Studied Subject: ").append(mostStudiedSubject.getName())
              .append(" (").append(TimeFormatter.formatDuration(mostStudiedSubject.getTime())).append(")\n");
        report.append("Least Studied Subject: ").append(leastStudiedSubject.getName())
              .append(" (").append(TimeFormatter.formatDuration(leastStudiedSubject.getTime())).append(")\n\n");
        report.append("Average Time per Subject: ").append(TimeFormatter.formatDuration((long)avgTimePerSubject)).append("\n");
        report.append("Total Tasks: ").append(totalTasks).append("\n");
        report.append("Average Tasks per Subject: ").append(String.format("%.1f", avgTasksPerSubject)).append("\n\n");
        
        // Time distribution by subject
        report.append("Time Distribution:\n");
        for (int i = 0; i < listModel.size(); i++) { // Calculate percentage of total time for each subject
            Subject subject = listModel.getElementAt(i); // Get the subject
            double percentage = (subject.getTime() / (double) totalTime) * 100; // Calculate percentage of total time spent studying this subject
            report.append(String.format("%s: %.1f%%\n", subject.getName(), percentage));// Append the subject name and percentage to the report
        }
    
        // Show analytics in a scrollable dialog
        JTextArea textArea = new JTextArea(report.toString()); // Create a text area with the report text
        textArea.setEditable(false);//read only
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Study Analytics",  // the text is shown as a pop up dialogue window
            JOptionPane.PLAIN_MESSAGE);
    }
}
