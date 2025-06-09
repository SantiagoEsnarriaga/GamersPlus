package view;

import model.Usuario;
import model.Videojuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListaDeseadosPanel extends JPanel {
    private Usuario usuario;
    private DefaultListModel<Videojuego> modeloLista;
    private JList<Videojuego> listaDeseados;
    private JTextField campoTitulo;
    private JTextField campoGenero;
    private JButton botonAgregar;
    private JButton botonEliminar;

    private int siguienteIdVideojuego = 1000; // Contador para IDs nuevos

    public ListaDeseadosPanel(Usuario usuario) {
        this.usuario = usuario;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Lista de Deseados"));

        modeloLista = new DefaultListModel<>();
        listaDeseados = new JList<>(modeloLista);
        listaDeseados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listaDeseados);
        add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar videojuegos con campos y botones
        JPanel panelAgregar = new JPanel(new GridLayout(3, 2, 5, 5));
        campoTitulo = new JTextField();
        campoGenero = new JTextField();
        botonAgregar = new JButton("Agregar");
        botonEliminar = new JButton("Eliminar seleccionado");

        panelAgregar.add(new JLabel("Título:"));
        panelAgregar.add(campoTitulo);
        panelAgregar.add(new JLabel("Género:"));
        panelAgregar.add(campoGenero);
        panelAgregar.add(botonAgregar);
        panelAgregar.add(botonEliminar);

        add(panelAgregar, BorderLayout.SOUTH);

        actualizarLista(); // Carga inicial de la lista

        botonAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = campoTitulo.getText().trim();
                String genero = campoGenero.getText().trim();

                if (!titulo.isEmpty() && !genero.isEmpty()) {
                    // Crear videojuego nuevo y agregarlo a la lista de deseados
                    Videojuego nuevo = new Videojuego(siguienteIdVideojuego++, titulo, genero);
                    usuario.getListaDeseados().agregar(nuevo);
                    actualizarLista();
                    campoTitulo.setText(""); // Limpiar campo título
                    campoGenero.setText(""); // Limpiar campo género
                } else {
                    // Aviso si faltan datos
                    JOptionPane.showMessageDialog(ListaDeseadosPanel.this,
                            "Por favor ingrese título y género.",
                            "Campos vacíos",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        botonEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Videojuego seleccionado = listaDeseados.getSelectedValue();
                if (seleccionado != null) {
                    usuario.getListaDeseados().eliminar(seleccionado);
                    actualizarLista();
                } else {
                    // Aviso si no se seleccionó ningún videojuego para eliminar
                    JOptionPane.showMessageDialog(ListaDeseadosPanel.this,
                            "Seleccione un videojuego para eliminar.",
                            "Ninguno seleccionado",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    // Refresca la lista en pantalla con los datos actuales del usuario
    private void actualizarLista() {
        modeloLista.clear();
        for (Videojuego v : usuario.getListaDeseados().obtenerLista()) {
            modeloLista.addElement(v);
        }
    }
}

