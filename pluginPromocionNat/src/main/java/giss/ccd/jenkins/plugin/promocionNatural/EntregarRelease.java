package giss.ccd.jenkins.plugin.promocionNatural;


import com.sun.xml.ws.fault.ServerSOAPFaultException;
import com.sun.xml.ws.wsdl.parser.InaccessibleWSDLException;
import es.seg_social.ccd.promocionnatservice.Elementos;
import es.seg_social.ccd.promocionnatservice.Libreria;
import es.seg_social.ccd.promocionnatservice.Modulo;
import es.seg_social.ccd.promocionnatservice.Modulos;
import giss.ccd.jenkins.plugin.model.ModuloOrigen;
import giss.ccd.jenkins.plugin.promocionNatural.model.Resultado;
import giss.ccd.jenkins.plugin.promocionNatural.util.ConversionString;
import giss.ccd.jenkins.plugin.promocionNatural.ws.ServicioPromocionNat;
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
import jakarta.xml.ws.Holder;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;


import javax.servlet.ServletException;

import java.io.File;
import java.io.IOException;


import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Builder del Step "Promocion Natural - Entregar release"
 */
public class EntregarRelease extends Builder implements SimpleBuildStep {

    private final String aplicacion;
    private final String version;
    private final String proceso;
    private final String rutaFichero;
    private final String estadoRetorno;

    private final static String PREFIJO_JSON= "entregarReleaseOutput";
    private final static String OPERACION= "promocionNatural";
    private final static String PROCESADOS= "procesados";


    @DataBoundConstructor
    public EntregarRelease(String aplicacion,
                           String version,
                           String proceso,
                           String rutaFichero,
                           String estadoRetorno) {
        this.aplicacion = aplicacion.toUpperCase();
        this.version = version.toUpperCase();
        this.proceso = proceso.toUpperCase();
        this.rutaFichero = rutaFichero;
        this.estadoRetorno = estadoRetorno;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        String endpoint = ConfiguracionGlobal.get().getEndpoint();

        listener.getLogger().println(" - Endpoint: " + endpoint);
        listener.getLogger().println(" - Aplicación: " + aplicacion);
        listener.getLogger().println(" - Versión: " + version);
        listener.getLogger().println(" - Proceso: " + proceso);
        listener.getLogger().println(" - Fichero: " + rutaFichero);

        Resultado resultado = new Resultado();

        RecursosFicheros utilFicheros = new RecursosFicheros();
        File ultimoFicheroModificado = utilFicheros.ultimoFicheroModificado(rutaFichero, ConfiguracionGlobal.get().getPatronFichero());

        String respuesta = "-1";
        run.setResult(Result.SUCCESS);
        int contadorModulos = 0;

        try {
            //Parseo del fichero de modulos
            if(ultimoFicheroModificado==null){
                listener.getLogger().println(Messages.DescriptorImpl_excepciones_noExisteFichero());
                respuesta="0";
            }else {
                Map<String, List<ModuloOrigen>> mapLibreriaModulos = new HashMap<>();
                mapLibreriaModulos = utilFicheros.recuperarModulos(ultimoFicheroModificado);

                //Creacion de objeto Elementos->Librerias->Modulos
                if (mapLibreriaModulos.isEmpty()) {
                    listener.getLogger().println(Messages.DescriptorImpl_excepciones_noExisteLibrerias());
                    respuesta="0";
                } else {
                    listener.getLogger().println(Messages.DescriptorImpl_mensaje_inicioProcesoFichero());

                    Elementos elementos = new Elementos();
                    List<Libreria> listLibrerias = elementos.getLibreria();
                    for (Map.Entry<String, List<ModuloOrigen>> libreriaModulos : mapLibreriaModulos.entrySet()) {
                        Libreria libreria = new Libreria();
                        Modulos modulo = new Modulos();
                        for (ModuloOrigen mod : libreriaModulos.getValue()) {
                            contadorModulos = contadorModulos +1;
                            Modulo moduloServicio = new Modulo();
                            moduloServicio.setNombre(mod.getNombre());
                            moduloServicio.setModo(mod.getModo());
                            modulo.getModulo().add(moduloServicio);
                        }
                        libreria.setNombre(libreriaModulos.getKey().toUpperCase());
                        libreria.setModulos(modulo);
                        listLibrerias.add(libreria);
                    }
                    listener.getLogger().println(Messages.DescriptorImpl_mensaje_finProcesoFichero() + " " + contadorModulos + " módulos");


                    //Parametros de retorno
                    Holder<String> codRetorno = new Holder<>();
                    Holder<String> descRetorno = new Holder<>();

                    //Llamada al servicio
                    listener.getLogger().println(Messages.DescriptorImpl_mensaje_inicioEntregarRelease());
                    ServicioPromocionNat servicio = new ServicioPromocionNat();
                    Thread t = Thread.currentThread();
                    ClassLoader orig = t.getContextClassLoader();
                    t.setContextClassLoader(ServicioPromocionNat.class.getClassLoader());
                    try {
                        servicio.inicializarServicioPromocionNat(endpoint).entregarRelease(
                                aplicacion,
                                version,
                                proceso,
                                elementos,
                                codRetorno,
                                descRetorno);
                    } finally {
                        t.setContextClassLoader(orig);
                    }

                    if(codRetorno.value!=null){
                        respuesta = codRetorno.value;
                        resultado.setER_codEntregarRelease(codRetorno.value);
                    }

                    if(descRetorno.value!=null){
                        resultado.setER_descEntregarRelease(descRetorno.value);
                    }

                    listener.getLogger().println(" - Código de retorno: " + resultado.getER_codEntregarRelease());
                    listener.getLogger().println(" - Descripción: " + resultado.getER_descEntregarRelease());
                    listener.getLogger().println(Messages.DescriptorImpl_mensaje_finEntregarRelease());

                    //En el caso de que se reciba un codigo distinto de cero, se marca la ejecucion como se ha indicado en la ejecucion
                    if(codRetorno.value==null || !codRetorno.value.equals("0")) {
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

                }

                listener.getLogger().println(Messages.DescriptorImpl_mensaje_moverFicheroProcesado());

                String carpetaProcesado = ConfiguracionGlobal.get().getCarpetaProcesado();
                if(carpetaProcesado==null || carpetaProcesado.trim().isEmpty()){
                    carpetaProcesado = PROCESADOS;
                }

                String nombreFichProcesado = ultimoFicheroModificado.getName().split("\\.")[0] + "_job" + String.valueOf(run.getNumber() + "." + RecursosFicheros.TXT);
                utilFicheros.moverDirectorio(ultimoFicheroModificado,Paths.get(rutaFichero, carpetaProcesado).toString(),nombreFichProcesado);

            }
        }catch (IOException e) {
            listener.error(Messages.DescriptorImpl_excepciones_errorGenerarProcesado());
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            resultado.setHayException(true);
        }catch (InaccessibleWSDLException e) {
            listener.error(Messages.DescriptorImpl_excepciones_inaccesibleWSDL());
            listener.error(e.getMessage());
            resultado.setHayException(true);
        }catch (ServerSOAPFaultException e) {
            listener.error(e.getMessage());
            resultado.setHayException(true);
        }catch (Exception e) {
            listener.error(e.getMessage());
            listener.getLogger().println(e.getCause());
            resultado.setHayException(true);
        }finally {
            //Creación de objeto de respuesta para pintar datos en pantalla

            if(resultado.isHayException()) {
                run.setResult(Result.FAILURE);
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                resultado.setCodigo("-1");
            }

            resultado.setCodigo(respuesta);
            resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
            resultado.setApp(aplicacion);
            resultado.setVersion(version);
            if(ultimoFicheroModificado == null){
                resultado.setER_fichero(Messages.DescriptorImpl_excepciones_noExisteFichero());
            }else{
                resultado.setER_fichero(ultimoFicheroModificado.getName());
            }
            resultado.setER_proceso(ConversionString.recuperarEntorno(proceso));
            resultado.setER_modulosProcesados(contadorModulos);

            try {

                //Creacion de fichero JSON
                listener.getLogger().println(Messages.DescriptorImpl_mensaje_inicioCreacionJSON());

                JSONObject objPlugin = new JSONObject();
                JSONObject objServicio = new JSONObject();

                objServicio.put("codRetorno", resultado.getER_codEntregarRelease() );
                objServicio.put("descRetorno",  resultado.getER_descEntregarRelease());

                objPlugin.put("respuesta", respuesta);
                objPlugin.put("modulosProcesados", contadorModulos);
                objPlugin.put("respuestaServicio", objServicio);

                EnvVars envVars=run.getEnvironment(listener);
                String rutaBuildLocal = Paths.get(String.valueOf(workspace), OPERACION, envVars.get("BUILD_ID")).toString();

                UtilJSON utilJSON= new UtilJSON();
                utilJSON.guardarJSON(objPlugin, rutaBuildLocal, PREFIJO_JSON);

                listener.getLogger().println("Creación de fichero JSON de respuesta en : " + Paths.get(rutaBuildLocal).toString());
                listener.getLogger().println(Messages.DescriptorImpl_mensaje_finCreacionJSON());

                listener.getLogger().println("El plugin se ha ejecutado con código: " + respuesta);

                run.addAction(new EntregarReleaseAction(resultado));

            }catch (Exception e) {
                listener.error(Messages.DescriptorImpl_excepciones_errorGenerarJSON());
                listener.error(e.getMessage());
                listener.getLogger().println(e.getCause());
                run.setResult(Result.FAILURE);
                resultado.setEstadoFinal(Objects.toString(run.getResult(),""));
                resultado.setHayException(true);
                resultado.setMensajeException(Messages.Excepcion_mensaje());
                resultado.setCodigo("-1");
                run.addAction(new EntregarReleaseAction(resultado));
            }
        }
    }

    @Override
    public DescriptorImpl getDescriptor(){
        return(DescriptorImpl) super.getDescriptor();
    }

    @Symbol("entregarRelease")
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
        public FormValidation doCheckRutaFichero(@QueryParameter String value)
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
            return Messages.EntregarRelease_DescriptorImpl_DisplayName();
        }
    }

    public String getAplicacion() {return aplicacion;}
    public String getVersion() {return version;}
    public String getProceso() {return proceso;}
    public String getRutaFichero() {return rutaFichero;}
    public String getEstadoRetorno() {return estadoRetorno;}

}
