package es.jbp.comun.configurador.estructura;

import es.jbp.comun.configurador.PanelEditor;
import es.jbp.comun.configurador.Ventana;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Representa un nodo del arbol de elementos
 *
 * @author jberjano
 */
public class NodoElemento {

    protected String titulo;
    protected String camino;
    protected Elemento elemento;
    protected List<NodoElemento> nodosHijo = new ArrayList();
    protected PanelEditor panelEditor;
    protected String valor;

    public NodoElemento() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCamino() {
        return camino;
    }

    public void setCamino(String camino) {
        this.camino = camino;
        for (NodoElemento hijo : nodosHijo) {
            hijo.setCamino(camino + "." + hijo.getTitulo());
        }
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public int getNumeroHijos() {
        if (esArray()) {
            return getValorEntero();
        } else {
            return nodosHijo == null ? 0 : nodosHijo.size();
        }
    }

    public void setNodosHijo(List<NodoElemento> nodosHijo) {
        this.nodosHijo = nodosHijo;
    }

    public PanelEditor getPanelEditor() {
        return panelEditor;
    }

    public void setPanelEditor(PanelEditor panelEditor) {
        this.panelEditor = panelEditor;
    }

    @Override
    public String toString() {
        if (elemento == null) {
            return titulo;
        }
        return elemento.getNombre();
    }

    public List<NodoElemento> getNodosHijo() {
        if (esArray()) {
            return getNodosHijoArray();
        } else {
            return nodosHijo;
        }
    }

    public NodoElemento getNodoHijo(String tituloHijo) {

        for (NodoElemento hijo : nodosHijo) {
            if (hijo.getTitulo().equals(tituloHijo)) {
                return hijo;
            }
        }
        return null;
    }

    public String getImagen() {
        if (elemento != null) {
            return elemento.getImagen();
        }
        return null;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
        if (esArray()) {
            getNodoElementoArray().dimensionar(getValorEntero());
        }
        if (elemento != null) {
            Ventana.getInstancia().getMemorando().recordarValor(elemento.getNombre(), valor);
        }
    }

    public int getValorEntero() {
        try {
            if ((valor == null || valor.isEmpty()) && elemento != null) {
                return Integer.parseInt(elemento.getValorPorDefecto());
            } else {
                return Integer.parseInt(valor);
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public boolean esArray() {
        return getNodoElementoArray() != null;
    }

    public NodoElementoArray getNodoElementoArray() {
        NodoElemento nodoElementoHijo = getNodoHijo("#");
        if (nodoElementoHijo instanceof NodoElementoArray) {
            return (NodoElementoArray) nodoElementoHijo;
        } else {
            return null;
        }
    }

    private List<NodoElemento> getNodosHijoArray() {

        NodoElementoArray nodoElementoArray = getNodoElementoArray();
        return nodoElementoArray.getNodosElemento();
    }

    public void agregarNodoHijo(NodoElemento nuevoHijo) {
        nodosHijo.add(nuevoHijo);
    }

    protected NodoElemento clonar() {
        NodoElemento clon = new NodoElemento();
        clon.copiar(this);
        return clon;
    }

    protected void copiar(NodoElemento nodo) {
        this.titulo = nodo.titulo;
        this.camino = nodo.camino;
        this.elemento = nodo.elemento;
        this.nodosHijo = new ArrayList();
        for (NodoElemento hijo : nodo.nodosHijo) {
            this.nodosHijo.add(hijo.clonar());
        }
    }

    public ImageIcon getIcono() {

        String nombreImagen = elemento != null ? elemento.getImagen() : null;        
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            nombreImagen = "punto";
        }
        ImageIcon icono = null;
        try {
            URL url = getClass().getResource("/es/shs/comun/configurador/imagenes/" + nombreImagen + ".png");
            if (url != null) {
                icono = new ImageIcon(url);
            }
        } catch (Throwable ex) {
            icono = null;
            //ex.printStackTrace();
        }
        return icono;
    }

    public boolean tieneValorPorDefecto() {
        
        for (NodoElemento nodo : nodosHijo) {
            if (!nodo.tieneValorPorDefecto()) {
                return false;
            }
        }
        
        if (valor == null || valor.isEmpty()) {
            return true;
        }
        return valor.equals(elemento.getValorPorDefecto());
    }
}
