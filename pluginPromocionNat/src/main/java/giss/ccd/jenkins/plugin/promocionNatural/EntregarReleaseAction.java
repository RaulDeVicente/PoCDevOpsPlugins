package giss.ccd.jenkins.plugin.promocionNatural;

import giss.ccd.jenkins.plugin.promocionNatural.model.Resultado;
import hudson.model.Action;

public class EntregarReleaseAction implements Action {

    private Resultado resultado;
    private String icon;

    public EntregarReleaseAction(Resultado resultado) {
        this.resultado = resultado;
        this.icon = "/plugin/promocionNatural/imagenes/summary_ico.png";
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
