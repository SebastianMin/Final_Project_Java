public class TimeFormatter {

    public static String formatDuration(long milliseconds) {
        // Define time constants
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        // Calculate the number of days, hours, minutes, and seconds
        long days = milliseconds / daysInMilli;
        milliseconds %= daysInMilli;

        long hours = milliseconds / hoursInMilli;
        milliseconds %= hoursInMilli;

        long minutes = milliseconds / minutesInMilli;
        milliseconds %= minutesInMilli;

        long seconds = milliseconds / secondsInMilli;

        // Build the display string
        StringBuilder timeString = new StringBuilder();

        if (days > 0) {
            timeString.append(days).append(" days ");
        }
        if (hours > 0 || days > 0) {  // Show hours if there are any days or hours
            timeString.append(hours).append(" hours ");
        }
        if (minutes > 0 || hours > 0 || days > 0) { // Show minutes if there's any higher unit
            timeString.append(minutes).append(" minutes ");
        }
        if (seconds > 0 || minutes > 0 || hours > 0 || days > 0) {  // Show seconds if there's any higher unit
            timeString.append(seconds).append(" seconds");
        }

        // If the time is less than a minute, show seconds only
        if (timeString.length() == 0) {
            timeString.append(seconds).append(" seconds");
        }

        return timeString.toString().trim();
    }
}
