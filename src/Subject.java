
public class Subject {
    private final String name;
    private String[] tasks;
    private long time = 0;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String[] getTasks() {
        return tasks;
    }

    public long getTime() {
        return time;
    }

    public void addTime(long time) {
        this.time = this.time + time;
    }

    public void addTask() {
        // 
    }

/*     public boolean inputCheck(String input) {
        return input.matches("[a-zA-Z0-9]");
    } */
}
