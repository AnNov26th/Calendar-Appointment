package Entities;

import java.time.LocalDateTime;

public class Reminder {
    private LocalDateTime time;
    private String message;

    public Reminder(LocalDateTime time, String message) {
        this.time = time;
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}