import DAL.AppointmentDAO;
import BLL.CalendarLogic;
import GUI.UI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        AppointmentDAO dao = new AppointmentDAO();
        CalendarLogic logic = new CalendarLogic(dao);

        SwingUtilities.invokeLater(() -> {
            new UI(logic);
        });
    }
}