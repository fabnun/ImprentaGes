package com.kreadi.imprentaGes.gui;

import com.kreadi.imprentaGes.DB;
import com.kreadi.swing.KPlainDocument;
import com.kreadi.swing.KSwingTools;
import com.kreadi.swing.KTable;
import com.kreadi.swing.KTableCellListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

public class ConfigGui extends javax.swing.JDialog {

    public static boolean open1 = false;
    public static boolean open2 = false;
    public static boolean open3 = false;
    private JFileChooser jfc = new JFileChooser();
    FileFilter f1 = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.isFile() && f.getName().toLowerCase().endsWith(".form");
        }

        @Override
        public String getDescription() {
            return "ImprentaGes Formato de Impresión";
        }
    };
    FileFilter f2 = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.isFile() && f.getName().toLowerCase().endsWith(".zip");
        }

        @Override
        public String getDescription() {
            return "Respaldo DB Zip";
        }
    };

    DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();

    public ConfigGui(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        jfc.setFileFilter(f1);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    jTable1.getCellEditor().stopCellEditing();
                } catch (Exception ex) {
                }
                try {
                    jTable2.getCellEditor().stopCellEditing();
                } catch (Exception ex) {
                }
            }

        });
        jDialog3.setSize(303, 206);
        jDialog3.setIconImage(new ImageIcon("icono.png").getImage());
        jComboBox1.setModel(comboModel);
        setIconImage(new ImageIcon("icono.png").getImage());
        jDialog1.setIconImage(new ImageIcon("icono.png").getImage());
        jTable1.setModel(dtm);
        jTable3.setModel(dtm2);
        jTextField3.setDocument(new KPlainDocument(3, "\\d*", null, null));
        jTextField4.setDocument(new KPlainDocument(3, "\\d*", null, null));
        jTextField1.setDocument(new KPlainDocument(512, "(\\d| )*", null, null));
        jTextField5.setDocument(new KPlainDocument(512, "(\\d| |\\[)*", null, null));
        jTextArea1.setDocument(new KPlainDocument(4096, "[^\\t\\r]*", null, null));

        new KTableCellListener(jTable1, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KTableCellListener tcl = (KTableCellListener) e.getSource();
                int row = tcl.getRow();
                int col = tcl.getColumn();
                try {
                    Object[] obj = new Object[dtm.getColumnCount()+1];
                    for (int i = 0; i < obj.length-1; i++) {
                        Object o = dtm.getValueAt(row, i);
                        if (o instanceof String) {
                            obj[i] = o.toString().trim();
                        } else {
                            obj[i] = o;
                        }
                    }
                    obj[obj.length-1]=obj[0];
                    DB.instance.updateUsuario.executeUpdate(obj);
                    DB.instance.auditoria("Configura usuario ", obj[0].toString() + " -> " + jTable1.getColumnName(col) + ":" + obj[col].toString());
                    updateData();
                } catch (SQLException ex) {
                    DB.instance.auditoria("ERROR", "Al actualizar datos de usuarios " + ex.getMessage());
                    ex.printStackTrace();
                    Object o = tcl.getOldValue();
                    dtm.setValueAt(o, row, col);
                }

            }
        });

        new KTableCellListener(jTable3, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KTableCellListener tcl = (KTableCellListener) e.getSource();
                int row = tcl.getRow();
                int col = tcl.getColumn();

                try {
                    Object[] obj = new Object[dtm2.getColumnCount() + 1];
                    for (int i = 0; i < obj.length - 1; i++) {
                        Object o = dtm2.getValueAt(row, i);
                        if (o instanceof String) {
                            obj[i] = o.toString().trim();
                        } else {
                            obj[i] = o;
                        }
                    }
                    obj[obj.length - 1] = obj[0];
                    DB.instance.updateCaja.executeUpdate(obj);
                    DB.instance.auditoria("Configura caja ", obj[0].toString() + " -> " + jTable3.getColumnName(col) + ":" + obj[col].toString());
                } catch (SQLException ex) {
                    DB.instance.auditoria("ERROR", "Al actualizar datos de cajas " + ex.getMessage());
                    ex.printStackTrace();
                    Object o = tcl.getOldValue();
                    dtm.setValueAt(o, row, col);
                }

            }
        });

    }

    DefaultTableModel dtm = new DefaultTableModel(new Object[]{"Usuario", "Nombre", "Email", "Teléfonos", "P.Precios", "P.Config", "P.Caja", "P.OT", "Activo"}, 0) {

        Class[] classes = new Class[]{String.class, String.class, String.class, String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class};

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return classes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column > 0;
        }

    };

    DefaultTableModel dtm2 = new DefaultTableModel(new Object[]{"Caja", "Impresora Factura", "Impresora Boleta", "Impresora OT", "Impresora Informe", "Usuarios", "Activa"}, 0) {

        Class[] classes = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class};

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return classes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column > 0;
        }

    };

    public void updateData() throws SQLException {

        List<Object[]> list = DB.instance.getFormatoNames.executeQuery();
        comboModel.removeAllElements();
        for (Object[] rowData : list) {
            comboModel.addElement((String) rowData[0]);
        }

        while (dtm.getRowCount() > 0) {
            dtm.removeRow(0);
        }

        list = DB.instance.selectUsuarios.executeQuery();
        for (Object[] rowData : list) {
            if (todos || (rowData[8] != null && (boolean) rowData[8])) {
                dtm.addRow(rowData);
            }
        }
        while (dtm2.getRowCount() > 0) {
            dtm2.removeRow(0);
        }
        List<Object[]> list2 = DB.instance.selectCajas.executeQuery();
        for (Object[] rowData : list2) {
            if (todos2 || rowData[6] != null && (boolean) rowData[6]) {
                dtm2.addRow(rowData);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new JDialog(this, true);
        jButton2 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new com.kreadi.swing.KTable(
            new String[]{"PROPIEDAD","VALOR"},
            new Class[]{String.class, String.class},
            new boolean[]{false, true},
            new int[]{0, 128},
            new String[]{null,null}   
        );
        jLabel13 = new javax.swing.JLabel();
        jDialog2 = new JDialog(this, true);
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new JTextArea(){

            Color back=new Color(244,244,244);

            @Override
            public void paint(Graphics g) {
                g.setColor(back);
                try{
                    g.fillRect(0, 0, 
                        8*Integer.parseInt(jTextField3.getText()),
                        17*Integer.parseInt(jTextField4.getText()));
                } catch(Exception e){}
                super.paintComponent(g);
            }

        };
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jDialog3 = new javax.swing.JDialog();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel11 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jButton13 = new javax.swing.JButton();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenuItem34 = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jCheckBox2 = new javax.swing.JCheckBox();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton9 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();

        jDialog1.setTitle("Parametros");
        jDialog1.setFont(Main.defaultFont14);
        jDialog1.setMinimumSize(new java.awt.Dimension(320, 240));
        jDialog1.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog1.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        jButton2.setFont(Main.defaultFont14
        );
        jButton2.setText("Guardar y Cerrar");
        jButton2.setFocusable(false);
        jButton2.setMaximumSize(new java.awt.Dimension(178, 26));
        jButton2.setMinimumSize(new java.awt.Dimension(178, 26));
        jButton2.setPreferredSize(new java.awt.Dimension(178, 26));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTable2.setFont(Main.defaultFont14);
        jTable2.setAutoscrolls(false);
        jTable2.setRowHeight(22);
        jScrollPane4.setViewportView(jTable2);

        jLabel13.setText("Reinicie para aplicar");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jDialog2.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog2.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        jLabel1.setFont(Main.defaultFont14);
        jLabel1.setText("CÓDIGO ASCII INICIAL");

        jTextField1.setFont(Main.defaultFont14);

        jLabel3.setFont(Main.defaultFont14);
        jLabel3.setText("ANCHO");

        jTextField3.setFont(Main.defaultFont14);
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jLabel4.setFont(Main.defaultFont14);
        jLabel4.setText("ALTO");

        jTextField4.setFont(Main.defaultFont14);
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4KeyTyped(evt);
            }
        });

        jTextField5.setFont(Main.defaultFont14);

        jLabel5.setFont(Main.defaultFont14);
        jLabel5.setText("CÓDIGO ASCII FINAL");

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Main.defaultFont14), "Formato (Presione el boton derecho del mouse para desplegar el Menú)"));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(Main.defaultFont14);
        jTextArea1.setRows(5);
        jTextArea1.setOpaque(false);
        jTextArea1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextArea1CaretUpdate(evt);
            }
        });
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea1MousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTextArea1);

        jButton1.setFont(Main.defaultFont18);
        jButton1.setText("GUARDAR Y CERRAR");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(Main.defaultFont14);

        jButton15.setFont(Main.defaultFont18);
        jButton15.setText("EXPORTAR");
        jButton15.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton15.setFocusable(false);
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton17.setFont(Main.defaultFont18);
        jButton17.setText("IMPORTAR");
        jButton17.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton17.setFocusable(false);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton19.setFont(Main.defaultFont18);
        jButton19.setText("?");
        jButton19.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton19.setFocusable(false);
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog2Layout.createSequentialGroup()
                        .addComponent(jButton19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jDialog3.setTitle("Modificación de Clave");
        jDialog3.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog3.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jDialog3.setResizable(false);

        jLabel6.setFont(Main.defaultFont14);
        jLabel6.setText("Usuario");

        jLabel7.setFont(Main.defaultFont14);
        jLabel7.setText("Nombre");

        jLabel8.setFont(Main.defaultFont14);
        jLabel8.setText("jLabel8");

        jLabel9.setFont(Main.defaultFont14);
        jLabel9.setText("jLabel8");

        jLabel10.setFont(Main.defaultFont14);
        jLabel10.setText("Clave Nueva");

        jPasswordField1.setFont(Main.defaultFont14);

        jLabel11.setFont(Main.defaultFont14);
        jLabel11.setText("Clave Nueva");

        jPasswordField2.setFont(Main.defaultFont14);

        jButton13.setFont(Main.defaultFont14);
        jButton13.setText("Guardar y Cerrar");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPasswordField2)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(jPasswordField1))
                .addContainerGap())
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("Cabecera");
        jMenu1.setToolTipText("");

        jMenuItem35.setText("número OT (Código de barra)");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem35);

        jMenuItem24.setText("número OT ");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem24);

        jMenuItem1.setText("número de factura/boleta");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem25.setText("celulares");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem25);

        jMenuItem26.setText("web");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem26);

        jMenuItem27.setText("mail");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem27);

        jMenuItem28.setText("nombre de fantasia");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem28);

        jMenuItem29.setText("sucursales");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem29);

        jMenuItem2.setText("dia Factura/Boleta");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("mes Factura/Boleta");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("año Factura/Boleta");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("razon");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("dirección");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem7.setText("comuna");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem8.setText("ciudad");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuItem9.setText("fono");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem9);

        jMenuItem10.setText("rut");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenuItem15.setText("digito verificador");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem15);

        jMenuItem11.setText("giro");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem30.setText("fecha solicitud");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem30);

        jMenuItem31.setText("fecha entrega");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem31);

        jMenuItem32.setText("Servicio de Impuestos Internos");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem32);

        jPopupMenu1.add(jMenu1);

        jMenu2.setText("Detalle");
        jMenu2.setToolTipText("");

        jMenuItem16.setText("cantidad");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem16);

        jMenuItem17.setText("detalle");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem17);

        jMenuItem18.setText("folio");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem18);

        jMenuItem19.setText("precio");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem19);

        jMenuItem20.setText("total");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem20);

        jPopupMenu1.add(jMenu2);

        jMenu3.setText("Pie");
        jMenu3.setToolTipText("");

        jMenuItem22.setText("neto");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem22);

        jMenuItem23.setText("iva");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem23);

        jMenuItem21.setText("total final");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem21);

        jMenuItem33.setText("abono");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem33);

        jMenuItem34.setText("saldo");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem34);

        jPopupMenu1.add(jMenu3);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuración");

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 400));

        jScrollPane1.setBackground(Main.c1);
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Usuarios y Permisos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Main.defaultFont14));
        jScrollPane1.setFocusable(false);

        jTable1.setFont(Main.defaultFont14);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"user1", null, null, null},
                {"user2", null, null, null},
                {"user3", null, null, null}
            },
            new String [] {
                "Usuario", "Precios", "Configuración", "Movimientos"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setRowHeight(24);
        jScrollPane1.setViewportView(jTable1);

        jCheckBox1.setFont(Main.defaultFont14);
        jCheckBox1.setText("Mostras desactivados");
        jCheckBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCheckBox1.setFocusable(false);
        jCheckBox1.setOpaque(false);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton6.setFont(Main.defaultFont14);
        jButton6.setText("R");
        jButton6.setToolTipText("Renombrar Usuario");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setEnabled(false);
        jButton6.setFocusable(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton4.setFont(Main.defaultFont14);
        jButton4.setText("+");
        jButton4.setToolTipText("Nuevo Usuario");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusable(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton8.setFont(Main.defaultFont14);
        jButton8.setText("P");
        jButton8.setToolTipText("Cambiar clave");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.setFocusable(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1065, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4)
                        .addComponent(jButton6)
                        .addComponent(jCheckBox1)))
                .addContainerGap())
        );

        jSplitPane1.setTopComponent(jPanel1);

        jScrollPane3.setBackground(Main.c1);
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cajas e Impresoras", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Main.defaultFont14));
        jScrollPane3.setFocusable(false);

        jTable3.setFont(Main.defaultFont14);
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"user1", "pass1", null, null, null, null},
                {"user2", "pass2", null, null, null, null},
                {"user3", "pass3", null, null, null, null}
            },
            new String [] {
                "Caja", "Impresora Boleta", "Impresora Factura", "Impresora OT", "Impresora Informe", "Usuarios"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable3.setRowHeight(24);
        jScrollPane3.setViewportView(jTable3);

        jCheckBox2.setFont(Main.defaultFont14);
        jCheckBox2.setText("Mostras desactivadas");
        jCheckBox2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCheckBox2.setFocusable(false);
        jCheckBox2.setOpaque(false);
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jButton5.setFont(Main.defaultFont14);
        jButton5.setText("R");
        jButton5.setToolTipText("Renombrar Caja");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setEnabled(false);
        jButton5.setFocusable(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setFont(Main.defaultFont14);
        jButton7.setText("+");
        jButton7.setToolTipText("Nueva Caja");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.setFocusable(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1065, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton7)
                        .addComponent(jButton5))
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Formatos de Impresión, otros Parametros y Respaldo de Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Main.defaultFont14));

        jComboBox1.setFont(Main.defaultFont14);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "factura", "boleta", "ot" }));
        jComboBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButton9.setFont(Main.defaultFont14);
        jButton9.setText("+");
        jButton9.setToolTipText("Nuevo formato");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.setFocusable(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton3.setFont(Main.defaultFont14);
        jButton3.setText("-");
        jButton3.setToolTipText("Eliminar formato");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusable(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton11.setFont(Main.defaultFont14);
        jButton11.setText("R");
        jButton11.setToolTipText("Renombrar formato");
        jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton11.setFocusable(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton10.setFont(Main.defaultFont14);
        jButton10.setText("Otros Parametros");
        jButton10.setToolTipText("Configuración de parametros");
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.setFocusable(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton12.setFont(Main.defaultFont14);
        jButton12.setText("E");
        jButton12.setToolTipText("Editar formato");
        jButton12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton12.setFocusable(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jButton10)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11)
                    .addComponent(jButton10)
                    .addComponent(jButton12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1067, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String usuario = JOptionPane.showInputDialog("ingrese el id del usuario");
        if (usuario != null) {
            usuario = usuario.trim().toLowerCase();
            try {
                if (DB.instance.existUsuario.executeFirstQuery(usuario) == null) {
                    DB.instance.insertUsuario.executeUpdate(usuario, "", "", "", "", false, false, false, false, true);
                    DB.instance.auditoria("Crea nuevo usuario", usuario);
                    updateData();
                } else {
                    JOptionPane.showMessageDialog(this, "id de usuario en uso, elija otro");
                }
            } catch (SQLException e) {
                DB.instance.auditoria("ERROR", "Al verificar existencia de usuario " + e.getMessage());
                e.printStackTrace();
            }

        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int row = jTable1.getSelectedRow();
        String old = ((String) dtm.getValueAt(row, 0)).trim();
        String usuario = JOptionPane.showInputDialog("ingrese el id del usuario");
        if (usuario != null) {
            usuario = usuario.trim().toLowerCase();
            try {
                if (DB.instance.existUsuario.executeFirstQuery(usuario) == null) {
                    Object[] obj = new Object[dtm.getColumnCount() + 1];
                    for (int i = 0; i < obj.length - 1; i++) {
                        Object o = dtm.getValueAt(row, i);
                        if (o instanceof String) {
                            obj[i] = o.toString().trim();
                        } else {
                            obj[i] = o;
                        }
                    }
                    obj[0] = usuario;
                    obj[obj.length - 1] = old;
                    DB.instance.updateUsuario.executeUpdate(obj);
                    dtm.setValueAt(usuario, row, 0);
                } else {
                    JOptionPane.showMessageDialog(this, "id de usuario en uso, elija otro");
                }
            } catch (SQLException e) {
                DB.instance.auditoria("ERROR", "Al verificar existencia de usuario " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    boolean todos = false;
    boolean todos2 = false;
    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        todos = jCheckBox1.isSelected();
        try {
            updateData();
        } catch (SQLException e) {
            DB.instance.auditoria("ERROR", "Al ver usuarios activos " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String caja = JOptionPane.showInputDialog("ingrese el id de la caja");
        if (caja != null) {
            caja = caja.trim().toLowerCase();
            try {
                if (DB.instance.existCaja.executeFirstQuery(caja) == null) {
                    DB.instance.insertCaja.executeUpdate(caja, "", "", "", "", "", true);
                    DB.instance.auditoria("Crea nueva caja", caja);
                    updateData();
                } else {
                    JOptionPane.showMessageDialog(this, "id de la caja en uso, elija otro");
                }
            } catch (SQLException e) {
                DB.instance.auditoria("ERROR", "Al verificar existencia de caja " + e.getMessage());

                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int row = jTable3.getSelectedRow();
        String old = ((String) dtm2.getValueAt(row, 0)).trim();
        String caja = JOptionPane.showInputDialog("ingrese el id de caja");
        if (caja != null) {
            caja = caja.trim().toLowerCase();
            try {
                if (DB.instance.existCaja.executeFirstQuery(caja) == null) {
                    Object[] obj = new Object[dtm2.getColumnCount() + 1];
                    for (int i = 0; i < obj.length - 1; i++) {
                        Object o = dtm2.getValueAt(row, i);
                        if (o instanceof String) {
                            obj[i] = o.toString().trim();
                        } else {
                            obj[i] = o;
                        }
                    }
                    obj[0] = caja;
                    obj[obj.length - 1] = old;
                    DB.instance.updateCaja.executeUpdate(obj);
                    dtm2.setValueAt(caja, row, 0);
                } else {
                    JOptionPane.showMessageDialog(this, "id de la caja en uso, elija otro");
                }
            } catch (SQLException e) {
                DB.instance.auditoria("ERROR", "Al verificar existencia de caja " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        todos2 = jCheckBox2.isSelected();
        try {
            updateData();
        } catch (SQLException e) {
            DB.instance.auditoria("ERROR", "Al ver cajas activas " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        try {
            KTable table = (KTable) jTable2;
            while (table.getRowCount() > 0) {
                table.removeRow(0);
            }
            List<Object[]> result = DB.instance.getParametros.executeQuery();
            for (Object[] o : result) {
                table.addRow(o);
            }
            jDialog1.setVisible(true);
            open3 = true;
        } catch (Exception e) {
            DB.instance.auditoria("ERROR", "Al cargar parametros " + e.getMessage());
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String formato = (String) jComboBox1.getSelectedItem();
        if (formato != null && JOptionPane.showConfirmDialog(this, "Desea eliminar el formato " + formato, "Eliminar formato", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                DB.instance.removeFormato.executeUpdate(formato);
                updateData();
            } catch (Exception e) {
                DB.instance.auditoria("ERROR", "Al eliminar formato " + formato + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        String formato = (String) jComboBox1.getSelectedItem();
        if (formato != null) {
            try {
                Object[] result = DB.instance.getFormato.executeFirstQuery(formato);
                jDialog2.setTitle("Formato de Impresión " + formato);
                jTextField3.setText(result[0].toString());
                jTextField4.setText(result[1].toString());
                jTextField1.setText(result[2].toString());
                jTextField5.setText(result[3].toString());
                String clob = (String) result[4];
                jTextArea1.setText(clob);
            } catch (Exception e) {
                DB.instance.auditoria("ERROR", "Al cargar formato " + formato + " " + e.getMessage());
                e.printStackTrace();
            }
            open2 = true;
            jDialog2.setVisible(true);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String formato = (String) jComboBox1.getSelectedItem();
        if (formato != null) {
            String newname = JOptionPane.showInputDialog(this, "Nuevo nombre para el formato " + formato, "");
            if (newname != null && newname.trim().length() > 0) {
                try {
                    DB.instance.renameFormato.executeUpdate(newname.trim(), formato);
                    updateData();
                } catch (Exception e) {
                    DB.instance.auditoria("ERROR", "Al renombrar formato " + formato + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        String newname = JOptionPane.showInputDialog(this, "Nombre para el formato nuevo", "");
        if (newname != null && newname.trim().length() > 0) {
            try {
                DB.instance.newFormato.executeUpdate(newname.trim());
                updateData();
            } catch (Exception e) {
                DB.instance.auditoria("ERROR", "Al crear nuevo formato " + newname + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton9ActionPerformed


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String formato = (String) jComboBox1.getSelectedItem();
        if (formato != null) {
            try {
                String code = jTextArea1.getText().replaceAll("[ \\t\\r]+\n", "\n");
                DB.instance.updateFormato.executeUpdate(Integer.parseInt(jTextField3.getText()), Integer.parseInt(jTextField4.getText()), jTextField1.getText(), jTextField5.getText(), code, formato);
                jDialog2.setVisible(false);
                DB.instance.auditoria("Configura formato", formato);
            } catch (NumberFormatException | SQLException e) {
                DB.instance.auditoria("ERROR", "Al guardar  formato " + formato + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextArea1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextArea1CaretUpdate
        jLabel2.setText("Posición " + KSwingTools.getColumnAtCaret(jTextArea1) + ":" + KSwingTools.getLineAtCaret(jTextArea1));
    }//GEN-LAST:event_jTextArea1CaretUpdate

    private void jTextArea1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MousePressed
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jPopupMenu1.show(jTextArea1, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTextArea1MousePressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        addField("[$doc]");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        addField("[dia]");
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        addField("[mes]");
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        addField("[año]");
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        addField("[razon]");
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        addField("[direccion]");
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        addField("[comuna]");
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        addField("[ciudad]");
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        addField("[fono]");
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        addField("[rut]");
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        addField("[v]");
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        addField("[giro]");
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        addField("[$cant]");
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        addField("[det]");
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        addField("[$folio]");
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        addField("[$precio]");
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        addField("[$total]");
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        addField("[$neto]");
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        addField("[$iva]");
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        addField("[$totfinal]");
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        jTextArea1.repaint();
        jTextArea1.updateUI();
    }//GEN-LAST:event_jTextField3KeyReleased

    private void addField(String text) {
        jTextArea1.insert(text, jTextArea1.getCaretPosition());
    }

    private void jTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyTyped
        jTextArea1.repaint();
        jTextArea1.updateUI();
    }//GEN-LAST:event_jTextField4KeyTyped

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        addField("[$numero]");
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        addField("[cel]");
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        addField("[web]");
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        addField("[mail]");
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        addField("[fantasia]");
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        addField("[sucursales]");
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        KTable kt = (KTable) jTable2;
        try {
            jTable2.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        for (int i = 0; i < kt.getRowCount(); i++) {
            try {
                DB.instance.updateParam.executeUpdate(kt.getValueAt(i, 1), kt.getValueAt(i, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DB.instance.auditoria("Configura parametros", null);
        DB.loadParams();
        jDialog1.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        addField("[fechasol]");
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        addField("[fechaent]");
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        addField("[sii]");
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        addField("[$abono]");
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        addField("[$saldo]");
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        addField("[barcode]");
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            String user = (String) jTable1.getValueAt(row, 0);
            String name = (String) jTable1.getValueAt(row, 1);
            jLabel8.setText(user);
            jLabel9.setText(name);
            jPasswordField1.setText("");
            jPasswordField2.setText("");

            jDialog3.setLocationRelativeTo(jButton8);
            jDialog3.setVisible(true);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        String pass1 = new String(jPasswordField1.getPassword());
        String pass2 = new String(jPasswordField2.getPassword());
        String user = jLabel8.getText();
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(jDialog3, "Debe repetir la clave y esta tiene no puede estar vacia.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                int upd = DB.instance.setPass2.executeUpdate(pass1, user);
                if (upd == 1) {
                    DB.instance.auditoria("Cambia clave", user);
                    JOptionPane.showMessageDialog(jDialog3, "Clave modificada");
                    jDialog3.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(jDialog3, "clave actual no es correcta", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | HeadlessException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        jfc.setFileFilter(f1);
        if (jfc.showSaveDialog(jDialog2) == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            if (!f.getName().endsWith(".form")) {
                f = new File(f.getAbsolutePath() + ".form");
            }
            try (FileOutputStream fos = new FileOutputStream(f); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(new String[]{jTextField3.getText(), jTextField4.getText(), jTextField1.getText(), jTextField5.getText(), jTextArea1.getText()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton15ActionPerformed


    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        jfc.setFileFilter(f1);
        if (jfc.showOpenDialog(jDialog2) == JFileChooser.APPROVE_OPTION) {
            try (FileInputStream fis = new FileInputStream(jfc.getSelectedFile()); ObjectInputStream ois = new ObjectInputStream(fis)) {
                String[] data = (String[]) ois.readObject();
                jTextField3.setText(data[0]);
                jTextField4.setText(data[1]);
                jTextField1.setText(data[2]);
                jTextField5.setText(data[3]);
                jTextArea1.setText(data[4]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        String msg = "Campos Soportados: (* campos del detalle)\n"
                + "abono, año, barcode, cant(*), cel, ciudad, comuna, det(*), dia, dir, doc, fantasia, fechaent, fechasol, folio(*),\n"
                + "fono, giro, iva, mail, mes, neto, numero, precio(*), razon, rut, saldo, sii, sucursales, total(*), totfinal, web.\n\n"
                + "Ejemplos:\nCampo Numerico, formateado, alineado a la derecha: [$precio]\n"
                + "Campo Numerico, no formateado, alineado a la derecha: [%precio]\n"
                + "Campo de texto, ocupa el ancho real: [razon+]";
        JOptionPane.showMessageDialog(jDialog2, msg);
    }//GEN-LAST:event_jButton19ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox jComboBox1;
    public javax.swing.JDialog jDialog1;
    public javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JSplitPane jSplitPane1;
    public javax.swing.JTable jTable1;
    public javax.swing.JTable jTable2;
    public javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
