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
    private final static String PREFIJO_JSON= "iniciarPruebaOutput_";
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

        //Parametros de retorno
        final Holder<String> ticketPrueba = new Holder<>();
        final Holder<String> codRetorno = new Holder<>();
        final Holder<String> descRetorno = new Holder<>();
        final Holder<String> descRetornoLargo = new Holder<>();

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

        }catch (MalformedURLException e) {
            listener.error(Messages.DescriptorImpl_excepciones_errorURLEndpoint());
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            run.setResult(Result.FAILURE);
        }catch (ServerSOAPFaultException e) {
            listener.error(e.getMessage());
            run.setResult(Result.FAILURE);
        }catch (Exception e) {
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            run.setResult(Result.FAILURE);
        }finally {
            Resultado resultado = new Resultado();
            resultado.setCodigo(respuesta);
            resultado.setEstadoFinal(run.getResult().toString());
            resultado.setApp(aplicacion);
            resultado.setVersion(version);
            resultado.setIP_codIniciarPrueba(codRetorno.value);
            resultado.setIP_descIniciarPrueba(descRetorno.value);
            resultado.setIP_descLargaIniciarPrueba(descRetornoLargo.value);

            try {
                //Creacion de fichero JSON
                JSONObject objPlugin = new JSONObject();
                JSONObject objServicio = new JSONObject();

                objServicio.put("ticketPrueba", ticketPrueba.value);
                objServicio.put("codRetorno", codRetorno.value);
                objServicio.put("descRetorno",descRetorno.value);
                objServicio.put("descRetornoLargo", descRetornoLargo.value);

                objPlugin.put("respuesta", respuesta);
                objPlugin.put("respuestaServicio",objServicio);

                EnvVars envVars=run.getEnvironment(listener);
                String urlFichero = Paths.get(String.valueOf(workspace), OPERACION).toString();
                String nombreFichero = PREFIJO_JSON + envVars.get("BUILD_ID");

                UtilJSON utilJSON= new UtilJSON();

                utilJSON.guardarJSON(objPlugin, urlFichero,nombreFichero);

                listener.getLogger().println("Creación de fichero JSON de respuesta en : " + Paths.get(urlFichero).toString());
                listener.getLogger().println("El plugin se ha ejecutado con código: " + respuesta);
                listener.getLogger().println("Ticket de prueba: " + Objects.toString(ticketPrueba.value,""));
                listener.getLogger().println("Código de retorno: " + Objects.toString(codRetorno.value,""));
                listener.getLogger().println("Descripción: " + Objects.toString(descRetorno.value,""));
                listener.getLogger().println("Descripcion larga: " + Objects.toString(descRetornoLargo.value,""));

                run.addAction(new IniciarPruebaAction(resultado));

                }catch (RuntimeException e) {
                    listener.error(Messages.DescriptorImpl_excepciones_errorGenerarJSON());
                    listener.error(e.getMessage());
                    listener.getLogger().println(e.getCause());
                    run.setResult(Result.FAILURE);
                    resultado.setEstadoFinal(run.getResult().toString());
                    run.addAction(new IniciarPruebaAction(resultado));
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
