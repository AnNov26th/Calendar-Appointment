package GUI;

import BLL.CalendarLogic;
import Entities.Appointment;
import Entities.GroupMeeting;
import Entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class UI extends JFrame {
    private CalendarLogic calendarLogic;

    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    private JPanel centerCardPanel;
    private CardLayout cardLayout;
    private JPanel calendarGridPanel;
    private JLabel monthLabel;
    private YearMonth currentDisplayedMonth;
    private boolean isTableView = true;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private final Color COLOR_BG = new Color(248, 249, 250);
    private final Color COLOR_NAVY = new Color(44, 62, 80);
    private final Color TEXT_DARK = new Color(51, 51, 51);

    private final Color[] PASTEL_COLORS = {
            new Color(255, 179, 186), new Color(255, 223, 186),
            new Color(255, 255, 186), new Color(186, 255, 201),
            new Color(186, 225, 255), new Color(226, 191, 255)
    };
    private Random random = new Random();

    public UI(CalendarLogic logic) {
        this.calendarLogic = logic;
        this.currentDisplayedMonth = YearMonth.now();

        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 12));

        setupMainFrame();
        refreshAllViews();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                calendarLogic.saveAll();
            }
        });

        startReminderTask();

        setVisible(true);
    }

    private void setupMainFrame() {
        setTitle("Hệ Thống Quản Lý Cuộc Hẹn");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        RoundedPanel headerPanel = new RoundedPanel(15, COLOR_NAVY);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JLabel titleLabel = new JLabel("LỊCH HẸN CHUNG");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        centerCardPanel = new JPanel(cardLayout);

        setupTable();
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 228), 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerCardPanel.add(scrollPane, "TABLE_VIEW");

        JPanel calendarContainer = buildCalendarView();
        centerCardPanel.add(calendarContainer, "CALENDAR_VIEW");

        mainPanel.add(centerCardPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(COLOR_BG);

        ModernButton switchViewButton = new ModernButton("Đổi Góc Nhìn", new Color(142, 68, 173));
        switchViewButton.addActionListener(e -> {
            isTableView = !isTableView;
            cardLayout.show(centerCardPanel, isTableView ? "TABLE_VIEW" : "CALENDAR_VIEW");
        });

        ModernButton addButton = new ModernButton("Thêm Cuộc Hẹn +", COLOR_SUCCESS);
        addButton.addActionListener(e -> showAddFormDialog());

        buttonPanel.add(switchViewButton);
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildCalendarView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 228), 1, true));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(Color.WHITE);

        ModernButton prevBtn = new ModernButton("< Trước", COLOR_PRIMARY);
        prevBtn.setPreferredSize(new Dimension(100, 35));
        prevBtn.addActionListener(e -> {
            currentDisplayedMonth = currentDisplayedMonth.minusMonths(1);
            refreshCalendarGrid();
        });

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        monthLabel.setPreferredSize(new Dimension(200, 30));

        ModernButton nextBtn = new ModernButton("Sau >", COLOR_PRIMARY);
        nextBtn.setPreferredSize(new Dimension(100, 35));
        nextBtn.addActionListener(e -> {
            currentDisplayedMonth = currentDisplayedMonth.plusMonths(1);
            refreshCalendarGrid();
        });

        navPanel.add(prevBtn);
        navPanel.add(monthLabel);
        navPanel.add(nextBtn);
        panel.add(navPanel, BorderLayout.NORTH);

        calendarGridPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarGridPanel.setBackground(new Color(230, 230, 230));
        panel.add(calendarGridPanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshCalendarGrid() {
        calendarGridPanel.removeAll();
        monthLabel.setText("Tháng " + currentDisplayedMonth.getMonthValue() + " - " + currentDisplayedMonth.getYear());

        String[] daysOfWeek = { "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
        for (String day : daysOfWeek) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(new Color(236, 240, 241));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calendarGridPanel.add(lbl);
        }

        LocalDate firstDayOfMonth = currentDisplayedMonth.atDay(1);
        int daysInMonth = currentDisplayedMonth.lengthOfMonth();
        int dayOfWeekIndex = firstDayOfMonth.getDayOfWeek().getValue() - 1;

        for (int i = 0; i < dayOfWeekIndex; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(250, 250, 250));
            calendarGridPanel.add(emptyPanel);
        }

        List<Appointment> allApps = calendarLogic.getAllAppointments();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentDisplayedMonth.atDay(day);

            JPanel dayCell = new JPanel(new BorderLayout());
            dayCell.setBackground(Color.WHITE);
            dayCell.setBorder(new LineBorder(new Color(240, 240, 240)));

            JLabel dayLbl = new JLabel(String.valueOf(day));
            dayLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLbl.setBorder(new EmptyBorder(2, 5, 0, 0));

            if (currentDate.equals(LocalDate.now())) {
                dayLbl.setForeground(COLOR_PRIMARY);
                dayCell.setBackground(new Color(240, 248, 255));
            }
            dayCell.add(dayLbl, BorderLayout.NORTH);

            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setOpaque(false);

            for (Appointment app : allApps) {
                if (app.getStartTime().toLocalDate().equals(currentDate)) {
                    JLabel eventLbl = new JLabel(" " + app.getName());
                    eventLbl.setOpaque(true);
                    eventLbl.setBackground(PASTEL_COLORS[random.nextInt(PASTEL_COLORS.length)]);
                    eventLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    eventLbl.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                    eventLbl.setToolTipText("Nhấn để xem chi tiết");
                    eventLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    eventLbl.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            showDetailDialog(app);
                        }
                    });

                    eventsPanel.add(eventLbl);
                }
            }
            dayCell.add(eventsPanel, BorderLayout.CENTER);
            calendarGridPanel.add(dayCell);
        }

        int totalCells = dayOfWeekIndex + daysInMonth;
        int remainingCells = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < remainingCells; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(250, 250, 250));
            calendarGridPanel.add(emptyPanel);
        }

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }

    private void setupTable() {
        String[] columns = { "Người đặt", "Tên cuộc hẹn", "Địa điểm", "Bắt đầu", "Kết thúc", "Loại" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(40);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentTable.setForeground(TEXT_DARK);
        appointmentTable.setGridColor(new Color(230, 230, 230));
        appointmentTable.setSelectionBackground(new Color(212, 230, 241));
        appointmentTable.setShowVerticalLines(false);

        JTableHeader tableHeader = appointmentTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(100, 45));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < appointmentTable.getColumnCount(); i++) {
            appointmentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = appointmentTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Appointment app = calendarLogic.getAllAppointments().get(row);
                        showDetailDialog(app);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showTablePopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showTablePopupMenu(e);
                }
            }
        });
    }

    private void showTablePopupMenu(MouseEvent e) {
        int row = appointmentTable.rowAtPoint(e.getPoint());
        if (row >= 0 && row < appointmentTable.getRowCount()) {
            appointmentTable.setRowSelectionInterval(row, row);
            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem detailItem = new JMenuItem("Xem chi tiết");
            detailItem.addActionListener(ev -> {
                Appointment app = calendarLogic.getAllAppointments().get(row);
                showDetailDialog(app);
            });

            JMenuItem deleteItem = new JMenuItem("Xóa cuộc hẹn");
            deleteItem.setForeground(Color.RED);
            deleteItem.addActionListener(ev -> {
                Appointment app = calendarLogic.getAllAppointments().get(row);
                int confirm = JOptionPane.showConfirmDialog(UI.this,
                        "Bạn có chắc chắn muốn xóa cuộc hẹn: " + app.getName() + "?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    calendarLogic.deleteAppointment(app);
                    refreshAllViews();
                    showCustomInfoDialog("Thành công", "Đã xóa cuộc hẹn!");
                }
            });

            popupMenu.add(detailItem);
            popupMenu.add(deleteItem);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void refreshAllViews() {
        tableModel.setRowCount(0);
        List<Appointment> appointments = calendarLogic.getAllAppointments();
        for (Appointment app : appointments) {
            tableModel.addRow(new Object[] {
                    app.getOwner().getUserName(), app.getName(), app.getLocation(),
                    app.getStartTime().format(FORMATTER), app.getEndTime().format(FORMATTER),
                    (app instanceof GroupMeeting) ? "Group Meeting" : "Cá nhân"
            });
        }
        refreshCalendarGrid();
    }

    private void showAddFormDialog() {
        CustomDialog dialog = new CustomDialog(this, "TẠO CUỘC HẸN MỚI");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField userField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JTextField locationField = createStyledTextField();

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        JTextField startField = createStyledTextField();
        startField.setText(now.format(FORMATTER));
        JTextField endField = createStyledTextField();
        endField.setText(now.plusHours(1).format(FORMATTER));

        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Cá nhân", "Group Meeting" });
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeCombo.setPreferredSize(new Dimension(220, 38));

        addFormRow(panel, "Người đặt:", userField, gbc, 0);
        addFormRow(panel, "Tên cuộc hẹn:", nameField, gbc, 1);
        addFormRow(panel, "Địa điểm:", locationField, gbc, 2);
        addFormRow(panel, "Loại:", typeCombo, gbc, 3);
        addFormRow(panel, "Bắt đầu:", startField, gbc, 4);
        addFormRow(panel, "Kết thúc:", endField, gbc, 5);

        ModernButton saveButton = new ModernButton("Lưu Cuộc Hẹn", COLOR_SUCCESS);
        saveButton.setPreferredSize(new Dimension(200, 40));
        saveButton.addActionListener(e -> {
            try {
                String userName = userField.getText().trim();
                if (userName.isEmpty()) {
                    showWarning("Vui lòng nhập tên Người đặt!");
                    return;
                }

                User owner = calendarLogic.getOrCreateUser(userName);
                String name = nameField.getText();
                String location = locationField.getText();
                LocalDateTime start = LocalDateTime.parse(startField.getText(), FORMATTER);
                LocalDateTime end = LocalDateTime.parse(endField.getText(), FORMATTER);

                Appointment newApp;
                if (typeCombo.getSelectedIndex() == 1) {
                    newApp = new GroupMeeting(name, location, start, end, owner);
                } else {
                    newApp = new Appointment(name, location, start, end, owner);
                }

                if (!newApp.checkValid()) {
                    showWarning("Thông tin không hợp lệ! Vui lòng kiểm tra tên và thời gian.");
                    return;
                }

                Appointment conflictApp = calendarLogic.checkConflict(start, end, owner);
                if (conflictApp != null) {
                    Object[] options = { "Chọn giờ khác", "Thay thế lịch cũ" };
                    int choice = JOptionPane.showOptionDialog(dialog,
                            "Trùng lịch với: " + conflictApp.getName() + ".\nBạn muốn làm gì?",
                            "Xung đột", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                            options[0]);

                    if (choice == 1) {
                        calendarLogic.deleteAppointment(conflictApp);
                        calendarLogic.confirmAndSave(newApp);
                        showCustomInfoDialog("Thành công", "Đã ghi đè và lưu lịch mới!");
                    } else {
                        return;
                    }
                } else {
                    calendarLogic.confirmAndSave(newApp);
                    showCustomInfoDialog("Thành công", "Đã lưu cuộc hẹn!");
                }

                dialog.dispose();
                refreshAllViews();
            } catch (Exception ex) {
                showWarning("Sai định dạng! Dạng chuẩn: yyyy-MM-dd HH:mm");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        dialog.setMainContent(panel);
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel p, String label, JComponent tf, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        p.add(tf, gbc);
    }

    private void startReminderTask() {
        Timer timer = new Timer(10000, e -> {
            LocalDateTime now = LocalDateTime.now();
            List<Appointment> apps = calendarLogic.getAllAppointments();

            for (Appointment app : apps) {
                if (app.getReminder() != null && !app.isReminderTriggered()) {
                    LocalDateTime remTime = app.getReminder().getTime();

                    if (!now.isBefore(remTime) && now.isBefore(app.getStartTime())) {
                        app.setReminderTriggered(true);
                        showReminderPopup(app);
                    }
                }
            }
        });
        timer.setInitialDelay(2000);
        timer.start();
    }

    private void showReminderPopup(Appointment app) {
        JDialog popup = new JDialog(this);
        popup.setUndecorated(true);
        popup.setAlwaysOnTop(true);
        popup.setSize(380, 190);
        popup.setBackground(new Color(0, 0, 0, 0));

        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel accent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                g2.dispose();
            }
        };
        accent.setPreferredSize(new Dimension(6, 0));
        accent.setOpaque(false);
        container.add(accent, BorderLayout.WEST);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 15, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;

        JLabel titleLbl = new JLabel("THÔNG BÁO NHẮC NHỞ");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(COLOR_PRIMARY);
        content.add(titleLbl, gbc);

        gbc.gridy++;
        JLabel nameLbl = new JLabel(app.getName());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLbl.setForeground(TEXT_DARK);
        content.add(nameLbl, gbc);

        gbc.gridy++;
        JLabel msgLbl = new JLabel(
                "<html><body style='width: 250px'>" + app.getReminder().getMessage() + "</body></html>");
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLbl.setForeground(new Color(100, 100, 100));
        content.add(msgLbl, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JLabel detailsLbl = new JLabel(
                "Bắt đầu: " + app.getStartTime().format(FORMATTER) + "  |  Địa điểm: " + app.getLocation());
        detailsLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsLbl.setForeground(COLOR_NAVY);
        content.add(detailsLbl, gbc);

        container.add(content, BorderLayout.CENTER);

        ModernButton closeBtn = new ModernButton("Đã hiểu", COLOR_SUCCESS);
        closeBtn.addActionListener(e -> popup.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(closeBtn);
        container.add(btnPanel, BorderLayout.SOUTH);

        popup.add(container);

        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int finalX = screen.x + screen.width - popup.getWidth() - 20;
        int startY = screen.y + screen.height;
        int finalY = screen.y + screen.height - popup.getHeight() - 20;

        popup.setLocation(finalX, startY);
        popup.setVisible(true);

        Timer animateTimer = new Timer(10, null);
        animateTimer.addActionListener(new ActionListener() {
            int currentY = startY;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentY > finalY) {
                    currentY -= 5;
                    if (currentY < finalY)
                        currentY = finalY;
                    popup.setLocation(finalX, currentY);
                } else {
                    animateTimer.stop();
                }
            }
        });
        animateTimer.start();
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(220, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    public void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showCustomInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDetailDialog(Appointment app) {
        CustomDialog detailDialog = new CustomDialog(this, "CHI TIẾT CUỘC HẸN");
        detailDialog.setSize(500, 550);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        String[][] data = {
                { "Người đặt:", app.getOwner().getUserName() },
                { "Tên cuộc hẹn:", app.getName() },
                { "Địa điểm:", app.getLocation() },
                { "Thời gian bắt đầu:", app.getStartTime().format(FORMATTER) },
                { "Thời gian kết thúc:", app.getEndTime().format(FORMATTER) },
                { "Loại cuộc hẹn:", (app instanceof GroupMeeting) ? "Họp Nhóm" : "Cá nhân" }
        };

        for (int i = 0; i < data.length; i++) {
            gbc.gridy = i;
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            JLabel lbl = new JLabel(data[i][0]);
            lbl.setFont(labelFont);
            lbl.setForeground(COLOR_NAVY);
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JLabel val = new JLabel(data[i][1]);
            val.setFont(valueFont);
            panel.add(val, gbc);
        }

        int nextRow = data.length;
        if (app instanceof GroupMeeting) {
            GroupMeeting gm = (GroupMeeting) app;
            StringBuilder sb = new StringBuilder();
            for (User u : gm.getParticipants()) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(u.getUserName());
            }

            gbc.gridy = nextRow++;
            gbc.gridx = 0;
            JLabel lbl = new JLabel("Người tham gia:");
            lbl.setFont(labelFont);
            lbl.setForeground(COLOR_NAVY);
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            JLabel val = new JLabel("<html><body style='width: 220px'>" + sb.toString() + "</body></html>");
            val.setFont(valueFont);
            panel.add(val, gbc);
        }

        ModernButton closeBtn = new ModernButton("Đóng", COLOR_PRIMARY);
        closeBtn.addActionListener(e -> detailDialog.dispose());

        gbc.gridy = nextRow;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        panel.add(closeBtn, gbc);

        detailDialog.setMainContent(panel);
        detailDialog.setVisible(true);
    }

    class ModernButton extends JButton {
        private Color baseColor;

        public ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 20, 8, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed())
                g2.setColor(baseColor.darker());
            else if (getModel().isRollover())
                g2.setColor(baseColor.brighter());
            else
                g2.setColor(baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    class CustomDialog extends JDialog {
        private JPanel contentArea;

        public CustomDialog(JFrame parent, String titleText) {
            super(parent, titleText, true);
            setSize(450, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(Color.WHITE);

            JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
            header.setBackground(COLOR_NAVY);
            header.setBorder(new EmptyBorder(15, 0, 15, 0));
            JLabel title = new JLabel(titleText);
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            header.add(title);
            add(header, BorderLayout.NORTH);

            contentArea = new JPanel(new BorderLayout());
            contentArea.setBackground(Color.WHITE);
            add(contentArea, BorderLayout.CENTER);
        }

        public void setMainContent(JPanel p) {
            contentArea.add(p, BorderLayout.CENTER);
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color color;

        public RoundedPanel(int r, Color c) {
            this.radius = r;
            this.color = c;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}