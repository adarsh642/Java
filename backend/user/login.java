import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import db.UserRepository;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class login extends JFrame {

    public login() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);

        // --- LOGIN CARD ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(250, 250, 250));
        cardPanel.setPreferredSize(new Dimension(280, 240));
        
        // Subtle grey border around card
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field with Placeholder
        JTextField userField = new JTextField();
        setupPlaceholder(userField, "Username");

        // Password Field with Placeholder
        JPasswordField passField = new JPasswordField();
        setupPlaceholder(passField, "Password");

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(new Color(40, 40, 40));
        loginButton.setBackground(new Color(220, 220, 220));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new LineBorder(new Color(190, 190, 190), 1));
        loginButton.setMaximumSize(new Dimension(240, 36));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> authenticate(userField, passField));

        // Add components to card
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(userField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        cardPanel.add(passField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        cardPanel.add(loginButton);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        // --- 2. SIGN UP TEXT + LINK PANEL ---
        JPanel signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signUpPanel.setOpaque(false);
        signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel("Don't have an account?");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(new Color(100, 100, 100));

        JLabel signUpLink = new JLabel("Sign Up");
        signUpLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signUpLink.setForeground(new Color(30, 100, 180)); // Link Blue
        signUpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Register().setVisible(true);
            }
        });

        signUpPanel.add(textLabel);
        signUpPanel.add(signUpLink);
        cardPanel.add(signUpPanel);

        // Position card in body
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        bodyPanel.add(cardPanel, gbc);

    
       
        add(bodyPanel, BorderLayout.CENTER);
    }

    private void authenticate(JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        if (username.isEmpty() || username.equals("Username") || password.isEmpty() || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Enter your username and password.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserRepository.Account account = UserRepository.authenticate(username, password);
            if (account == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            if (account.isAdmin()) {
                new Admin().setVisible(true);
            } else {
                new Feedback(account.getUsername()).setVisible(true);
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Could not log in: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setMaximumSize(new Dimension(240, 34));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new login().setVisible(true));
    }
}