package giss.ccd.jenkins.plugin.util;


import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

/**
 * Clase de utilidad que realiza operaciones sobre ficheros JSON
 */
public class UtilJSON {

    private static final String EXT_JSON = ".json";

    public UtilJSON() {
    }

   /**
     * Genera un fichero JSON con el nombre y ruta indicadas
     *
     * @param json          Fichero JSON
     * @param ruta          Ruta en la que se generara el fichero JSON
     * @param nombreFichero Nombre del fichero JSON
     * @throws IOException io exception
    */
     public void guardarJSON(JSONObject json, String ruta, String nombreFichero) throws IOException {

       String jsonString = json.toString();
        File directorio = new File(ruta);
        PrintWriter writer;
        if (!directorio.exists()){
            directorio.mkdir();
        }
        String urlFichero = Paths.get(ruta, nombreFichero + EXT_JSON).toString();
        writer = new PrintWriter(new FileWriter(urlFichero));
        writer.write(jsonString);

        if(writer != null) {
            writer.flush();
            writer.close();
        }
    }

}
