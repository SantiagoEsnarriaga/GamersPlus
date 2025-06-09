package view;

import model.*;
import exceptions.ExcepcionesIntercambios;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class IntercambioPanel extends JPanel {
    private Usuario usuarioActual;
    private JTabbedPane tabbedPane;
    private IntercambiosActivosPanel intercambiosActivosPanel;
    private PropuestaIntercambioPanel propuestaPanel;
    private HistorialIntercambiosPanel historialPanel;
    private List<Usuario> todosLosUsuarios;

    public IntercambioPanel(Usuario usuario, List<Usuario> usuarios) {
        this.usuarioActual = usuario;
        this.todosLosUsuarios = usuarios;
        
        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        tabbedPane = new JTabbedPane();
        
        // Panel para crear nuevas propuestas
        propuestaPanel = new PropuestaIntercambioPanel(usuarioActual, todosLosUsuarios);
        tabbedPane.addTab("Proponer Intercambio", propuestaPanel);
        
        // Panel para ver intercambios activos (recibidos y enviados)
        intercambiosActivosPanel = new IntercambiosActivosPanel(usuarioActual, todosLosUsuarios);
        tabbedPane.addTab("Intercambios Activos", intercambiosActivosPanel);
        
        // Panel para ver historial
        historialPanel = new HistorialIntercambiosPanel(usuarioActual);
        tabbedPane.addTab("Historial", historialPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void actualizarPaneles() {
        if (intercambiosActivosPanel != null) {
            intercambiosActivosPanel.actualizarListas();
        }
        if (historialPanel != null) {
            historialPanel.actualizarHistorial();
        }
    }
}

// Panel para crear nuevas propuestas de intercambio
class PropuestaIntercambioPanel extends JPanel {
    private Usuario usuarioActual;
    private List<Usuario> todosLosUsuarios;
    private JComboBox<Usuario> comboUsuarios;
    private JList<Videojuego> listaMisJuegos;
    private JList<Videojuego> listaJuegosOtroUsuario;
    private DefaultListModel<Videojuego> modeloMisJuegos;
    private DefaultListModel<Videojuego> modeloJuegosOtroUsuario;
    
    public PropuestaIntercambioPanel(Usuario usuario, List<Usuario> usuarios) {
        this.usuarioActual = usuario;
        this.todosLosUsuarios = usuarios;
        
        setLayout(new BorderLayout());
        inicializarComponentes();
        actualizarMisJuegos();
    }
    
    private void inicializarComponentes() {
        // Panel superior - selección de usuario
        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Intercambiar con:"));
        
        comboUsuarios = new JComboBox<>();
        for (Usuario u : todosLosUsuarios) {
            if (!u.equals(usuarioActual)) {
                comboUsuarios.addItem(u);
            }
        }
        comboUsuarios.addActionListener(e -> actualizarJuegosOtroUsuario());
        panelSuperior.add(comboUsuarios);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - listas de juegos
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Mis juegos
        JPanel panelMisJuegos = new JPanel(new BorderLayout());
        panelMisJuegos.setBorder(BorderFactory.createTitledBorder("Mis Juegos Disponibles"));
        
        modeloMisJuegos = new DefaultListModel<>();
        listaMisJuegos = new JList<>(modeloMisJuegos);
        listaMisJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelMisJuegos.add(new JScrollPane(listaMisJuegos), BorderLayout.CENTER);
        
        // Juegos del otro usuario
        JPanel panelJuegosOtro = new JPanel(new BorderLayout());
        panelJuegosOtro.setBorder(BorderFactory.createTitledBorder("Juegos del Otro Usuario"));
        
        modeloJuegosOtroUsuario = new DefaultListModel<>();
        listaJuegosOtroUsuario = new JList<>(modeloJuegosOtroUsuario);
        listaJuegosOtroUsuario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelJuegosOtro.add(new JScrollPane(listaJuegosOtroUsuario), BorderLayout.CENTER);
        
        panelCentral.add(panelMisJuegos);
        panelCentral.add(panelJuegosOtro);
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - botón proponer
        JPanel panelInferior = new JPanel();
        JButton btnProponer = new JButton("Proponer Intercambio");
        btnProponer.addActionListener(e -> proponerIntercambio());
        panelInferior.add(btnProponer);
        add(panelInferior, BorderLayout.SOUTH);
        
        // Actualizar juegos del primer usuario seleccionado
        if (comboUsuarios.getItemCount() > 0) {
            actualizarJuegosOtroUsuario();
        }
    }
    
    private void actualizarMisJuegos() {
        modeloMisJuegos.clear();
        for (Videojuego juego : usuarioActual.obtenerBiblioteca()) {
            if (juego.estaDisponible()) {
                modeloMisJuegos.addElement(juego);
            }
        }
    }
    
    private void actualizarJuegosOtroUsuario() {
        modeloJuegosOtroUsuario.clear();
        Usuario usuarioSeleccionado = (Usuario) comboUsuarios.getSelectedItem();
        if (usuarioSeleccionado != null) {
            for (Videojuego juego : usuarioSeleccionado.obtenerBiblioteca()) {
                if (juego.estaDisponible()) {
                    modeloJuegosOtroUsuario.addElement(juego);
                }
            }
        }
    }
    
    private void proponerIntercambio() {
        Videojuego miJuego = listaMisJuegos.getSelectedValue();
        Videojuego juegoAjeno = listaJuegosOtroUsuario.getSelectedValue();
        Usuario otroUsuario = (Usuario) comboUsuarios.getSelectedItem();
        
        if (miJuego == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar uno de sus juegos para ofrecer", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (juegoAjeno == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un juego del otro usuario", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Intercambio nuevoIntercambio = usuarioActual.proponerIntercambio(miJuego, juegoAjeno, otroUsuario);
            
            JOptionPane.showMessageDialog(this,
                "Intercambio propuesto exitosamente!\n" +
                "Ofrecido: " + miJuego.getTitulo() + "\n" +
                "Solicitado: " + juegoAjeno.getTitulo() + "\n" +
                "Para: " + otroUsuario.getNombre(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Limpiar selecciones
            listaMisJuegos.clearSelection();
            listaJuegosOtroUsuario.clearSelection();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al proponer intercambio: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Panel para ver intercambios activos
class IntercambiosActivosPanel extends JPanel {
    private Usuario usuarioActual;
    private List<Usuario> todosLosUsuarios;
    private JList<Intercambio> listaIntercambiosRecibidos;
    private JList<Intercambio> listaIntercambiosEnviados;
    private DefaultListModel<Intercambio> modeloRecibidos;
    private DefaultListModel<Intercambio> modeloEnviados;
    
    public IntercambiosActivosPanel(Usuario usuario, List<Usuario> usuarios) {
        this.usuarioActual = usuario;
        this.todosLosUsuarios = usuarios;
        setLayout(new GridLayout(2, 1, 0, 10));
        inicializarComponentes();
        actualizarListas();
    }
    
    private void inicializarComponentes() {
        // Panel intercambios recibidos
        JPanel panelRecibidos = new JPanel(new BorderLayout());
        panelRecibidos.setBorder(BorderFactory.createTitledBorder("Intercambios Recibidos"));
        
        modeloRecibidos = new DefaultListModel<>();
        listaIntercambiosRecibidos = new JList<>(modeloRecibidos);
        listaIntercambiosRecibidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaIntercambiosRecibidos.setCellRenderer(new IntercambioListCellRenderer());
        
        JPanel botonesRecibidos = new JPanel();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnRechazar = new JButton("Rechazar");
        JButton btnContrapropuesta = new JButton("Contrapropuesta");
        
        btnAceptar.addActionListener(e -> aceptarIntercambio());
        btnRechazar.addActionListener(e -> rechazarIntercambio());
        btnContrapropuesta.addActionListener(e -> crearContrapropuesta());
        
        botonesRecibidos.add(btnAceptar);
        botonesRecibidos.add(btnRechazar);
        botonesRecibidos.add(btnContrapropuesta);
        
        panelRecibidos.add(new JScrollPane(listaIntercambiosRecibidos), BorderLayout.CENTER);
        panelRecibidos.add(botonesRecibidos, BorderLayout.SOUTH);
        
        // Panel intercambios enviados
        JPanel panelEnviados = new JPanel(new BorderLayout());
        panelEnviados.setBorder(BorderFactory.createTitledBorder("Intercambios Enviados"));
        
        modeloEnviados = new DefaultListModel<>();
        listaIntercambiosEnviados = new JList<>(modeloEnviados);
        listaIntercambiosEnviados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaIntercambiosEnviados.setCellRenderer(new IntercambioListCellRenderer());
        
        panelEnviados.add(new JScrollPane(listaIntercambiosEnviados), BorderLayout.CENTER);
        
        add(panelRecibidos);
        add(panelEnviados);
    }
    
    public void actualizarListas() {
        modeloRecibidos.clear();
        modeloEnviados.clear();
        
        for (Intercambio intercambio : usuarioActual.obtenerIntercambiosActivos()) {
            if (!intercambio.esFinalizado()) {
                if (intercambio.getReceptor().equals(usuarioActual)) {
                    modeloRecibidos.addElement(intercambio);
                } else if (intercambio.getEmisor().equals(usuarioActual)) {
                    modeloEnviados.addElement(intercambio);
                }
            }
        }
    }
    
    private void aceptarIntercambio() {
        Intercambio intercambio = listaIntercambiosRecibidos.getSelectedValue();
        if (intercambio == null) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un intercambio para aceptar",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea aceptar este intercambio?\n" +
            "Recibirá: " + intercambio.getJuegoOfrecido().getTitulo() + "\n" +
            "Dará: " + intercambio.getJuegoSolicitado().getTitulo(),
            "Confirmar Intercambio",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            intercambio.aceptar();
            usuarioActual.agregarAHistorial(intercambio);
            intercambio.getEmisor().agregarAHistorial(intercambio);
            
            JOptionPane.showMessageDialog(this,
                "Intercambio aceptado exitosamente!",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            actualizarListas();
        }
    }
    
    private void rechazarIntercambio() {
        Intercambio intercambio = listaIntercambiosRecibidos.getSelectedValue();
        if (intercambio == null) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un intercambio para rechazar",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea rechazar este intercambio?",
            "Confirmar Rechazo",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            intercambio.rechazar();
            usuarioActual.agregarAHistorial(intercambio);
            intercambio.getEmisor().agregarAHistorial(intercambio);
            
            JOptionPane.showMessageDialog(this,
                "Intercambio rechazado",
                "Información", JOptionPane.INFORMATION_MESSAGE);
            
            actualizarListas();
        }
    }
    
    private void crearContrapropuesta() {
        Intercambio intercambio = listaIntercambiosRecibidos.getSelectedValue();
        if (intercambio == null) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un intercambio para hacer una contrapropuesta",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!intercambio.puedeSerContrapropuesto()) {
            JOptionPane.showMessageDialog(this,
                "Este intercambio no puede ser contrapropuesto.\n" +
                "Estado: " + intercambio.getEstado(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Abrir diálogo de contrapropuesta
        ContrapropuestaDialog dialog = new ContrapropuestaDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            intercambio, usuarioActual);
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            try {
                Intercambio contrapropuesta = intercambio.crearContrapropuesta(
                    dialog.getJuegoOfrecido(),
                    dialog.getJuegoSolicitado());
                
                // Agregar la contrapropuesta a los intercambios activos del emisor original
                intercambio.getEmisor().agregarIntercambio(contrapropuesta);
                usuarioActual.agregarIntercambio(contrapropuesta);
                
                JOptionPane.showMessageDialog(this,
                    "Contrapropuesta creada exitosamente!\n" +
                    "Ofrecido: " + contrapropuesta.getJuegoOfrecido().getTitulo() + "\n" +
                    "Solicitado: " + contrapropuesta.getJuegoSolicitado().getTitulo(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                actualizarListas();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al crear contrapropuesta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// Renderer personalizado para mostrar intercambios con información adicional
class IntercambioListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof Intercambio) {
            Intercambio intercambio = (Intercambio) value;
            
            String texto = String.format(
                "<html><b>ID: %d</b> - %s<br>" +
                "<b>Ofrece:</b> %s<br>" +
                "<b>Solicita:</b> %s<br>" +
                "<b>Estado:</b> %s%s</html>",
                intercambio.getId(),
                intercambio.getEmisor().getNombre(),
                intercambio.getJuegoOfrecido().getTitulo(),
                intercambio.getJuegoSolicitado().getTitulo(),
                intercambio.getEstado(),
                intercambio.esContrapropuesta() ? " (CONTRAPROPUESTA)" : ""
            );
            
            setText(texto);
            
            // Cambiar color según el tipo de intercambio
            if (intercambio.esContrapropuesta()) {
                setBackground(isSelected ? Color.ORANGE.darker() : Color.ORANGE.brighter());
            } else if (intercambio.getEstado().equals("contrapropuesto")) {
                setBackground(isSelected ? Color.YELLOW.darker() : Color.YELLOW.brighter());
            }
        }
        
        return this;
    }
}

// Panel para ver historial de intercambios
class HistorialIntercambiosPanel extends JPanel {
    private Usuario usuarioActual;
    private JList<Intercambio> listaHistorial;
    private DefaultListModel<Intercambio> modeloHistorial;
    private JTextArea areaDetalles;
    
    public HistorialIntercambiosPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        setLayout(new BorderLayout());
        inicializarComponentes();
        actualizarHistorial();
    }
    
    private void inicializarComponentes() {
        // Lista del historial
        modeloHistorial = new DefaultListModel<>();
        listaHistorial = new JList<>(modeloHistorial);
        listaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaHistorial.addListSelectionListener(e -> mostrarDetalles());
        listaHistorial.setCellRenderer(new IntercambioListCellRenderer());
        
        // Área de detalles
        areaDetalles = new JTextArea(10, 30);
        areaDetalles.setEditable(false);
        areaDetalles.setBackground(getBackground());
        
        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(listaHistorial),
            new JScrollPane(areaDetalles));
        splitPane.setDividerLocation(300);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    public void actualizarHistorial() {
        modeloHistorial.clear();
        for (Intercambio intercambio : usuarioActual.obtenerHistorialIntercambios()) {
            modeloHistorial.addElement(intercambio);
        }
    }
    
    private void mostrarDetalles() {
        Intercambio intercambio = listaHistorial.getSelectedValue();
        if (intercambio != null) {
            areaDetalles.setText(intercambio.toString());
        } else {
            areaDetalles.setText("Seleccione un intercambio para ver los detalles");
        }
    }
}

// Diálogo para crear contrapropuestas
class ContrapropuestaDialog extends JDialog {
    private Usuario usuarioActual;
    private Intercambio intercambioOriginal;
    private JList<Videojuego> listaMisJuegos;
    private JList<Videojuego> listaJuegosOtroUsuario;
    private DefaultListModel<Videojuego> modeloMisJuegos;
    private DefaultListModel<Videojuego> modeloJuegosOtroUsuario;
    private boolean confirmado = false;
    private Videojuego juegoOfrecido;
    private Videojuego juegoSolicitado;
    
    public ContrapropuestaDialog(JFrame parent, Intercambio intercambioOriginal, Usuario usuarioActual) {
        super(parent, "Crear Contrapropuesta", true);
        this.usuarioActual = usuarioActual;
        this.intercambioOriginal = intercambioOriginal;
        
        initComponents();
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior con información del intercambio original
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Intercambio Original"));
        
        JLabel lblInfo = new JLabel(String.format(
            "<html><b>%s</b> ofrece <b>%s</b> y solicita <b>%s</b></html>",
            intercambioOriginal.getEmisor().getNombre(),
            intercambioOriginal.getJuegoOfrecido().getTitulo(),
            intercambioOriginal.getJuegoSolicitado().getTitulo()));
        
        panelInfo.add(lblInfo, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.NORTH);
        
        // Panel central con las listas de juegos
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Mis juegos para ofrecer
        JPanel panelMisJuegos = new JPanel(new BorderLayout());
        panelMisJuegos.setBorder(BorderFactory.createTitledBorder("Juegos que puedo ofrecer"));
        
        modeloMisJuegos = new DefaultListModel<>();
        listaMisJuegos = new JList<>(modeloMisJuegos);
        listaMisJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Cargar mis juegos disponibles
        for (Videojuego juego : usuarioActual.obtenerBiblioteca()) {
            if (juego.estaDisponible()) {
                modeloMisJuegos.addElement(juego);
            }
        }
        
        panelMisJuegos.add(new JScrollPane(listaMisJuegos), BorderLayout.CENTER);
        
        // Juegos del otro usuario que puedo solicitar
        JPanel panelJuegosOtro = new JPanel(new BorderLayout());
        panelJuegosOtro.setBorder(BorderFactory.createTitledBorder(
            "Juegos de " + intercambioOriginal.getEmisor().getNombre()));
        
        modeloJuegosOtroUsuario = new DefaultListModel<>();
        listaJuegosOtroUsuario = new JList<>(modeloJuegosOtroUsuario);
        listaJuegosOtroUsuario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Cargar juegos del otro usuario
        for (Videojuego juego : intercambioOriginal.getEmisor().obtenerBiblioteca()) {
            if (juego.estaDisponible()) {
                modeloJuegosOtroUsuario.addElement(juego);
            }
        }
        
        panelJuegosOtro.add(new JScrollPane(listaJuegosOtroUsuario), BorderLayout.CENTER);
        
        panelCentral.add(panelMisJuegos);
        panelCentral.add(panelJuegosOtro);
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        JButton btnConfirmar = new JButton("Crear Contrapropuesta");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnConfirmar.addActionListener(e -> confirmarContrapropuesta());
        btnCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void confirmarContrapropuesta() {
        juegoOfrecido = listaMisJuegos.getSelectedValue();
        juegoSolicitado = listaJuegosOtroUsuario.getSelectedValue();
        
        if (juegoOfrecido == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un juego para ofrecer",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (juegoSolicitado == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un juego para solicitar",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar que no sea la misma combinación que el intercambio original
        if (juegoOfrecido.equals(intercambioOriginal.getJuegoSolicitado()) && 
            juegoSolicitado.equals(intercambioOriginal.getJuegoOfrecido())) {
            JOptionPane.showMessageDialog(this,
                "Esta combinación es igual al intercambio original",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Crear contrapropuesta?\n" +
            "Ofrecerá: " + juegoOfrecido.getTitulo() + "\n" +
            "Solicitará: " + juegoSolicitado.getTitulo(),
            "Confirmar Contrapropuesta",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            confirmado = true;
            dispose();
        }
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public Videojuego getJuegoOfrecido() {
        return juegoOfrecido;
    }
    
    public Videojuego getJuegoSolicitado() {
        return juegoSolicitado;
    }
}