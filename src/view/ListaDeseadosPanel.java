package view;

import controller.UsuarioController;
import database.VideojuegoDeseadoDAO;
import database.VideojuegoDeseadoDAOImpl;
import model.VideojuegoDeseado;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ListaDeseadosPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final UsuarioController usuarioController;
    private final DefaultListModel<VideojuegoDeseado> modeloLista;
    private final JList<VideojuegoDeseado> listaDeseados;
    private final JTextField campoTitulo;

    private final JComboBox<String> comboGenero;
    private final VideojuegoDeseadoDAO videojuegoDeseadoDAO = new VideojuegoDeseadoDAOImpl();

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

    public ListaDeseadosPanel(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Lista de Deseados"));
        setBackground(new Color(240, 248, 255));

        // Panel de lista con scroll
        modeloLista = new DefaultListModel<>();
        listaDeseados = new JList<>(modeloLista);
        listaDeseados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaDeseados.setCellRenderer(new DeseadoListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(listaDeseados);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Panel de controles
        JPanel panelControles = new JPanel(new GridBagLayout());
        panelControles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos de entrada
        campoTitulo = new JTextField(15);
        comboGenero = new JComboBox<>(GENEROS);
        comboGenero.setSelectedIndex(0);
        
        // Estilo del combo
        comboGenero.setBackground(Color.WHITE);
        comboGenero.setFont(campoTitulo.getFont());
        
        // Botones
        JButton botonAgregar = new JButton("Agregar");
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        
        // Mejorar estilo de botones
        configurarBoton(botonAgregar, new Color(46, 125, 50), Color.WHITE);
        configurarBoton(botonEliminar, new Color(211, 47, 47), Color.WHITE);
        
        // Configurar acciones
        botonAgregar.addActionListener(this::agregarVideojuego);
        botonEliminar.addActionListener(this::eliminarVideojuego);
        
        // Diseño de la grilla
        gbc.gridx = 0; gbc.gridy = 0;
        panelControles.add(new JLabel("Título:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panelControles.add(campoTitulo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelControles.add(new JLabel("Género:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panelControles.add(comboGenero, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0;
        panelControles.add(crearPanelBotones(botonAgregar, botonEliminar), gbc);
        
        add(panelControles, BorderLayout.SOUTH);
        
        // Carga inicial de datos
        actualizarLista();
    }

    private void configurarBoton(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setFont(boton.getFont().deriveFont(Font.BOLD));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel crearPanelBotones(JButton agregar, JButton eliminar) {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.add(agregar);
        panelBotones.add(eliminar);
        return panelBotones;
    }

    private void agregarVideojuego(ActionEvent e) {
        String titulo = campoTitulo.getText().trim();
        String genero = (String) comboGenero.getSelectedItem();

        // Validaciones
        if (titulo.isEmpty()) {
            mostrarAdvertencia("Por favor ingrese el título del videojuego.", "Título vacío");
            campoTitulo.requestFocus();
            return;
        }

        if (genero == null || genero.equals("Seleccionar género...")) {
            mostrarAdvertencia("Por favor seleccione un género.", "Género no seleccionado");
            comboGenero.requestFocus();
            return;
        }

        // Manejo especial para "Otro"
        if (genero.equals("Otro")) {
            String generoPersonalizado = JOptionPane.showInputDialog(
                this,
                "Ingrese el género personalizado:",
                "Género personalizado",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (generoPersonalizado == null || generoPersonalizado.trim().isEmpty()) {
                return; // Usuario canceló o no ingresó nada
            }
            
            genero = generoPersonalizado.trim();
        }

        try {
            // Obtener ID del usuario actual
            int usuarioId = usuarioController.getId();
            
            // Agregar a la lista de deseados usando el DAO correcto
            boolean exito = videojuegoDeseadoDAO.agregarAListaDeseados(
                usuarioId, 
                titulo, 
                genero, 
                null, // plataforma como null
                null  // descripción como null
            );
            
            if (exito) {
                actualizarLista();
                limpiarCampos();
                mostrarExito("Videojuego agregado exitosamente a tu lista de deseados.");
            } else {
                mostrarAdvertencia("El videojuego ya está en tu lista de deseados.", "Duplicado");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al agregar videojuego: " + ex.getMessage(), "Error");
        }
    }

    private void eliminarVideojuego(ActionEvent e) {
        VideojuegoDeseado seleccionado = listaDeseados.getSelectedValue();
        
        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un videojuego para eliminar.", "Ninguno seleccionado");
            return;
        }

        // Confirmación antes de eliminar
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar \"" + seleccionado.getTitulo() + "\" de su lista de deseados?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int usuarioId = usuarioController.getId();
            
            // Eliminar usando el DAO correcto
            boolean exito = videojuegoDeseadoDAO.eliminarDeListaDeseados(usuarioId, seleccionado.getId());
            
            if (exito) {
                actualizarLista();
                mostrarExito("Videojuego eliminado de tu lista de deseados.");
            } else {
                mostrarError("No se pudo eliminar el videojuego de la lista.", "Error");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al eliminar videojuego: " + ex.getMessage(), "Error");
        }
    }

    public void actualizarLista() {
        modeloLista.clear();
        
        try {
            int usuarioId = usuarioController.getId();
            List<VideojuegoDeseado> juegos = videojuegoDeseadoDAO.obtenerListaDeseados(usuarioId);
            
            for (VideojuegoDeseado juego : juegos) {
                modeloLista.addElement(juego);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al cargar la lista de deseados: " + ex.getMessage(), "Error");
        }
    }

    private void limpiarCampos() {
        campoTitulo.setText("");
        comboGenero.setSelectedIndex(0);
        campoTitulo.requestFocus();
    }

    private void mostrarError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Renderer personalizado para la lista
    private static class DeseadoListCellRenderer extends DefaultListCellRenderer {
    	private static final long serialVersionUID = 1L;
    	
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                     int index, boolean isSelected, 
                                                     boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof VideojuegoDeseado) {
                VideojuegoDeseado juego = (VideojuegoDeseado) value;
                
                // Formato simplificado solo con título y género
                String texto = juego.getTitulo() + " (" + juego.getGenero() + ")";
                setText(texto);
                
                // Sin tooltip ya que no tenemos descripción
                setToolTipText(null);
                
                // Mejorar visualización
                setFont(getFont().deriveFont(Font.PLAIN, 12f));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
            
            return this;
        }
    }
}