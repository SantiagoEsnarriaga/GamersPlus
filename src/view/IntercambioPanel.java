package view;

import controller.BibliotecaController;
import controller.IntercambioController;
import model.Usuario;
import model.Videojuego;
import model.Intercambio;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class IntercambioPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final Usuario usuarioActual;
    private final List<Usuario> todosLosUsuarios;
    private final IntercambioController intercambioController;
    private final BibliotecaController bibliotecaController;
    
    // Componentes de propuesta
    private JComboBox<Usuario> comboUsuarios;
    private JList<Videojuego> listaMisJuegos, listaJuegosOtroUsuario;
    private DefaultListModel<Videojuego> modeloMisJuegos, modeloJuegosOtroUsuario;
    
    // Componentes de intercambios activos
    private JList<Intercambio> listaIntercambiosRecibidos, listaIntercambiosEnviados;
    private DefaultListModel<Intercambio> modeloRecibidos, modeloEnviados;
    
    // Componentes de historial
    private JList<Intercambio> listaHistorial;
    private DefaultListModel<Intercambio> modeloHistorial;
    private JTextArea areaDetalles;
    
    public IntercambioPanel(Usuario usuario, List<Usuario> usuarios, IntercambioController controller) {
        this.usuarioActual = Objects.requireNonNull(usuario, "Usuario no puede ser nulo");
        this.todosLosUsuarios = Objects.requireNonNull(usuarios, "Lista de usuarios no puede ser nula");
        this.intercambioController = Objects.requireNonNull(controller, "Controlador no puede ser nulo");
        this.bibliotecaController = new BibliotecaController(usuarioActual);
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabbedPane.addTab("Proponer Intercambio", crearPanelPropuesta());
        tabbedPane.addTab("Intercambios Activos", crearPanelIntercambiosActivos());
        tabbedPane.addTab("Historial", crearPanelHistorial());
        
        add(tabbedPane, BorderLayout.CENTER);
        actualizarTodos();
    }
    
    private JPanel crearPanelPropuesta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con selector de usuario
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.add(new JLabel("Intercambiar con:"));
        
        comboUsuarios = new JComboBox<>();
        comboUsuarios.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value instanceof Usuario) {
                Usuario u = (Usuario) value;
                label.setText(u.getNombre() + " (" + u.getEmail() + ")");
            }
            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return label;
        });
        
        todosLosUsuarios.stream()
                .filter(u -> !u.equals(usuarioActual))
                .forEach(comboUsuarios::addItem);
        comboUsuarios.addActionListener(e -> actualizarJuegosOtroUsuario());
        panelSuperior.add(comboUsuarios);
        
        // Panel central con listas de juegos
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 0));
        
        modeloMisJuegos = new DefaultListModel<>();
        listaMisJuegos = crearListaJuegos(modeloMisJuegos, "Mis Juegos Disponibles", Color.BLUE);
        
        modeloJuegosOtroUsuario = new DefaultListModel<>();
        listaJuegosOtroUsuario = crearListaJuegos(modeloJuegosOtroUsuario, "Juegos del Otro Usuario", Color.RED);
        
        panelCentral.add(crearPanelConTitulo(listaMisJuegos, "Mis Juegos Disponibles", Color.BLUE));
        panelCentral.add(crearPanelConTitulo(listaJuegosOtroUsuario, "Juegos del Otro Usuario", Color.RED));
        
        // Botón proponer con color mejorado
        JPanel panelInferior = new JPanel();
        JButton btnProponer = crearBoton("Proponer Intercambio", ColorPalette.AZUL_PROPONER);
        btnProponer.setForeground(Color.BLACK); // ← Forzar texto negro
        btnProponer.addActionListener(e -> proponerIntercambio());
        panelInferior.add(btnProponer);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelIntercambiosActivos() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel recibidos
        JPanel panelRecibidos = new JPanel(new BorderLayout());
        modeloRecibidos = new DefaultListModel<>();
        listaIntercambiosRecibidos = crearListaIntercambios(modeloRecibidos);
        
        JPanel botonesRecibidos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAceptar = crearBoton("Aceptar", ColorPalette.VERDE_ACEPTAR);
        JButton btnRechazar = crearBoton("Rechazar", ColorPalette.ROJO_RECHAZAR);
        btnRechazar.setForeground(Color.BLACK); // ← Forzar texto negro
        JButton btnContrapropuesta = crearBoton("Contrapropuesta", ColorPalette.NARANJA_CONTRAPROPUESTA);
        
        btnAceptar.addActionListener(e -> procesarIntercambio("aceptar"));
        btnRechazar.addActionListener(e -> procesarIntercambio("rechazar"));
        btnContrapropuesta.addActionListener(e -> crearContrapropuesta());
        
        botonesRecibidos.add(btnAceptar);
        botonesRecibidos.add(btnRechazar);
        botonesRecibidos.add(btnContrapropuesta);
        
        panelRecibidos.setBorder(crearBordeConTitulo("Intercambios Recibidos", Color.BLUE));
        panelRecibidos.add(new JScrollPane(listaIntercambiosRecibidos), BorderLayout.CENTER);
        panelRecibidos.add(botonesRecibidos, BorderLayout.SOUTH);
        
        // Panel enviados
        JPanel panelEnviados = new JPanel(new BorderLayout());
        modeloEnviados = new DefaultListModel<>();
        listaIntercambiosEnviados = crearListaIntercambios(modeloEnviados);
        
        panelEnviados.setBorder(crearBordeConTitulo("Intercambios Enviados", new Color(34, 139, 34)));
        panelEnviados.add(new JScrollPane(listaIntercambiosEnviados), BorderLayout.CENTER);
        
        panel.add(panelRecibidos);
        panel.add(panelEnviados);
        
        return panel;
    }
    
    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modeloHistorial = new DefaultListModel<>();
        listaHistorial = crearListaIntercambios(modeloHistorial);
        listaHistorial.addListSelectionListener(e -> mostrarDetalles());
        
        areaDetalles = new JTextArea(10, 30);
        areaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaDetalles.setEditable(false);
        areaDetalles.setBackground(new Color(240, 240, 240));
        areaDetalles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        areaDetalles.setText("Seleccione un intercambio para ver los detalles");
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(listaHistorial), new JScrollPane(areaDetalles));
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }
    
    // Métodos auxiliares para crear componentes
    private JList<Videojuego> crearListaJuegos(DefaultListModel<Videojuego> modelo, String titulo, Color color) {
        JList<Videojuego> lista = new JList<>(modelo);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value instanceof Videojuego) {
                Videojuego juego = (Videojuego) value;
                label.setText(juego.getTitulo() + " - " + juego.getGenero());
            }
            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });
        return lista;
    }
    
    private JList<Intercambio> crearListaIntercambios(DefaultListModel<Intercambio> modelo) {
        JList<Intercambio> lista = new JList<>(modelo);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value instanceof Intercambio) {
                Intercambio i = (Intercambio) value;
                String texto = String.format(
                    "<html><b>ID: %d</b> - %s<br>" +
                    "<font color='blue'>Ofrece: %s</font><br>" +
                    "<font color='green'>Solicita: %s</font><br>" +
                    "<b>Estado:</b> %s%s</html>",
                    i.getId(), i.getEmisor().getNombre(),
                    i.getJuegoOfrecido().getTitulo(), i.getJuegoSolicitado().getTitulo(),
                    i.getEstado(), i.esContrapropuesta() ? " (CONTRAPROPUESTA)" : ""
                );
                label.setText(texto);
                
                if (i.esContrapropuesta()) {
                    label.setBackground(isSelected ? Color.ORANGE.darker() : new Color(255, 240, 200));
                } else if (i.getEstado().equals("contrapropuesto")) {
                    label.setBackground(isSelected ? Color.YELLOW.darker() : new Color(255, 255, 200));
                } else {
                    label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                }
            }
            label.setOpaque(true);
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });
        return lista;
    }
    
    private JPanel crearPanelConTitulo(JComponent componente, String titulo, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeConTitulo(titulo, color));
        panel.add(new JScrollPane(componente), BorderLayout.CENTER);
        return panel;
    }
    
    private javax.swing.border.Border crearBordeConTitulo(String titulo, Color color) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2), titulo,
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), color
        );
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        
        // Mejorar contraste del texto según el color de fondo
        if (esColorClaro(color)) {
            boton.setForeground(Color.BLACK); // Texto negro para fondos claros
        } else {
            boton.setForeground(Color.WHITE); // Texto blanco para fondos oscuros
        }
        
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        // Efectos hover para mejor interactividad
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
                boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return boton;
    }

    // Método auxiliar para determinar si un color es claro
    private boolean esColorClaro(Color color) {
        // Fórmula de luminancia para determinar si un color es claro u oscuro
        double luminancia = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminancia > 0.5;
    }

    // Paleta de colores mejorada con mejor contraste
    private static class ColorPalette {
    	public static final Color VERDE_ACEPTAR = new Color(50, 205, 50);   
    	public static final Color ROJO_RECHAZAR = new Color(255, 69, 58); 
        public static final Color NARANJA_CONTRAPROPUESTA = new Color(255, 140, 0); 
        public static final Color AZUL_PROPONER = new Color(70, 130, 180);     
        public static final Color GRIS_CANCELAR = new Color(105, 105, 105);    
    }
    
    // Métodos de funcionalidad
    private void proponerIntercambio() {
        Videojuego miJuego = listaMisJuegos.getSelectedValue();
        Videojuego juegoAjeno = listaJuegosOtroUsuario.getSelectedValue();
        Usuario otroUsuario = (Usuario) comboUsuarios.getSelectedItem();
        
        if (miJuego == null || juegoAjeno == null || otroUsuario == null) {
            mostrarError("Debe seleccionar tanto un juego suyo como uno del otro usuario");
            return;
        }
        
        try {
            Intercambio nuevoIntercambio = intercambioController.proponerIntercambio(otroUsuario, miJuego, juegoAjeno);
            mostrarMensaje("¡Intercambio propuesto exitosamente!\nOfrecido: " + 
                nuevoIntercambio.getJuegoOfrecido().getTitulo() + "\nSolicitado: " + 
                nuevoIntercambio.getJuegoSolicitado().getTitulo());
            listaMisJuegos.clearSelection();
            listaJuegosOtroUsuario.clearSelection();
        } catch (Exception ex) {
            mostrarError("Error al proponer intercambio: " + ex.getMessage());
        }
    }
    
    private void procesarIntercambio(String accion) {
        Intercambio intercambio = listaIntercambiosRecibidos.getSelectedValue();
        if (intercambio == null) {
            mostrarError("Seleccione un intercambio para " + accion);
            return;
        }
        
        String mensaje = accion.equals("aceptar") ? 
            "¿Está seguro que desea aceptar este intercambio?" :
            "¿Está seguro que desea rechazar este intercambio?";
            
        int confirmacion = JOptionPane.showConfirmDialog(this, mensaje, 
            "Confirmar " + accion.substring(0, 1).toUpperCase() + accion.substring(1),
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (accion.equals("aceptar")) {
                    intercambioController.aceptarIntercambio(intercambio);
                    mostrarMensaje("¡Intercambio aceptado exitosamente!");
                } else {
                    intercambioController.rechazarIntercambio(intercambio);
                    mostrarMensaje("Intercambio rechazado");
                }
                actualizarIntercambiosActivos();
            } catch (Exception ex) {
                mostrarError("Error al " + accion + ": " + ex.getMessage());
            }
        }
    }
    
    private void crearContrapropuesta() {
        Intercambio intercambio = listaIntercambiosRecibidos.getSelectedValue();
        if (intercambio == null) {
            mostrarError("Seleccione un intercambio para contraproponer");
            return;
        }
        
        if (!intercambio.puedeSerContrapropuesto()) {
            mostrarError("Este intercambio no puede ser contrapropuesto. Estado: " + intercambio.getEstado());
            return;
        }
        
        // Crear dialog con botones mejorados
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Crear Contrapropuesta", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panelDialog = new JPanel(new BorderLayout(10, 10));
        panelDialog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Información del intercambio original
        JLabel lblInfo = new JLabel(String.format(
            "<html><b>%s</b> ofrece <b>%s</b> y solicita <b>%s</b></html>",
            intercambio.getEmisor().getNombre(),
            intercambio.getJuegoOfrecido().getTitulo(),
            intercambio.getJuegoSolicitado().getTitulo()));
        panelDialog.add(lblInfo, BorderLayout.NORTH);
        
        // Listas para contrapropuesta
        JPanel panelListas = new JPanel(new GridLayout(1, 2, 10, 0));
        
        DefaultListModel<Videojuego> modeloMisJuegosDialog = new DefaultListModel<>();
        JList<Videojuego> listaMisJuegosDialog = new JList<>(modeloMisJuegosDialog);
        bibliotecaController.obtenerJuegosDisponibles().forEach(modeloMisJuegosDialog::addElement);
        
        DefaultListModel<Videojuego> modeloJuegosOtroDialog = new DefaultListModel<>();
        JList<Videojuego> listaJuegosOtroDialog = new JList<>(modeloJuegosOtroDialog);
        BibliotecaController controllerOtro = new BibliotecaController(intercambio.getEmisor());
        controllerOtro.obtenerJuegosDisponibles().forEach(modeloJuegosOtroDialog::addElement);
        
        panelListas.add(crearPanelConTitulo(listaMisJuegosDialog, "Mis Juegos", Color.BLUE));
        panelListas.add(crearPanelConTitulo(listaJuegosOtroDialog, "Sus Juegos", Color.RED));
        panelDialog.add(panelListas, BorderLayout.CENTER);
        
        // Botones con colores mejorados
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnConfirmar = crearBoton("Crear Contrapropuesta", ColorPalette.VERDE_ACEPTAR);
        JButton btnCancelar = crearBoton("Cancelar", ColorPalette.GRIS_CANCELAR);
        
        btnConfirmar.addActionListener(e -> {
            Videojuego juegoOfrecido = listaMisJuegosDialog.getSelectedValue();
            Videojuego juegoSolicitado = listaJuegosOtroDialog.getSelectedValue();
            
            if (juegoOfrecido == null || juegoSolicitado == null) {
                mostrarError("Debe seleccionar ambos juegos");
                return;
            }
            
            try {
                intercambioController.crearContrapropuesta(intercambio, juegoOfrecido, juegoSolicitado);
                mostrarMensaje("¡Contrapropuesta creada exitosamente!");
                actualizarIntercambiosActivos();
                dialog.dispose();
            } catch (Exception ex) {
                mostrarError("Error al crear contrapropuesta: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        panelDialog.add(panelBotones, BorderLayout.SOUTH);
        
        dialog.add(panelDialog);
        dialog.setVisible(true);
    }
    
    // Métodos de actualización
    public void actualizarPaneles() {
        actualizarTodos();
    }
    
    private void actualizarTodos() {
        actualizarMisJuegos();
        actualizarJuegosOtroUsuario();
        actualizarIntercambiosActivos();
        actualizarHistorial();
    }
    
    private void actualizarMisJuegos() {
        modeloMisJuegos.clear();
        bibliotecaController.obtenerJuegosDisponibles().forEach(modeloMisJuegos::addElement);
    }
    
    private void actualizarJuegosOtroUsuario() {
        modeloJuegosOtroUsuario.clear();
        Usuario usuarioSeleccionado = (Usuario) comboUsuarios.getSelectedItem();
        if (usuarioSeleccionado != null) {
            BibliotecaController controllerOtro = new BibliotecaController(usuarioSeleccionado);
            controllerOtro.obtenerJuegosDisponibles().forEach(modeloJuegosOtroUsuario::addElement);
        }
    }
    
    private void actualizarIntercambiosActivos() {
        modeloRecibidos.clear();
        modeloEnviados.clear();
        
        for (Intercambio intercambio : intercambioController.obtenerIntercambiosActivos()) {
            if (intercambio.getReceptor().equals(usuarioActual)) {
                modeloRecibidos.addElement(intercambio);
            } else if (intercambio.getEmisor().equals(usuarioActual)) {
                modeloEnviados.addElement(intercambio);
            }
        }
    }
    
    private void actualizarHistorial() {
        modeloHistorial.clear();
        intercambioController.obtenerHistorial().forEach(modeloHistorial::addElement);
    }
    
    private void mostrarDetalles() {
        Intercambio intercambio = listaHistorial.getSelectedValue();
        if (intercambio != null) {
            areaDetalles.setText(intercambio.toString());
            areaDetalles.setCaretPosition(0);
        } else {
            areaDetalles.setText("Seleccione un intercambio para ver los detalles");
        }
    }
    
    // Métodos de utilidad
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}