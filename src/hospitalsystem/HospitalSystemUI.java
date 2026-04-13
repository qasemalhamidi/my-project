package hospitalsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class HospitalSystemUI extends JFrame {

    // ── Colors & Fonts ──────────────────────────────────────────────
    private static final Color BG        = new Color(245, 247, 252);
    private static final Color SIDEBAR   = new Color(26,  43,  77);
    private static final Color ACCENT    = new Color(52,  152, 219);
    private static final Color ACCENT2   = new Color(46,  204, 113);
    private static final Color DANGER    = new Color(231, 76,  60);
    private static final Color WARN      = new Color(243, 156, 18);
    private static final Color CARD_BG   = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44,  62,  80);
    private static final Color TEXT_MID  = new Color(127, 140, 141);
    private static final Color TBL_HEAD  = new Color(52,  73,  94);
    private static final Font  FONT_TITLE= new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font  FONT_SMALL= new Font("Segoe UI", Font.PLAIN, 11);

    private final HospitalData data = new HospitalData();
    private final CardLayout cards = new CardLayout();
    private final JPanel mainPanel = new JPanel(cards);

    // Stat labels on dashboard
    private JLabel lblTotalPatients, lblActivePatients, lblTotalStaff, lblDoctors;

    // Patient table
    private DefaultTableModel patientModel;
    private JTable patientTable;
    private JTextField tfPatSearch;

    // Staff table
    private DefaultTableModel staffModel;
    private JTable staffTable;
    private JTextField tfStaffSearch;

    // ── Constructor ─────────────────────────────────────────────────
    public HospitalSystemUI() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Try to load saved patients
        try { data.loadPatients(); } catch (IOException ignored) {}

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);

        mainPanel.setBackground(BG);
        mainPanel.add(buildDashboard(),  "dashboard");
        mainPanel.add(buildPatientsPanel(), "patients");
        mainPanel.add(buildStaffPanel(), "staff");
        mainPanel.add(buildReportsPanel(), "reports");
        add(mainPanel, BorderLayout.CENTER);

        cards.show(mainPanel, "dashboard");
        refreshDashboard();
        setVisible(true);
    }

    // ── SIDEBAR ─────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setBackground(SIDEBAR);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Logo area
        JLabel logo = new JLabel("+", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("Hospital System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        sb.add(logo);
        sb.add(Box.createVerticalStrut(4));
        sb.add(appName);
        sb.add(Box.createVerticalStrut(30));

        // Nav buttons
        String[][] navItems = {
            {"[=]", "Dashboard", "dashboard"},
            {"[P]", "Patients",  "patients"},
            {"[S]", "Staff",     "staff"},
            {"[R]", "Reports",   "reports"},
        };

        for (String[] item : navItems) {
            sb.add(makeNavBtn(item[0] + "  " + item[1], item[2]));
            sb.add(Box.createVerticalStrut(6));
        }

        sb.add(Box.createVerticalGlue());

        // Save button at bottom
        JButton btnSave = makeStyledBtn("Save Data", ACCENT2, Color.WHITE);
        btnSave.setMaximumSize(new Dimension(170, 38));
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.addActionListener(e -> saveData());
        sb.add(btnSave);

        return sb;
    }

    private JButton makeNavBtn(String label, String card) {
        JButton btn = new JButton(label);
        btn.setFont(FONT_BODY);
        btn.setForeground(new Color(189, 195, 199));
        btn.setBackground(SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(210, 42));
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 22, 0, 0));
        btn.addActionListener(e -> {
            cards.show(mainPanel, card);
            if ("dashboard".equals(card)) refreshDashboard();
            if ("patients".equals(card)) refreshPatientTable(data.getAllPatients());
            if ("staff".equals(card)) refreshStaffTable(data.getAllStaff());
            if ("reports".equals(card)) buildReportsContent((JPanel) getReportsPanel());
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(44, 62, 80));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR);
                btn.setForeground(new Color(189, 195, 199));
            }
        });
        return btn;
    }

    // ── DASHBOARD ───────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setBackground(BG);
        statsRow.setBorder(new EmptyBorder(20, 0, 20, 0));

        lblTotalPatients = new JLabel("0", SwingConstants.CENTER);
        lblActivePatients = new JLabel("0", SwingConstants.CENTER);
        lblTotalStaff = new JLabel("0", SwingConstants.CENTER);
        lblDoctors = new JLabel("0", SwingConstants.CENTER);

        statsRow.add(makeStatCard("Total Patients", lblTotalPatients, ACCENT, "Pts"));
        statsRow.add(makeStatCard("Active Patients", lblActivePatients, ACCENT2, "Act"));
        statsRow.add(makeStatCard("Staff Members", lblTotalStaff, WARN, "Stf"));
        statsRow.add(makeStatCard("Doctors", lblDoctors, DANGER, "Doc"));

        // Quick actions
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(statsRow, BorderLayout.NORTH);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quickActions.setBackground(BG);
        JLabel qaTitle = new JLabel("Quick Actions");
        qaTitle.setFont(FONT_BOLD);
        qaTitle.setForeground(TEXT_DARK);

        JButton btnAddP = makeStyledBtn("+ Add Patient", ACCENT, Color.WHITE);
        btnAddP.addActionListener(e -> { cards.show(mainPanel, "patients"); showAddPatientDialog(); });

        JButton btnAddS = makeStyledBtn("+ Add Staff", ACCENT2, Color.WHITE);
        btnAddS.addActionListener(e -> { cards.show(mainPanel, "staff"); showAddEmployeeDialog(); });

        JButton btnReport = makeStyledBtn("View Reports", new Color(155, 89, 182), Color.WHITE);
        btnReport.addActionListener(e -> cards.show(mainPanel, "reports"));

        quickActions.add(btnAddP);
        quickActions.add(btnAddS);
        quickActions.add(btnReport);

        JPanel qaWrapper = new JPanel(new BorderLayout());
        qaWrapper.setBackground(BG);
        qaWrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
        qaWrapper.add(qaTitle, BorderLayout.NORTH);
        qaWrapper.add(quickActions, BorderLayout.CENTER);

        center.add(qaWrapper, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        return p;
    }

    private JPanel makeStatCard(String label, JLabel valueLabel, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 225, 235), 1, true),
            new EmptyBorder(16, 18, 16, 18)));

        JLabel iconLbl = new JLabel(icon, SwingConstants.LEFT);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accent);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MID);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(CARD_BG);
        top.add(iconLbl, BorderLayout.WEST);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private void refreshDashboard() {
        List<Patient> pts = data.getAllPatients();
        List<Employee> stf = data.getAllStaff();
        lblTotalPatients.setText(String.valueOf(pts.size()));
        lblActivePatients.setText(String.valueOf(data.countActive()));
        lblTotalStaff.setText(String.valueOf(stf.size()));
        lblDoctors.setText(String.valueOf(stf.stream().filter(e -> e instanceof Doctor).count()));
    }

    // ── PATIENTS PANEL ──────────────────────────────────────────────
    private JPanel buildPatientsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        JLabel title = new JLabel("Patient Management");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);

        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hRight.setBackground(BG);
        tfPatSearch = new JTextField(16);
        styleTextField(tfPatSearch, "Search patients...");
        JButton btnSearch = makeStyledBtn("Search", ACCENT, Color.WHITE);
        JButton btnAdd = makeStyledBtn("+ Add Patient", ACCENT, Color.WHITE);
        JButton btnEdit = makeStyledBtn("Edit", WARN, Color.WHITE);
        JButton btnDel = makeStyledBtn("Delete", DANGER, Color.WHITE);

        btnSearch.addActionListener(e -> {
            String q = tfPatSearch.getText().trim();
            refreshPatientTable(q.isEmpty() ? data.getAllPatients() : data.searchPatients(q));
        });
        tfPatSearch.addActionListener(e -> btnSearch.doClick());
        btnAdd.addActionListener(e -> showAddPatientDialog());
        btnEdit.addActionListener(e -> showEditPatientDialog());
        btnDel.addActionListener(e -> deleteSelectedPatient());

        hRight.add(tfPatSearch);
        hRight.add(btnSearch);
        hRight.add(btnAdd);
        hRight.add(btnEdit);
        hRight.add(btnDel);

        header.add(title, BorderLayout.WEST);
        header.add(hRight, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"File No.", "ID", "Name", "Age", "Phone", "Blood", "Diagnosis", "Address", "Status"};
        patientModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = buildStyledTable(patientModel);
        p.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        refreshPatientTable(data.getAllPatients());
        return p;
    }

    private void refreshPatientTable(List<Patient> list) {
        patientModel.setRowCount(0);
        for (Patient pt : list) {
            patientModel.addRow(new Object[]{
                pt.getFileNumber(), pt.getId(), pt.getName(), pt.getAge(),
                pt.getPhoneNumber(), pt.getBloodType(), pt.getDiagnosis(),
                pt.getAddress(), pt.getStatus()
            });
        }
    }

    private void showAddPatientDialog() {
        JDialog dlg = new JDialog(this, "Add New Patient", true);
        dlg.setSize(480, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fId = new JTextField(14);
        JTextField fName = new JTextField(14);
        JTextField fAge = new JTextField(14);
        JTextField fFile = new JTextField(14);
        JTextField fPhone = new JTextField(14);
        JTextField fAddr = new JTextField(14);
        JTextField fDiag = new JTextField(14);
        String[] bloods = {"A+","A-","B+","B-","AB+","AB-","O+","O-","Unknown"};
        JComboBox<String> cbBlood = new JComboBox<>(bloods);
        String[] statuses = {"Active","Discharged"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);

        Object[][] rows = {
            {"Patient ID *", fId}, {"Full Name *", fName}, {"Age *", fAge},
            {"File Number *", fFile}, {"Phone", fPhone}, {"Address", fAddr},
            {"Diagnosis *", fDiag}, {"Blood Type", cbBlood}, {"Status", cbStatus}
        };

        for (int i = 0; i < rows.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel((String)rows[i][0]);
            lbl.setFont(FONT_SMALL);
            form.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 1;
            form.add((Component)rows[i][1], gc);
        }

        JButton btnOk = makeStyledBtn("Add Patient", ACCENT2, Color.WHITE);
        btnOk.addActionListener(e -> {
            try {
                int id = Integer.parseInt(fId.getText().trim());
                String name = fName.getText().trim();
                int age = Integer.parseInt(fAge.getText().trim());
                String file = fFile.getText().trim();
                if (name.isEmpty() || file.isEmpty()) { showError(dlg, "Name and File Number are required."); return; }
                // PatientBuilder — Builder Pattern
                Patient pt = new PatientBuilder(id, name, age, file)
                        .address(fAddr.getText().trim())
                        .diagnosis(fDiag.getText().trim())
                        .phoneNumber(fPhone.getText().trim())
                        .bloodType((String) cbBlood.getSelectedItem())
                        .status((String) cbStatus.getSelectedItem())
                        .build();
                if (!data.addPatient(pt)) { showError(dlg, "Duplicate ID or File Number!"); return; }
                refreshPatientTable(data.getAllPatients());
                refreshDashboard();
                showSuccess(dlg, "Patient added successfully!");
                dlg.dispose();
            } catch (NumberFormatException ex) {
                showError(dlg, "ID and Age must be numbers.");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(CARD_BG);
        JButton btnCancelAdd = makeStyledBtn("Cancel", TEXT_MID, Color.WHITE);
        btnCancelAdd.addActionListener(x -> dlg.dispose());
        bottom.add(btnCancelAdd);
        bottom.add(btnOk);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showEditPatientDialog() {
        int row = patientTable.getSelectedRow();
        if (row < 0) { showError(this, "Please select a patient to edit."); return; }
        String file = (String) patientModel.getValueAt(row, 0);
        Patient p = data.searchByFile(file);
        if (p == null) return;

        JDialog dlg = new JDialog(this, "Edit Patient: " + p.getName(), true);
        dlg.setSize(440, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fAddr = new JTextField(p.getAddress(), 14);
        JTextField fDiag = new JTextField(p.getDiagnosis(), 14);
        JTextField fPhone = new JTextField(p.getPhoneNumber(), 14);
        String[] bloods = {"A+","A-","B+","B-","AB+","AB-","O+","O-","Unknown"};
        JComboBox<String> cbBlood = new JComboBox<>(bloods);
        cbBlood.setSelectedItem(p.getBloodType());
        String[] statuses = {"Active","Discharged"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        cbStatus.setSelectedItem(p.getStatus());

        Object[][] rows = {
            {"Address", fAddr}, {"Diagnosis", fDiag},
            {"Phone", fPhone}, {"Blood Type", cbBlood}, {"Status", cbStatus}
        };
        for (int i = 0; i < rows.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            form.add(new JLabel((String)rows[i][0]) {{ setFont(FONT_SMALL); }}, gc);
            gc.gridx = 1; gc.weightx = 1;
            form.add((Component)rows[i][1], gc);
        }

        JButton btnOk = makeStyledBtn("Save Changes", ACCENT2, Color.WHITE);
        btnOk.addActionListener(e -> {
            data.updatePatient(file, fAddr.getText().trim(), fDiag.getText().trim(),
                    fPhone.getText().trim(), (String)cbBlood.getSelectedItem(),
                    (String)cbStatus.getSelectedItem());
            refreshPatientTable(data.getAllPatients());
            showSuccess(dlg, "Patient updated.");
            dlg.dispose();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(CARD_BG);
        JButton btnCancel = makeStyledBtn("Cancel", TEXT_MID, Color.WHITE); btnCancel.addActionListener(x -> dlg.dispose()); bottom.add(btnCancel);
        bottom.add(btnOk);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteSelectedPatient() {
        int row = patientTable.getSelectedRow();
        if (row < 0) { showError(this, "Please select a patient."); return; }
        String file = (String) patientModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete patient with file " + file + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            data.deletePatient(file);
            refreshPatientTable(data.getAllPatients());
            refreshDashboard();
        }
    }

    // ── STAFF PANEL ─────────────────────────────────────────────────
    private JPanel buildStaffPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        JLabel title = new JLabel("Staff Management");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);

        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hRight.setBackground(BG);
        tfStaffSearch = new JTextField(16);
        styleTextField(tfStaffSearch, "Search staff...");
        JButton btnSearch = makeStyledBtn("Search", ACCENT, Color.WHITE);
        JButton btnAdd = makeStyledBtn("+ Add Staff", ACCENT, Color.WHITE);
        JButton btnEdit = makeStyledBtn("Edit", WARN, Color.WHITE);
        JButton btnDel = makeStyledBtn("Remove", DANGER, Color.WHITE);

        btnSearch.addActionListener(e -> {
            String q = tfStaffSearch.getText().trim();
            refreshStaffTable(q.isEmpty() ? data.getAllStaff() : data.searchStaff(q));
        });
        tfStaffSearch.addActionListener(e -> btnSearch.doClick());
        btnAdd.addActionListener(e -> showAddEmployeeDialog());
        btnEdit.addActionListener(e -> showEditEmployeeDialog());
        btnDel.addActionListener(e -> deleteSelectedEmployee());

        hRight.add(tfStaffSearch); hRight.add(btnSearch);
        hRight.add(btnAdd); hRight.add(btnEdit); hRight.add(btnDel);
        header.add(title, BorderLayout.WEST);
        header.add(hRight, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Age", "Role", "Department", "Details", "Base Salary", "Net Salary"};
        staffModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = buildStyledTable(staffModel);
        p.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        refreshStaffTable(data.getAllStaff());
        return p;
    }

    private void refreshStaffTable(List<Employee> list) {
        staffModel.setRowCount(0);
        for (Employee e : list) {
            String details = "";
            if (e instanceof Doctor d) details = d.getSpecialization() + " (" + d.getYearsOfExperience() + " yrs)";
            else if (e instanceof Nurse n) details = n.getShift() + " shift";
            staffModel.addRow(new Object[]{
                e.getId(), e.getName(), e.getAge(), e.getRole(), e.getDepartment(),
                details, String.format("%.2f", e.getBaseSalary()),
                String.format("%.2f", e.calculateSalary())
            });
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dlg = new JDialog(this, "Add New Staff Member", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fId = new JTextField(14);
        JTextField fName = new JTextField(14);
        JTextField fAge = new JTextField(14);
        JTextField fDept = new JTextField(14);
        JTextField fSalary = new JTextField(14);
        String[] roles = {"Doctor", "Nurse", "Receptionist"};
        JComboBox<String> cbRole = new JComboBox<>(roles);
        JTextField fSpec = new JTextField(14);  // Doctor specialization / Nurse shift
        JTextField fExtra = new JTextField(14); // years of exp

        JLabel lblSpec = new JLabel("Specialization");
        JLabel lblExtra = new JLabel("Years Exp.");

        cbRole.addActionListener(e -> {
            String r = (String) cbRole.getSelectedItem();
            if ("Doctor".equals(r)) {
                lblSpec.setText("Specialization"); lblExtra.setText("Years Exp.");
                fSpec.setEnabled(true); fExtra.setEnabled(true);
            } else if ("Nurse".equals(r)) {
                lblSpec.setText("Shift (Day/Night)"); lblExtra.setText("-");
                fSpec.setEnabled(true); fExtra.setEnabled(false); fExtra.setText("");
            } else {
                lblSpec.setText("-"); lblExtra.setText("-");
                fSpec.setEnabled(false); fSpec.setText("");
                fExtra.setEnabled(false); fExtra.setText("");
            }
        });

        Object[][] rows = {
            {"Employee ID *", fId}, {"Full Name *", fName}, {"Age", fAge},
            {"Role *", cbRole}, {"Department", fDept}, {"Base Salary *", fSalary}
        };
        for (int i = 0; i < rows.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            form.add(new JLabel((String)rows[i][0]) {{ setFont(FONT_SMALL); }}, gc);
            gc.gridx = 1; gc.weightx = 1;
            form.add((Component)rows[i][1], gc);
        }
        gc.gridx = 0; gc.gridy = rows.length; gc.weightx = 0; lblSpec.setFont(FONT_SMALL); form.add(lblSpec, gc);
        gc.gridx = 1; gc.weightx = 1; form.add(fSpec, gc);
        gc.gridx = 0; gc.gridy = rows.length+1; gc.weightx = 0; lblExtra.setFont(FONT_SMALL); form.add(lblExtra, gc);
        gc.gridx = 1; gc.weightx = 1; form.add(fExtra, gc);

        JButton btnOk = makeStyledBtn("Add Staff", ACCENT2, Color.WHITE);
        btnOk.addActionListener(e -> {
            try {
                int id = Integer.parseInt(fId.getText().trim());
                String name = fName.getText().trim();
                if (name.isEmpty()) { showError(dlg, "Name is required."); return; }
                int age = fAge.getText().trim().isEmpty() ? 30 : Integer.parseInt(fAge.getText().trim());
                double sal = Double.parseDouble(fSalary.getText().trim());
                String dept = fDept.getText().trim();
                String role = (String) cbRole.getSelectedItem();
                // EmployeeBuilder — Builder Pattern
                EmployeeBuilder eb = new EmployeeBuilder(id, name, age)
                        .role(role).baseSalary(sal).department(dept);
                if ("Doctor".equals(role)) {
                    int yrs = fExtra.getText().trim().isEmpty() ? 0 : Integer.parseInt(fExtra.getText().trim());
                    eb.specialization(fSpec.getText().trim()).yearsOfExperience(yrs);
                } else if ("Nurse".equals(role)) {
                    eb.shift(fSpec.getText().trim().isEmpty() ? "Day" : fSpec.getText().trim());
                }
                Employee emp = eb.build();
                if (!data.addEmployee(emp)) { showError(dlg, "Duplicate employee ID!"); return; }
                refreshStaffTable(data.getAllStaff());
                refreshDashboard();
                showSuccess(dlg, "Staff member added!");
                dlg.dispose();
            } catch (NumberFormatException ex) {
                showError(dlg, "Check numeric fields (ID, Age, Salary).");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(CARD_BG);
        JButton btnCancel = makeStyledBtn("Cancel", TEXT_MID, Color.WHITE); btnCancel.addActionListener(x -> dlg.dispose()); bottom.add(btnCancel);
        bottom.add(btnOk);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteSelectedEmployee() {
        int row = staffTable.getSelectedRow();
        if (row < 0) { showError(this, "Please select a staff member."); return; }
        int id = (int) staffModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove staff member with ID " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            data.deleteEmployee(id);
            refreshStaffTable(data.getAllStaff());
            refreshDashboard();
        }
    }

    private void showEditEmployeeDialog() {
        int row = staffTable.getSelectedRow();
        if (row < 0) { showError(this, "Please select a staff member to edit."); return; }

        int id = (int) staffModel.getValueAt(row, 0);
        Employee emp = data.findEmployee(id);
        if (emp == null) return;

        JDialog dlg = new JDialog(this, "Edit Staff: " + emp.getName(), true);
        dlg.setSize(460, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fName   = new JTextField(emp.getName(), 14);
        JTextField fAge    = new JTextField(String.valueOf(emp.getAge()), 14);
        JTextField fDept   = new JTextField(emp.getDepartment() != null ? emp.getDepartment() : "", 14);
        JTextField fSalary = new JTextField(String.valueOf(emp.getBaseSalary()), 14);
        JTextField fSpec   = new JTextField(14);
        JTextField fExtra  = new JTextField(14);
        JLabel lblSpec  = new JLabel("Specialization");
        JLabel lblExtra = new JLabel("Years Exp.");

        // Pre-fill role-specific fields
        if (emp instanceof Doctor d) {
            fSpec.setText(d.getSpecialization());
            fExtra.setText(String.valueOf(d.getYearsOfExperience()));
        } else if (emp instanceof Nurse n) {
            lblSpec.setText("Shift (Day/Night)");
            fSpec.setText(n.getShift());
            fExtra.setEnabled(false);
            lblExtra.setText("-");
        } else {
            fSpec.setEnabled(false);
            fExtra.setEnabled(false);
            lblSpec.setText("-");
            lblExtra.setText("-");
        }

        Object[][] rows = {
            {"Full Name *", fName}, {"Age", fAge},
            {"Department", fDept}, {"Base Salary *", fSalary}
        };
        for (int i = 0; i < rows.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel((String) rows[i][0]);
            lbl.setFont(FONT_SMALL);
            form.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 1;
            form.add((Component) rows[i][1], gc);
        }
        gc.gridx = 0; gc.gridy = rows.length;     gc.weightx = 0; lblSpec.setFont(FONT_SMALL);  form.add(lblSpec, gc);
        gc.gridx = 1; gc.weightx = 1;              form.add(fSpec, gc);
        gc.gridx = 0; gc.gridy = rows.length + 1; gc.weightx = 0; lblExtra.setFont(FONT_SMALL); form.add(lblExtra, gc);
        gc.gridx = 1; gc.weightx = 1;              form.add(fExtra, gc);

        JButton btnOk = makeStyledBtn("Save Changes", ACCENT2, Color.WHITE);
        btnOk.addActionListener(e -> {
            try {
                String name = fName.getText().trim();
                if (name.isEmpty()) { showError(dlg, "Name is required."); return; }
                int age    = fAge.getText().trim().isEmpty() ? emp.getAge() : Integer.parseInt(fAge.getText().trim());
                double sal = Double.parseDouble(fSalary.getText().trim());
                String dept = fDept.getText().trim();

                emp.setName(name);
                emp.setAge(age);
                emp.setDepartment(dept);
                emp.setBaseSalary(sal);

                if (emp instanceof Doctor d) {
                    d.setSpecialization(fSpec.getText().trim());
                    if (!fExtra.getText().trim().isEmpty())
                        d.setYearsOfExperience(Integer.parseInt(fExtra.getText().trim()));
                } else if (emp instanceof Nurse n) {
                    if (!fSpec.getText().trim().isEmpty())
                        n.setShift(fSpec.getText().trim());
                }

                refreshStaffTable(data.getAllStaff());
                showSuccess(dlg, "Staff member updated successfully!");
                dlg.dispose();
            } catch (NumberFormatException ex) {
                showError(dlg, "Check numeric fields (Age, Salary, Years Exp.).");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(CARD_BG);
        JButton btnCancel = makeStyledBtn("Cancel", TEXT_MID, Color.WHITE);
        btnCancel.addActionListener(x -> dlg.dispose());
        bottom.add(btnCancel);
        bottom.add(btnOk);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── REPORTS PANEL ───────────────────────────────────────────────
    private JPanel reportsContentPanel;

    private JPanel buildReportsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Reports & Statistics");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);

        JButton btnRefresh = makeStyledBtn("Refresh", ACCENT, Color.WHITE);
        btnRefresh.addActionListener(e -> buildReportsContent(reportsContentPanel));
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BG);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(btnRefresh, BorderLayout.EAST);
        p.add(titleRow, BorderLayout.NORTH);

        reportsContentPanel = new JPanel();
        reportsContentPanel.setLayout(new BoxLayout(reportsContentPanel, BoxLayout.Y_AXIS));
        reportsContentPanel.setBackground(BG);

        JScrollPane sp = new JScrollPane(reportsContentPanel);
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        p.add(sp, BorderLayout.CENTER);

        buildReportsContent(reportsContentPanel);
        return p;
    }

    private Component getReportsPanel() {
        return reportsContentPanel != null ? reportsContentPanel : new JPanel();
    }

    private void buildReportsContent(JPanel panel) {
        panel.removeAll();

        List<Patient> pts  = data.getAllPatients();
        List<Employee> stf = data.getAllStaff();

        long active     = data.countActive();
        long discharged = pts.stream().filter(p -> "Discharged".equals(p.getStatus())).count();
        long doctors    = stf.stream().filter(e -> e instanceof Doctor).count();
        long nurses     = stf.stream().filter(e -> e instanceof Nurse).count();
        long receps     = stf.stream().filter(e -> e instanceof Receptionist).count();
        double totalPayroll = stf.stream().mapToDouble(Employee::calculateSalary).sum();

        // ── Row 1: Summary + Patient Status Pie ─────────────────────
        JPanel row1 = new JPanel(new GridLayout(1, 2, 14, 0));
        row1.setBackground(BG);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel summCard = makeCard("Summary Statistics");
        addReportRow(summCard, "Total Patients",     String.valueOf(pts.size()));
        addReportRow(summCard, "Active Patients",    String.valueOf(active));
        addReportRow(summCard, "Discharged Patients",String.valueOf(discharged));
        addReportRow(summCard, "Total Staff",        String.valueOf(stf.size()));
        addReportRow(summCard, "Doctors",            String.valueOf(doctors));
        addReportRow(summCard, "Nurses",             String.valueOf(nurses));
        addReportRow(summCard, "Monthly Payroll",    String.format(java.util.Locale.US, "$%.2f", totalPayroll));

        JPanel pieCard = makeCard("Patient Status");
        PieChart piePatients = new PieChart(
            new String[]{"Active", "Discharged"},
            new double[]{active, discharged},
            new Color[]{ACCENT2, new Color(231, 76, 60)}
        );
        piePatients.setPreferredSize(new Dimension(0, 160));
        pieCard.add(piePatients);

        row1.add(summCard);
        row1.add(pieCard);

        // ── Row 2: Staff Pie + Bar chart ────────────────────────────
        JPanel row2 = new JPanel(new GridLayout(1, 2, 14, 0));
        row2.setBackground(BG);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel staffPieCard = makeCard("Staff Distribution");
        PieChart pieStaff = new PieChart(
            new String[]{"Doctors", "Nurses", "Receptionists"},
            new double[]{doctors, nurses, receps},
            new Color[]{ACCENT, WARN, new Color(155, 89, 182)}
        );
        pieStaff.setPreferredSize(new Dimension(0, 160));
        staffPieCard.add(pieStaff);

        JPanel barCard = makeCard("Salary Breakdown (Bar Chart)");
        String[] barLabels = stf.stream().map(e -> e.getName().split(" ")[0]).toArray(String[]::new);
        double[] barVals   = stf.stream().mapToDouble(Employee::calculateSalary).toArray();
        BarChart barChart  = new BarChart(barLabels, barVals, ACCENT);
        barChart.setPreferredSize(new Dimension(0, 160));
        barCard.add(barChart);

        row2.add(staffPieCard);
        row2.add(barCard);

        // ── Patient Diagnoses Table ──────────────────────────────────
        JPanel diagCard = makeCard("Patient Diagnoses List");
        if (pts.isEmpty()) {
            JLabel noData = new JLabel("  No patients on record.");
            noData.setFont(FONT_SMALL);
            noData.setForeground(TEXT_MID);
            diagCard.add(noData);
        } else {
            for (Patient p : pts) {
                addReportRow(diagCard, p.getName() + "  (" + p.getFileNumber() + ")", p.getDiagnosis());
            }
        }

        // ── Salary Breakdown List ────────────────────────────────────
        JPanel salCard = makeCard("Staff Salary Breakdown");
        for (Employee e : stf) {
            addReportRow(salCard,
                e.getName() + "  [" + e.getRole() + "]",
                String.format(java.util.Locale.US, "$%.2f", e.calculateSalary()));
        }

        panel.add(Box.createVerticalStrut(4));
        panel.add(row1);
        panel.add(Box.createVerticalStrut(14));
        panel.add(row2);
        panel.add(Box.createVerticalStrut(14));
        panel.add(diagCard);
        panel.add(Box.createVerticalStrut(14));
        panel.add(salCard);
        panel.add(Box.createVerticalStrut(14));
        panel.revalidate();
        panel.repaint();
    }

    private JPanel makeCard(String heading) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 225, 235), 1, true),
            new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel h = new JLabel(heading);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setForeground(TEXT_DARK);
        card.add(h);
        card.add(Box.createVerticalStrut(8));
        return card;
    }

    private void addReportRow(JPanel card, String key, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        k.setForeground(TEXT_MID);
        // Force LTR + English digits
        k.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
        JLabel v = new JLabel(val, SwingConstants.RIGHT);
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(TEXT_DARK);
        v.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        card.add(row);
    }

    // ── PIE CHART ───────────────────────────────────────────────────
    private static class PieChart extends JPanel {
        private final String[] labels;
        private final double[] values;
        private final Color[]  colors;

        PieChart(String[] labels, double[] values, Color[] colors) {
            this.labels = labels; this.values = values; this.colors = colors;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = 0;
            for (double v : values) total += v;

            int size   = Math.min(getWidth() / 2 - 20, getHeight() - 20);
            if (size < 10) return;
            int cx = size / 2 + 10, cy = getHeight() / 2;
            int x  = cx - size / 2,  y  = cy - size / 2;

            if (total == 0) {
                g2.setColor(new Color(220, 220, 220));
                g2.fillOval(x, y, size, size);
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString("No data", x + size / 2 - 20, cy + 4);
            } else {
                double angle = 0;
                for (int i = 0; i < values.length; i++) {
                    double sweep = values[i] / total * 360.0;
                    g2.setColor(colors[i % colors.length]);
                    g2.fillArc(x, y, size, size, (int) angle, (int) sweep);
                    angle += sweep;
                }
                // white border between slices
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                angle = 0;
                for (double v : values) {
                    double sweep = v / total * 360.0;
                    g2.drawLine(cx, cy,
                        (int)(cx + size/2.0 * Math.cos(Math.toRadians(angle))),
                        (int)(cy - size/2.0 * Math.sin(Math.toRadians(angle))));
                    angle += sweep;
                }
            }

            // Legend
            int lx = cx + size / 2 + 20, ly = cy - (labels.length * 18) / 2;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int i = 0; i < labels.length; i++) {
                g2.setColor(colors[i % colors.length]);
                g2.fillRoundRect(lx, ly + i * 18, 12, 12, 4, 4);
                g2.setColor(new Color(44, 62, 80));
                String pct = total > 0 ? String.format(java.util.Locale.US, " %.0f%%", values[i] / total * 100) : " 0%";
                g2.drawString(labels[i] + pct, lx + 16, ly + i * 18 + 11);
            }
        }
    }

    // ── BAR CHART ───────────────────────────────────────────────────
    private static class BarChart extends JPanel {
        private final String[] labels;
        private final double[] values;
        private final Color    barColor;

        BarChart(String[] labels, double[] values, Color barColor) {
            this.labels = labels; this.values = values; this.barColor = barColor;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (values.length == 0) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padL = 10, padR = 10, padT = 10, padB = 30;
            int chartW = getWidth()  - padL - padR;
            int chartH = getHeight() - padT - padB;
            if (chartW < 10 || chartH < 10) return;

            double maxVal = 0;
            for (double v : values) if (v > maxVal) maxVal = v;
            if (maxVal == 0) maxVal = 1;

            int n = values.length;
            int barW = Math.max(4, (chartW / n) - 6);

            for (int i = 0; i < n; i++) {
                int barH  = (int)(values[i] / maxVal * chartH);
                int bx    = padL + i * (chartW / n) + (chartW / n - barW) / 2;
                int by    = padT + chartH - barH;

                // shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(bx + 2, by + 2, barW, barH, 6, 6);

                // bar
                g2.setColor(barColor);
                g2.fillRoundRect(bx, by, barW, barH, 6, 6);

                // value label
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(new Color(44, 62, 80));
                String valStr = String.format(java.util.Locale.US, "$%.0f", values[i]);
                FontMetrics fm = g2.getFontMetrics();
                int tx = bx + (barW - fm.stringWidth(valStr)) / 2;
                if (by - 3 > padT) g2.drawString(valStr, tx, by - 3);

                // name label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(new Color(127, 140, 141));
                String lbl = labels[i].length() > 6 ? labels[i].substring(0, 5) + "." : labels[i];
                int lx = bx + (barW - fm.stringWidth(lbl)) / 2;
                g2.drawString(lbl, lx, padT + chartH + 16);
            }

            // baseline
            g2.setColor(new Color(220, 225, 235));
            g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);
        }
    }

    // ── HELPERS ─────────────────────────────────────────────────────
    private JTable buildStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(FONT_BODY);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setBackground(CARD_BG);
        t.setSelectionBackground(new Color(200, 230, 255));
        t.setSelectionForeground(TEXT_DARK);
        t.setFillsViewportHeight(true);

        JTableHeader header = t.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(TBL_HEAD);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBackground(TBL_HEAD);
                setForeground(Color.WHITE);
                setFont(FONT_BOLD);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(SwingConstants.LEFT);
                setOpaque(true);
                return this;
            }
        });

        // Alternating row colors
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 249, 252));
                return this;
            }
        });
        return t;
    }

    private JButton makeStyledBtn(String label, Color bg, Color fg) {
        JButton btn = new JButton(label);
        btn.setFont(FONT_SMALL);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            public void mouseEntered(MouseEvent e) { btn.setBackground(orig.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(orig); }
        });
        return btn;
    }

    private void styleTextField(JTextField tf, String placeholder) {
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 215, 225), 1, true),
            new EmptyBorder(4, 8, 4, 8)));
        tf.setForeground(TEXT_DARK);
    }

    private void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveData() {
        try {
            data.savePatients();
            showSuccess(this, "Data saved successfully to patients.txt!");
        } catch (IOException e) {
            showError(this, "Failed to save: " + e.getMessage());
        }
    }

    // ── MAIN ────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(HospitalSystemUI::new);
    }
}
