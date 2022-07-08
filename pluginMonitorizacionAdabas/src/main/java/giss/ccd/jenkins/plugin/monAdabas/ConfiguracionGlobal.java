package giss.ccd.jenkins.plugin.monAdabas;

import giss.ccd.jenkins.plugin.monAdabas.ws.ServicioMonAdabas;
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
    private String urlMonada;
    private String timeoutPDF;

    public ConfiguracionGlobal() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public String getProtocolo() {return protocolo;}
    public String getServidor() {return servidor;}
    public String getPuerto() {return puerto;}
    public String getUrlMonada() {return urlMonada;}
    public String getTimeoutPDF() {return timeoutPDF;}

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
    public void setUrlMonada(String urlMonada) {
        this.urlMonada = urlMonada;
        save();
    }

    @DataBoundSetter
    public void setTimeoutPDF(String timeoutPDF) {
        this.timeoutPDF = timeoutPDF;
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

    public String getEndpoint(){
        return  getProtocolo() + ServicioMonAdabas.UNION_PROTOCOLO +
                getServidor()  + ServicioMonAdabas.UNION_PUERTO   +
                getPuerto()    + ServicioMonAdabas.SERVICE;
    }
}
