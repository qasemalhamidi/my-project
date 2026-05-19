package hospitalsystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Locale;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Concrete Observer 5
 * ============================================================
 * يسجّل كل الأحداث ويعرضها في الـ Event Log panel
 * ============================================================
 */
public class EventLogObserver implements HospitalObserver {

    private final HospitalData subject;
    private final JPanel       logListPanel;
    private int logCount = 0;

    // Colors — passed from UI
    private final Color ACCENT, ACCENT2, WARN, DANGER, CARD_BG, TEXT_DARK, TEXT_MID;

    public EventLogObserver(HospitalData subject, JPanel logListPanel,
                            Color accent, Color accent2, Color warn, Color danger,
                            Color cardBg, Color textDark, Color textMid) {
        this.subject      = subject;
        this.logListPanel = logListPanel;
        this.ACCENT    = accent;
        this.ACCENT2   = accent2;
        this.WARN      = warn;
        this.DANGER    = danger;
        this.CARD_BG   = cardBg;
        this.TEXT_DARK = textDark;
        this.TEXT_MID  = textMid;
    }

    @Override
    public void subscribe() { subject.registerObserver(this); }

    @Override
    public void unsubscribe() { subject.removeObserver(this); }

    @Override
    public void update(HospitalEvent event) {
        // DATA_LOADED لا يُسجَّل في الـ log (فلترة في notifyObservers)
        if (logListPanel == null) return;
        logCount++;
        java.time.LocalTime now = java.time.LocalTime.now();
        String time = String.format(Locale.US, "%02d:%02d:%02d",
                now.getHour(), now.getMinute(), now.getSecond());
        addEntry(event, time, logCount, true);
    }

    /** يُعيد بناء الـ log panel من سجل HospitalData */
    public void rebuildFromLog() {
        if (logListPanel == null) return;
        logListPanel.removeAll();
        java.util.List<HospitalData.EventRecord> records = subject.getEventLog();
        logCount = records.size();
        for (int i = records.size() - 1; i >= 0; i--) {
            HospitalData.EventRecord r = records.get(i);
            addEntry(r.event, r.time, i + 1, false);
        }
        logListPanel.revalidate();
        logListPanel.repaint();
    }

    public void clearLog() {
        subject.clearEventLog();
        logListPanel.removeAll();
        logCount = 0;
        logListPanel.revalidate();
        logListPanel.repaint();
    }

    private void addEntry(HospitalEvent event, String time, int num, boolean insertAtTop) {
        Color evColor;
        String notified;
        switch (event) {
            case PATIENT_ADDED    -> { evColor = ACCENT2; notified = "Dashboard, PatientTable, EventLog"; }
            case PATIENT_UPDATED  -> { evColor = WARN;    notified = "PatientTable, EventLog"; }
            case PATIENT_DELETED  -> { evColor = DANGER;  notified = "Dashboard, PatientTable, EventLog"; }
            case EMPLOYEE_ADDED   -> { evColor = ACCENT;  notified = "Dashboard, StaffTable, DeptTree, EventLog"; }
            case EMPLOYEE_UPDATED -> { evColor = WARN;    notified = "StaffTable, DeptTree, EventLog"; }
            case EMPLOYEE_DELETED -> { evColor = DANGER;  notified = "Dashboard, StaffTable, DeptTree, EventLog"; }
            default               -> { evColor = TEXT_MID; notified = "EventLog"; }
        }

        JPanel entry = new JPanel(new BorderLayout(10, 0));
        entry.setBackground(CARD_BG);
        entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        entry.setBorder(new CompoundBorder(
            new MatteBorder(0, 4, 0, 0, evColor),
            new EmptyBorder(8, 12, 8, 12)));

        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setBackground(CARD_BG);
        JLabel nameL = new JLabel("#" + num + "  " + event.getLabel());
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameL.setForeground(evColor);
        JLabel notifL = new JLabel("Notified: " + notified);
        notifL.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        notifL.setForeground(TEXT_MID);
        left.add(nameL, BorderLayout.NORTH);
        left.add(notifL, BorderLayout.SOUTH);

        JLabel timeL = new JLabel(time, SwingConstants.RIGHT);
        timeL.setFont(new Font("Consolas", Font.PLAIN, 11));
        timeL.setForeground(TEXT_MID);

        entry.add(left, BorderLayout.CENTER);
        entry.add(timeL, BorderLayout.EAST);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(240, 242, 245));

        if (insertAtTop) {
            logListPanel.add(entry, 0);
            logListPanel.add(sep, 1);
            logListPanel.revalidate();
            logListPanel.repaint();
        } else {
            logListPanel.add(entry);
            logListPanel.add(sep);
        }
    }
}
