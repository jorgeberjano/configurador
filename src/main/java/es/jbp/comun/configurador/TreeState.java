package es.jbp.comun.configurador;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class TreeState {

    private final JTree tree;
    private final Set<TreePath> estado = new HashSet<>();
    private TreePath selectionPath;

    public TreeState(JTree tree) {
        this.tree = tree;
    }

    public void guardar() {
        selectionPath = tree.getSelectionPath();
        estado.clear();        
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath path = tree.getPathForRow(i);
            if (tree.isExpanded(i)) {
                estado.add(path);
            }
        }        
    }

    public void restaurar() {
        tree.setSelectionPath(selectionPath);
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath tp = tree.getPathForRow(i);
            if (estado.contains(tp)) {
                tree.expandRow(i);
            }
        }
    }
}
