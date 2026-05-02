import DAL.AppointmentDAO;
import BLL.CalendarLogic;
import GUI.UI;
import Entities.User;
import Entities.Appointment;
import Entities.GroupMeeting;

import java.time.LocalDateTime;
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
        User u1 = new User("U01", "Trần Hoài An");
        User u2 = new User("U02", "Nguyễn Bá Giàu");
        User u3 = new User("U03", "Trần Kim Lanh");

        dao.saveUser(u1);
        dao.saveUser(u2);
        dao.saveUser(u3);

        LocalDateTime testStart = LocalDateTime.now().plusMinutes(60);
        dao.saveAppointment(
                new Appointment("Test Giao diện Nhắc nhở", "Phòng họp Online", testStart, testStart.plusHours(1), u1));

        createApp(dao, "Phân tích yêu cầu hệ thống", "Thư viện", 5, 5, 14, 0, 2, u1);
        createApp(dao, "Thiết kế Database SQL", "Phòng trọ", 5, 5, 9, 0, 3, u2);
        createApp(dao, "Vẽ Sequence Diagram", "Thư viện", 5, 6, 15, 0, 2, u3);

        createGroup(dao, "Review sơ đồ thiết kế", "Google Meet", 5, 9, 19, 0, 1, u2, u1, u3);
        createApp(dao, "Code UI logic (Bỏ qua 3-layer)", "Phòng Lab", 5, 11, 8, 30, 4, u1);
        createApp(dao, "Học tiếng Anh", "Nhà văn hóa", 5, 12, 18, 0, 1, u1);
        createApp(dao, "Chuẩn bị kịch bản cho Host Minh Tâm", "Hòa Khánh", 5, 13, 15, 0, 2, u3);

        createApp(dao, "Tạo form Đăng nhập WinForms", "Phòng trọ", 5, 15, 20, 0, 2, u2);
        createGroup(dao, "Ghép code UI lần 1", "Phòng tự học", 5, 16, 9, 0, 3, u1, u2);
        createApp(dao, "Sửa lỗi query bảng Category", "Phòng Lab", 5, 18, 13, 0, 2, u1);
        createApp(dao, "Họp ban chủ nhiệm CLB", "Trường BK", 5, 19, 17, 30, 1, u3);

        createApp(dao, "Kiểm tra kết nối JDBC", "Phòng trọ", 5, 20, 10, 0, 2, u2);
        createApp(dao, "Chơi Cầu Lông", "Sân cầu lông", 5, 21, 12, 0, 1, u1);
        createGroup(dao, "Fix bug chức năng Thêm/Xóa", "Discord", 5, 22, 21, 0, 2, u1, u2);
        createApp(dao, "Viết báo cáo chương 1, 2", "Thư viện", 5, 25, 8, 0, 3, u3);

        createApp(dao, "Sửa lỗi code logic", "Phòng trọ", 5, 26, 14, 0, 2, u1);
        createApp(dao, "Test thuật toán tìm kiếm", "Phòng Lab", 5, 27, 9, 30, 2, u2);
        createGroup(dao, "Test toàn bộ hệ thống", "Google Meet", 5, 29, 20, 0, 2, u1, u2, u3);
        createApp(dao, "Chốt form báo cáo tiến độ", "Thư viện", 5, 30, 15, 0, 1, u3);

        createGroup(dao, "Họp chuẩn bị slide", "Phòng tự học", 6, 1, 14, 0, 2, u3, u1, u2);
        createApp(dao, "Nghiên cứu biểu đồ thống kê", "Phòng trọ", 6, 2, 19, 0, 2, u1);
        createApp(dao, "Gặp thầy hướng dẫn Bách Khoa", "Văn phòng khoa", 6, 3, 9, 0, 1, u1);
        createApp(dao, "Cập nhật Class Diagram", "Thư viện", 6, 4, 15, 30, 2, u2);

        createApp(dao, "Kiểm thử bảo mật đăng nhập", "Phòng Lab", 6, 5, 14, 0, 2, u1);
        createGroup(dao, "Báo cáo tiến độ (Giữa kỳ)", "Phòng Lab", 6, 6, 8, 0, 3, u1, u2, u3);
        createApp(dao, "Thêm tính năng xuất Excel", "Phòng Lab", 6, 7, 13, 0, 3, u2);
        createGroup(dao, "Tổng duyệt chương trình", "Google Meet", 6, 8, 14, 0, 2, u1, u2, u3);

        createApp(dao, "In báo cáo bản nháp", "Tiệm photo", 6, 9, 10, 0, 1, u3);
        createApp(dao, "Soát lỗi chính tả tài liệu", "Phòng trọ", 6, 10, 20, 0, 2, u3);
        createApp(dao, "Check lại cáp HDMI máy chiếu", "Phòng hội thảo", 6, 10, 16, 0, 1, u2);
        createGroup(dao, "BẢO VỆ ĐỒ ÁN CHÍNH THỨC", "Phòng Hội Đồng", 6, 11, 7, 30, 4, u1, u2, u3);

        createApp(dao, "Dọn dẹp phòng trọ", "Phòng trọ", 6, 12, 9, 0, 1, u1);
        createGroup(dao, "Đi ăn mừng hết đồ án!", "Quán nướng", 6, 13, 18, 0, 3, u2, u1, u3);
        createApp(dao, "Dọn dẹp code rác", "Phòng trọ", 6, 14, 9, 0, 2, u1);
        createApp(dao, "Lên kế hoạch hè", "Cafe", 6, 15, 15, 0, 2, u2);
    }

    private static void createApp(AppointmentDAO dao, String name, String loc,
            int month, int day, int hour, int minute, int duration, User owner) {
        LocalDateTime start = LocalDateTime.of(2026, month, day, hour, minute);
        dao.saveAppointment(new Appointment(name, loc, start, start.plusHours(duration), owner));
    }

    private static void createGroup(AppointmentDAO dao, String name, String loc,
            int month, int day, int hour, int minute, int duration,
            User owner, User... guests) {
        LocalDateTime start = LocalDateTime.of(2026, month, day, hour, minute);
        GroupMeeting gm = new GroupMeeting(name, loc, start, start.plusHours(duration), owner);
        for (User guest : guests) {
            gm.addParticipant(guest);
        }
        dao.saveAppointment(gm);
    }
}