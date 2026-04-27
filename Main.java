import DAL.AppointmentDAO;
import BLL.CalendarLogic;
import GUI.UI;
import Entities.User;
import Entities.Appointment;
import Entities.GroupMeeting;

import java.time.LocalDateTime;
import java.util.Random;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        AppointmentDAO dao = new AppointmentDAO();
        CalendarLogic logic = new CalendarLogic(dao);

        setupSampleData(dao);

        SwingUtilities.invokeLater(() -> {
            new UI(logic);
        });
    }

    private static void setupSampleData(AppointmentDAO dao) {
        User u1 = new User("01", "Trần Hoài An");
        User u2 = new User("02", "Nguyễn Bá Giàu");
        User u3 = new User("03", "Trần Kim Lanh");

        dao.saveUser(u1);
        dao.saveUser(u2);
        dao.saveUser(u3);

        User[] users = { u1, u2, u3 };

        String[] subjects = {
                "Báo cáo tiến độ PBL3", "Fix bug C# quản lý shop giày",
                "Chuẩn bị Hugo Camping Emberline", "Thiết kế Database SQL Server",
                "Họp ban tổ chức Talkshow", "Review giao diện WinForms",
                "Phân tích hệ thống OOAD", "Luyện tập Guitar / Piano",
                "Test thuật toán game Cờ Caro", "Gặp thầy hướng dẫn Bách Khoa"
        };

        String[] locations = {
                "Phòng lab Khu C", "Google Meet", "Thư viện BK",
                "Quán Cafe khu Hòa Khánh", "Discord nhóm", "Phòng tự học"
        };

        Random rand = new Random();

        LocalDateTime baseDate = LocalDateTime.of(2026, 4, 27, 0, 0);

        for (int i = 0; i < 20; i++) {
            User owner = users[rand.nextInt(users.length)];
            String name = subjects[rand.nextInt(subjects.length)];
            String location = locations[rand.nextInt(locations.length)];

            int daysToAdd = rand.nextInt(65);
            int hour = 8 + rand.nextInt(9);
            int duration = 1 + rand.nextInt(3);

            LocalDateTime start = baseDate.plusDays(daysToAdd).withHour(hour).withMinute(0);
            LocalDateTime end = start.plusHours(duration);

            if (rand.nextBoolean()) {
                GroupMeeting gm = new GroupMeeting(name, location, start, end, owner);

                User participant = users[rand.nextInt(users.length)];
                if (!participant.getUserID().equals(owner.getUserID())) {
                    gm.addParticipant(participant);
                }
                dao.saveAppointment(gm);
            } else {
                Appointment app = new Appointment(name, location, start, end, owner);
                dao.saveAppointment(app);
            }
        }
    }
}