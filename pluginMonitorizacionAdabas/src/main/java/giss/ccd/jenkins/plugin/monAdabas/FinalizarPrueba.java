package giss.ccd.jenkins.plugin.monAdabas;


import giss.ccd.jenkins.plugin.monAdabas.model.Resultado;
import giss.ccd.jenkins.plugin.monAdabas.ws.ServicioMonAdabas;
import giss.ccd.jenkins.plugin.util.RecursosFicheros;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.net.URI;
import java.util.Objects;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

/**
 * Builder del Step "MonitorizacionAdabas - Finalizar prueba"
 */
public class FinalizarPrueba extends Builder implements SimpleBuildStep {

    private final String ticketPrueba;
    private String estadoRetorno;
    private int intervaloPooling = 60;
    private int timeoutPooling = 600;
    private final static String PREFIJO_JSON= "finalizarPruebaOutput";
    private final static String OPERACION= "monitorizacionAdabas";

    /**
     * Instantiates a new Finalizar prueba.
     *
     * @param ticketPrueba the ticket prueba
     */
    @DataBoundConstructor
    public FinalizarPrueba(String ticketPrueba,
                           String estadoRetorno,
                           String intervaloPooling,
                           String timeoutPooling) {
        this.ticketPrueba = ticketPrueba;
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

    public String getTicketPrueba() {
        return ticketPrueba;
    }
    public String getEstadoRetorno() {return estadoRetorno;}
    public int getIntervaloPooling() {return intervaloPooling;}
    public int getTimeoutPooling() {return timeoutPooling;}
    public void setEstadoRetorno(String estadoRetorno) {this.estadoRetorno = estadoRetorno;}

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        String endpoint = ConfiguracionGlobal.get().getEndpoint();
        URI urlMonada = URI.create(ConfiguracionGlobal.get().getUrlMonada());
        listener.getLogger().println("Endpoint: " + endpoint);
        listener.getLogger().println("TicketPrueba: " + ticketPrueba);
        listener.getLogger().println("Intervalo Pool: " + intervaloPooling);
        listener.getLogger().println("Timeout Pool: " + timeoutPooling);
        listener.getLogger().println("URL Monada: " + urlMonada);

        Resultado resultado = new Resultado();

        String respuesta="-1";
        run.setResult(Result.SUCCESS);
        try {
            //Parametros de retorno
            final Holder<String> codRetornoFinalizarPrueba = new Holder<String>();
            final Holder<String> descRetornoFinalizarPrueba = new Holder<String>();
            final Holder<String> descRetornoLargoFinalizarPrueba = new Holder<String>();

            //Llamada al servicio
            ServicioMonAdabas servicio = new ServicioMonAdabas();
            Thread t = Thread.currentThread();
            ClassLoader orig = t.getContextClassLoader();
            t.setContextClassLoader(ServicioMonAdabas.class.getClassLoader());
            try {
                servicio.inicializarServicioMonitAdabas(endpoint).finalizarPrueba(
                        ticketPrueba,
                        codRetornoFinalizarPrueba,
                        descRetornoFinalizarPrueba,
                        descRetornoLargoFinalizarPrueba);
            } finally {
                t.setContextClassLoader(orig);
            }

            if(codRetornoFinalizarPrueba.value!=null){
                respuesta = codRetornoFinalizarPrueba.value;
                resultado.setFP_codFinalizarPrueba(codRetornoFinalizarPrueba.value);
            }

            if(descRetornoFinalizarPrueba.value!=null){
                resultado.setFP_descFinalizarPrueba(descRetornoFinalizarPrueba.value);
            }

            if(descRetornoLargoFinalizarPrueba.value!=null){
                resultado.setFP_descLargaFinalizarPrueba(descRetornoLargoFinalizarPrueba.value);
            }

            listener.getLogger().println("Respuesta del servicio finalizarPrueba:");
            listener.getLogger().println(" - Código de retorno: " + Objects.toString(codRetornoFinalizarPrueba.value,""));
            listener.getLogger().println(" - Descripción: " + Objects.toString(descRetornoFinalizarPrueba.value,""));
            listener.getLogger().println(" - Descripcion larga: " + Objects.toString(descRetornoLargoFinalizarPrueba.value,""));


            //Llamada al servicio diferido de pruebaFinalizada
            if(respuesta!=null && respuesta.equals("0")) {

                //Parametros de retorno
                final Holder<String> nombreFicheroResumen = new Holder<String>();
                final Holder<String> nombreFicheroDetalle = new Holder<String>();
                final Holder<String> codRetornoPruebaFinalizada = new Holder<String>();
                final Holder<String> descRetornoPruebaFinalizada = new Holder<String>();
                final Holder<String> pendienteFinalizar = new Holder<String>();

                int tiempoConsumido = 0;
                String finalizado="-1";

                while (timeoutPooling>=tiempoConsumido){
                    t.setContextClassLoader(ServicioMonAdabas.class.getClassLoader());
                    try {
                        servicio.inicializarServicioMonitAdabas(endpoint).pruebaFinalizada(
                                ticketPrueba,
                                pendienteFinalizar,
                                nombreFicheroResumen,
                                nombreFicheroDetalle,
                                codRetornoPruebaFinalizada,
                                descRetornoPruebaFinalizada);
                    } finally {
                        t.setContextClassLoader(orig);
                    }

                    if(codRetornoPruebaFinalizada.value!=null) {
                        respuesta = codRetornoPruebaFinalizada.value;
                        resultado.setFP_codPruebaFinalizada(codRetornoPruebaFinalizada.value);
                    }

                    if(descRetornoPruebaFinalizada.value!=null) {
                        resultado.setFP_descPruebaFinalizada(descRetornoPruebaFinalizada.value);
                    }

                    if(nombreFicheroResumen.value!=null) {
                        resultado.setFP_nombreFicheroResumen(nombreFicheroResumen.value+=".pdf");
                    }

                    if(nombreFicheroDetalle.value!=null) {
                        resultado.setFP_nombreFicheroDetalle(nombreFicheroDetalle.value+=".pdf");

                    }

                    if(pendienteFinalizar.value!=null){
                        finalizado = pendienteFinalizar.value;
                    }

                    //Si se produce un error en el servicio o si ya ha terminado, salimos.
                    if(!respuesta.equals("0") || finalizado.equals("0")){
                        break;
                    }

                    tiempoConsumido = tiempoConsumido + intervaloPooling;

                    if (tiempoConsumido >= timeoutPooling){
                        respuesta ="-1";
                        descRetornoPruebaFinalizada.value = descRetornoPruebaFinalizada + ". Se ha producido un Timeout, tiempo de espera: " + timeoutPooling + " segundos.";
                        break;
                    }else {
                        listener.getLogger().println("Esperando a la finalización del análisis de las pruebas. Volviendo a llamar al servicio en " + intervaloPooling + " segundos...");
                        Thread.sleep(intervaloPooling * 1000);
                    }
               }
            }
        } catch (MalformedURLException e) {
            listener.error(Messages.DescriptorImpl_excepciones_errorURLEndpoint());
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            run.setResult(Result.FAILURE);
            resultado.setHayException(true);
            resultado.setCodigo("-1");
            resultado.setMensajeException(Messages.Excepcion_mensaje());
        }catch (ServerSOAPFaultException e) {
            listener.error(e.getMessage());
            run.setResult(Result.FAILURE);
            resultado.setHayException(true);
            resultado.setCodigo("-1");
            resultado.setMensajeException(Messages.Excepcion_mensaje());
        }catch (Exception e) {
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            run.setResult(Result.FAILURE);
            resultado.setHayException(true);
            resultado.setCodigo("-1");
            resultado.setMensajeException(Messages.Excepcion_mensaje());
        } finally {
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
            resultado.setTicketPrueba(ticketPrueba);

            try {
                //Descarga de informes (url base de Monada + nombre recibido)

                EnvVars envVars=run.getEnvironment(listener);
                String rutaBuildLocal = Paths.get(String.valueOf(workspace), OPERACION, envVars.get("BUILD_ID")).toString();
                String urlFicheroResumen ="";
                String urlFicheroDetalle ="";

                if(resultado.getFP_nombreFicheroResumen()!=null) {
                    urlFicheroResumen = urlMonada.resolve(resultado.getFP_nombreFicheroResumen()).toString();
                    RecursosFicheros recursosFicheros = new RecursosFicheros();
                    recursosFicheros.descargarFicheroURL(new URL(urlFicheroResumen), rutaBuildLocal,  resultado.getFP_nombreFicheroResumen());
                }

                if(resultado.getFP_nombreFicheroDetalle()!=null) {
                    urlFicheroDetalle = urlMonada.resolve(resultado.getFP_nombreFicheroDetalle()).toString();
                    RecursosFicheros recursosFicheros = new RecursosFicheros();
                    recursosFicheros.descargarFicheroURL(new URL(urlFicheroDetalle), rutaBuildLocal,  resultado.getFP_nombreFicheroDetalle());
                }

                resultado.setFP_urlFicheroResumen(urlFicheroResumen);
                resultado.setFP_urlFicheroDetalle(urlFicheroDetalle);

                //Creacion de fichero JSON
                JSONObject objPlugin = new JSONObject();
                JSONObject objFinalizarPrueba = new JSONObject();
                JSONObject objPruebaFinalizada = new JSONObject();

                objFinalizarPrueba.put("codRetorno", resultado.getFP_codFinalizarPrueba());
                objFinalizarPrueba.put("descRetorno",resultado.getFP_descFinalizarPrueba());
                objFinalizarPrueba.put("descRetornoLargo", resultado.getFP_descLargaFinalizarPrueba());

                objPruebaFinalizada.put("codRetorno", resultado.getFP_codPruebaFinalizada());
                objPruebaFinalizada.put("descRetorno",resultado.getFP_descPruebaFinalizada());

                objPruebaFinalizada.put("urlFicheroResumen", urlFicheroResumen);
                objPruebaFinalizada.put("urlFicheroDetalle", urlFicheroDetalle);

                objPlugin.put("respuesta", respuesta);
                objPlugin.put("respuestaFinalizarPrueba",objFinalizarPrueba);
                objPlugin.put("respuestaPruebaFinalizada",objPruebaFinalizada);

                UtilJSON utilJSON= new UtilJSON();
                utilJSON.guardarJSON(objPlugin, rutaBuildLocal, PREFIJO_JSON);

                if(resultado.getFP_codPruebaFinalizada()!=null) {
                    listener.getLogger().println("Respuesta del servicio pruebaFinalizada:");
                    listener.getLogger().println(" - Código de retorno: " + resultado.getFP_codPruebaFinalizada());
                    listener.getLogger().println(" - Descripción: " + resultado.getFP_descPruebaFinalizada());
                    listener.getLogger().println(" - URL Fichero Resumen: " + urlFicheroResumen);
                    listener.getLogger().println(" - URL Fichero Detalle: " + urlFicheroDetalle);
                }
                listener.getLogger().println("Ficheros generados en: " + rutaBuildLocal);
                listener.getLogger().println("El plugin se ha ejecutado con código: " + respuesta);

                run.addAction(new FinalizarPruebaAction(resultado));
            }catch (RuntimeException e) {
                listener.error(Messages.DescriptorImpl_excepciones_errorGenerarJSON());
                listener.error(e.getMessage());
                listener.getLogger().println(e.getCause());
                run.setResult(Result.FAILURE);
                resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
                resultado.setHayException(true);
                resultado.setCodigo("-1");
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                run.addAction(new FinalizarPruebaAction(resultado));
            }catch (Exception e) {
                listener.error(e.getMessage());
                listener.getLogger().println(e.getCause());
                run.setResult(Result.FAILURE);
                resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
                resultado.setHayException(true);
                resultado.setCodigo("-1");
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                run.addAction(new FinalizarPruebaAction(resultado));
            }
        }
    }

    @Override
    public DescriptorImpl getDescriptor(){
        return(DescriptorImpl) super.getDescriptor();
    }

    @Symbol("monAdabasFinalizaPrueba")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl(){
            load();
        }

        public FormValidation doCheckTicketPrueba(@QueryParameter String value)
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
            return Messages.FinalizarPrueba_DescriptorImpl_DisplayName();
        }

    }


}
