package com.kreadi.imprentaGes.gui;

import com.kreadi.imprentaGes.DB;
import com.kreadi.swing.KTable;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class PreciosGui extends javax.swing.JDialog {

    public static boolean open1 = false;

    private final DefaultTableModel dtm;

    public PreciosGui(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        setIconImage(new ImageIcon("icono.png").getImage());
        setSize(480, 320);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new com.kreadi.swing.KTable(
            new String[]{"Producto","Precio"},
            new Class[]{String.class, Integer.class},
            new boolean[]{true, true}, 
            new int[]{128, 9},
            new String[]{"toUpper", "\\d*"}
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lista de Precios");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setFocusable(false);
        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/save.png"))); // NOI18N
        jButton4.setText("Guardar");
        jButton4.setToolTipText("Guardar Cambios");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusable(false);
        jButton4.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton4.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton4.setPreferredSize(new java.awt.Dimension(128, 36));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4);

        jLabel1.setText("  ");
        jLabel1.setMaximumSize(new java.awt.Dimension(32565, 8));
        jLabel1.setMinimumSize(new java.awt.Dimension(8, 8));
        jLabel1.setPreferredSize(new java.awt.Dimension(8, 8));
        jPanel1.add(jLabel1);

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/new.png"))); // NOI18N
        jButton1.setToolTipText("Nuevo Producto");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton1.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton1.setPreferredSize(new java.awt.Dimension(64, 36));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jLabel7.setText("  ");
        jPanel1.add(jLabel7);

        jButton6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/calc.png"))); // NOI18N
        jButton6.setToolTipText("Multiplicar Precios");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setFocusable(false);
        jButton6.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton6.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton6.setPreferredSize(new java.awt.Dimension(64, 36));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6);

        jLabel5.setText("  ");
        jPanel1.add(jLabel5);

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/clone.png"))); // NOI18N
        jButton3.setToolTipText("Clonar Selección");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusable(false);
        jButton3.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton3.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton3.setPreferredSize(new java.awt.Dimension(64, 36));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        jLabel6.setText("  ");
        jPanel1.add(jLabel6);

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/sort.png"))); // NOI18N
        jButton5.setToolTipText("Ordenar Alfabeticamente");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusable(false);
        jButton5.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton5.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton5.setPreferredSize(new java.awt.Dimension(64, 36));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5);

        jLabel4.setText("  ");
        jPanel1.add(jLabel4);

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/excel2.png"))); // NOI18N
        jButton7.setToolTipText("Importar/Exportar a Excel");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.setFocusable(false);
        jButton7.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton7.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton7.setPreferredSize(new java.awt.Dimension(64, 36));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton7);

        jLabel8.setText("  ");
        jPanel1.add(jLabel8);

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/remove.png"))); // NOI18N
        jButton2.setToolTipText("Eliminar Selección");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setMaximumSize(new java.awt.Dimension(64, 36));
        jButton2.setMinimumSize(new java.awt.Dimension(64, 36));
        jButton2.setPreferredSize(new java.awt.Dimension(64, 36));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        getContentPane().add(jPanel1);

        jTable1.setFont(Main.defaultFont14);
        jTable1.setRowHeight(24);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Clonar Registro
     */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        int[] rows = jTable1.getSelectedRows();
        int size = dtm.getRowCount();
        for (int i = 0; i < rows.length; i++) {
            dtm.insertRow(size, new Object[]{dtm.getValueAt(rows[i], 0), dtm.getValueAt(rows[i], 1)});
            size++;
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * Nuevo Registro
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        int size = dtm.getRowCount();
        dtm.insertRow(size, new Object[]{"", null});
        jTable1.editCellAt(size, 0);
        jTable1.changeSelection(size, 0, false, false);
        Component editor = jTable1.getEditorComponent();
        try {
            editor.requestFocusInWindow();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Eliminar registros
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        int[] rows = jTable1.getSelectedRows();
        if (rows.length > 0) {
            int row = rows[0];
            for (int i = rows.length - 1; i >= 0; i--) {
                dtm.removeRow(rows[i]);
            }
            try {
                jTable1.editCellAt(row, 0);
                jTable1.changeSelection(row, 0, false, false);
            } catch (Exception e) {

            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * Ordenar
     */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        LinkedList<Comparable[]> list = new LinkedList<>();
        for (int i = 0; i < dtm.getRowCount(); i++) {
            Comparable o[] = new Comparable[]{(Comparable) dtm.getValueAt(i, 0), (Comparable) dtm.getValueAt(i, 1)};
            list.add(o);
        }
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i)[0].compareTo(list.get(j)[0]) > 0) {
                    Comparable o[] = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, o);
                    i = -1;
                    break;
                }
            }
        }
        int i = 0;
        for (Comparable[] o : list) {
            dtm.setValueAt(o[0], i, 0);
            dtm.setValueAt(o[1], i, 1);
            i++;
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    public void updateData() throws SQLException {
        List<Object[]> result = DB.instance.getPrecios.executeQuery();
        while (dtm.getRowCount() > 0) {
            dtm.removeRow(0);
        }
        for (Object[] o : result) {
            dtm.addRow(o);
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        LinkedList<Comparable[]> list = new LinkedList<>();
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < dtm.getRowCount(); i++) {
            String prod = ((String) dtm.getValueAt(i, 0)).trim();
            Integer prec = (Integer) dtm.getValueAt(i, 1);
            if (prec==null || (prec != null && prec <= 0)) {
                JOptionPane.showMessageDialog(this, "Debe ingresar precio mayor a cero", "Precio Invalido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (prod.length() == 0) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un texto descriptivo del producto", "Producto Vacio", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (set.contains(prod.toLowerCase())) {
                JOptionPane.showMessageDialog(this, prod, "Producto duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            set.add(prod.toLowerCase());
            Comparable o[] = new Comparable[]{prod, prec};
            list.add(o);
        }
        try {
            DB.instance.delPrecios.executeUpdate();
            int i = 0;
            for (Comparable[] o : list) {
                DB.instance.insertPrecios.executeUpdate(new Object[]{i, o[0], o[1]});
                i++;
            }
            DB.instance.delPrecios.getConnection().commit();
            DB.instance.auditoria("Actualiza lista de precios", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            try {
                DB.instance.delPrecios.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        JOptionPane.showMessageDialog(this, "Se ha guardado la lista de precios", "Actualización de Precios", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_formWindowClosing

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        int[] rows = jTable1.getSelectedRows();
        if (rows.length > 0) {
            String s = JOptionPane.showInputDialog("Ingrese multiplicador de precios (EJ: 1.2 incrementa en 20% los precios)");
            if (s != null) {
                try {
                    double val = Double.parseDouble(s.trim());
                    for (int i : rows) {
                        Integer old = (Integer) dtm.getValueAt(i, 1);
                        old = old == null ? 0 : old;
                        old = (int) (Math.round((double) old * val));
                        dtm.setValueAt(old, i, 1);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR al multiplicar", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            jTable1.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        Object[] options = new Object[]{"Exportar lista de precios a archivo excel", "Importar lista de precios desde archivo excel"};
        Object resp = JOptionPane.showInputDialog(this, "Seleccione una opción", "Importar/Exportar a Excel", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (options[0].equals(resp)) {
            try {
                Main.exportTable2PDF(this, "Lista de Precios", "precios", (KTable) jTable1, 13);
            } catch (IOException | WriteException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error al Exportar Precios", JOptionPane.ERROR_MESSAGE);
            }
        } else if (options[1].equals(resp)) {
            Main.jfc.setSelectedFile(new File(Main.jfc.getCurrentDirectory(), "precios"));
            if (Main.jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = Main.jfc.getSelectedFile();
                if (!f.getName().toLowerCase().endsWith(".xls")) {
                    f = new File(f.getParent(), f.getName() + ".xls");
                }
                try {
                    Workbook workbook = Workbook.getWorkbook(f);
                    Sheet sheet = workbook.getSheet(0);
                    Cell c1, c2;
                    int row = 1;
                    while (dtm.getRowCount() > 0) {
                        dtm.removeRow(0);
                    }

                    try {
                        do {
                            c1 = sheet.getCell(0, row);
                            c2 = sheet.getCell(1, row);
                            dtm.addRow(new Object[]{c1.getContents(), Integer.parseInt(c2.getContents())});
                            row++;
                        } while (true);
                    } catch (Exception e) {
                    }

                } catch (IOException | BiffException e) {
                    e.printStackTrace();
                }

            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
