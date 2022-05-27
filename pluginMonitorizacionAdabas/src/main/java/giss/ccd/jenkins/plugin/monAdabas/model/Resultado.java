package giss.ccd.jenkins.plugin.monAdabas.model;

public class Resultado {


    private String codigo = null;
    private String app = null;
    private String version = null;
    private String estadoFinal = null;

    //Propiedades para la accion de INICIAR PRUEBA
    private String ticketPrueba = null;
    private String IP_codIniciarPrueba = null;
    private String IP_descIniciarPrueba = null;
    private String IP_descLargaIniciarPrueba= null;
/*
    //Propiedades para la accion de ENTREGAR RELEASE
    private String ER_fichero = null;
    private String ER_proceso = null;
    private int ER_modulosProcesados = 0;
    private String ER_codEntregarRelease = null;
    private String ER_descEntregarRelease = null;
*/

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public String getTicketPrueba() {
        return ticketPrueba;
    }

    public void setTicketPrueba(String ticketPrueba) {
        this.ticketPrueba = ticketPrueba;
    }

    public String getIP_codIniciarPrueba() {
        return IP_codIniciarPrueba;
    }

    public void setIP_codIniciarPrueba(String IP_codIniciarPrueba) {
        this.IP_codIniciarPrueba = IP_codIniciarPrueba;
    }

    public String getIP_descIniciarPrueba() {
        return IP_descIniciarPrueba;
    }

    public void setIP_descIniciarPrueba(String IP_descIniciarPrueba) {
        this.IP_descIniciarPrueba = IP_descIniciarPrueba;
    }

    public String getIP_descLargaIniciarPrueba() {
        return IP_descLargaIniciarPrueba;
    }

    public void setIP_descLargaIniciarPrueba(String IP_descLargaIniciarPrueba) {
        this.IP_descLargaIniciarPrueba = IP_descLargaIniciarPrueba;
    }
}
