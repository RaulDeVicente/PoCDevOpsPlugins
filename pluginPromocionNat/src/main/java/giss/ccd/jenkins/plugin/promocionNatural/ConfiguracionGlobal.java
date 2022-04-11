package giss.ccd.jenkins.plugin.promocionNatural;


import giss.ccd.jenkins.plugin.promocionNatural.ws.ServicioPromocionNat;
import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;



/**
 * Example of Jenkins global configuration.
 */
@Extension
public class ConfiguracionGlobal extends GlobalConfiguration {

    /** @return the singleton instance */
    public static ConfiguracionGlobal get() {
        return GlobalConfiguration.all().get(ConfiguracionGlobal.class);
    }

    private String protocolo;
    private String servidor;
    private String puerto;
    private String patronFichero;
    private String carpetaProcesado;

    public ConfiguracionGlobal() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public String getProtocolo() {return protocolo;}
    public String getServidor() {return servidor;}
    public String getPuerto() {return puerto;}
    public String getPatronFichero() {return patronFichero;}
    public String getCarpetaProcesado() {return carpetaProcesado;}


    @DataBoundSetter
    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
        save();
    }

    @DataBoundSetter
    public void setServidor(String servidor) {
        this.servidor = servidor;
        save();
    }

    @DataBoundSetter
    public void setPuerto(String puerto) {
        this.puerto = puerto;
        save();
    }

    @DataBoundSetter
    public void setPatronFichero(String patronFichero) {
        this.patronFichero = patronFichero;
        save();
    }

    @DataBoundSetter
    public void setCarpetaProcesado(String carpetaProcesado) {
        this.carpetaProcesado = carpetaProcesado;
        save();
    }

    public FormValidation doCheckServidor(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning(Messages.Configuracion_global_avisoServidor());
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckPuerto(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning(Messages.Configuracion_global_avisoPuerto());
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckPatronFichero(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning(Messages.Configuracion_global_avisoPatronFichero());
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckCarpetaProcesado(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning(Messages.Configuracion_global_avisoCarpetaProcesado());
        }
        return FormValidation.ok();
    }

    public String getEndpoint(){
        return  getProtocolo() + ServicioPromocionNat.UNION_PROTOCOLO +
                getServidor()  + ServicioPromocionNat.UNION_PUERTO   +
                getPuerto()    + ServicioPromocionNat.SERVICE;
    }
}
