package hospitalsystem;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Locale;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Concrete Observer 4
 * ============================================================
 * يحدّث شجرة الأقسام (Composite Pattern tree)
 * عند أي تغيير في بيانات الموظفين
 * ============================================================
 */
public class DeptTreeObserver implements HospitalObserver {

    private final HospitalData   subject;
    private final DefaultTreeModel treeModel;
    private final JTree           tree;

    private static final String NODE_HOSPITAL = "HOSPITAL|";
    private static final String NODE_DEPT     = "DEPT|";
    private static final String NODE_DOCTOR   = "DOCTOR|";
    private static final String NODE_NURSE    = "NURSE|";
    private static final String NODE_RECEP    = "RECEP|";

    public DeptTreeObserver(HospitalData subject,
                            DefaultTreeModel treeModel, JTree tree) {
        this.subject   = subject;
        this.treeModel = treeModel;
        this.tree      = tree;
    }

    @Override
    public void subscribe() { subject.registerObserver(this); }

    @Override
    public void unsubscribe() { subject.removeObserver(this); }

    @Override
    public void update(HospitalEvent event) {
        switch (event) {
            case EMPLOYEE_ADDED, EMPLOYEE_UPDATED,
                 EMPLOYEE_DELETED, DATA_LOADED -> refreshTree();
        }
    }

    private void refreshTree() {
        if (tree == null || treeModel == null) return;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            NODE_HOSPITAL + "Hospital"
            + "||" + subject.getTotalHeadCount() + " staff  •  $"
            + String.format(Locale.US, "%.0f", subject.getTotalPayroll()) + " total payroll");

        for (Department dept : subject.getDepartments()) {
            if (dept.getHeadCount() == 0) continue;

            DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(
                NODE_DEPT + dept.getName()
                + "||" + dept.getHeadCount() + " staff  •  $"
                + String.format(Locale.US, "%.0f", dept.getTotalSalary()) + " payroll");

            for (HospitalComponent child : dept.getChildren()) {
                if (child instanceof EmployeeLeaf leaf) {
                    String prefix = switch (leaf.getType()) {
                        case "Doctor" -> NODE_DOCTOR;
                        case "Nurse"  -> NODE_NURSE;
                        default       -> NODE_RECEP;
                    };
                    dNode.add(new DefaultMutableTreeNode(
                        prefix + leaf.getName()
                        + "||" + leaf.getType() + "  •  $"
                        + String.format(Locale.US, "%.0f", leaf.getTotalSalary()) + " / month"));
                }
            }
            root.add(dNode);
        }

        treeModel.setRoot(root);
        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
    }
}
