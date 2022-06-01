package giss.ccd.jenkins.plugin.promocionNatural.util;

import giss.ccd.jenkins.plugin.promocionNatural.Messages;

public class ConversionString {

    /**
     * Devuelve el literal asociado al código de entorno recibido.
     * Valores posibles: IC, CE, PE, FO, PR
     * @param entorno Código del entorno
     * @return valorEntorno Valor asociado al entorno recibido
     */
    public static String recuperarEntorno(String entorno) {

        String valorEntorno = null;

        if (entorno != null) {
            switch (entorno) {
                case "IC":
                    valorEntorno = Messages.IC_valor();
                    break;
                case "CE":
                    valorEntorno = Messages.CE_valor();
                    break;
                case "PE":
                    valorEntorno = Messages.PE_valor();
                    break;
                case "FO":
                    valorEntorno = Messages.FO_valor();
                    break;
                case "PR":
                    valorEntorno = Messages.PR_valor();
                    break;
                default:
                    valorEntorno = entorno;
            }
        }
        return valorEntorno;
    }
}