package model;

import java.util.ArrayList;
import java.util.List;

public class ListaDeseados {
    // Contador estático para asignar IDs únicos a cada lista
    private static int contadorListas = 1;

    private int id;  // ID único de la lista
    private List<Videojuego> juegos;  // Lista que almacena los videojuegos deseados

    // Constructor que inicializa el ID y la lista vacía
    public ListaDeseados() {
        this.id = contadorListas++;
        this.juegos = new ArrayList<>();
    }

    // Agrega un videojuego a la lista si no está ya presente
    public void agregar(Videojuego v) {
        if (v == null) {
            System.out.println("No se puede agregar un videojuego nulo.");
            return;
        }
        if (!juegos.contains(v)) {
            juegos.add(v);
            System.out.println("Videojuego agregado a la lista de deseados: " + v.getTitulo());
        } else {
            System.out.println("El videojuego ya está en la lista de deseados.");
        }
    }

    // Elimina un videojuego de la lista si está presente
    public void eliminar(Videojuego v) {
        if (juegos.remove(v)) {
            System.out.println("Videojuego eliminado de la lista de deseados: " + v.getTitulo());
        } else {
            System.out.println("El videojuego no se encuentra en la lista.");
        }
    }

    // Verifica si un videojuego está en la lista
    public boolean contiene(Videojuego v) {
        return juegos.contains(v);
    }

    // Devuelve la lista completa de videojuegos deseados
    public List<Videojuego> obtenerLista() {
        return juegos;
    }

    // Retorna el ID de esta lista
    public int getId() {
        return id;
    }

    // Representación en texto de la lista con títulos y géneros
    @Override
    public String toString() {
        if (juegos.isEmpty()) {
            return "Lista de deseados vacía.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Lista de deseados (ID ").append(id).append("):\n");
        for (Videojuego v : juegos) {
            sb.append("- ").append(v.getTitulo()).append(" (").append(v.getGenero()).append(")\n");
        }
        return sb.toString();
    }
}

