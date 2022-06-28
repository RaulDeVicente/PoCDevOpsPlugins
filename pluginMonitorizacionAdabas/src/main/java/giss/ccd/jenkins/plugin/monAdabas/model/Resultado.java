package giss.ccd.jenkins.plugin.monAdabas.model;

public class Resultado {


    private String codigo = null;
    private String app = null;
    private String version = null;
    private String estadoFinal = null;
    private boolean hayException = false;
    private String mensajeException = null;

    //Propiedades para la accion de INICIAR PRUEBA
    private String ticketPrueba = null;
    private String IP_codIniciarPrueba = null;
    private String IP_descIniciarPrueba = null;
    private String IP_descLargaIniciarPrueba= null;

    //Propiedades para la accion de FINALIZAR PRUEBA
    private String FP_codFinalizarPrueba = null;
    private String FP_descFinalizarPrueba = null;
    private String FP_descLargaFinalizarPrueba = null;
    private String FP_nombreFicheroResumen = null;
    private String FP_nombreFicheroDetalle = null;
    private String FP_urlFicheroResumen = "";
    private String FP_urlFicheroDetalle = "";
    private String FP_codPruebaFinalizada = null;
    private String FP_descPruebaFinalizada = null;


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

    public boolean isHayException() {
        return hayException;
    }

    public void setHayException(boolean hayException) {
        this.hayException = hayException;
    }

    public String getMensajeException() {
        return mensajeException;
    }

    public void setMensajeException(String mensajeException) {
        this.mensajeException = mensajeException;
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

    public String getFP_codFinalizarPrueba() {
        return FP_codFinalizarPrueba;
    }

    public void setFP_codFinalizarPrueba(String FP_codFinalizarPrueba) {
        this.FP_codFinalizarPrueba = FP_codFinalizarPrueba;
    }

    public String getFP_descFinalizarPrueba() {
        return FP_descFinalizarPrueba;
    }

    public void setFP_descFinalizarPrueba(String FP_descFinalizarPrueba) {
        this.FP_descFinalizarPrueba = FP_descFinalizarPrueba;
    }

    public String getFP_descLargaFinalizarPrueba() {
        return FP_descLargaFinalizarPrueba;
    }

    public void setFP_descLargaFinalizarPrueba(String FP_descLargaFinalizarPrueba) {
        this.FP_descLargaFinalizarPrueba = FP_descLargaFinalizarPrueba;
    }

    public String getFP_nombreFicheroResumen() {
        return FP_nombreFicheroResumen;
    }

    public void setFP_nombreFicheroResumen(String FP_nombreFicheroResumen) {
        this.FP_nombreFicheroResumen = FP_nombreFicheroResumen;
    }

    public String getFP_nombreFicheroDetalle() {
        return FP_nombreFicheroDetalle;
    }

    public void setFP_nombreFicheroDetalle(String FP_nombreFicheroDetalle) {
        this.FP_nombreFicheroDetalle = FP_nombreFicheroDetalle;
    }

    public String getFP_urlFicheroResumen() {
        return FP_urlFicheroResumen;
    }

    public void setFP_urlFicheroResumen(String FP_urlFicheroResumen) {
        this.FP_urlFicheroResumen = FP_urlFicheroResumen;
    }

    public String getFP_urlFicheroDetalle() {
        return FP_urlFicheroDetalle;
    }

    public void setFP_urlFicheroDetalle(String FP_urlFicheroDetalle) {
        this.FP_urlFicheroDetalle = FP_urlFicheroDetalle;
    }

    public String getFP_codPruebaFinalizada() {
        return FP_codPruebaFinalizada;
    }

    public void setFP_codPruebaFinalizada(String FP_codPruebaFinalizada) {
        this.FP_codPruebaFinalizada = FP_codPruebaFinalizada;
    }

    public String getFP_descPruebaFinalizada() {
        return FP_descPruebaFinalizada;
    }

    public void setFP_descPruebaFinalizada(String FP_descPruebaFinalizada) {
        this.FP_descPruebaFinalizada = FP_descPruebaFinalizada;
    }
}
