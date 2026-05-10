package DAL;

import Entities.Appointment;
import Entities.GroupMeeting;
import Entities.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private static final String CSV_FILE_PATH = "Data/appointments.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private List<Appointment> dbAppointments = new ArrayList<>();
    private List<User> dbUsers = new ArrayList<>();

    public AppointmentDAO() {
        loadFromCSV();
    }

    public void saveAppointment(Appointment app) {
        if (!dbAppointments.contains(app)) {
            dbAppointments.add(app);
        }
        saveToCSV();
    }

    public void removeAppointment(Appointment app) {
        dbAppointments.remove(app);
        saveToCSV();
    }

    public List<Appointment> getAllAppointments() {
        return dbAppointments;
    }

    public void saveUser(User user) {
        boolean exists = false;
        for (User u : dbUsers) {
            if (u.getUserID().equals(user.getUserID())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            dbUsers.add(user);
            saveToCSV();
        }
    }

    public void saveAll() {
        saveToCSV();
    }

    public List<User> getAllUsers() {
        return dbUsers;
    }

    private void saveToCSV() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(CSV_FILE_PATH), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            for (Appointment app : dbAppointments) {
                StringBuilder sb = new StringBuilder();
                if (app instanceof GroupMeeting) {
                    sb.append("G,");
                } else {
                    sb.append("A,");
                }
                sb.append(escape(app.getName())).append(",");
                sb.append(escape(app.getLocation())).append(",");
                sb.append(app.getStartTime().format(formatter)).append(",");
                sb.append(app.getEndTime().format(formatter)).append(",");
                sb.append(escape(app.getOwner().getUserID())).append(",");
                sb.append(escape(app.getOwner().getUserName())).append(",");

                if (app instanceof GroupMeeting) {
                    GroupMeeting gm = (GroupMeeting) app;
                    List<String> pList = new ArrayList<>();
                    for (User p : gm.getParticipants()) {
                        pList.add(p.getUserID() + ":" + p.getUserName());
                    }
                    sb.append(String.join(";", pList));
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving to CSV: " + e.getMessage());
        }
    }

    private void loadFromCSV() {
        File file = new File(CSV_FILE_PATH);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine && line.startsWith("\ufeff")) {
                    line = line.substring(1);
                }
                firstLine = false;

                String[] parts = line.split(",", -1);
                if (parts.length < 7)
                    continue;

                String type = parts[0];
                String name = unescape(parts[1]);
                String location = unescape(parts[2]);
                LocalDateTime start = LocalDateTime.parse(parts[3], formatter);
                LocalDateTime end = LocalDateTime.parse(parts[4], formatter);
                String ownerID = unescape(parts[5]);
                String ownerName = unescape(parts[6]);

                User owner = getOrCreateUser(ownerID, ownerName);
                Appointment app;

                if (type.equals("G")) {
                    GroupMeeting gm = new GroupMeeting(name, location, start, end, owner);
                    if (parts.length > 7 && !parts[7].isEmpty()) {
                        String[] participants = parts[7].split(";");
                        for (String p : participants) {
                            String[] pParts = p.split(":");
                            if (pParts.length == 2) {
                                gm.addParticipant(getOrCreateUser(pParts[0], pParts[1]));
                            }
                        }
                    }
                    app = gm;
                } else {
                    app = new Appointment(name, location, start, end, owner);
                }
                dbAppointments.add(app);
            }
        } catch (IOException e) {
            System.err.println("Error loading from CSV: " + e.getMessage());
        }
    }

    private User getOrCreateUser(String id, String name) {
        for (User u : dbUsers) {
            if (u.getUserID().equals(id))
                return u;
        }
        User newUser = new User(id, name);
        dbUsers.add(newUser);
        return newUser;
    }

    private String escape(String s) {
        if (s == null)
            return "";
        return s.replace(",", "\\c").replace(";", "\\s").replace(":", "\\v");
    }

    private String unescape(String s) {
        if (s == null)
            return "";
        return s.replace("\\c", ",").replace("\\s", ";").replace("\\v", ":");
    }
}