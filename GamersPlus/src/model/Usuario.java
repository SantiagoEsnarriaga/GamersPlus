package model;

import java.util.Objects;

public class Usuario {
    
    private int id;
    
    // Datos inmutables del usuario - no cambian después del registro
    private final String nombre;
    private final String email;
    
    // Solo la contraseña puede cambiar (ej: reset password)
    private String contraseña;

    // Constructor para casos donde no tenemos la contraseña (ej: consultas públicas)
    public Usuario(int id, String nombre, String email) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.email = Objects.requireNonNull(email, "El email no puede ser nulo");
    }

    // Constructor completo con contraseña
    public Usuario(int id, String nombre, String email, String contraseña) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.email = Objects.requireNonNull(email, "El email no puede ser nulo");
        this.contraseña = contraseña;
    }

    // ======== GETTERS Y SETTERS ========
    
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getContraseña() { 
        return contraseña; 
    }
    
    // Solo la contraseña puede cambiar después de crear el usuario
    public void setContraseña(String contraseña) { 
        this.contraseña = contraseña; 
    }

    // ======== MÉTODOS SOBRESCRITOS ========
    
    /**
     * Compara usuarios por ID (clave primaria en BD)
     * Dos usuarios son iguales si tienen el mismo ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id == usuario.id;
    }

    /**
     * Hash code basado en ID para usar en colecciones (HashMap, HashSet, etc.)
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Representación en string del usuario
     * Solo muestra el nombre para simplicidad en la interfaz de usuario
     */
    @Override
    public String toString() {
        return nombre;
    }
}