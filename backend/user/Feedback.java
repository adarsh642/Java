import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import db.FeedbackRepository;

public class Feedback extends JFrame {

    private String existingFeedback = ""; // In-memory storage for feedback
    private JTextArea feedbackArea;
    private JButton addTabBtn;
    private JButton editTabBtn;
    private JButton submitBtn;
    private final String username;
    private Integer feedbackId;

    public Feedback() {
        this("guest");
    }

    public Feedback(String username) {
        this.username = username;
        setTitle("Feedback");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

       
        // --- MAIN BODY ---
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);

        // --- FEEDBACK CARD ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(250, 250, 250));
        cardPanel.setPreferredSize(new Dimension(320, 360));
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- TOGGLE TAB BUTTONS ---
        JPanel tabContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        tabContainer.setOpaque(false);
        tabContainer.setMaximumSize(new Dimension(300, 36));

        addTabBtn = new JButton("Add Feedback");
        editTabBtn = new JButton("Edit Feedback");

        styleTabButton(addTabBtn, true);
        styleTabButton(editTabBtn, false);

        tabContainer.add(addTabBtn);
        tabContainer.add(editTabBtn);

        // --- FEEDBACK INPUT AREA ---
        JLabel label = new JLabel("Your Feedback:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(80, 80, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackArea = new JTextArea(6, 20);
        feedbackArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setMaximumSize(new Dimension(280, 140));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 5, 5, 5)
        ));

        loadExistingFeedback();
        setupPlaceholder();

        // --- SUBMIT BUTTON ---
        submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setForeground(new Color(40, 40, 40));
        submitBtn.setBackground(new Color(220, 220, 220));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(new LineBorder(new Color(190, 190, 190), 1));
        submitBtn.setMaximumSize(new Dimension(280, 34));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- ACTION LISTENERS ---
        addTabBtn.addActionListener(e -> switchToAddTab());
        editTabBtn.addActionListener(e -> switchToEditTab());

        submitBtn.addActionListener(e -> {
            String text = feedbackArea.getText().trim();
            if (!text.equals("Write your feedback here...") && !text.isEmpty()) {
                try {
                    boolean addingFeedback = feedbackId == null;
                    if (feedbackId == null) {
                        FeedbackRepository.Feedback feedback = FeedbackRepository.add(username, text);
                        feedbackId = feedback.getId();
                    } else {
                        FeedbackRepository.update(feedbackId, text);
                    }
                    existingFeedback = text;
                        String message = addingFeedback
                            ? "Feedback added successfully!"
                            : "Feedback updated successfully!";
                        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(this, "Could not save feedback: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter some feedback.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add components to card
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(tabContainer);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(label);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(scrollPane);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(submitBtn);

        // Center card in frame body
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        bodyPanel.add(cardPanel, gbc);

      
        add(bodyPanel, BorderLayout.CENTER);
    }

    private void loadExistingFeedback() {
        try {
            FeedbackRepository.Feedback feedback = FeedbackRepository.findLatestForUser(username);
            if (feedback != null) {
                feedbackId = feedback.getId();
                existingFeedback = feedback.getText();
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Could not load feedback: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTabButton(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(125, 32));
        if (active) {
            btn.setBackground(new Color(215, 215, 215));
            btn.setForeground(Color.BLACK);
            btn.setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            btn.setBackground(new Color(230, 230, 230));
            btn.setForeground(new Color(70, 70, 70));
            btn.setBorder(new LineBorder(new Color(210, 210, 210), 1));
        }
    }

    private void switchToAddTab() {
        styleTabButton(addTabBtn, true);
        styleTabButton(editTabBtn, false);
        setupPlaceholder();
    }

    private void switchToEditTab() {
        styleTabButton(addTabBtn, false);
        styleTabButton(editTabBtn, true);

        if (existingFeedback.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No previous feedback found. Please add new feedback first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            switchToAddTab();
        } else {
            feedbackArea.setText(existingFeedback);
            feedbackArea.setForeground(Color.BLACK);
        }
    }

    private void setupPlaceholder() {
        if (existingFeedback.isEmpty()) {
            feedbackArea.setText("Write your feedback here...");
            feedbackArea.setForeground(Color.GRAY);
        } else {
            feedbackArea.setText(existingFeedback);
            feedbackArea.setForeground(Color.BLACK);
        }

        feedbackArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (feedbackArea.getText().equals("Write your feedback here...")) {
                    feedbackArea.setText("");
                    feedbackArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (feedbackArea.getText().trim().isEmpty()) {
                    feedbackArea.setText("Write your feedback here...");
                    feedbackArea.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Feedback().setVisible(true));
    }
}