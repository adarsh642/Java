import db.FeedbackRepository;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Admin extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public Admin() {
        setTitle("Admin Feedback Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP BANNER ---
        JPanel topBanner = new JPanel();
        topBanner.setBackground(new Color(90, 90, 90));
        topBanner.setPreferredSize(new Dimension(800, 100));

        // --- MAIN BODY ---
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);

        // --- CARD PANEL ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout(0, 15));
        cardPanel.setBackground(new Color(250, 250, 250));
        cardPanel.setPreferredSize(new Dimension(680, 400));
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Admin Feedback Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));

        // Add New Feedback Button Bar
        JButton addBtn = new JButton("+ Add New Feedback");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.setBackground(new Color(230, 230, 230));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddDialog());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(addBtn, BorderLayout.EAST);

        // --- TABLE SETUP ---
        String[] columnNames = {"ID", "User", "Feedback", "Date", "Actions"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the Actions column is interactive
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setGridColor(new Color(220, 220, 220));

        // Column Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(270);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(190);

        // Center align ID and Date columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Custom Action Buttons Renderer and Editor
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionPanelRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionPanelEditor());
        loadFeedbackRows();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(new Color(210, 210, 210), 1));

        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        bodyPanel.add(cardPanel, gbc);

        add(topBanner, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    private void showAddDialog() {
        String feedbackText = JOptionPane.showInputDialog(this, "Enter Feedback:", "Add Feedback", JOptionPane.PLAIN_MESSAGE);
        if (feedbackText != null && !feedbackText.trim().isEmpty()) {
            try {
                FeedbackRepository.Feedback feedback = FeedbackRepository.add("admin", feedbackText.trim());
                tableModel.addRow(new Object[]{feedback.getId(), feedback.getUsername(), feedback.getText(),
                        java.time.LocalDate.now().toString(), "Actions"});
            } catch (Exception exception) {
                showDatabaseError(exception);
            }
        }
    }

    private void loadFeedbackRows() {
        try {
            for (FeedbackRepository.Feedback feedback : FeedbackRepository.findAll()) {
                tableModel.addRow(new Object[]{feedback.getId(), feedback.getUsername(), feedback.getText(),
                        feedback.getDate(), "Actions"});
            }
        } catch (Exception exception) {
            showDatabaseError(exception);
        }
    }

    private void showDatabaseError(Exception exception) {
        JOptionPane.showMessageDialog(this, "Database error: " + exception.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    // --- ACTION PANEL CONTAINING VIEW, EDIT, DELETE BUTTONS ---
    private class ActionPanel extends JPanel {
        JButton viewBtn = createButton("View");
        JButton editBtn = createButton("Edit");
        JButton deleteBtn = createButton("Delete");

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 8));
            setOpaque(true);
            setBackground(Color.WHITE);
            add(viewBtn);
            add(editBtn);
            add(deleteBtn);
        }

        private JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setBackground(new Color(230, 230, 230));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            btn.setPreferredSize(new Dimension(52, 26));
            return btn;
        }
    }

    // --- TABLE CELL RENDERER ---
    private class ActionPanelRenderer extends ActionPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // --- TABLE CELL EDITOR FOR BUTTON ACTIONS ---
    private class ActionPanelEditor extends DefaultCellEditor {
        private ActionPanel panel;
        private int currentRow;

        public ActionPanelEditor() {
            super(new JTextField());
            panel = new ActionPanel();

            panel.viewBtn.addActionListener(e -> {
                fireEditingStopped();
                String feedback = (String) tableModel.getValueAt(currentRow, 2);
                JOptionPane.showMessageDialog(Admin.this, feedback, "View Feedback", JOptionPane.INFORMATION_MESSAGE);
            });

            panel.editBtn.addActionListener(e -> {
                fireEditingStopped();
                int id = (Integer) tableModel.getValueAt(currentRow, 0);
                String currentText = (String) tableModel.getValueAt(currentRow, 2);
                String updatedText = JOptionPane.showInputDialog(Admin.this, "Edit Feedback:", currentText);
                if (updatedText != null && !updatedText.trim().isEmpty()) {
                    try {
                        FeedbackRepository.update(id, updatedText.trim());
                        tableModel.setValueAt(updatedText.trim(), currentRow, 2);
                    } catch (Exception exception) {
                        showDatabaseError(exception);
                    }
                }
            });

            panel.deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(Admin.this, "Delete this feedback?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int id = (Integer) tableModel.getValueAt(currentRow, 0);
                        FeedbackRepository.delete(id);
                        tableModel.removeRow(currentRow);
                    } catch (Exception exception) {
                        showDatabaseError(exception);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Admin().setVisible(true));
    }
}