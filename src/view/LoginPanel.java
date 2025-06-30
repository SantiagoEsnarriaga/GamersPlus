package view;

import controller.UsuarioController;
import database.UsuarioDAOImpl;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Optional;

public class LoginPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private MainFrame mainFrame;
    private static final Color BG = new Color(23, 25, 35);
    private static final Color FORM_BG = new Color(32, 36, 48);
    private static final Color ACCENT = new Color(88, 166, 255);
    private static final Color ACCENT_HOVER = new Color(65, 142, 230);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color SUCCESS_HOVER = new Color(22, 163, 74);
    private static final Color CANCEL = new Color(71, 85, 105);
    private static final Color CANCEL_HOVER = new Color(100, 116, 139);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color BORDER = new Color(51, 65, 85);

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel formPanel = crearFormPanel();
        
        agregarTitulo(formPanel);
        
        agregarCampos(formPanel);
        
        agregarBotones(formPanel);
        
        add(formPanel, new GridBagConstraints());
    }
    
    private JPanel crearFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(FORM_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        return panel;
    }
    
    private void agregarTitulo(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel titulo = new JLabel("GAMERSPLUS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titulo, gbc);
        
        JLabel subtitulo = new JLabel("Sistema de Intercambio de Videojuegos");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 30, 0);
        panel.add(subtitulo, gbc);
    }
    
    private void agregarCampos(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 15);
        gbc.gridwidth = 1;
        
        // Email
        gbc.gridy = 2; gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(crearLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        emailField = crearTextField(25);
        panel.add(emailField, gbc);
        
        // Contraseña
        gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(crearLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = crearPasswordField(25);
        panel.add(passwordField, gbc);
    }
    
    private void agregarBotones(JPanel panel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        JButton loginBtn = crearBoton("INICIAR SESIÓN", ACCENT, ACCENT_HOVER, e -> intentarLogin());
        JButton registerBtn = crearBoton("REGISTRARSE", SUCCESS, SUCCESS_HOVER, e -> mostrarRegistro());
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 0, 0); gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
    }
    
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private JTextField crearTextField(int columnas) {
        JTextField field = new JTextField(columnas);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estiloTextField(field);
        return field;
    }
    
    private JPasswordField crearPasswordField(int columnas) {
        JPasswordField field = new JPasswordField(columnas);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estiloTextField(field);
        return field;
    }
    
    private void estiloTextField(JTextField field) {
        field.setBackground(BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        
        javax.swing.border.Border borderNormal = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        javax.swing.border.Border borderFocus = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)
        );
        
        field.setBorder(borderNormal);
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(borderFocus);
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(borderNormal);
            }
        });
    }
    
    private JButton crearBoton(String texto, Color bg, Color hover, java.awt.event.ActionListener action) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 35, 14, 35));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }
    
    private JButton crearBotonPequeño(String texto, Color bg, Color hover, java.awt.event.ActionListener action) {
        JButton btn = crearBoton(texto, bg, hover, action);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        return btn;
    }
    
    private void mostrarRegistro() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registro de Usuario", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FORM_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Título
        JLabel titulo = new JLabel("CREAR CUENTA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titulo, gbc);
        
        // Resetear para campos
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos
        JTextField nombreField = agregarCampoRegistro(formPanel, "Nombre:", 1, gbc);
        JTextField emailRegField = agregarCampoRegistro(formPanel, "Email:", 2, gbc);
        JPasswordField passRegField = agregarCampoPassword(formPanel, "Contraseña:", 3, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnRegistrar = crearBotonPequeño("REGISTRAR", SUCCESS, SUCCESS_HOVER, 
            e -> registrarUsuario(nombreField.getText(), emailRegField.getText(), 
                                new String(passRegField.getPassword()), dialog));
        JButton btnCancelar = crearBotonPequeño("CANCELAR", CANCEL, CANCEL_HOVER, 
            e -> dialog.dispose());
        
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JTextField agregarCampoRegistro(JPanel panel, String labelText, int row, GridBagConstraints gbc) {
        gbc.gridy = row; gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(crearLabel(labelText), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        JTextField field = crearTextField(20);
        panel.add(field, gbc);
        return field;
    }
    
    private JPasswordField agregarCampoPassword(JPanel panel, String labelText, int row, GridBagConstraints gbc) {
        gbc.gridy = row; gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(crearLabel(labelText), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START;
        JPasswordField field = crearPasswordField(20);
        panel.add(field, gbc);
        return field;
    }
    
    private void intentarLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos", "Campos requeridos");
            return;
        }
        
        Optional<Usuario> usuarioOpt = UsuarioController.buscarPorEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getContraseña() != null && usuario.getContraseña().trim().equals(password)) {
                mainFrame.usuarioLogueado(usuario);
            } else {
                mostrarError("Contraseña incorrecta", "Error de autenticación");
            }
        } else {
            mostrarError("Usuario no encontrado", "Error de login");
        }
        
        passwordField.setText("");
    }
    
    private void registrarUsuario(String nombre, String email, String password, JDialog dialog) {
        nombre = nombre.trim(); email = email.trim(); password = password.trim();
        
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos", "Campos requeridos");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            mostrarError("Por favor ingrese un email válido", "Email inválido");
            return;
        }
        
        if (password.length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres", "Contraseña muy corta");
            return;
        }
        
        if (UsuarioController.buscarPorEmail(email).isPresent()) {
            mostrarError("El email ya está registrado", "Error de registro");
            return;
        }
        
        Usuario nuevoUsuario = new Usuario(0, nombre, email, password);
        if (new UsuarioDAOImpl().save(nuevoUsuario)) {
            JOptionPane.showMessageDialog(dialog,
                "Usuario registrado exitosamente\nYa puede iniciar sesión",
                "¡Bienvenido a Gamersplus!",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            emailField.setText(email);
        } else {
            mostrarError("Error al registrar el usuario", "Error de registro");
        }
    }
    
    private void mostrarError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
}