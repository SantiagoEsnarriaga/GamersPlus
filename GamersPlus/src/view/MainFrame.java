package view;

import controller.BibliotecaController;
import controller.IntercambioController;
import controller.UsuarioController;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
    private BibliotecaPanel bibliotecaPanel;
    private IntercambioPanel intercambioPanel;
    private ListaDeseadosPanel listaDeseadosPanel;
    private JTabbedPane tabbedPane;
    private Usuario usuarioActual;
    private LoginPanel loginPanel;
    private JPanel mainPanel;
    private static final List<Usuario> todosLosUsuarios = new ArrayList<>();
    
    public MainFrame() {
        setTitle("Sistema de Intercambio de Videojuegos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new CardLayout());
        
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");
        
        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, "MAIN");
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public void usuarioLogueado(Usuario usuario) {
        this.usuarioActual = usuario;
        
        if (!todosLosUsuarios.contains(usuario)) {
            todosLosUsuarios.add(usuario);
        }
        
        inicializarComponentesPrincipales();
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "MAIN");
    }
    
    private void inicializarComponentesPrincipales() {
        tabbedPane.removeAll();
        
        BibliotecaController bibliotecaController = new BibliotecaController(usuarioActual);
        UsuarioController usuarioController = new UsuarioController(usuarioActual);
        
        // Corrección: crear IntercambioController con los parámetros correctos
        IntercambioController intercambioController = new IntercambioController(
            usuarioActual, 
            new BibliotecaController(usuarioActual) // Crear nuevo controlador para intercambios
        );
        
        bibliotecaPanel = new BibliotecaPanel(bibliotecaController);
        listaDeseadosPanel = new ListaDeseadosPanel(usuarioController);
        
        intercambioPanel = new IntercambioPanel(
            usuarioActual, 
            todosLosUsuarios, 
            intercambioController
        );
        
        tabbedPane.addTab("Mi Biblioteca", bibliotecaPanel);
        tabbedPane.addTab("Lista de Deseados", listaDeseadosPanel);
        tabbedPane.addTab("Intercambios", intercambioPanel);
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == intercambioPanel) {
                intercambioPanel.actualizarPaneles();
            }
            if (tabbedPane.getSelectedComponent() == bibliotecaPanel) {
                bibliotecaPanel.actualizarListaJuegos();
            }
            if (tabbedPane.getSelectedComponent() == listaDeseadosPanel) {
                listaDeseadosPanel.actualizarLista();
            }
        });
        
        configurarHeader();
    }
    
    private void configurarHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(70, 130, 180));
        
        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombre());
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> cerrarSesion());
        btnLogout.setBackground(new Color(220, 80, 60));
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        headerPanel.add(lblUsuario, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }
    
    private void cerrarSesion() {
        this.usuarioActual = null;
        
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
    
    public static List<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(todosLosUsuarios);
    }
    
    public static void agregarUsuario(Usuario usuario) {
        if (!todosLosUsuarios.contains(usuario)) {
            todosLosUsuarios.add(usuario);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}