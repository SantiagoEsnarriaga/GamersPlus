package exceptions;


public class ExcepcionesIntercambios {
    
  
    public static class UsuarioNoEncontradoException extends Exception {
        public UsuarioNoEncontradoException(int idUsuario) {
            super("Usuario con ID " + idUsuario + " no encontrado");
        }
    }

    // Excepción para videojuegos no disponibles
    public static class VideojuegoNoDisponibleException extends Exception {
        public VideojuegoNoDisponibleException(int idVideojuego) {
            super("Videojuego con ID " + idVideojuego + " no disponible para intercambio");
        }
    }

    // Excepción para intercambios inválidos
    public static class IntercambioInvalidoException extends Exception {
        public IntercambioInvalidoException(String mensaje) {
            super(mensaje);
        }
    }

    // Excepción para datos de entrada inválidos
    public static class DatosInvalidosException extends Exception {
        public DatosInvalidosException(String campo) {
            super("Dato inválido para el campo: " + campo);
        }
    }

    // Excepción para operaciones no permitidas
    public static class OperacionNoPermitidaException extends Exception {
        public OperacionNoPermitidaException(String operacion) {
            super("Operación no permitida: " + operacion);
        }
    }
}