package com.example.unitylab_expoconfig.ui.proyectos;

public class Proyecto {
    private int id;
    private String nombreProyecto;
    private String descripcion;
    private String nombreEquipo;
    private String materia;
    private String grupo;
    private String semestre;
    private String carrera;
    private String herramientasUtilizadas;
    private String arquitectura;
    private String funcionesPrincipales;
    private int idProfesor;
    private int idEstudianteLider;
    private String fechaCreacion;
    private String estado;
    private String urlCartel;
    private String nombreProfesor;
    private String nombreEstudianteLider;

    // Constructor vacío
    public Proyecto() {}

    // Constructor completo
    public Proyecto(int id, String nombreProyecto, String descripcion, String nombreEquipo,
                    String materia, String grupo, String semestre, String carrera,
                    String herramientasUtilizadas, String arquitectura, String funcionesPrincipales,
                    int idProfesor, int idEstudianteLider, String fechaCreacion, String estado,
                    String urlCartel) {
        this.id = id;
        this.nombreProyecto = nombreProyecto;
        this.descripcion = descripcion;
        this.nombreEquipo = nombreEquipo;
        this.materia = materia;
        this.grupo = grupo;
        this.semestre = semestre;
        this.carrera = carrera;
        this.herramientasUtilizadas = herramientasUtilizadas;
        this.arquitectura = arquitectura;
        this.funcionesPrincipales = funcionesPrincipales;
        this.idProfesor = idProfesor;
        this.idEstudianteLider = idEstudianteLider;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.urlCartel = urlCartel;
    }

    // Getters
    public int getId() { return id; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getDescripcion() { return descripcion; }
    public String getNombreEquipo() { return nombreEquipo; }
    public String getMateria() { return materia; }
    public String getGrupo() { return grupo; }
    public String getSemestre() { return semestre; }
    public String getCarrera() { return carrera; }
    public String getHerramientasUtilizadas() { return herramientasUtilizadas; }
    public String getArquitectura() { return arquitectura; }
    public String getFuncionesPrincipales() { return funcionesPrincipales; }
    public int getIdProfesor() { return idProfesor; }
    public int getIdEstudianteLider() { return idEstudianteLider; }
    public String getFechaCreacion() { return fechaCreacion; }
    public String getEstado() { return estado; }
    public String getUrlCartel() { return urlCartel; }
    public String getNombreProfesor() { return nombreProfesor; }
    public String getNombreEstudianteLider() { return nombreEstudianteLider; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public void setMateria(String materia) { this.materia = materia; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public void setSemestre(String semestre) { this.semestre = semestre; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setHerramientasUtilizadas(String herramientasUtilizadas) { this.herramientasUtilizadas = herramientasUtilizadas; }
    public void setArquitectura(String arquitectura) { this.arquitectura = arquitectura; }
    public void setFuncionesPrincipales(String funcionesPrincipales) { this.funcionesPrincipales = funcionesPrincipales; }
    public void setIdProfesor(int idProfesor) { this.idProfesor = idProfesor; }
    public void setIdEstudianteLider(int idEstudianteLider) { this.idEstudianteLider = idEstudianteLider; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setUrlCartel(String urlCartel) { this.urlCartel = urlCartel; }
    public void setNombreProfesor(String nombreProfesor) { this.nombreProfesor = nombreProfesor; }
    public void setNombreEstudianteLider(String nombreEstudianteLider) { this.nombreEstudianteLider = nombreEstudianteLider; }

    // Metodo para obtener un resumen del proyecto
    public String getResumen() {
        return nombreProyecto + " - " + nombreEquipo + " (" + materia + ")";
    }

    // Metodo para verificar si el proyecto está activo
    public boolean isActivo() {
        return "ACTIVO".equals(estado);
    }

    // Metodo para obtener el color del estado (para UI)
    public int getColorEstado() {
        switch (estado) {
            case "ACTIVO":
                return android.graphics.Color.GREEN;
            case "EN_DESARROLLO":
                return android.graphics.Color.BLUE;
            case "COMPLETADO":
                return android.graphics.Color.parseColor("#4CAF50");
            case "PAUSADO":
                return android.graphics.Color.parseColor("#FF9800");
            case "CANCELADO":
                return android.graphics.Color.RED;
            default:
                return android.graphics.Color.GRAY;
        }
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", nombreEquipo='" + nombreEquipo + '\'' +
                ", materia='" + materia + '\'' +
                ", estado='" + estado + '\'' +
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