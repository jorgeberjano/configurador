package es.jbp.comun.configurador.estructura;

import es.jbp.comun.configurador.Serializacion;
import es.jbp.comun.configurador.Ventana;
import flexjson.JSON;
import flexjson.JSONDeserializer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Estructura de la configuración.
 * Contiene tanto la lista de elementos como la estructura de arbol.
 * @author jberjano
 */
public class Estructura {

    private String archivoConfiguracion = "configuracion.properties";
    private String aplicacion = "Nombre aplicacion";
    private String version = "1.0";
    private List<Elemento> elementos = new ArrayList();
    private NodoElemento raiz;

    public static Estructura importarEstructura(String nombreArchivoPropiedades) throws Exception {
        String userDir = System.getProperty("user.dir");

        BufferedReader reader;
        reader = new BufferedReader(new FileReader(userDir + "/" + nombreArchivoPropiedades));

        Estructura estructura = new Estructura();

        List<String> listaSiNo = new ArrayList(2);
        listaSiNo.add("SI");
        listaSiNo.add("NO");

        StringBuilder descripcion = new StringBuilder();
        while (reader.ready()) {
            String str = reader.readLine();
            if (str.startsWith("#")) {
                str = str.replace('#', ' ').trim();
                if (!str.isEmpty()) {
                    descripcion.append(str);
                    descripcion.append("\n");
                }
            } else {
                int indice = str.indexOf('=');
                if (indice > 0) {
                    String nombre = str.substring(0, indice).trim();
                    String valor = str.substring(indice + 1).trim();

                    Elemento elemento = new Elemento();
                    elemento.setNombre(nombre);
                    if (valor.toUpperCase().equals("SI") || valor.toUpperCase().equals("NO")) {
                        elemento.setTipo("booleano");
                        elemento.setOpciones(listaSiNo);
                    } else {
                        elemento.setTipo("cadena");
                    }
                    elemento.setDescripcion(descripcion.toString());
                    elemento.setValorPorDefecto(valor);
                    estructura.agregarElemento(elemento);
                    descripcion = new StringBuilder();
                }
            }
        }
        return estructura;
    }

    public void serializar(String nombreArchivo) {
        Serializacion.serializar(this, nombreArchivo);
    }

    public static Estructura deserializar(String nombreArchivo) {
        
        InputStreamReader reader = Serializacion.obtenerReader(nombreArchivo, true);
        if (reader == null) {
            return null;
        }
        JSONDeserializer<Estructura> deserializador = new JSONDeserializer()
                .use(null, Estructura.class)
                .use("elementos", ArrayList.class)
                .use("elementos.values", Elemento.class);
        Estructura estructura = deserializador.deserialize(reader);

        return estructura;
    }

    public String getArchivoConfiguracion() {
        return archivoConfiguracion;
    }

    public void setArchivoConfiguracion(String archivoConfiguracion) {
        this.archivoConfiguracion = archivoConfiguracion;
    }

    public String getAplicacion() {
        return aplicacion;
    }

    public void setAplicacion(String aplicacion) {
        this.aplicacion = aplicacion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(List<Elemento> elementos) {
        this.elementos = elementos;
    }

    @Override
    public String toString() {
        return "Configuración";
    }

    @JSON(include = false)
    public NodoElemento getRaiz() {
        if (raiz == null) {
            crearArbol();
        }
        return raiz;
    }

    public void crearArbol() {

        raiz = new NodoElemento();

        for (Elemento elemento : elementos) {
            agregarElementoAlArbol(elemento);
        }
    }

    private void agregarElementoAlArbol(Elemento elemento) {
       
        String[] camino = elemento.getNombre().split("\\.");

        NodoElemento nodo = raiz;
        for (String parte : camino) {
            nodo = obtenerHijo(nodo, parte.trim());
        }
        nodo.setElemento(elemento);
    }
    
    public NodoElemento obtenerHijo(NodoElemento nodo, String tituloHijo) {
        NodoElemento hijo = nodo.getNodoHijo(tituloHijo);
        if (hijo != null) {
            return hijo;
        }
        boolean esArray = tituloHijo.equals("#");
        NodoElemento nuevoHijo = esArray ? new NodoElementoArray() : new NodoElemento();
        nuevoHijo.setTitulo(tituloHijo);
        String camino = nodo.getCamino();
        if (camino == null) {
            camino = tituloHijo;
        } else {
            camino = camino + "." + tituloHijo;
        }
        nuevoHijo.setCamino(camino);
        
        nodo.agregarNodoHijo(nuevoHijo);
        
        return nuevoHijo;
    }

    private void agregarElemento(Elemento elemento) {
        elementos.add(elemento);
    }

    public void cargarValores() {
        String userDir = System.getProperty("user.dir");
        Properties propiedades = new Properties();
        try {
            propiedades.load(new FileReader(userDir + File.separator + archivoConfiguracion));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        cargarValores(getRaiz(), propiedades);
    }

    private void cargarValores(NodoElemento nodo, Properties propiedades) {
        
        String nombre = nodo.getCamino();
        if (nombre != null) {
            String valor = propiedades.getProperty(nombre);
            if (valor == null) {
                valor = "";
            }
            nodo.setValor(valor);
        }

        for (NodoElemento hijo : nodo.getNodosHijo()) {
            cargarValores(hijo, propiedades);
        }

    }

    public boolean guardarValores() {
        String userDir = System.getProperty("user.dir");
        Path rutaCompleta = FileSystems.getDefault().getPath(userDir + File.separator + archivoConfiguracion);

        try {
            OutputStreamWriter writer;
            writer = new OutputStreamWriter(new FileOutputStream(rutaCompleta.toString()), "UTF-8");
            writer.write("# Configuración de " + getAplicacion() + " " + getVersion());
            writer.write("\n\n");

            guardarValores(raiz, writer);

            writer.flush();
            writer.close();
        } catch (IOException ex) {
           Ventana.getInstancia().error("No se ha podido guardar la configuración", ex);
           return false;
        }
        return true;
    }

    private void guardarValores(NodoElemento nodo, OutputStreamWriter writer) throws IOException {

        Elemento elemento = nodo.getElemento();
        if (elemento != null && elemento.tieneEdicion()) {
            String[] lineasDescripcion = elemento.getDescripcion().split("\n");
            for (String linea : lineasDescripcion) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                writer.write("# ");
                writer.write(linea);
                writer.write("\n");
            }
            writer.write(nodo.getCamino());
            writer.write(" = ");
            String valor = nodo.getValor();
            if (valor == null || valor.isEmpty()) {
                valor = elemento.getValorPorDefecto();
            }
            if (valor != null) {                
                writer.write(sustituirSecuenciasEscape(valor));
            }
            writer.write("\n");
        }
        
        for (NodoElemento hijo : nodo.getNodosHijo()) {
            guardarValores(hijo, writer);
        }
    }

    private String sustituirSecuenciasEscape(String texto) {
        return texto.replace("\\", "\\\\");
    }
}
