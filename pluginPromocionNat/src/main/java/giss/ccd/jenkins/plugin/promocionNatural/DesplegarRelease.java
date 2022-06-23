package giss.ccd.jenkins.plugin.promocionNatural;

import com.sun.xml.ws.fault.ServerSOAPFaultException;
import com.sun.xml.ws.wsdl.parser.InaccessibleWSDLException;
import giss.ccd.jenkins.plugin.promocionNatural.model.Resultado;
import giss.ccd.jenkins.plugin.promocionNatural.util.ConversionString;
import giss.ccd.jenkins.plugin.promocionNatural.ws.ServicioPromocionNat;
import giss.ccd.jenkins.plugin.util.UtilJSON;
import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import jakarta.xml.ws.Holder;

import javax.servlet.ServletException;
import java.io.IOException;
//import java.net.MalformedURLException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * Builder del Step "Promocion Natural - Desplegar release"
 */
public class DesplegarRelease extends Builder implements SimpleBuildStep {

    private final String aplicacion;
    private final String version;
    private final String entornoDestino;
    private final String estadoRetorno;
    private int intervaloPooling = 60;
    private int timeoutPooling = 600;

    private final static String PREFIJO_JSON= "desplegarReleaseOutput";
    private final static String OPERACION= "promocionNatural";

    /**
     * Instantiates a new DesplegarRelease.
     *
     * @param aplicacion Aplicación
     * @param version Versión
     * @param entornoDestino Entorno de destino
     */
    @DataBoundConstructor
    public DesplegarRelease(String aplicacion,
                            String version,
                            String entornoDestino,
                            String estadoRetorno,
                            String intervaloPooling,
                            String timeoutPooling) {
        this.aplicacion = aplicacion;
        this.version = version;
        this.entornoDestino = entornoDestino;
        this.estadoRetorno = estadoRetorno;
        if(intervaloPooling!=null) {
            int valorInt = Integer.parseInt(intervaloPooling);
            if(valorInt>0) {
                this.intervaloPooling = valorInt;
            }
        }
        if(timeoutPooling!=null) {
            int valorInt = Integer.parseInt(timeoutPooling);
            if(valorInt>0) {
                this.timeoutPooling = valorInt;
            }
        }
    }

    public String getAplicacion() {
        return aplicacion;
    }
    public String getVersion() {
        return version;
    }
    public String getEntornoDestino() {
        return entornoDestino;
    }
    public String getEstadoRetorno() {return estadoRetorno;}
    public int getIntervaloPooling() {return intervaloPooling;}
    public int getTimeoutPooling() {return timeoutPooling;}

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        String endpoint = ConfiguracionGlobal.get().getEndpoint();

        listener.getLogger().println("Endpoint: " + endpoint);
        listener.getLogger().println("Aplicación: " + aplicacion);
        listener.getLogger().println("Versión: " + version);
        listener.getLogger().println("Entorno destino: " + entornoDestino);
        listener.getLogger().println("Intervalo Pool: " + intervaloPooling);
        listener.getLogger().println("Timeout Pool: " + timeoutPooling);

        Resultado resultado = new Resultado();

        String respuesta = "-1";
        run.setResult(Result.SUCCESS);

        try {
            //Parametros de retorno
            final Holder<String> codRetornoDesplegarRelease = new Holder<>();
            final Holder<String> descRetornoDesplegarRelease = new Holder<>();

            //Llamada al servicio
            ServicioPromocionNat servicio = new ServicioPromocionNat();
            Thread t = Thread.currentThread();
            ClassLoader orig = t.getContextClassLoader();
            t.setContextClassLoader(ServicioPromocionNat.class.getClassLoader());
            try {
                servicio.inicializarServicioPromocionNat(endpoint).desplegarRelease(
                        aplicacion,
                        version,
                        entornoDestino,
                        codRetornoDesplegarRelease,
                        descRetornoDesplegarRelease);
            } finally {
                t.setContextClassLoader(orig);
            }

            if(codRetornoDesplegarRelease.value!=null){
                respuesta = codRetornoDesplegarRelease.value;
                resultado.setDR_codDesplegarRelease(codRetornoDesplegarRelease.value);
            }

            if(descRetornoDesplegarRelease.value!=null){
                resultado.setDR_descDesplegarRelease(descRetornoDesplegarRelease.value);
            }

            listener.getLogger().println("Respuesta del servicio desplegarRelease:");
            listener.getLogger().println(" - Código de retorno: " + Objects.toString(codRetornoDesplegarRelease.value,""));
            listener.getLogger().println(" - Descripción: " + Objects.toString(descRetornoDesplegarRelease.value,""));

            if(respuesta!=null && respuesta.equals("0")) {
                //Parametros de retorno
                final Holder<String> codRetornoReleaseDesplegada = new Holder<>();
                final Holder<String> descRetornoReleaseDesplegada = new Holder<>();

                int tiempoConsumido = 0;
                String finalizado = "-1";
                final Holder<String> pendienteFinalizar = new Holder<>();
                while (timeoutPooling >= tiempoConsumido) {
                    t.setContextClassLoader(ServicioPromocionNat.class.getClassLoader());
                    try {
                        servicio.inicializarServicioPromocionNat(endpoint).releaseDesplegada(
                                aplicacion,
                                version,
                                entornoDestino,
                                pendienteFinalizar,
                                codRetornoReleaseDesplegada,
                                descRetornoReleaseDesplegada);
                       } finally {
                        t.setContextClassLoader(orig);
                    }

                    if (codRetornoReleaseDesplegada.value != null) {
                        respuesta = codRetornoReleaseDesplegada.value;
                        resultado.setDR_codReleaseDesplegada(codRetornoReleaseDesplegada.value);
                    }

                    if (descRetornoReleaseDesplegada.value != null) {
                        resultado.setDR_descReleaseDesplegada(descRetornoReleaseDesplegada.value);
                    }

                    if (pendienteFinalizar.value != null) {
                        finalizado = pendienteFinalizar.value;
                    }

                    //Si se produce un error en el servicio o si ya ha terminado, salimos.
                    if (!respuesta.equals("0") || finalizado.equals("0")) {
                        break;
                    }

                    tiempoConsumido = tiempoConsumido + intervaloPooling;

                    if (tiempoConsumido >= timeoutPooling) {
                        respuesta = "-1";
                        descRetornoReleaseDesplegada.value = descRetornoReleaseDesplegada + ". Se ha producido un Timeout, tiempo de espera: " + timeoutPooling + " segundos.";
                        break;
                    } else {
                        listener.getLogger().println("Esperando a la instalación de la release. Volviendo a llamar al servicio en " + intervaloPooling + " segundos...");
                        Thread.sleep(intervaloPooling * 1000);
                    }
                }
            }
        }catch (MalformedURLException e) {
            listener.error(Messages.DescriptorImpl_excepciones_errorURLEndpoint());
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            resultado.setHayException(true);
        }catch (ServerSOAPFaultException e) {
            listener.error(e.getMessage());
            resultado.setHayException(true);
        }catch (InaccessibleWSDLException e) {
            listener.error(Messages.DescriptorImpl_excepciones_inaccesibleWSDL());
            listener.error(e.getMessage());
            resultado.setHayException(true);
        }catch (Exception e) {
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            resultado.setHayException(true);
        }finally {

            if(resultado.isHayException()){
                run.setResult(Result.FAILURE);
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                resultado.setCodigo("-1");
            }

            if(!respuesta.equals("0")) {
                listener.error(Messages.DescriptorImpl_excepciones_retornoDistintoCero());
                switch (estadoRetorno.toUpperCase()){
                    case "SUCCESS": run.setResult(Result.SUCCESS);
                        break;
                    case "UNSTABLE": run.setResult(Result.UNSTABLE);
                        break;
                    default : run.setResult(Result.FAILURE);
                        break;
                }
            }

            //Creación de objeto de respuesta para pintar datos en pantalla
            resultado.setCodigo(respuesta);
            resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
            resultado.setApp(aplicacion);
            resultado.setVersion(version);
            resultado.setDR_entornoDestino(ConversionString.recuperarEntorno(entornoDestino));
            try {


                //Creacion de fichero JSON
                JSONObject objPlugin = new JSONObject();
                JSONObject objDesplegarRelease = new JSONObject();
                JSONObject objReleaseDesplegada = new JSONObject();

                objDesplegarRelease.put("codRetorno", resultado.getDR_codDesplegarRelease());
                objDesplegarRelease.put("descRetorno",resultado.getDR_descDesplegarRelease());

                objReleaseDesplegada.put("codRetorno", resultado.getDR_codReleaseDesplegada());
                objReleaseDesplegada.put("descRetorno",resultado.getDR_descReleaseDesplegada());

                objPlugin.put("respuesta", respuesta);
                objPlugin.put("respuestaDesplegarRelease",objDesplegarRelease);
                objPlugin.put("respuestaReleaseDesplegada",objReleaseDesplegada);

                EnvVars envVars=run.getEnvironment(listener);
                String rutaBuildLocal = Paths.get(String.valueOf(workspace), OPERACION, envVars.get("BUILD_ID")).toString();

                UtilJSON utilJSON= new UtilJSON();
                utilJSON.guardarJSON(objPlugin, rutaBuildLocal, PREFIJO_JSON);


                if(resultado.getDR_codReleaseDesplegada()!=null) {
                    listener.getLogger().println("Respuesta del servicio releaseDesplegada:");
                    listener.getLogger().println(" - Código de retorno: " + resultado.getDR_codReleaseDesplegada());
                    listener.getLogger().println(" - Descripción: " + resultado.getDR_descReleaseDesplegada());
                }
                listener.getLogger().println("Creación de fichero JSON de respuesta en: " + Paths.get(rutaBuildLocal));
                listener.getLogger().println("El plugin se ha ejecutado con código: " + respuesta);

                run.addAction(new DesplegarReleaseAction(resultado));

            }catch (RuntimeException e) {
                listener.error(Messages.DescriptorImpl_excepciones_errorGenerarJSON());
                listener.error(e.getMessage());
                listener.getLogger().println(e.getCause());
                run.setResult(Result.FAILURE);
                resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
                resultado.setHayException(true);
                resultado.setCodigo("-1");
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                run.addAction(new DesplegarReleaseAction(resultado));
            }
            catch (Exception e) {
                listener.error(e.getMessage());
                listener.getLogger().println(e.getCause());
                run.setResult(Result.FAILURE);
                resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
                resultado.setHayException(true);
                resultado.setCodigo("-1");
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                run.addAction(new DesplegarReleaseAction(resultado));
            }
        }
    }

    @Override
    public DescriptorImpl getDescriptor(){
        return(DescriptorImpl) super.getDescriptor();
    }


    @Symbol("desplegarRelease")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl(){
            load();
        }

        public FormValidation doCheckAplicacion(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckVersion(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckEntornoDestino(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckIntervaloPooling(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            if (value!=null) {
                int valorInt = Integer.parseInt(value);
                if(valorInt==0) {
                    return FormValidation.error(Messages.DescriptorImpl_errors_numerico());
                }
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckTimeoutPooling(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            if (value!=null) {
                int valorInt = Integer.parseInt(value);
                if(valorInt==0) {
                    return FormValidation.error(Messages.DescriptorImpl_errors_numerico());
                }
            }
            return FormValidation.ok();
        }
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.DesplegarRelease_DescriptorImpl_DisplayName();
        }

    }


}
