package giss.ccd.jenkins.plugin.util;

import giss.ccd.jenkins.plugin.model.ModuloOrigen;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Clase de utilidad que realiza operaciones sobre ficheros de modulos
 */
public class RecursosFicheros {

    private static final String ACCION_SAVE = "SAVE";
    private static final String ACCION_SCRATCH = "SCRATCH";
    private static final String VALOR_SAVE = "E";
    private static final String VALOR_SCRATCH = "B";
    public static final String TXT = "txt";

    public RecursosFicheros() {
    }

    /**
     * Recupera el ultimo fichero modificado en base a un directorio y un patron en el nombre.
     *
     * @param directorio Directorio en el que se encuentran los ficheros.
     * @param patron     Patron usado para filtrar los nombres de los ficheros.
     * @return Devuelve un objeto de tipo File con el ultimo fichero modificado.
     */
    public File ultimoFicheroModificado(String directorio, String patron) {
        // Recuperamos los ficheros desde el directorio y patron indicados
        File f = new File(directorio);
        File[] ficheros = f.listFiles((dir, name) -> name.toUpperCase().contains(patron.toUpperCase()));
        //Comparamos las fechas de modificacion para quedarnos con el ultimo modificado
        if(ficheros!=null && ficheros.length>0){
            File ultimoFicheroModificado = ficheros[0];
            for(int i=1; i<ficheros.length; i++){
                if(ultimoFicheroModificado.lastModified() < ficheros[i].lastModified()) {
                    ultimoFicheroModificado = ficheros[i];
                }
            }
            return ultimoFicheroModificado;
        }
        return null;
    }

    /**
     * Devuelve los modulos de un fichero
     *
     * @param fichero       Fichero de modulos.
    * @return Devuelve un objeto de tipo Map con la relacion Libreria/modulo.
     * @throws FileNotFoundException the file not found exception
     */
    public Map<String, List<ModuloOrigen>> recuperarModulos (File fichero) throws FileNotFoundException {

        Scanner myReader = new Scanner(fichero);
        Map<String, List<ModuloOrigen>> mapLibreriaModulos = new HashMap<>();

        while (myReader.hasNextLine()) {
            String libreria;
            String accionModulo;
            String linea = myReader.nextLine();
            String[] partesLinea = linea.split(",");

            if (partesLinea.length == 5) {
                libreria = partesLinea[1];
                accionModulo = partesLinea[4];
                ModuloOrigen moduloOrigen = new ModuloOrigen();
                moduloOrigen.setNombre(partesLinea[2]);

                switch (accionModulo.toUpperCase()){
                    case ACCION_SAVE:
                        moduloOrigen.setModo(VALOR_SAVE);
                        break;
                    case ACCION_SCRATCH:
                        moduloOrigen.setModo(VALOR_SCRATCH);
                        break;
                    default:
                        moduloOrigen.setModo("");
                        break;
                }

                mapLibreriaModulos.computeIfAbsent(libreria, k -> new ArrayList<>()).add(moduloOrigen);

            }
        }
        myReader.close();
        return mapLibreriaModulos;
    }

    /**
     * Mueve un fichero al directorio indicado.
     *
     * @param origen File origen.
     * @param rutaDestino String ruta destino.
     * @param nombreDestino String nombre destino.
     */
    public void moverDirectorio(File origen, String rutaDestino, String nombreDestino) throws IOException {

        File directorio = new File(rutaDestino);

        if (!directorio.exists()){
            directorio.mkdirs();
        }
        Files.copy(origen.toPath(),  Paths.get(rutaDestino, nombreDestino), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(origen.toPath());

    }

    /**
     * Descargar un fichero desde una url.
     *
     * @param urlOrigen URL origen.
     * @param rutaDestino String ruta destino.
     * @param nombreDestino String nombre destino.
     */
    public void descargarFicheroURL(URL urlOrigen, String rutaDestino, String nombreDestino) throws IOException {
        BufferedInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File directorio = new File(rutaDestino);
            if (!directorio.exists()){
                directorio.mkdirs();
            }

            inputStream = new BufferedInputStream(urlOrigen.openStream());
            outputStream = new FileOutputStream(Paths.get(rutaDestino,nombreDestino).toString());

            byte[] data =new byte[1024];
            int count;
            while((count=inputStream.read(data,0,1024))!=-1){
                outputStream.write(data,0,count);
            }
        } catch (IOException e) {
            throw e;
        }finally {
            try{
                if (inputStream!=null){
                    inputStream.close();
                }
                if (outputStream!=null){
                    outputStream.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }





    }


}

