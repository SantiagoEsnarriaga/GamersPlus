package view;

import controller.BibliotecaController;
import model.Videojuego;
import javax.swing.*;
import java.awt.*;

public class BibliotecaPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final BibliotecaController bibliotecaController;
    private JList<Videojuego> listaJuegos;
    private DefaultListModel<Videojuego> modeloLista;

    // Géneros más comunes de videojuegos
    private static final String[] GENEROS = {
        "Seleccionar género...",
        "Acción",
        "Aventura",
        "RPG",
        "Estrategia",
        "Deportes",
        "Carreras",
        "Simulación",
        "Puzzle",
        "Plataformas",
        "Shooter",
        "Fighting",
        "Horror",
        "Sandbox",
        "MMORPG",
        "Battle Royale",
        "Indie",
        "Arcade",
        "Música/Ritmo",
        "Otro"
    };

    public BibliotecaPanel(BibliotecaController bibliotecaController) {
        this.bibliotecaController = bibliotecaController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Mi Biblioteca de Videojuegos"));
        setBackground(new Color(245, 245, 250));
        inicializarComponentes();
        actualizarListaJuegos();
    }

    private void inicializarComponentes() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(245, 245, 250));
        
        JButton btnAgregar = new JButton("Agregar Juego");
        JButton btnEliminar = new JButton("Eliminar Juego");
        
        configurarBoton(btnAgregar, new Color(46, 125, 50), Color.WHITE);
        configurarBoton(btnEliminar, new Color(211, 47, 47), Color.WHITE);
        
        btnAgregar.addActionListener(e -> mostrarDialogoAgregarJuego());
        btnEliminar.addActionListener(e -> eliminarJuegoSeleccionado());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        modeloLista = new DefaultListModel<>();
        listaJuegos = new JList<>(modeloLista);
        listaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJuegos.setCellRenderer(new VideojuegoListCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(listaJuegos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(panelBotones, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configurarBoton(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setFont(boton.getFont().deriveFont(Font.BOLD));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(140, 35));
    }

    private void mostrarDialogoAgregarJuego() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Agregar Juego a Biblioteca", true);
        dialogo.setLayout(new GridBagLayout());
        dialogo.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField tituloField = new JTextField(20);
        JComboBox<String> generoCombo = new JComboBox<>(GENEROS);
        generoCombo.setSelectedIndex(0); 
        generoCombo.setBackground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialogo.add(new JLabel("Título del juego:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        dialogo.add(tituloField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        dialogo.add(new JLabel("Género:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        dialogo.add(generoCombo, gbc);

        JPanel panelBotonesDialogo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");
        
        configurarBoton(btnConfirmar, new Color(46, 125, 50), Color.WHITE);
        configurarBoton(btnCancelar, new Color(158, 158, 158), Color.WHITE);

        btnConfirmar.addActionListener(e -> {
            String titulo = tituloField.getText().trim();
            String genero = (String) generoCombo.getSelectedItem();
            
            // Validaciones mejoradas
            if (titulo.isEmpty()) {
                mostrarError(dialogo, "Por favor, ingrese el título del juego.", "Título vacío");
                tituloField.requestFocus();
                return;
            }
            
            if (genero == null || genero.equals("Seleccionar género...")) {
                mostrarError(dialogo, "Por favor, seleccione un género.", "Género no seleccionado");
                generoCombo.requestFocus();
                return;
            }
            
            // Manejo especial para "Otro"
            if (genero.equals("Otro")) {
                String generoPersonalizado = JOptionPane.showInputDialog(
                    dialogo,
                    "Ingrese el género personalizado:",
                    "Género personalizado",
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (generoPersonalizado == null || generoPersonalizado.trim().isEmpty()) {
                    return; // Usuario canceló
                }
                
                genero = generoPersonalizado.trim();
            }
            
            // Crear y agregar el juego
            Videojuego nuevoJuego = new Videojuego(0, titulo, genero, 0);
            
            try {
                bibliotecaController.agregarJuego(nuevoJuego);
                actualizarListaJuegos();
                dialogo.dispose();
                mostrarExito("Juego agregado exitosamente a tu biblioteca.");
            } catch (Exception ex) {
                mostrarError(dialogo, "Error al agregar juego: " + ex.getMessage(), "Error");
            }
        });

        btnCancelar.addActionListener(e -> dialogo.dispose());

        panelBotonesDialogo.add(btnConfirmar);
        panelBotonesDialogo.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.insets = new Insets(20, 10, 10, 10);
        dialogo.add(panelBotonesDialogo, gbc);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);
        
        SwingUtilities.invokeLater(() -> tituloField.requestFocus());
        
        dialogo.setVisible(true);
    }

    private void eliminarJuegoSeleccionado() {
        int indiceSeleccionado = listaJuegos.getSelectedIndex();
        
        if (indiceSeleccionado == -1) {
            mostrarAdvertencia("Por favor, seleccione un juego para eliminar.", "Ningún juego seleccionado");
            return;
        }
        
        Videojuego juegoAEliminar = modeloLista.getElementAt(indiceSeleccionado);
        
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            String.format("¿Está seguro de que desea eliminar \"%s\" de su biblioteca?\n\nEsta acción no se puede deshacer.", 
                         juegoAEliminar.getTitulo()),
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                bibliotecaController.eliminarJuego(juegoAEliminar);
                actualizarListaJuegos();
                mostrarExito("Juego eliminado exitosamente de tu biblioteca.");
            } catch (IllegalArgumentException ex) {
                mostrarError(this, ex.getMessage(), "Error al eliminar");
            } catch (Exception ex) {
                mostrarError(this, "Error inesperado al eliminar el juego: " + ex.getMessage(), "Error");
            }
        }
    }

    public void actualizarListaJuegos() {
        modeloLista.clear();
        bibliotecaController.obtenerTodosLosJuegos().forEach(modeloLista::addElement);
        
        // Mostrar mensaje si no hay juegos
        if (modeloLista.isEmpty()) {
            listaJuegos.setEnabled(false);
        } else {
            listaJuegos.setEnabled(true);
        }
    }

    // Métodos auxiliares para mostrar mensajes
    private void mostrarError(Component parent, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(parent, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class VideojuegoListCellRenderer extends DefaultListCellRenderer {
    	private static final long serialVersionUID = 1L;
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Videojuego) {
                Videojuego juego = (Videojuego) value;
                
                String estadoTexto = juego.estaDisponible() ? "Disponible" : "No disponible";
                setText(String.format("🎮 %s | %s | %s", 
                                    juego.getTitulo(), 
                                    juego.getGenero(), 
                                    estadoTexto));
                
                if (!juego.estaDisponible()) {
                    setForeground(isSelected ? Color.LIGHT_GRAY : Color.GRAY);
                    setFont(getFont().deriveFont(Font.ITALIC));
                } else {
                    setForeground(isSelected ? Color.WHITE : new Color(33, 37, 41));
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                setFont(getFont().deriveFont(12f));
                
                if (isSelected) {
                    setBackground(new Color(63, 81, 181));
                } else {
                    setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
            }
            
            return this;
        }
    }
}
