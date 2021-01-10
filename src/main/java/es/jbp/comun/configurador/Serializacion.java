package es.jbp.comun.configurador;

import flexjson.JSONSerializer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author jberjano
 */
public class Serializacion {

    public static boolean serializar(Object objeto, String nombreArchivo) {
        FileWriter writer;
        String pathConfig = System.getProperty("user.dir");
        try {
            writer = new FileWriter(pathConfig + "/" + nombreArchivo);
        } catch (FileNotFoundException ex) {
            error("No se ha podido crear el archivo " + nombreArchivo, ex);
            return false;
        } catch (IOException ex) {
            error("No se ha podido guardar el archivo " + nombreArchivo, ex);
            return false;
        }

        JSONSerializer serializador = new JSONSerializer().exclude("*.class");
        serializador.prettyPrint(true);
        serializador.deepSerialize(objeto, writer);

        try {
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            error("No se ha podido cerrar el archivo " + nombreArchivo, ex);
            return false;
        }
        return true;
    }

    private static void error(String mensaje, Exception ex) {
        Ventana.getInstancia().error(mensaje, ex);
    }

    public static InputStreamReader obtenerReader(String nombreArchivo, boolean mostrarErrores) {
        String pathConfig = System.getProperty("user.dir");
        try {
            return new InputStreamReader(new FileInputStream(pathConfig + "/" + nombreArchivo), "UTF-8");
        } catch (FileNotFoundException ex) {
            if (mostrarErrores) {
                error("No se ha encontrado el archivo " + nombreArchivo, ex);   
            }
            return null;
        } catch (IOException ex) {
            if (mostrarErrores) {
                error("No se ha podido cargar el archivo " + nombreArchivo, ex);
            }
            return null;
        }
    }
}
