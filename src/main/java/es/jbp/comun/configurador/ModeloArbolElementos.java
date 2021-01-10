package es.jbp.comun.configurador;

import es.jbp.comun.configurador.estructura.Estructura;
import es.jbp.comun.configurador.estructura.NodoElemento;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Modelo del arbol de elementos 
 * @author jberjano
 */
public class ModeloArbolElementos implements TreeModel {

    private Estructura estructura;
    private final List<TreeModelListener> listeners = new ArrayList();

    public ModeloArbolElementos(Estructura estructura) {
        this.estructura = estructura;
    }

    @Override
    public Object getRoot() {
        return estructura.getRaiz();
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof NodoElemento) {
            return ((NodoElemento) parent).getNodosHijo().get(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof NodoElemento) {
            return ((NodoElemento) parent).getNodosHijo() == null ? 0
                    : ((NodoElemento) parent).getNodosHijo().size();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
    
    public void actualizar(TreePath path) {
        for (TreeModelListener l : listeners) {
            TreeModelEvent e = new TreeModelEvent(this, path);
            l.treeStructureChanged(e);
        }
    }

}
