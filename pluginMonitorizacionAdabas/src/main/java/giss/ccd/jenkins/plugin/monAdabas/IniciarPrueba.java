package giss.ccd.jenkins.plugin.monAdabas;


import es.seg_social.ccd.monadabas.service.*;
import giss.ccd.jenkins.plugin.monAdabas.model.Resultado;
import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import giss.ccd.jenkins.plugin.monAdabas.model.PruebaForm;
import giss.ccd.jenkins.plugin.util.UtilJSON;
import giss.ccd.jenkins.plugin.monAdabas.ws.ServicioMonAdabas;
import jenkins.tasks.SimpleBuildStep;
import org.json.JSONObject;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import jakarta.xml.ws.Holder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

/**
 * Builder del step "MonitorizacionAdabas - Iniciar prueba"
 */
public class IniciarPrueba extends Builder implements SimpleBuildStep {

    private final String aplicacion;
    private final String version;
    private List<PruebaForm> listaPruebas;
    private final String estadoPruebas;


    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private final static String PREFIJO_JSON= "iniciarPruebaOutput";
    private final static String OPERACION= "monitorizacionAdabas";

    @DataBoundConstructor
    public IniciarPrueba(String aplicacion,
                         String version,
                         List<PruebaForm> listaPruebas,
                         String estadoPruebas) {
        this.aplicacion = aplicacion.toUpperCase();
        this.version = version.toUpperCase();
        this.listaPruebas = listaPruebas;
        this.estadoPruebas= estadoPruebas;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        String endpoint = ConfiguracionGlobal.get().getEndpoint();

        listener.getLogger().println("Endpoint: " + endpoint);
        listener.getLogger().println("Aplicación: " + aplicacion);
        listener.getLogger().println("Versión: " + version);
        listener.getLogger().println("Acción al no recibir ticket: " + estadoPruebas);

        Resultado resultado = new Resultado();

        String respuesta = "-1";
        run.setResult(Result.SUCCESS);

        try {
           //Se elimina el objeto Elementos/modulos desde version 1.2.0

           //Creacion de objeto Pruebas
           Pruebas pruebas = new Pruebas();
           if (listaPruebas == null || listaPruebas.isEmpty()) {
               listener.error(Messages.DescriptorImpl_excepciones_noExistePruebas());
               run.setResult(Result.FAILURE);
           } else {
               List<Prueba> lPruebas = pruebas.getPrueba();
               for (PruebaForm pruebaForm : listaPruebas) {
                   Prueba prueba = new Prueba();
                   prueba.setTipoPrueba(pruebaForm.getTipoPrueba().toUpperCase());
                   prueba.setAlcance(pruebaForm.getAlcance().toUpperCase());
                   prueba.setElemento(pruebaForm.getElemento().toUpperCase());
                   prueba.setUsuario(pruebaForm.getUsuario().toUpperCase());
                   lPruebas.add(prueba);
               }
           }
            //Parametros de retorno
            final Holder<String> ticketPrueba = new Holder<>();
            final Holder<String> codRetorno = new Holder<>();
            final Holder<String> descRetorno = new Holder<>();
            final Holder<String> descRetornoLargo = new Holder<>();

            //Llamada al servicio
            ServicioMonAdabas servicio = new ServicioMonAdabas();
            Thread t = Thread.currentThread();
            ClassLoader orig = t.getContextClassLoader();
            t.setContextClassLoader(ServicioMonAdabas.class.getClassLoader());
            try {
                servicio.inicializarServicioMonitAdabas(endpoint).iniciarPrueba(
                        aplicacion,
                        version,
                        pruebas,
                        ticketPrueba,
                        codRetorno,
                        descRetorno,
                        descRetornoLargo);
            } finally {
                t.setContextClassLoader(orig);
            }

            if(codRetorno.value!=null){
                respuesta = codRetorno.value;
                resultado.setIP_codIniciarPrueba(codRetorno.value);
            }

            if(descRetorno.value!=null){
                resultado.setIP_descIniciarPrueba(descRetorno.value);
            }

            if(descRetornoLargo.value!=null){
                resultado.setIP_descLargaIniciarPrueba(descRetornoLargo.value);
            }

            if(ticketPrueba.value!=null){
                resultado.setTicketPrueba(ticketPrueba.value);
            }

            //En el caso de que no se reciba ticket, se marca la ejecucion como se ha indicado en la ejecucion
           if(ticketPrueba.value==null || ticketPrueba.value.isEmpty()) {
                    listener.error(Messages.DescriptorImpl_excepciones_noExisteTicket());
                    switch (estadoPruebas.toUpperCase()) {
                        case "SUCCESS":
                            run.setResult(Result.SUCCESS);
                            break;
                        case "UNSTABLE":
                            run.setResult(Result.UNSTABLE);
                            break;
                        default : run.setResult(Result.FAILURE);
                            break;
                    }
                }
            }
        catch (MalformedURLException e) {
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
            }finally {
                //Creación de objeto de respuesta para pintar datos en pantalla
                resultado.setCodigo(respuesta);
                resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
                resultado.setApp(aplicacion);
                resultado.setVersion(version);

                try {
                    //Creacion de fichero JSON
                    JSONObject objPlugin = new JSONObject();
                    JSONObject objServicio = new JSONObject();

                    objServicio.put("ticketPrueba", resultado.getTicketPrueba());
                    objServicio.put("codRetorno", resultado.getIP_codIniciarPrueba());
                    objServicio.put("descRetorno",resultado.getIP_descIniciarPrueba());
                    objServicio.put("descRetornoLargo", resultado.getIP_descLargaIniciarPrueba());

                    objPlugin.put("respuesta", respuesta);
                    objPlugin.put("respuestaServicio",objServicio);

                    EnvVars envVars=run.getEnvironment(listener);
                    String rutaBuildLocal = Paths.get(String.valueOf(workspace), OPERACION, envVars.get("BUILD_ID")).toString();

                    UtilJSON utilJSON= new UtilJSON();
                    utilJSON.guardarJSON(objPlugin, rutaBuildLocal, PREFIJO_JSON);

                    listener.getLogger().println("Creación de fichero JSON de respuesta en : " + Paths.get(rutaBuildLocal).toString());
                    listener.getLogger().println("El plugin se ha ejecutado con código: " + respuesta);
                    listener.getLogger().println("Ticket de prueba: " + resultado.getTicketPrueba());
                    listener.getLogger().println("Código de retorno: " + resultado.getIP_codIniciarPrueba());
                    listener.getLogger().println("Descripción: " + resultado.getIP_descIniciarPrueba());
                    listener.getLogger().println("Descripcion larga: " + resultado.getIP_descLargaIniciarPrueba());

                    run.addAction(new IniciarPruebaAction(resultado));

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

    @Symbol("monAdabasIniciaPrueba")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl(){
            load();
        }
        //VALIDACIONES
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

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.IniciarPrueba_DescriptorImpl_DisplayName();
        }
    }


    public String getAplicacion() {return aplicacion;}
    public String getVersion() {return version;}
    public List<PruebaForm> getListaPruebas() {return listaPruebas;}
    public String getEstadoPruebas() {return estadoPruebas;}

}
