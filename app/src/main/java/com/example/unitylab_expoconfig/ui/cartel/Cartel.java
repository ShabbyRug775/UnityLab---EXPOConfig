package com.example.unitylab_expoconfig.ui.cartel;

public class Cartel {
    private String id;
    private String nombreProyecto;
    private String equipo;
    private String materia;
    private String grupo;
    private String estado; // "pendiente", "imprimiendo", "impreso", "error"
    private String fechaSolicitud;
    private String urlArchivo;
    private int progresoImpresion;

    public Cartel() {
        // Constructor vacío necesario para Firebase
    }

    // Constructor completo
    public Cartel(int id, int idProyecto, String nombreProyecto, String equipo,
                  String materia, String grupo, String estado,
                  String fechaSolicitud, String urlArchivo, int progresoImpresion) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.nombreProyecto = nombreProyecto;
        this.equipo = equipo;
        this.materia = materia;
        this.grupo = grupo;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.urlArchivo = urlArchivo;
        this.progresoImpresion = progresoImpresion;
    }

    // Getters y Setters (generados automáticamente)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getUrlArchivo() { return urlArchivo; }
    public void setUrlArchivo(String urlArchivo) { this.urlArchivo = urlArchivo; }
    public int getProgresoImpresion() { return progresoImpresion; }
    public void setProgresoImpresion(int progresoImpresion) { this.progresoImpresion = progresoImpresion; }
}