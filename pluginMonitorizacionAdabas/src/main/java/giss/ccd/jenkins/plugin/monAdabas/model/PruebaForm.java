package giss.ccd.jenkins.plugin.monAdabas.model;

import giss.ccd.jenkins.plugin.monAdabas.Messages;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

public class PruebaForm extends AbstractDescribableImpl<PruebaForm> {

    private String tipoPrueba;
    private String alcance;
    private String elemento;
    private String usuario;

    @DataBoundConstructor
    public PruebaForm(String tipoPrueba, String alcance, String elemento, String usuario) {
        this.tipoPrueba = tipoPrueba;
        this.alcance = alcance;
        this.elemento = elemento;
        this.usuario = usuario;
    }

    /**
     * The type Descriptor.
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<PruebaForm> {

        @Override
        public String getDisplayName(){
            return "";
        }

        public FormValidation doCheckTipoPrueba(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckAlcance(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckElemento(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUsuario(@QueryParameter String value)
                throws IOException, ServletException {
            if (Util.fixEmptyAndTrim(value)==null) {
                return FormValidation.error(Messages.DescriptorImpl_errors_obligatorio());
            }
            return FormValidation.ok();
        }
    }

    public String getTipoPrueba() {return tipoPrueba;}

    public void setTipoPrueba(String tipoPrueba) {this.tipoPrueba = tipoPrueba;}

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
