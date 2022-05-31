package giss.ccd.jenkins.plugin.monAdabas;

import giss.ccd.jenkins.plugin.monAdabas.model.Resultado;
import hudson.model.Action;

public class FinalizarPruebaAction implements Action {

    private Resultado resultado;
    private String icon;

    public FinalizarPruebaAction(Resultado resultado) {
        this.resultado = resultado;
        this.icon = "/plugin/monitorizacionAdabas/imagenes/summary_ico.png";
    }

    public Resultado getResultado() {
        return resultado;
    }
    
    public String getIcon()
    {
        return this.icon;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

}
