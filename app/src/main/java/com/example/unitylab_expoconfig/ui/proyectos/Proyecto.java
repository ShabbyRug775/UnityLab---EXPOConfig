package com.example.unitylab_expoconfig.ui.proyectos;

public class Proyecto {
    private int id;
    private String nombreProyecto;
    private String descripcion;
    private int idProfesor;
    private int idEquipo;
    private String fechaCreacion;

    // Constructor vacío
    public Proyecto() {}

    public Proyecto(int id, String nombreProyecto, String descripcion,
                    int idProfesor, int idEquipo, String fechaCreacion) {
        this.id = id;
        this.nombreProyecto = nombreProyecto;
        this.descripcion = descripcion;
        this.idProfesor = idProfesor;
        this.idEquipo = idEquipo;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters
    public int getId() { return id; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getDescripcion() { return descripcion; }
    public int getIdProfesor() { return idProfesor; }
    public int getIdEquipo() { return idEquipo; }
    public String getFechaCreacion() { return fechaCreacion; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setIdProfesor(int idProfesor) { this.idProfesor = idProfesor; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", nombre='" + nombreProyecto + '\'' +
                ", equipo=" + (idEquipo) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proyecto proyecto = (Proyecto) obj;
        return id == proyecto.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}