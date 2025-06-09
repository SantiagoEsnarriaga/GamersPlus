package model;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;                                       // Identificador único del usuario
    private String nombre;                               // Nombre del usuario
    private String email;                               // Email del usuario
    private List<Videojuego> biblioteca;               // Juegos que posee el usuario
    private List<Intercambio> intercambiosPropuestos; // Intercambios iniciados por el usuario
    private List<Intercambio> intercambiosRecibidos; // Intercambios recibidos por el usuario
    private List<Intercambio> historialIntercambios; // Registro de intercambios finalizados
    private ListaDeseados listaDeseados;             // Lista de juegos que desea el usuario
    
    // Constructor inicializa atributos y listas
    public Usuario(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.biblioteca = new ArrayList<>();
        this.intercambiosPropuestos = new ArrayList<>();
        this.intercambiosRecibidos = new ArrayList<>();
        this.historialIntercambios = new ArrayList<>();
        this.listaDeseados = new ListaDeseados();
    }
    
    // Getters para acceder a atributos básicos
    public int getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public ListaDeseados getListaDeseados() {
        return listaDeseados;
    }
    
    // Métodos para gestionar la biblioteca de videojuegos
    public List<Videojuego> obtenerBiblioteca() {
        return biblioteca;
    }
    
    public void agregarVideojuego(Videojuego juego) {
        if (juego != null && !biblioteca.contains(juego)) {
            biblioteca.add(juego);
        }
    }
    
    public void eliminarVideojuego(Videojuego juego) {
        biblioteca.remove(juego);
    }
    
    // Devuelve la lista de todos los intercambios activos (propuestos y recibidos)
    public List<Intercambio> obtenerIntercambiosActivos() {
        List<Intercambio> todos = new ArrayList<>();
        todos.addAll(intercambiosPropuestos);
        todos.addAll(intercambiosRecibidos);
        return todos;
    }
    
    // Retorna el historial completo de intercambios finalizados
    public List<Intercambio> obtenerHistorialIntercambios() {
        return historialIntercambios;
    }
    
    // Proponer un intercambio inicial con otro usuario.
     // Valida que los juegos y usuario receptor no sean nulos,
      // y que el juego ofrecido esté en la biblioteca y disponible.
     
    public Intercambio proponerIntercambio(Videojuego propio, Videojuego ajeno, Usuario receptor) {
        if (propio == null || ajeno == null || receptor == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }
        if (!biblioteca.contains(propio)) {
            throw new IllegalArgumentException("No puedes ofrecer un juego que no está en tu biblioteca");
        }
        if (!propio.estaDisponible()) {
            throw new IllegalArgumentException("El juego ofrecido no está disponible para intercambio");
        }
        
        Intercambio nuevo = new Intercambio(this, receptor, propio, ajeno);
        this.intercambiosPropuestos.add(nuevo);
        receptor.recibirIntercambio(nuevo);
        return nuevo;
    }
    
      // Agrega un intercambio a las listas internas según sea emisor o receptor.
     // Utilizado para contrapropuestas y gestión de intercambios activos.
     
    public void agregarIntercambio(Intercambio intercambio) {
        if (intercambio == null) {
            return;
        }
        
        if (intercambio.getEmisor().equals(this)) {
            if (!intercambiosPropuestos.contains(intercambio)) {
                intercambiosPropuestos.add(intercambio);
            }
        } else if (intercambio.getReceptor().equals(this)) {
            if (!intercambiosRecibidos.contains(intercambio)) {
                intercambiosRecibidos.add(intercambio);
            }
        }
    }
    
    
     // Método privado para registrar un intercambio recibido.
      //Evita duplicados en la lista de intercambios recibidos.
     
    private void recibirIntercambio(Intercambio intercambio) {
        if (intercambio != null && !intercambiosRecibidos.contains(intercambio)) {
            this.intercambiosRecibidos.add(intercambio);
        }
    }
    
    // Valida que la contrapropuesta sea posible y agrega la nueva propuesta.
     
    public Intercambio crearContrapropuesta(Intercambio intercambioOriginal, 
                                           Videojuego miJuegoOfrecido, 
                                           Videojuego juegoSolicitado) {
        if (intercambioOriginal == null) {
            throw new IllegalArgumentException("El intercambio original no puede ser nulo");
        }
        if (!intercambioOriginal.getReceptor().equals(this)) {
            throw new IllegalArgumentException("Solo puedes contraproponer a intercambios dirigidos a ti");
        }
        if (miJuegoOfrecido == null || juegoSolicitado == null) {
            throw new IllegalArgumentException("Los juegos no pueden ser nulos");
        }
        if (!biblioteca.contains(miJuegoOfrecido)) {
            throw new IllegalArgumentException("No puedes ofrecer un juego que no está en tu biblioteca");
        }
        if (!miJuegoOfrecido.estaDisponible()) {
            throw new IllegalArgumentException("El juego ofrecido no está disponible para intercambio");
        }
        
        Intercambio contrapropuesta = intercambioOriginal.crearContrapropuesta(
            miJuegoOfrecido, juegoSolicitado);
        
        this.intercambiosPropuestos.add(contrapropuesta);
        intercambioOriginal.getEmisor().recibirIntercambio(contrapropuesta);
        
        return contrapropuesta;
    }
    
    
     // Obtiene los intercambios recibidos que aún están pendientes.
     
    public List<Intercambio> obtenerIntercambiosRecibidosPendientes() {
        List<Intercambio> pendientes = new ArrayList<>();
        for (Intercambio intercambio : intercambiosRecibidos) {
            if (intercambio.getEstado().equals("pendiente")) {
                pendientes.add(intercambio);
            }
        }
        return pendientes;
    }
    
    
     // Obtiene los intercambios propuestos que están pendientes o contrapropuestos.
     
    public List<Intercambio> obtenerIntercambiosPropuestosPendientes() {
        List<Intercambio> pendientes = new ArrayList<>();
        for (Intercambio intercambio : intercambiosPropuestos) {
            String estado = intercambio.getEstado();
            if (estado.equals("pendiente") || estado.equals("contrapropuesto")) {
                pendientes.add(intercambio);
            }
        }
        return pendientes;
    }
    
    
     // Verifica si el usuario tiene contrapropuestas pendientes de responder.
     
    public boolean tieneContrapropuestasPendientes() {
        for (Intercambio intercambio : intercambiosRecibidos) {
            if (intercambio.getEstado().equals("pendiente") && intercambio.esContrapropuesta()) {
                return true;
            }
        }
        return false;
    }
    
    
     // Devuelve todas las contrapropuestas recibidas que están pendientes.
     
    public List<Intercambio> obtenerContrapropuestasRecibidas() {
        List<Intercambio> contrapropuestas = new ArrayList<>();
        for (Intercambio intercambio : intercambiosRecibidos) {
            if (intercambio.esContrapropuesta() && intercambio.getEstado().equals("pendiente")) {
                contrapropuestas.add(intercambio);
            }
        }
        return contrapropuestas;
    }
    
    
     // Añade un intercambio al historial y lo remueve de las listas activas.
      // También maneja el historial para contrapropuestas y sus intercambios originales.
     
    public void agregarAHistorial(Intercambio intercambio) {
        if (intercambio != null && !historialIntercambios.contains(intercambio)) {
            this.historialIntercambios.add(intercambio);
            
            this.intercambiosPropuestos.remove(intercambio);
            this.intercambiosRecibidos.remove(intercambio);
            
            if (intercambio.tieneContrapropuesta() && intercambio.getContrapropuesta().esFinalizado()) {
                Intercambio contrapropuesta = intercambio.getContrapropuesta();
                if (!historialIntercambios.contains(contrapropuesta)) {
                    this.historialIntercambios.add(contrapropuesta);
                    this.intercambiosPropuestos.remove(contrapropuesta);
                    this.intercambiosRecibidos.remove(contrapropuesta);
                }
            }
            
            if (intercambio.esContrapropuesta() && intercambio.getIntercambioOriginal().esFinalizado()) {
                Intercambio original = intercambio.getIntercambioOriginal();
                if (!historialIntercambios.contains(original)) {
                    this.historialIntercambios.add(original);
                    this.intercambiosPropuestos.remove(original);
                    this.intercambiosRecibidos.remove(original);
                }
            }
        }
    }
    
    
     // Limpia las listas activas eliminando los intercambios finalizados.
     
    public void limpiarIntercambiosFinalizados() {
        intercambiosPropuestos.removeIf(intercambio -> intercambio.esFinalizado());
        intercambiosRecibidos.removeIf(intercambio -> intercambio.esFinalizado());
    }
    
    @Override
    public String toString() {
        return nombre; // Para mostrar el nombre en interfaces, ej. ComboBox
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id == usuario.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
