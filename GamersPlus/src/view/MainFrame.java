package view;

import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private BibliotecaPanel bibliotecaPanel;
    private IntercambioPanel intercambioPanel;
    private ListaDeseadosPanel listaDeseadosPanel;  // Nuevo panel
    private JTabbedPane tabbedPane;
    private Usuario usuarioActual;
    private LoginPanel loginPanel;
    private JPanel mainPanel;
    private static List<Usuario> todosLosUsuarios = new ArrayList<>(); // Lista compartida de usuarios
    
    public MainFrame() {
        setTitle("Sistema de Intercambio de Videojuegos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new CardLayout());
        
        // Crear panel de login
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");
        
        // Crear panel principal (inicialmente oculto)
        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, "MAIN");
        
        add(mainPanel);
    }
    
    public void usuarioLogueado(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Agregar usuario a la lista si no existe
        if (!todosLosUsuarios.contains(usuario)) {
            todosLosUsuarios.add(usuario);
        }
        
        inicializarComponentesPrincipales();
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "MAIN");
    }
    
    private void inicializarComponentesPrincipales() {
        tabbedPane.removeAll();
        
        // Inicializar paneles
        bibliotecaPanel = new BibliotecaPanel(usuarioActual);
        intercambioPanel = new IntercambioPanel(usuarioActual, todosLosUsuarios);
        listaDeseadosPanel = new ListaDeseadosPanel(usuarioActual);  // Inicializar nuevo panel
        
        // Agregar paneles al tabbedPane
        tabbedPane.addTab("Mi Biblioteca", bibliotecaPanel);
        tabbedPane.addTab("Lista de Deseados", listaDeseadosPanel);  // Agregar pestaña nueva
        tabbedPane.addTab("Intercambios", intercambioPanel);
        
        // Listener para actualizar intercambios cuando se cambia de tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == intercambioPanel) {
                intercambioPanel.actualizarPaneles();
            }
            // Podés agregar aquí para actualizar listaDeseadosPanel o bibliotecaPanel si necesitás
        });
        
        // Agregar botón de cerrar sesión
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> cerrarSesion());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("Usuario: " + usuarioActual.getNombre()), BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Limpiar el header anterior si existe
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainPanel) {
                remove(comp);
            }
        }
        
        add(headerPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }
    
    private void cerrarSesion() {
        this.usuarioActual = null;
        
        // Limpiar header
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainPanel) {
                remove(comp);
            }
        }
        
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "LOGIN");
        revalidate();
        repaint();
    }
    
    // Método estático para obtener todos los usuarios (útil para el sistema de intercambios)
    public static List<Usuario> obtenerTodosLosUsuarios() {
        return todosLosUsuarios;
    }
    
    // Método para agregar un usuario a la lista (llamado desde LoginPanel)
    public static void agregarUsuario(Usuario usuario) {
        if (!todosLosUsuarios.contains(usuario)) {
            todosLosUsuarios.add(usuario);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
