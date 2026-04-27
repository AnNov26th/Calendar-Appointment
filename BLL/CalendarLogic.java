package BLL;

import DAL.AppointmentDAO;
import Entities.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CalendarLogic {
    private AppointmentDAO dao;

    public CalendarLogic(AppointmentDAO dao) {
        this.dao = dao;
    }

    public User getOrCreateUser(String userName) {
        for (User u : dao.getAllUsers()) {
            if (u.getUserName().equalsIgnoreCase(userName)) {
                return u;
            }
        }
        String newID = "U" + String.format("%02d", dao.getAllUsers().size() + 1);
        User newUser = new User(newID, userName);
        dao.saveUser(newUser);
        return newUser;
    }

    public Appointment checkConflict(LocalDateTime start, LocalDateTime end, User owner) {
        for (Appointment existing : dao.getAllAppointments()) {
            boolean isSameOwner = existing.getOwner().getUserID().equals(owner.getUserID());
            boolean isOverlap = start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime());

            if (isSameOwner && isOverlap && !(existing instanceof GroupMeeting)) {
                return existing;
            }
        }
        return null;
    }

    public void confirmAndSave(Appointment app) {
        dao.saveAppointment(app);
    }

    public void deleteAppointment(Appointment app) {
        dao.removeAppointment(app);
    }

    public List<Appointment> getAllAppointments() {
        return dao.getAllAppointments();
    }

    public GroupMeeting findGroupMeeting(String name, Duration duration) {
        for (Appointment existing : dao.getAllAppointments()) {
            if (existing instanceof GroupMeeting && existing.getName().equals(name)) {
                Duration existingDuration = Duration.between(existing.getStartTime(), existing.getEndTime());
                if (existingDuration.equals(duration))
                    return (GroupMeeting) existing;
            }
        }
        return null;
    }
}