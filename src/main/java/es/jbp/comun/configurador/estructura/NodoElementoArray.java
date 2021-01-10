package es.jbp.comun.configurador.estructura;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nodo de tipo array del arbol de elementos
 * @author jberjano
 */
public class NodoElementoArray extends NodoElemento {
    
    private static final int MAXIMO_ELEMENTOS = 100;
    private final ArrayList<NodoElemento> nodosElemento = new ArrayList();
           
    public void dimensionar(int numeroElementos) {
        if (numeroElementos < 0) {
            numeroElementos = 0;
        }
        if (numeroElementos > MAXIMO_ELEMENTOS) {
            numeroElementos = MAXIMO_ELEMENTOS;
        }
        
        int numeroElementosActuales = nodosElemento.size();
        if (numeroElementos > numeroElementosActuales) {
            for (int i = numeroElementosActuales; i < numeroElementos; i++) {
                int n = i + 1;
                NodoElemento nuevoNodo = clonar();                  
                nuevoNodo.setTitulo(Integer.toString(n));
                String nuevoCamino = camino.replaceFirst("#", nuevoNodo.getTitulo());
                nuevoNodo.setCamino(nuevoCamino);                
                nodosElemento.add(nuevoNodo);
            }
        } else if (numeroElementos < numeroElementosActuales) {
            for (int i = numeroElementosActuales; i > numeroElementos; i--) {
                nodosElemento.remove(i - 1);
            }
        }
    }
    
    public List<NodoElemento> getNodosElemento() {
        return nodosElemento;
    }

    @Override
    protected NodoElemento clonar() {
         NodoElementoArray clon = new NodoElementoArray();
         clon.copiar(this);
         return clon;
    }
    
    public boolean tieneValorPorDefecto() {
        boolean b = super.tieneValorPorDefecto();
        
        for (NodoElemento nodo : nodosElemento) {
            b = b && nodo.tieneValorPorDefecto();
        }
        return b;
    }
   
}
