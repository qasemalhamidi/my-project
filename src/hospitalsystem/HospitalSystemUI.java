package hospitalsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HospitalSystemUI extends JFrame {

    // ── Colors & Fonts ──────────────────────────────────────────────
    private static final Color BG       = new Color(245, 247, 252);
    private static final Color SIDEBAR  = new Color(26,  43,  77);
    private static final Color ACCENT   = new Color(52,  152, 219);
    private static final Color ACCENT2  = new Color(46,  204, 113);
    private static final Color DANGER   = new Color(231, 76,  60);
    private static final Color WARN     = new Color(243, 156, 18);
    private static final Color CARD_BG  = Color.WHITE;
    private static final Color TEXT_DARK= new Color(44,  62,  80);
    private static final Color TEXT_MID = new Color(127, 140, 141);
    private static final Color TBL_HEAD = new Color(52,  73,  94);
    private static final Color COMPOSITE_COLOR = new Color(155, 89, 182);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private final HospitalData data = new HospitalData();
    private final CardLayout cards  = new CardLayout();
    private final JPanel mainPanel  = new JPanel(cards);

    // Dashboard stats
    private JLabel lblTotalPatients, lblActivePatients, lblTotalStaff, lblDoctors;

    // Patient table
    private DefaultTableModel patientModel;
    private JTable patientTable;
    private JTextField tfPatSearch;

    // Staff table
    private DefaultTableModel staffModel;
    private JTable staffTable;
    private JTextField tfStaffSearch;

    // Reports
    private JPanel reportsContentPanel;

    // Departments (Composite) tree
    private DefaultTreeModel deptTreeModel;
    private JTree deptTree;
    private JTextArea deptInfoArea;

    // ── Constructor ─────────────────────────────────────────────────
    public HospitalSystemUI() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        try { data.loadPatients(); } catch (IOException ignored) {}

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);

        mainPanel.setBackground(BG);
        mainPanel.add(buildDashboard(),    "dashboard");
        mainPanel.add(buildPatientsPanel(),"patients");
        mainPanel.add(buildStaffPanel(),   "staff");
        mainPanel.add(buildDeptPanel(),    "departments");
        mainPanel.add(buildReportsPanel(), "reports");
        add(mainPanel, BorderLayout.CENTER);

        cards.show(mainPanel, "dashboard");
        refreshDashboard();
        setVisible(true);
    }

    // ── SIDEBAR ─────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(215, 0));
        sb.setBackground(SIDEBAR);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("+", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("Hospital System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        sb.add(logo);
        sb.add(Box.createVerticalStrut(4));
        sb.add(appName);
        sb.add(Box.createVerticalStrut(28));

        String[][] nav = {
            {"[=] Dashboard",    "dashboard"},
            {"[P] Patients",     "patients"},
            {"[S] Staff",        "staff"},
            {"[D] Departments",  "departments"},
            {"[R] Reports",      "reports"},
        };
        for (String[] item : nav) {
            sb.add(makeNavBtn(item[0], item[1]));
            sb.add(Box.createVerticalStrut(4));
        }

        sb.add(Box.createVerticalGlue());

        JButton btnSave = makeStyledBtn("Save Data", ACCENT2, Color.WHITE);
        btnSave.setMaximumSize(new Dimension(175, 38));
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
        btn.setMaximumSize(new Dimension(215, 42));
        btn.setPreferredSize(new Dimension(215, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 22, 0, 0));
        btn.addActionListener(e -> {
            cards.show(mainPanel, card);
            if ("dashboard".equals(card))   refreshDashboard();
            if ("patients".equals(card))    refreshPatientTable(data.getAllPatients());
            if ("staff".equals(card))       refreshStaffTable(data.getAllStaff());
            if ("departments".equals(card)) refreshDeptTree();
            if ("reports".equals(card))     buildReportsContent(reportsContentPanel);
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(44,62,80)); btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(SIDEBAR); btn.setForeground(new Color(189,195,199)); }
        });
        return btn;
    }

    // ── DASHBOARD ───────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setBackground(BG);
        statsRow.setBorder(new EmptyBorder(20, 0, 20, 0));

        lblTotalPatients  = new JLabel("0", SwingConstants.CENTER);
        lblActivePatients = new JLabel("0", SwingConstants.CENTER);
        lblTotalStaff     = new JLabel("0", SwingConstants.CENTER);
        lblDoctors        = new JLabel("0", SwingConstants.CENTER);

        statsRow.add(makeStatCard("Total Patients",  lblTotalPatients,  ACCENT,  "Pts"));
        statsRow.add(makeStatCard("Active Patients",  lblActivePatients, ACCENT2, "Act"));
        statsRow.add(makeStatCard("Staff Members",    lblTotalStaff,     WARN,    "Stf"));
        statsRow.add(makeStatCard("Doctors",          lblDoctors,        DANGER,  "Doc"));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(statsRow, BorderLayout.NORTH);

        JPanel qa = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        qa.setBackground(BG);
        JLabel qaTitle = new JLabel("Quick Actions");
        qaTitle.setFont(FONT_BOLD); qaTitle.setForeground(TEXT_DARK);

        JButton b1 = makeStyledBtn("+ Add Patient",  ACCENT, Color.WHITE);
        JButton b2 = makeStyledBtn("+ Add Staff",    ACCENT2, Color.WHITE);
        JButton b3 = makeStyledBtn("Departments",    COMPOSITE_COLOR, Color.WHITE);
        JButton b4 = makeStyledBtn("View Reports",   new Color(52,73,94), Color.WHITE);

        b1.addActionListener(e -> { cards.show(mainPanel,"patients");    showAddPatientDialog(); });
        b2.addActionListener(e -> { cards.show(mainPanel,"staff");       showAddEmployeeDialog(); });
        b3.addActionListener(e -> { cards.show(mainPanel,"departments"); refreshDeptTree(); });
        b4.addActionListener(e ->   cards.show(mainPanel,"reports"));

        qa.add(b1); qa.add(b2); qa.add(b3); qa.add(b4);

        JPanel qaWrap = new JPanel(new BorderLayout());
        qaWrap.setBackground(BG);
        qaWrap.setBorder(new EmptyBorder(10,0,10,0));
        qaWrap.add(qaTitle, BorderLayout.NORTH);
        qaWrap.add(qa, BorderLayout.CENTER);
        center.add(qaWrap, BorderLayout.CENTER);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeStatCard(String label, JLabel valueLbl, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(220,225,235), 1, true),
            new EmptyBorder(16,18,16,18)));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        iconLbl.setForeground(accent);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(accent);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL); lbl.setForeground(TEXT_MID);
        JPanel top = new JPanel(new BorderLayout()); top.setBackground(CARD_BG);
        top.add(iconLbl, BorderLayout.WEST);
        card.add(top, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private void refreshDashboard() {
        lblTotalPatients.setText(String.valueOf(data.getAllPatients().size()));
        lblActivePatients.setText(String.valueOf(data.countActive()));
        lblTotalStaff.setText(String.valueOf(data.getTotalHeadCount())); // من الـ Composite!
        lblDoctors.setText(String.valueOf(data.getAllStaff().stream().filter(e -> e instanceof Doctor).count()));
    }

    // ── PATIENTS PANEL ──────────────────────────────────────────────
    private JPanel buildPatientsPanel() {
        JPanel p = new JPanel(new BorderLayout(0,12));
        p.setBackground(BG); p.setBorder(new EmptyBorder(20,20,20,20));

        JPanel header = new JPanel(new BorderLayout()); header.setBackground(BG);
        JLabel title = new JLabel("Patient Management");
        title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); hr.setBackground(BG);
        tfPatSearch = new JTextField(16); styleTextField(tfPatSearch,"Search patients...");
        JButton btnSrch = makeStyledBtn("Search",ACCENT,Color.WHITE);
        JButton btnAdd  = makeStyledBtn("+ Add Patient",ACCENT,Color.WHITE);
        JButton btnEdt  = makeStyledBtn("Edit",WARN,Color.WHITE);
        JButton btnDel  = makeStyledBtn("Delete",DANGER,Color.WHITE);

        btnSrch.addActionListener(e -> { String q=tfPatSearch.getText().trim();
            refreshPatientTable(q.isEmpty()?data.getAllPatients():data.searchPatients(q)); });
        tfPatSearch.addActionListener(e->btnSrch.doClick());
        btnAdd.addActionListener(e->showAddPatientDialog());
        btnEdt.addActionListener(e->showEditPatientDialog());
        btnDel.addActionListener(e->deleteSelectedPatient());

        hr.add(tfPatSearch); hr.add(btnSrch); hr.add(btnAdd); hr.add(btnEdt); hr.add(btnDel);
        header.add(title, BorderLayout.WEST); header.add(hr, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        String[] cols = {"File No.","ID","Name","Age","Phone","Blood","Diagnosis","Address","Status"};
        patientModel = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        patientTable = buildStyledTable(patientModel);
        p.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        refreshPatientTable(data.getAllPatients());
        return p;
    }

    private void refreshPatientTable(List<Patient> list) {
        patientModel.setRowCount(0);
        for (Patient pt : list)
            patientModel.addRow(new Object[]{pt.getFileNumber(),pt.getId(),pt.getName(),
                pt.getAge(),pt.getPhoneNumber(),pt.getBloodType(),pt.getDiagnosis(),pt.getAddress(),pt.getStatus()});
    }

    private void showAddPatientDialog() {
        JDialog dlg = new JDialog(this,"Add New Patient",true);
        dlg.setSize(460,440); dlg.setLocationRelativeTo(this); dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG); form.setBorder(new EmptyBorder(16,16,8,16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets=new Insets(6,6,6,6); gc.fill=GridBagConstraints.HORIZONTAL;

        JTextField fId=new JTextField(14),fName=new JTextField(14),fAge=new JTextField(14),
                   fFile=new JTextField(14),fPhone=new JTextField(14),fAddr=new JTextField(14),fDiag=new JTextField(14);
        JComboBox<String> cbBlood=new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-","Unknown"});
        JComboBox<String> cbStatus=new JComboBox<>(new String[]{"Active","Discharged"});

        Object[][] rows={{"Patient ID *",fId},{"Full Name *",fName},{"Age *",fAge},
            {"File Number *",fFile},{"Phone",fPhone},{"Address",fAddr},
            {"Diagnosis *",fDiag},{"Blood Type",cbBlood},{"Status",cbStatus}};
        for(int i=0;i<rows.length;i++){
            gc.gridx=0;gc.gridy=i;gc.weightx=0;
            JLabel l=new JLabel((String)rows[i][0]);l.setFont(FONT_SMALL);form.add(l,gc);
            gc.gridx=1;gc.weightx=1;form.add((Component)rows[i][1],gc);}

        JButton btnOk=makeStyledBtn("Add Patient",ACCENT2,Color.WHITE);
        btnOk.addActionListener(e->{
            try {
                int id=Integer.parseInt(fId.getText().trim());
                String name=fName.getText().trim(); int age=Integer.parseInt(fAge.getText().trim());
                String file=fFile.getText().trim();
                if(name.isEmpty()||file.isEmpty()){showError(dlg,"Name and File Number required.");return;}
                Patient pt=new PatientBuilder(id,name,age,file)
                        .address(fAddr.getText().trim()).diagnosis(fDiag.getText().trim())
                        .phoneNumber(fPhone.getText().trim()).bloodType((String)cbBlood.getSelectedItem())
                        .status((String)cbStatus.getSelectedItem()).build();
                if(!data.addPatient(pt)){showError(dlg,"Duplicate ID or File Number!");return;}
                refreshPatientTable(data.getAllPatients()); refreshDashboard();
                showSuccess(dlg,"Patient added!"); dlg.dispose();
            } catch(NumberFormatException ex){showError(dlg,"ID and Age must be numbers.");}
        });

        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setBackground(CARD_BG);
        JButton btnCancel=makeStyledBtn("Cancel",TEXT_MID,Color.WHITE); btnCancel.addActionListener(x->dlg.dispose());
        bottom.add(btnCancel); bottom.add(btnOk);
        dlg.add(form,BorderLayout.CENTER); dlg.add(bottom,BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void showEditPatientDialog() {
        int row=patientTable.getSelectedRow();
        if(row<0){showError(this,"Select a patient to edit.");return;}
        String file=(String)patientModel.getValueAt(row,0);
        Patient p=data.searchByFile(file); if(p==null)return;

        JDialog dlg=new JDialog(this,"Edit Patient: "+p.getName(),true);
        dlg.setSize(440,340); dlg.setLocationRelativeTo(this); dlg.setLayout(new BorderLayout());

        JPanel form=new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG); form.setBorder(new EmptyBorder(16,16,8,16));
        GridBagConstraints gc=new GridBagConstraints(); gc.insets=new Insets(6,6,6,6); gc.fill=GridBagConstraints.HORIZONTAL;

        JTextField fAddr=new JTextField(p.getAddress(),14),fDiag=new JTextField(p.getDiagnosis(),14),fPhone=new JTextField(p.getPhoneNumber(),14);
        JComboBox<String> cbBlood=new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-","Unknown"});
        cbBlood.setSelectedItem(p.getBloodType());
        JComboBox<String> cbStatus=new JComboBox<>(new String[]{"Active","Discharged"});
        cbStatus.setSelectedItem(p.getStatus());

        Object[][] rows={{"Address",fAddr},{"Diagnosis",fDiag},{"Phone",fPhone},{"Blood Type",cbBlood},{"Status",cbStatus}};
        for(int i=0;i<rows.length;i++){
            gc.gridx=0;gc.gridy=i;gc.weightx=0;JLabel l=new JLabel((String)rows[i][0]);l.setFont(FONT_SMALL);form.add(l,gc);
            gc.gridx=1;gc.weightx=1;form.add((Component)rows[i][1],gc);}

        JButton btnOk=makeStyledBtn("Save",ACCENT2,Color.WHITE);
        btnOk.addActionListener(e->{
            data.updatePatient(file,fAddr.getText().trim(),fDiag.getText().trim(),
                    fPhone.getText().trim(),(String)cbBlood.getSelectedItem(),(String)cbStatus.getSelectedItem());
            refreshPatientTable(data.getAllPatients()); showSuccess(dlg,"Patient updated."); dlg.dispose();
        });

        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setBackground(CARD_BG);
        JButton btnCancel=makeStyledBtn("Cancel",TEXT_MID,Color.WHITE); btnCancel.addActionListener(x->dlg.dispose());
        bottom.add(btnCancel); bottom.add(btnOk);
        dlg.add(form,BorderLayout.CENTER); dlg.add(bottom,BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void deleteSelectedPatient() {
        int row=patientTable.getSelectedRow();
        if(row<0){showError(this,"Select a patient.");return;}
        String file=(String)patientModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Delete patient "+file+"?","Confirm",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            data.deletePatient(file); refreshPatientTable(data.getAllPatients()); refreshDashboard();
        }
    }

    // ── STAFF PANEL ─────────────────────────────────────────────────
    private JPanel buildStaffPanel() {
        JPanel p=new JPanel(new BorderLayout(0,12));
        p.setBackground(BG); p.setBorder(new EmptyBorder(20,20,20,20));

        JPanel header=new JPanel(new BorderLayout()); header.setBackground(BG);
        JLabel title=new JLabel("Staff Management"); title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);

        JPanel hr=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); hr.setBackground(BG);
        tfStaffSearch=new JTextField(16); styleTextField(tfStaffSearch,"Search staff...");
        JButton btnSrch=makeStyledBtn("Search",ACCENT,Color.WHITE);
        JButton btnAdd=makeStyledBtn("+ Add Staff",ACCENT,Color.WHITE);
        JButton btnEdt=makeStyledBtn("Edit",WARN,Color.WHITE);
        JButton btnDel=makeStyledBtn("Remove",DANGER,Color.WHITE);

        btnSrch.addActionListener(e->{String q=tfStaffSearch.getText().trim();
            refreshStaffTable(q.isEmpty()?data.getAllStaff():data.searchStaff(q));});
        tfStaffSearch.addActionListener(e->btnSrch.doClick());
        btnAdd.addActionListener(e->showAddEmployeeDialog());
        btnEdt.addActionListener(e->showEditEmployeeDialog());
        btnDel.addActionListener(e->deleteSelectedEmployee());

        hr.add(tfStaffSearch);hr.add(btnSrch);hr.add(btnAdd);hr.add(btnEdt);hr.add(btnDel);
        header.add(title,BorderLayout.WEST); header.add(hr,BorderLayout.EAST);
        p.add(header,BorderLayout.NORTH);

        String[] cols={"ID","Name","Age","Role","Department","Details","Base Salary","Net Salary"};
        staffModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        staffTable=buildStyledTable(staffModel);
        p.add(new JScrollPane(staffTable),BorderLayout.CENTER);
        refreshStaffTable(data.getAllStaff());
        return p;
    }

    private void refreshStaffTable(List<Employee> list) {
        staffModel.setRowCount(0);
        for(Employee e:list){
            String details="";
            if(e instanceof Doctor d) details=d.getSpecialization()+" ("+d.getYearsOfExperience()+" yrs)";
            else if(e instanceof Nurse n) details=n.getShift()+" shift";
            staffModel.addRow(new Object[]{e.getId(),e.getName(),e.getAge(),e.getRole(),
                e.getDepartment(),details,
                String.format(Locale.US,"%.2f",e.getBaseSalary()),
                String.format(Locale.US,"%.2f",e.calculateSalary())});
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dlg=new JDialog(this,"Add New Staff Member",true);
        dlg.setSize(460,420); dlg.setLocationRelativeTo(this); dlg.setLayout(new BorderLayout());

        JPanel form=new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG); form.setBorder(new EmptyBorder(16,16,8,16));
        GridBagConstraints gc=new GridBagConstraints(); gc.insets=new Insets(6,6,6,6); gc.fill=GridBagConstraints.HORIZONTAL;

        JTextField fId=new JTextField(14),fName=new JTextField(14),fAge=new JTextField(14),
                   fDept=new JTextField(14),fSalary=new JTextField(14),fSpec=new JTextField(14),fExtra=new JTextField(14);
        JComboBox<String> cbRole=new JComboBox<>(new String[]{"Doctor","Nurse","Receptionist"});
        JLabel lblSpec=new JLabel("Specialization"), lblExtra=new JLabel("Years Exp.");

        cbRole.addActionListener(e->{
            String r=(String)cbRole.getSelectedItem();
            if("Doctor".equals(r)){lblSpec.setText("Specialization");lblExtra.setText("Years Exp.");fSpec.setEnabled(true);fExtra.setEnabled(true);}
            else if("Nurse".equals(r)){lblSpec.setText("Shift (Day/Night)");lblExtra.setText("-");fSpec.setEnabled(true);fExtra.setEnabled(false);fExtra.setText("");}
            else{lblSpec.setText("-");lblExtra.setText("-");fSpec.setEnabled(false);fSpec.setText("");fExtra.setEnabled(false);fExtra.setText("");}
        });

        Object[][] rows={{"Employee ID *",fId},{"Full Name *",fName},{"Age",fAge},
            {"Role *",cbRole},{"Department",fDept},{"Base Salary *",fSalary}};
        for(int i=0;i<rows.length;i++){
            gc.gridx=0;gc.gridy=i;gc.weightx=0;JLabel l=new JLabel((String)rows[i][0]);l.setFont(FONT_SMALL);form.add(l,gc);
            gc.gridx=1;gc.weightx=1;form.add((Component)rows[i][1],gc);}
        gc.gridx=0;gc.gridy=rows.length;gc.weightx=0;lblSpec.setFont(FONT_SMALL);form.add(lblSpec,gc);
        gc.gridx=1;gc.weightx=1;form.add(fSpec,gc);
        gc.gridx=0;gc.gridy=rows.length+1;gc.weightx=0;lblExtra.setFont(FONT_SMALL);form.add(lblExtra,gc);
        gc.gridx=1;gc.weightx=1;form.add(fExtra,gc);

        JButton btnOk=makeStyledBtn("Add Staff",ACCENT2,Color.WHITE);
        btnOk.addActionListener(e->{
            try{
                int id=Integer.parseInt(fId.getText().trim());
                String name=fName.getText().trim();
                if(name.isEmpty()){showError(dlg,"Name required.");return;}
                int age=fAge.getText().trim().isEmpty()?30:Integer.parseInt(fAge.getText().trim());
                double sal=Double.parseDouble(fSalary.getText().trim());
                String dept=fDept.getText().trim();
                String role=(String)cbRole.getSelectedItem();
                EmployeeBuilder eb=new EmployeeBuilder(id,name,age).role(role).baseSalary(sal).department(dept);
                if("Doctor".equals(role)){
                    int yrs=fExtra.getText().trim().isEmpty()?0:Integer.parseInt(fExtra.getText().trim());
                    eb.specialization(fSpec.getText().trim()).yearsOfExperience(yrs);
                } else if("Nurse".equals(role)){
                    eb.shift(fSpec.getText().trim().isEmpty()?"Day":fSpec.getText().trim());
                }
                Employee emp=eb.build();
                if(!data.addEmployee(emp)){showError(dlg,"Duplicate employee ID!");return;}
                refreshStaffTable(data.getAllStaff()); refreshDashboard(); refreshDeptTree();
                showSuccess(dlg,"Staff member added!"); dlg.dispose();
            } catch(NumberFormatException ex){showError(dlg,"Check numeric fields.");}
        });

        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setBackground(CARD_BG);
        JButton btnCancel=makeStyledBtn("Cancel",TEXT_MID,Color.WHITE); btnCancel.addActionListener(x->dlg.dispose());
        bottom.add(btnCancel); bottom.add(btnOk);
        dlg.add(form,BorderLayout.CENTER); dlg.add(bottom,BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void showEditEmployeeDialog() {
        int row=staffTable.getSelectedRow();
        if(row<0){showError(this,"Select a staff member to edit.");return;}
        int id=(int)staffModel.getValueAt(row,0);
        Employee emp=data.findEmployee(id); if(emp==null)return;

        JDialog dlg=new JDialog(this,"Edit Staff: "+emp.getName(),true);
        dlg.setSize(440,360); dlg.setLocationRelativeTo(this); dlg.setLayout(new BorderLayout());

        JPanel form=new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG); form.setBorder(new EmptyBorder(16,16,8,16));
        GridBagConstraints gc=new GridBagConstraints(); gc.insets=new Insets(6,6,6,6); gc.fill=GridBagConstraints.HORIZONTAL;

        JTextField fName=new JTextField(emp.getName(),14),fAge=new JTextField(String.valueOf(emp.getAge()),14),
                   fDept=new JTextField(emp.getDepartment()!=null?emp.getDepartment():"",14),
                   fSalary=new JTextField(String.valueOf(emp.getBaseSalary()),14),
                   fSpec=new JTextField(14),fExtra=new JTextField(14);
        JLabel lblSpec=new JLabel("Specialization"),lblExtra=new JLabel("Years Exp.");

        if(emp instanceof Doctor d){fSpec.setText(d.getSpecialization());fExtra.setText(String.valueOf(d.getYearsOfExperience()));}
        else if(emp instanceof Nurse n){lblSpec.setText("Shift");fSpec.setText(n.getShift());fExtra.setEnabled(false);lblExtra.setText("-");}
        else{fSpec.setEnabled(false);fExtra.setEnabled(false);lblSpec.setText("-");lblExtra.setText("-");}

        Object[][] rows={{"Full Name *",fName},{"Age",fAge},{"Department",fDept},{"Base Salary *",fSalary}};
        for(int i=0;i<rows.length;i++){
            gc.gridx=0;gc.gridy=i;gc.weightx=0;JLabel l=new JLabel((String)rows[i][0]);l.setFont(FONT_SMALL);form.add(l,gc);
            gc.gridx=1;gc.weightx=1;form.add((Component)rows[i][1],gc);}
        gc.gridx=0;gc.gridy=rows.length;gc.weightx=0;lblSpec.setFont(FONT_SMALL);form.add(lblSpec,gc);
        gc.gridx=1;gc.weightx=1;form.add(fSpec,gc);
        gc.gridx=0;gc.gridy=rows.length+1;gc.weightx=0;lblExtra.setFont(FONT_SMALL);form.add(lblExtra,gc);
        gc.gridx=1;gc.weightx=1;form.add(fExtra,gc);

        JButton btnOk=makeStyledBtn("Save Changes",ACCENT2,Color.WHITE);
        btnOk.addActionListener(e->{
            try{
                String name=fName.getText().trim();
                if(name.isEmpty()){showError(dlg,"Name required.");return;}
                int age=fAge.getText().trim().isEmpty()?emp.getAge():Integer.parseInt(fAge.getText().trim());
                double sal=Double.parseDouble(fSalary.getText().trim());
                emp.setName(name);emp.setAge(age);emp.setDepartment(fDept.getText().trim());emp.setBaseSalary(sal);
                if(emp instanceof Doctor d){d.setSpecialization(fSpec.getText().trim());
                    if(!fExtra.getText().trim().isEmpty())d.setYearsOfExperience(Integer.parseInt(fExtra.getText().trim()));}
                else if(emp instanceof Nurse n){if(!fSpec.getText().trim().isEmpty())n.setShift(fSpec.getText().trim());}
                refreshStaffTable(data.getAllStaff()); refreshDeptTree();
                showSuccess(dlg,"Staff updated."); dlg.dispose();
            }catch(NumberFormatException ex){showError(dlg,"Check numeric fields.");}
        });

        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setBackground(CARD_BG);
        JButton btnCancel=makeStyledBtn("Cancel",TEXT_MID,Color.WHITE); btnCancel.addActionListener(x->dlg.dispose());
        bottom.add(btnCancel); bottom.add(btnOk);
        dlg.add(form,BorderLayout.CENTER); dlg.add(bottom,BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void deleteSelectedEmployee() {
        int row=staffTable.getSelectedRow();
        if(row<0){showError(this,"Select a staff member.");return;}
        int id=(int)staffModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Remove staff ID "+id+"?","Confirm",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            data.deleteEmployee(id); refreshStaffTable(data.getAllStaff()); refreshDashboard(); refreshDeptTree();
        }
    }

    // ── DEPARTMENTS PANEL — Composite Pattern Visualization ──────────
    private JPanel buildDeptPanel() {
        JPanel p = new JPanel(new BorderLayout(0,12));
        p.setBackground(BG); p.setBorder(new EmptyBorder(20,20,20,20));

        // Header
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(BG);
        JLabel title = new JLabel("Departments  (Composite Pattern)");
        title.setFont(FONT_TITLE); title.setForeground(COMPOSITE_COLOR);

        JLabel desc = new JLabel("Each department is a Composite — click any node to see its salary and headcount.");
        desc.setFont(FONT_SMALL); desc.setForeground(TEXT_MID);

        JPanel titleBlock = new JPanel(new BorderLayout(0,4)); titleBlock.setBackground(BG);
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(desc,  BorderLayout.SOUTH);
        header.add(titleBlock, BorderLayout.WEST);
        p.add(header, BorderLayout.NORTH);

        // Split: tree left | info right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(320);
        split.setBackground(BG);
        split.setBorder(null);

        // Tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Hospital");
        deptTreeModel = new DefaultTreeModel(root);
        deptTree = new JTree(deptTreeModel);
        deptTree.setFont(FONT_BODY);
        deptTree.setBackground(CARD_BG);
        deptTree.setRowHeight(28);
        deptTree.setBorder(new EmptyBorder(8,8,8,8));

        JScrollPane treeSp = new JScrollPane(deptTree);
        treeSp.setBorder(new LineBorder(new Color(220,225,235),1,true));

        // Info area
        deptInfoArea = new JTextArea();
        deptInfoArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        deptInfoArea.setEditable(false);
        deptInfoArea.setBackground(new Color(250,250,252));
        deptInfoArea.setBorder(new EmptyBorder(12,14,12,14));
        deptInfoArea.setForeground(TEXT_DARK);
        JScrollPane infoSp = new JScrollPane(deptInfoArea);
        infoSp.setBorder(new LineBorder(new Color(220,225,235),1,true));

        // When tree node selected → show composite info
        deptTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) deptTree.getLastSelectedPathComponent();
            if(node == null) return;
            showDeptInfo(node.getUserObject().toString());
        });

        split.setLeftComponent(treeSp);
        split.setRightComponent(infoSp);
        p.add(split, BorderLayout.CENTER);

        refreshDeptTree();
        return p;
    }

    private void refreshDeptTree() {
        if(deptTree == null) return;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            "Hospital  [Staff: " + data.getTotalHeadCount() + "  |  Payroll: $"
            + String.format(Locale.US,"%.0f", data.getTotalPayroll()) + "]");

        for(Department dept : data.getDepartments()) {
            DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(
                dept.getName() + "  [" + dept.getHeadCount() + " staff  |  $"
                + String.format(Locale.US,"%.0f", dept.getTotalSalary()) + "]");

            for(HospitalComponent child : dept.getChildren()) {
                if(child instanceof EmployeeLeaf leaf) {
                    dNode.add(new DefaultMutableTreeNode(
                        "[" + leaf.getType() + "]  " + leaf.getName()
                        + "  —  $" + String.format(Locale.US,"%.0f", leaf.getTotalSalary())));
                }
            }
            root.add(dNode);
        }

        deptTreeModel.setRoot(root);
        // Expand all
        for(int i=0; i<deptTree.getRowCount(); i++) deptTree.expandRow(i);

        // Show hospital-level summary in info area
        showDeptInfo("Hospital");
    }

    private void showDeptInfo(String nodeName) {
        // Extract dept name (before "  [")
        String name = nodeName.contains("  [") ? nodeName.substring(0, nodeName.indexOf("  [")) : nodeName;

        StringBuilder sb = new StringBuilder();

        if("Hospital".equals(name)) {
            sb.append("=== HOSPITAL (Root Composite) ===\n\n");
            sb.append(String.format(Locale.US, "Total Staff   : %d\n", data.getTotalHeadCount()));
            sb.append(String.format(Locale.US, "Total Payroll : $%.2f\n\n", data.getTotalPayroll()));
            sb.append("Departments:\n");
            for(Department d : data.getDepartments()) {
                sb.append(String.format(Locale.US, "  %-15s  %2d staff   $%.0f\n",
                        d.getName(), d.getHeadCount(), d.getTotalSalary()));
            }
            sb.append("\n--- Composite Pattern ---\n");
            sb.append("getTotalSalary() on Hospital\n");
            sb.append("= sum of all departments\n");
            sb.append("= sum of all employees\n");
            sb.append("recursively.\n");
        } else {
            // Find matching department
            data.getDepartments().stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .ifPresentOrElse(dept -> {
                    sb.append("=== DEPARTMENT: ").append(dept.getName()).append(" ===\n\n");
                    sb.append(String.format(Locale.US,"Head Count    : %d\n", dept.getHeadCount()));
                    sb.append(String.format(Locale.US,"Total Payroll : $%.2f\n\n", dept.getTotalSalary()));
                    sb.append("Members:\n");
                    for(HospitalComponent c : dept.getChildren()) {
                        if(c instanceof EmployeeLeaf leaf) {
                            sb.append(String.format(Locale.US,"  [%-13s] %-15s  $%.2f\n",
                                    leaf.getType(), leaf.getName(), leaf.getTotalSalary()));
                        }
                    }
                    sb.append("\n--- Composite Pattern ---\n");
                    sb.append("dept.getTotalSalary()\n= sum of all members\nautomatically.\n");
                }, () -> {
                    // Leaf node — employee
                    String empName = name.contains("]  ") ? name.substring(name.indexOf("]  ")+3) : name;
                    empName = empName.contains("  —") ? empName.substring(0, empName.indexOf("  —")).trim() : empName;
                    String finalEmpName = empName;
                    data.getAllStaff().stream()
                        .filter(e -> e.getName().equalsIgnoreCase(finalEmpName))
                        .findFirst()
                        .ifPresent(e -> {
                            sb.append("=== EMPLOYEE (Leaf) ===\n\n");
                            sb.append("Name       : ").append(e.getName()).append("\n");
                            sb.append("Role       : ").append(e.getRole()).append("\n");
                            sb.append("Dept       : ").append(e.getDepartment()).append("\n");
                            sb.append(String.format(Locale.US,"Base Salary: $%.2f\n", e.getBaseSalary()));
                            sb.append(String.format(Locale.US,"Net Salary : $%.2f\n", e.calculateSalary()));
                            if(e instanceof Doctor d) sb.append("Spec.      : ").append(d.getSpecialization()).append("\n");
                            if(e instanceof Nurse n)  sb.append("Shift      : ").append(n.getShift()).append("\n");
                            sb.append("\n--- Composite Pattern ---\n");
                            sb.append("Leaf: getHeadCount() = 1\n");
                            sb.append("getTotalSalary() = own salary only\n");
                        });
                });
        }
        deptInfoArea.setText(sb.toString());
    }

    // ── REPORTS PANEL ───────────────────────────────────────────────
    private JPanel buildReportsPanel() {
        JPanel p=new JPanel(new BorderLayout(0,12));
        p.setBackground(BG); p.setBorder(new EmptyBorder(20,20,20,20));

        JLabel title=new JLabel("Reports & Statistics"); title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);
        JButton btnRef=makeStyledBtn("Refresh",ACCENT,Color.WHITE);
        btnRef.addActionListener(e->buildReportsContent(reportsContentPanel));
        JPanel titleRow=new JPanel(new BorderLayout()); titleRow.setBackground(BG);
        titleRow.add(title,BorderLayout.WEST); titleRow.add(btnRef,BorderLayout.EAST);
        p.add(titleRow,BorderLayout.NORTH);

        reportsContentPanel=new JPanel();
        reportsContentPanel.setLayout(new BoxLayout(reportsContentPanel,BoxLayout.Y_AXIS));
        reportsContentPanel.setBackground(BG);
        JScrollPane sp=new JScrollPane(reportsContentPanel);
        sp.setBorder(null); sp.setBackground(BG); sp.getVerticalScrollBar().setUnitIncrement(16);
        p.add(sp,BorderLayout.CENTER);
        buildReportsContent(reportsContentPanel);
        return p;
    }

    private void buildReportsContent(JPanel panel) {
        panel.removeAll();
        List<Patient> pts=data.getAllPatients(); List<Employee> stf=data.getAllStaff();
        long active=data.countActive(),discharged=pts.stream().filter(p->"Discharged".equals(p.getStatus())).count();
        long doctors=stf.stream().filter(e->e instanceof Doctor).count();
        long nurses=stf.stream().filter(e->e instanceof Nurse).count();
        long receps=stf.stream().filter(e->e instanceof Receptionist).count();
        double totalPayroll=data.getTotalPayroll(); // من الـ Composite!

        JPanel row1=new JPanel(new GridLayout(1,2,14,0)); row1.setBackground(BG); row1.setMaximumSize(new Dimension(Integer.MAX_VALUE,220));
        JPanel summCard=makeCard("Summary Statistics");
        addReportRow(summCard,"Total Patients",    String.valueOf(pts.size()));
        addReportRow(summCard,"Active Patients",   String.valueOf(active));
        addReportRow(summCard,"Discharged Patients",String.valueOf(discharged));
        addReportRow(summCard,"Total Staff",       String.valueOf(data.getTotalHeadCount()));
        addReportRow(summCard,"Doctors",           String.valueOf(doctors));
        addReportRow(summCard,"Nurses",            String.valueOf(nurses));
        addReportRow(summCard,"Monthly Payroll",   String.format(Locale.US,"$%.2f",totalPayroll));

        JPanel pieCard=makeCard("Patient Status");
        PieChart pie1=new PieChart(new String[]{"Active","Discharged"},new double[]{active,discharged},
                new Color[]{ACCENT2,DANGER}); pie1.setPreferredSize(new Dimension(0,160)); pieCard.add(pie1);
        row1.add(summCard); row1.add(pieCard);

        JPanel row2=new JPanel(new GridLayout(1,2,14,0)); row2.setBackground(BG); row2.setMaximumSize(new Dimension(Integer.MAX_VALUE,220));
        JPanel staffPie=makeCard("Staff Distribution");
        PieChart pie2=new PieChart(new String[]{"Doctors","Nurses","Receptionists"},new double[]{doctors,nurses,receps},
                new Color[]{ACCENT,WARN,COMPOSITE_COLOR}); pie2.setPreferredSize(new Dimension(0,160)); staffPie.add(pie2);

        JPanel barCard=makeCard("Salary Breakdown (Bar Chart)");
        String[] bLabels=stf.stream().map(e->e.getName().split(" ")[0]).toArray(String[]::new);
        double[] bVals=stf.stream().mapToDouble(Employee::calculateSalary).toArray();
        BarChart bar=new BarChart(bLabels,bVals,ACCENT); bar.setPreferredSize(new Dimension(0,160)); barCard.add(bar);
        row2.add(staffPie); row2.add(barCard);

        // Dept payroll breakdown — uses Composite!
        JPanel deptCard=makeCard("Department Payroll  (via Composite Pattern)");
        for(Department d : data.getDepartments()) {
            if(d.getHeadCount()>0)
                addReportRow(deptCard, d.getName()+" ("+d.getHeadCount()+" staff)",
                        String.format(Locale.US,"$%.2f",d.getTotalSalary()));
        }

        JPanel diagCard=makeCard("Patient Diagnoses List");
        if(pts.isEmpty()){JLabel nd=new JLabel("  No patients on record.");nd.setFont(FONT_SMALL);nd.setForeground(TEXT_MID);diagCard.add(nd);}
        else for(Patient p:pts) addReportRow(diagCard,p.getName()+"  ("+p.getFileNumber()+")",p.getDiagnosis());

        JPanel salCard=makeCard("Staff Salary Breakdown");
        for(Employee e:stf) addReportRow(salCard,e.getName()+"  ["+e.getRole()+"]",String.format(Locale.US,"$%.2f",e.calculateSalary()));

        panel.add(Box.createVerticalStrut(4));
        panel.add(row1); panel.add(Box.createVerticalStrut(14));
        panel.add(row2); panel.add(Box.createVerticalStrut(14));
        panel.add(deptCard); panel.add(Box.createVerticalStrut(14));
        panel.add(diagCard); panel.add(Box.createVerticalStrut(14));
        panel.add(salCard);  panel.add(Box.createVerticalStrut(14));
        panel.revalidate(); panel.repaint();
    }

    // ── TABLE BUILDER ────────────────────────────────────────────────
    private JTable buildStyledTable(DefaultTableModel model) {
        JTable t=new JTable(model);
        t.setFont(FONT_BODY); t.setRowHeight(32); t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0)); t.setBackground(CARD_BG);
        t.setSelectionBackground(new Color(200,230,255)); t.setSelectionForeground(TEXT_DARK);
        t.setFillsViewportHeight(true);
        JTableHeader h=t.getTableHeader();
        h.setFont(FONT_BOLD); h.setReorderingAllowed(false);
        h.setDefaultRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tbl,Object val,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(tbl,val,sel,foc,r,c);
                setBackground(TBL_HEAD); setForeground(Color.WHITE); setFont(FONT_BOLD);
                setBorder(new EmptyBorder(0,8,0,8)); setHorizontalAlignment(LEFT); setOpaque(true);
                return this;}});
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tbl,Object val,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(tbl,val,sel,foc,r,c);
                setBorder(new EmptyBorder(0,8,0,8));
                if(!sel)setBackground(r%2==0?CARD_BG:new Color(248,249,252));
                return this;}});
        return t;
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private JPanel makeCard(String heading){
        JPanel c=new JPanel(); c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setBackground(CARD_BG);
        c.setBorder(new CompoundBorder(new LineBorder(new Color(220,225,235),1,true),new EmptyBorder(14,16,14,16)));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        JLabel h=new JLabel(heading); h.setFont(FONT_BOLD); h.setForeground(TEXT_DARK); c.add(h);
        c.add(Box.createVerticalStrut(8)); return c;}

    private void addReportRow(JPanel card,String key,String val){
        JPanel row=new JPanel(new BorderLayout()); row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,26));
        JLabel k=new JLabel(key); k.setFont(new Font("Segoe UI",Font.PLAIN,12)); k.setForeground(TEXT_MID);
        k.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
        JLabel v=new JLabel(val,SwingConstants.RIGHT); v.setFont(new Font("Segoe UI",Font.BOLD,12)); v.setForeground(TEXT_DARK);
        v.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
        row.add(k,BorderLayout.WEST); row.add(v,BorderLayout.EAST); card.add(row);}

    private JButton makeStyledBtn(String label,Color bg,Color fg){
        JButton btn=new JButton(label); btn.setFont(FONT_SMALL); btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7,14,7,14)); btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter(){
            Color o=bg;
            public void mouseEntered(MouseEvent e){btn.setBackground(o.darker());}
            public void mouseExited(MouseEvent e){btn.setBackground(o);}});
        return btn;}

    private void styleTextField(JTextField tf,String ph){
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(new LineBorder(new Color(210,215,225),1,true),new EmptyBorder(4,8,4,8)));
        tf.setForeground(TEXT_DARK);}

    private void showError(Component p,String m){JOptionPane.showMessageDialog(p,m,"Error",JOptionPane.ERROR_MESSAGE);}
    private void showSuccess(Component p,String m){JOptionPane.showMessageDialog(p,m,"Success",JOptionPane.INFORMATION_MESSAGE);}

    private void saveData(){
        try{data.savePatients();showSuccess(this,"Data saved to patients.txt!");}
        catch(IOException e){showError(this,"Failed to save: "+e.getMessage());}
    }

    // ── PIE CHART ────────────────────────────────────────────────────
    private static class PieChart extends JPanel {
        private final String[] labels; private final double[] values; private final Color[] colors;
        PieChart(String[] l,double[] v,Color[] c){labels=l;values=v;colors=c;setBackground(Color.WHITE);}
        protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            double total=0; for(double v:values)total+=v;
            int size=Math.min(getWidth()/2-20,getHeight()-20); if(size<10)return;
            int cx=size/2+10,cy=getHeight()/2,x=cx-size/2,y=cy-size/2;
            if(total==0){g2.setColor(new Color(220,220,220));g2.fillOval(x,y,size,size);}
            else{double angle=0; for(int i=0;i<values.length;i++){double sw=values[i]/total*360;
                g2.setColor(colors[i%colors.length]);g2.fillArc(x,y,size,size,(int)angle,(int)sw);angle+=sw;}
                g2.setColor(Color.WHITE);g2.setStroke(new BasicStroke(2));angle=0;
                for(double v:values){double sw=v/total*360;
                    g2.drawLine(cx,cy,(int)(cx+size/2.0*Math.cos(Math.toRadians(angle))),
                        (int)(cy-size/2.0*Math.sin(Math.toRadians(angle))));angle+=sw;}}
            int lx=cx+size/2+20,ly=cy-(labels.length*18)/2;
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for(int i=0;i<labels.length;i++){g2.setColor(colors[i%colors.length]);
                g2.fillRoundRect(lx,ly+i*18,12,12,4,4);g2.setColor(TEXT_DARK);
                String pct=total>0?String.format(Locale.US," %.0f%%",values[i]/total*100):" 0%";
                g2.drawString(labels[i]+pct,lx+16,ly+i*18+11);}
        }
        private static final Color TEXT_DARK=new Color(44,62,80);
    }

    // ── BAR CHART ────────────────────────────────────────────────────
    private static class BarChart extends JPanel {
        private final String[] labels; private final double[] values; private final Color barColor;
        BarChart(String[] l,double[] v,Color c){labels=l;values=v;barColor=c;setBackground(Color.WHITE);}
        protected void paintComponent(Graphics g){
            super.paintComponent(g); if(values.length==0)return;
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int pL=10,pR=10,pT=10,pB=30,cW=getWidth()-pL-pR,cH=getHeight()-pT-pB;
            if(cW<10||cH<10)return;
            double maxV=0; for(double v:values)if(v>maxV)maxV=v; if(maxV==0)maxV=1;
            int n=values.length,bW=Math.max(4,(cW/n)-6);
            for(int i=0;i<n;i++){
                int bH=(int)(values[i]/maxV*cH),bx=pL+i*(cW/n)+(cW/n-bW)/2,by=pT+cH-bH;
                g2.setColor(new Color(0,0,0,20));g2.fillRoundRect(bx+2,by+2,bW,bH,6,6);
                g2.setColor(barColor);g2.fillRoundRect(bx,by,bW,bH,6,6);
                g2.setFont(new Font("Segoe UI",Font.BOLD,9));g2.setColor(new Color(44,62,80));
                String vs=String.format(Locale.US,"$%.0f",values[i]);
                FontMetrics fm=g2.getFontMetrics();int tx=bx+(bW-fm.stringWidth(vs))/2;
                if(by-3>pT)g2.drawString(vs,tx,by-3);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,9));g2.setColor(new Color(127,140,141));
                String lbl=labels[i].length()>6?labels[i].substring(0,5)+".":labels[i];
                g2.drawString(lbl,bx+(bW-fm.stringWidth(lbl))/2,pT+cH+16);}
            g2.setColor(new Color(220,225,235));g2.drawLine(pL,pT+cH,pL+cW,pT+cH);}
    }

    // ── MAIN ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ignored){}
        SwingUtilities.invokeLater(HospitalSystemUI::new);
    }
}
