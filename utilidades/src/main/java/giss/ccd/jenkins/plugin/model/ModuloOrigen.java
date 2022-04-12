package giss.ccd.jenkins.plugin.model;

public class ModuloOrigen {

    private String nombre;
    private String modo;

    public ModuloOrigen() {
    }

    public ModuloOrigen(String nombre, String modo) {
        this.nombre = nombre;
        this.modo = modo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }
}
