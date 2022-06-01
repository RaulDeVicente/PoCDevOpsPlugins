package giss.ccd.jenkins.plugin.promocionNatural.model;

public class Resultado {


    private String codigo = null;
    private String app = null;
    private String version = null;
    private String estadoFinal = null;

    private boolean hayException = false;
    private String mensajeException = null;


    /*Propiedades para la accion de PROMOCION NATURAL*/
    private String DR_entornoDestino = null;
    private String DR_codReleaseDesplegada = null;
    private String DR_descReleaseDesplegada = null;
    private String DR_codDesplegarRelease = null;
    private String DR_descDesplegarRelease = null;

    /*Propiedades para la accion de ENTREGAR RELEASE*/
    private String ER_fichero = null;
    private String ER_proceso = null;
    private int ER_modulosProcesados = 0;
    private String ER_codEntregarRelease = null;
    private String ER_descEntregarRelease = null;

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

    public String getDR_codReleaseDesplegada() {
        return DR_codReleaseDesplegada;
    }

    public void setDR_codReleaseDesplegada(String DR_codReleaseDesplegada) {
        this.DR_codReleaseDesplegada = DR_codReleaseDesplegada;
    }

    public String getDR_descReleaseDesplegada() {
        return DR_descReleaseDesplegada;
    }

    public void setDR_descReleaseDesplegada(String DR_descReleaseDesplegada) {
        this.DR_descReleaseDesplegada = DR_descReleaseDesplegada;
    }

    public String getDR_codDesplegarRelease() {
        return DR_codDesplegarRelease;
    }

    public void setDR_codDesplegarRelease(String DR_codDesplegarRelease) {
        this.DR_codDesplegarRelease = DR_codDesplegarRelease;
    }

    public String getDR_descDesplegarRelease() {
        return DR_descDesplegarRelease;
    }

    public void setDR_descDesplegarRelease(String DR_descDesplegarRelease) {
        this.DR_descDesplegarRelease = DR_descDesplegarRelease;
    }

    public String getER_fichero() {
        return ER_fichero;
    }

    public void setER_fichero(String ER_fichero) {
        this.ER_fichero = ER_fichero;
    }

    public String getER_proceso() {
        return ER_proceso;
    }

    public void setER_proceso(String ER_proceso) {
        this.ER_proceso = ER_proceso;
    }



    public String getER_codEntregarRelease() {
        return ER_codEntregarRelease;
    }

    public void setER_codEntregarRelease(String ER_codEntregarRelease) {
        this.ER_codEntregarRelease = ER_codEntregarRelease;
    }

    public String getER_descEntregarRelease() {
        return ER_descEntregarRelease;
    }

    public void setER_descEntregarRelease(String ER_descEntregarRelease) {
        this.ER_descEntregarRelease = ER_descEntregarRelease;
    }

    public int getER_modulosProcesados() {
        return ER_modulosProcesados;
    }

    public void setER_modulosProcesados(int ER_modulosProcesados) {
        this.ER_modulosProcesados = ER_modulosProcesados;
    }

    public String getDR_entornoDestino() {
        return DR_entornoDestino;
    }

    public void setDR_entornoDestino(String DR_entornoDestino) {
        this.DR_entornoDestino = DR_entornoDestino;
    }
}
