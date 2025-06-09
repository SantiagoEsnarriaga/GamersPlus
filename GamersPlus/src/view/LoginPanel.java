package view;

import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private MainFrame mainFrame;
    // Simulamos una base de datos de usuarios (en un proyecto real esto estaría en una base de datos)
    private static HashMap<String, Usuario> usuarios = new HashMap<>();

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        inicializarComponentes();
        // Agregamos algunos usuarios de prueba
        if (usuarios.isEmpty()) {
            agregarUsuariosPrueba();
        }
    }

    private void agregarUsuariosPrueba() {
        Usuario usuario1 = new Usuario(1, "Santiago Esnarriaga", "santi@email.com");
        Usuario usuario2 = new Usuario(2, "María García", "maria@email.com");
        Usuario usuario3 = new Usuario(3, "Carlos López", "carlos@email.com");
        
        usuarios.put("santi@email.com", usuario1);
        usuarios.put("maria@email.com", usuario2);
        usuarios.put("carlos@email.com", usuario3);
        
        // Agregar algunos juegos de prueba
        usuario1.agregarVideojuego(new model.Videojuego(1, "FIFA 25", "Deportes"));
        usuario1.agregarVideojuego(new model.Videojuego(2, "Call of Duty", "Acción"));
        usuario1.agregarVideojuego(new model.Videojuego(3, "The Witcher 3", "RPG"));
        
        usuario2.agregarVideojuego(new model.Videojuego(4, "Red Dead Redemption 2", "Aventura"));
        usuario2.agregarVideojuego(new model.Videojuego(5, "Bioshock", "Aventura"));
        usuario2.agregarVideojuego(new model.Videojuego(6, "Cyberpunk 2077", "RPG"));
        
        usuario3.agregarVideojuego(new model.Videojuego(7, "Gran Turismo 7", "Deportes"));
        usuario3.agregarVideojuego(new model.Videojuego(8, "God of War", "Acción"));
        usuario3.agregarVideojuego(new model.Videojuego(9, "Horizon Zero Dawn", "Aventura"));
        
        // Agregar a la lista global
        MainFrame.agregarUsuario(usuario1);
        MainFrame.agregarUsuario(usuario2);
        MainFrame.agregarUsuario(usuario3);
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel titleLabel = new JLabel("Sistema de Intercambio de Videojuegos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Instrucciones
        JLabel instrLabel = new JLabel("<html><center>Usuarios de prueba:<br>" +
            "santi@email.com, maria@email.com, carlos@email.com</center></html>");
        instrLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridy = 1;
        add(instrLabel, gbc);

        // Email
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Contraseña:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Iniciar Sesión");
        JButton registerButton = new JButton("Registrarse");

        loginButton.addActionListener(e -> intentarLogin());
        registerButton.addActionListener(e -> mostrarRegistro());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void intentarLogin() {
        String email = emailField.getText().trim();
        // En un sistema real, aquí verificarías la contraseña
        
        Usuario usuario = usuarios.get(email);
        if (usuario != null) {
            mainFrame.usuarioLogueado(usuario);
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario no encontrado. Use uno de los emails de prueba:\n" +
                "santi@email.com, maria@email.com, carlos@email.com",
                "Error de login",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarRegistro() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registro de Usuario", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos de registro
        JTextField nombreField = new JTextField(20);
        JTextField emailRegField = new JTextField(20);
        JPasswordField passRegField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        dialog.add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailRegField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        dialog.add(passRegField, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton registrarButton = new JButton("Registrar");
        JButton cancelarButton = new JButton("Cancelar");
        
        registrarButton.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            String email = emailRegField.getText().trim();
            
            if (nombre.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (usuarios.containsKey(email)) {
                JOptionPane.showMessageDialog(dialog,
                    "El email ya está registrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario nuevoUsuario = new Usuario(usuarios.size() + 1, nombre, email);
            usuarios.put(email, nuevoUsuario);
            MainFrame.agregarUsuario(nuevoUsuario);
            
            JOptionPane.showMessageDialog(dialog,
                "Usuario registrado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            dialog.dispose();
        });
        
        cancelarButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(registrarButton);
        buttonPanel.add(cancelarButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // Configurar el diálogo
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}