package DAL;

import Entities.Appointment;
import Entities.User;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private List<Appointment> dbAppointments = new ArrayList<>();
    private List<User> dbUsers = new ArrayList<>();

    public void saveAppointment(Appointment app) {
        dbAppointments.add(app);
    }

    public void removeAppointment(Appointment app) {
        dbAppointments.remove(app);
    }

    public List<Appointment> getAllAppointments() {
        return dbAppointments;
    }

    public void saveUser(User user) {
        dbUsers.add(user);
    }

    public List<User> getAllUsers() {
        return dbUsers;
    }
}