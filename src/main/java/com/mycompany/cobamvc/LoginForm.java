package com.mycompany.cobamvc;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginForm extends JFrame {
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);

    public LoginForm() {
        setTitle("Login System - Fatih RPL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("LOGIN SYSTEM", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(22f));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(9, 7, 9, 7);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Username"), c);
        c.gridx = 1;
        form.add(txtUsername, c);

        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Password"), c);
        c.gridx = 1;
        form.add(txtPassword, c);

        JButton btnLogin = new JButton("Login");
        JButton btnReset = new JButton("Reset");
        btnLogin.addActionListener(e -> prosesLogin());
        btnReset.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocusInWindow();
        });

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnLogin);
        buttons.add(btnReset);

        JPanel main = new JPanel(new BorderLayout(10, 15));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));
        main.add(title, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);
        setContentPane(main);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password harus diisi.");
            return;
        }

        String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                        "Login berhasil! Selamat datang, " + rs.getString("username") + ".");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Username atau password salah.",
                        "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Koneksi/query database gagal:\n" + ex.getMessage(),
                "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}
