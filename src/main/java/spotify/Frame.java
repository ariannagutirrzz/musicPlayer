package spotify;

import com.formdev.flatlaf.FlatDarkLaf; // Importa FlatLaf para el tema oscuro

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Frame extends JFrame implements ActionListener {

    // Variables para manejar el login
    public static String username;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    // Variable para almacenar al usuario logueado
    public static String loggedInUsername = null;

    // Constructor del Frame
    public Frame() throws SQLException {
        // Establecer el Look and Feel de FlatLaf (tema oscuro)
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verificar si el usuario ya está logueado
        if (loggedInUsername != null) {
            // Si está logueado, abrir la ventana principal de Spotify directamente
            new Spotify(loggedInUsername);  // Pasar el nombre de usuario logueado
            this.dispose();
        } else {
            // Si no está logueado, mostrar el formulario de login
            showLogin();
        }
    }

    // Mostrar la ventana de login
    private void showLogin() {
        // Configuración del JFrame
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Spotify");

        // Panel de fondo
        JPanel panelBackground = new JPanel();
        panelBackground.setLayout(new GridBagLayout());
        panelBackground.setBackground(Color.darkGray);
        this.add(panelBackground);

        // Componentes de UI
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginButton.setPreferredSize(new Dimension(120, 40)); // Tamaño uniforme

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        registerButton.setPreferredSize(new Dimension(120, 40)); // Tamaño uniforme

        // Agregar componentes al panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espaciado

        // Fila 0 (Username)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Alinear a la derecha
        panelBackground.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alinear a la izquierda
        panelBackground.add(usernameField, gbc);

        // Fila 1 (Password)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; // Alinear a la derecha
        panelBackground.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alinear a la izquierda
        panelBackground.add(passwordField, gbc);

        // Fila 2 (botones)
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1; // Hacer que ocupe una columna
        gbc.anchor = GridBagConstraints.CENTER; // Centrar
        panelBackground.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;// Mover a la siguiente columna
        panelBackground.add(registerButton, gbc);

        // Mostrar ventana
        this.setVisible(true);
    }

    // Lógica del login y registro
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Intentar la conexión y la consulta en un bloque try-catch
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Spotify", "postgres", "password");
                 Statement stmt = conn.createStatement()) {

                // Verificar si el username ya existe
                String checkSql = String.format("SELECT COUNT(*) FROM userData WHERE USERNAME = '%s'", username);
                ResultSet rs = stmt.executeQuery(checkSql);

                if (rs.next() && rs.getInt(1) > 0) {
                    // Si el username ya existe
                    JOptionPane.showMessageDialog(this, "El nombre de usuario ya está en uso.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Si el username no existe, proceder a insertarlo
                    String sql = String.format("INSERT INTO userData(USERNAME, PASSWORD) VALUES ('%s', '%s')", username, password);
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente!");
                    usernameField.setText("");
                    passwordField.setText("");
                }

            } catch (SQLException ex) {
                // Manejar la excepción
                JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == loginButton) {
            username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Spotify", "postgres", "password");
                 PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM userData WHERE USERNAME = ? AND PASSWORD = ?")) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    loggedInUsername = username;
                    new Spotify(username);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña inválida", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        new Frame();
    }
}
