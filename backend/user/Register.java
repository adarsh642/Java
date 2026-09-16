import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import db.UserRepository;

public class Register extends JFrame {

    public Register() {
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

     
        
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);

        // --- REGISTER CARD ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(250, 250, 250));
        cardPanel.setPreferredSize(new Dimension(280, 290));
        
        // Border styling matching the Login card
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Fields with Placeholders
        JTextField userField = new JTextField();
        setupPlaceholder(userField, "Username");

        JTextField emailField = new JTextField();
        setupPlaceholder(emailField, "Email");

        JPasswordField passField = new JPasswordField();
        setupPlaceholder(passField, "Password");
        JPasswordField confpassField = new JPasswordField();
        setupPlaceholder(confpassField, "ConfirmPassword");

        // Register Button
        JButton signupButton = new JButton("SignUp");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupButton.setForeground(new Color(40, 40, 40));
        signupButton.setBackground(new Color(220, 220, 220));
        signupButton.setFocusPainted(false);
        signupButton.setBorder(new LineBorder(new Color(190, 190, 190), 1));
        signupButton.setMaximumSize(new Dimension(240, 36));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.addActionListener(e -> register(userField, emailField, passField, confpassField));

        // Add components to card
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        cardPanel.add(userField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(emailField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(passField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        cardPanel.add(confpassField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        

        cardPanel.add(signupButton);
         cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));
           // --- 2. SIGN in TEXT + LINK PANEL ---
        JPanel signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signUpPanel.setOpaque(false);
        signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel("Already have an account?");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(new Color(100, 100, 100));

        JLabel signUpLink = new JLabel("Signin");
        signUpLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signUpLink.setForeground(new Color(30, 100, 180)); // Link Blue
        signUpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new login().setVisible(true);
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

        // Add panels to frame
        add(bodyPanel, BorderLayout.CENTER);
    }

    private void register(JTextField userField, JTextField emailField,
                          JPasswordField passField, JPasswordField confirmField) {
        String username = userField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword());
        String confirmation = new String(confirmField.getPassword());
        if (username.isEmpty() || username.equals("Username") || email.isEmpty() || email.equals("Email")
                || password.isEmpty() || password.equals("Password") || confirmation.isEmpty()
                || confirmation.equals("ConfirmPassword")) {
            JOptionPane.showMessageDialog(this, "Complete all fields.", "Registration", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmation)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserRepository.register(username, email, password);
            JOptionPane.showMessageDialog(this, "Registration successful. Please log in.", "Registration", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new login().setVisible(true);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Could not register: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        SwingUtilities.invokeLater(() -> new Register().setVisible(true));
    }
}