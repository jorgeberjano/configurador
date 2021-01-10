package es.jbp.comun.configurador;

import es.jbp.comun.configurador.estructura.NodoElemento;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 * Representador del arbol de elementos
 * @author jberjano
 */
public class ArbolCellRenderer implements TreeCellRenderer {

    private JLabel label;
    private Font fuenteNormal;
    private Font fuenteBold;

    public ArbolCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fuenteNormal = label.getFont();
        fuenteBold = fuenteNormal.deriveFont(Font.BOLD);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        
        ImageIcon icono = null;
        String titulo = value.toString();
        boolean resaltar = false;
        if (value instanceof NodoElemento) {
            NodoElemento nodo = (NodoElemento) value;
            String nombreImagen = nodo.getImagen();
            if (nombreImagen == null || nombreImagen.isEmpty()) {
                nombreImagen = "punto";
            }
            try {
                URL url = getClass().getResource("/es/shs/comun/configurador/imagenes/" + nombreImagen + ".png");
                if (url != null) {
                    icono = new ImageIcon(url);
                }
            } catch (Throwable ex) {
                icono = null;
                ex.printStackTrace();
            }

            titulo = nodo.getTitulo();
            resaltar = !nodo.tieneValorPorDefecto();
            
        } 
        
        label.setIcon(icono);
        if (titulo == null) {
            titulo = "configuración";
        }
        label.setFont(resaltar ? fuenteBold : fuenteNormal);
        label.setText(titulo);
        if (selected) {            
            label.setBackground(SystemColor.activeCaption);
            label.setForeground(SystemColor.activeCaptionText);
        } else {
            label.setBackground(SystemColor.window);
            label.setForeground(SystemColor.windowText);
        }       

        return label;
    }

}
