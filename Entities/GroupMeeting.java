package Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupMeeting extends Appointment {
    private List<User> participants;

    public GroupMeeting(String name, String location, LocalDateTime startTime, LocalDateTime endTime, User owner) {
        super(name, location, startTime, endTime, owner);
        this.participants = new ArrayList<>();

        if (owner != null) {
            this.participants.add(owner);
        }
    }

    public void addParticipant(User user) {
        if (!participants.contains(user)) {
            participants.add(user);
        }
    }

    public List<User> getParticipants() {
        return participants;
    }
}