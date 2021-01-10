package es.jbp.comun.configurador.estructura;

import es.jbp.comun.configurador.Serializacion;
import flexjson.JSONDeserializer;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * El memorando se encarga de recordarValor los valores editados
 *
 * @author jberjano
 */
public class Memorando {

    private Map<String, Set<String>> memoria = new HashMap();

    public Map<String, Set<String>> getMemoria() {
        return memoria;
    }

    public void setMemoria(Map<String, Set<String>> memoria) {
        this.memoria = memoria;
    }   

    public void serializar(String nombreArchivo) {
        Serializacion.serializar(this, nombreArchivo);
    }

    public static Memorando deserializar(String nombreArchivo) {
        
        InputStreamReader reader = Serializacion.obtenerReader(nombreArchivo, false);
        if (reader == null) {
            return new Memorando();              
        }
        JSONDeserializer<Memorando> deserializador = new JSONDeserializer()
                .use(null, Memorando.class);
  
        Memorando instancia = deserializador.deserialize(reader);

        return instancia;
    }
    
    public void recordarValor(String nombre, String valor) {
        if (valor == null || valor.isEmpty()) {
            return;
        }
        Set<String> set;
        if (!memoria.containsKey(nombre)) {
            set = new HashSet();
            memoria.put(nombre, set);
        } else {
            set = memoria.get(nombre);
        }        
        set.add(valor);
    }
    
    public List<String> getValoresRecordados(String nombre) {
        ArrayList<String> lista = new ArrayList();
        
        Set<String> valores = memoria.get(nombre);
        
        if (valores != null) {
            lista.addAll(valores);
        }
        return lista;
    }
}
