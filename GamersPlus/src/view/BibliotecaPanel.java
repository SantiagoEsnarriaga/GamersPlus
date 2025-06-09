package view;

import model.Usuario;
import model.Videojuego;
import javax.swing.*;
import java.awt.*;

public class BibliotecaPanel extends JPanel {
    private Usuario usuario;
    private JList<Videojuego> listaJuegos;
    private DefaultListModel<Videojuego> modeloLista;
    private static int contadorId = 1;

    public BibliotecaPanel(Usuario usuario) {
        this.usuario = usuario;
        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    // Configura los componentes visuales y botones
    private void inicializarComponentes() {
        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar Juego");
        JButton btnEliminar = new JButton("Eliminar Juego");
        
        btnAgregar.addActionListener(e -> mostrarDialogoAgregarJuego());
        btnEliminar.addActionListener(e -> eliminarJuegoSeleccionado());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        modeloLista = new DefaultListModel<>();
        listaJuegos = new JList<>(modeloLista);
        listaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listaJuegos);

        add(panelBotones, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Muestra ventana para ingresar nuevo juego
    private void mostrarDialogoAgregarJuego() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Agregar Juego", true);
        dialogo.setLayout(new GridLayout(4, 2, 10, 10));
        
        JTextField tituloField = new JTextField();
        JComboBox<String> generoCombo = new JComboBox<>(new String[]{
            "Acción", "Aventura", "RPG", "Deportes", "Estrategia", "Simulación"
        });

        dialogo.add(new JLabel("Título:"));
        dialogo.add(tituloField);
        dialogo.add(new JLabel("Género:"));
        dialogo.add(generoCombo);

        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");

        btnConfirmar.addActionListener(e -> {
            String titulo = tituloField.getText().trim();
            String genero = (String) generoCombo.getSelectedItem();
            
            if (!titulo.isEmpty()) {
                Videojuego nuevoJuego = new Videojuego(contadorId++, titulo, genero);
                modeloLista.addElement(nuevoJuego);
                if (usuario != null) {
                    usuario.agregarVideojuego(nuevoJuego);
                }
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, 
                    "Por favor, ingrese un título para el juego",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialogo.dispose());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);

        dialogo.add(new JLabel()); // Espacio vacío
        dialogo.add(new JLabel()); // Espacio vacío
        dialogo.add(panelBotones);

        dialogo.setSize(300, 200);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    // Elimina el juego seleccionado tras confirmación
    private void eliminarJuegoSeleccionado() {
        int indiceSeleccionado = listaJuegos.getSelectedIndex();
        if (indiceSeleccionado != -1) {
            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea eliminar este juego?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                Videojuego juegoAEliminar = modeloLista.getElementAt(indiceSeleccionado);
                modeloLista.remove(indiceSeleccionado);
                if (usuario != null) {
                    usuario.eliminarVideojuego(juegoAEliminar);
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, seleccione un juego para eliminar",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Refresca la lista de juegos con los datos actuales del usuario
    public void actualizarListaJuegos() {
        modeloLista.clear();
        if (usuario != null) {
            for (Videojuego juego : usuario.obtenerBiblioteca()) {
                modeloLista.addElement(juego);
            }
        }
    }
}
