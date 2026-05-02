package Entities;

import java.time.LocalDateTime;

public class Appointment {
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private User owner;

    private Reminder reminder;
    private boolean isReminderTriggered;

    public Appointment(String name, String location, LocalDateTime startTime, LocalDateTime endTime, User owner) {
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner = owner;

        this.reminder = new Reminder(startTime.minusHours(1), "Sắp đến cuộc hẹn: " + name);
        this.isReminderTriggered = false;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public User getOwner() {
        return owner;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public boolean isReminderTriggered() {
        return isReminderTriggered;
    }

    public void setReminderTriggered(boolean reminderTriggered) {
        this.isReminderTriggered = reminderTriggered;
    }

    public boolean checkValid() {
        if (name == null || name.trim().isEmpty() || owner == null) {
            return false;
        }
        if (startTime == null || endTime == null) {
            return false;
        }
        return endTime.isAfter(startTime);
    }
}