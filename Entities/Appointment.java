package Entities;

import java.time.LocalDateTime;

public class Appointment {
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private User owner;

    public Appointment(String name, String location, LocalDateTime startTime, LocalDateTime endTime, User owner) {
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner = owner;
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
