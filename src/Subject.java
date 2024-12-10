import java.util.ArrayList;

public class Subject {
    private final String name;
    private final ArrayList<String> tasks;
    private long time = 0;

    public Subject(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public long getTime() {
        return time;
    }

    public void addTime(long time) {
        this.time += time;
    }

    public void addTask(String task) {
        tasks.add(task);
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Time: " + TimeFormatter.formatDuration(time) + ", Tasks: " + tasks.toString();
    }
}
