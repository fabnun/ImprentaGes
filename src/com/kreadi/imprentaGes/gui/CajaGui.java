/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kreadi.imprentaGes.gui;

import com.kreadi.imprentaGes.DB;
import com.kreadi.swing.KSwingTools;
import com.kreadi.swing.KTable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.PlainDocument;

public class CajaGui extends javax.swing.JDialog {

    public static boolean open1 = false;

    /**
     * Creates new form CajaGui
     *
     * @param parent
     * @param modal
     */
    public CajaGui(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setIconImage(new ImageIcon("icono.png").getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        labelCaja = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        textObservacion = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new com.kreadi.swing.KTable(
            new String[]{"DESCRIPCION","VALOR"},
            new Class[]{String.class, Integer.class},
            new boolean[]{false, false},
            new int[]{0,0},
            new String[]{null,null}   
        );
        labelTurno = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labelFecha = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        textEfectivo = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        textCheques = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        textTransferencias = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        labelUsuario = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Imprenta Ges 1.0");

        jLabel2.setFont(Main.defaultFont18);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("CAJA");

        labelCaja.setBackground(Main.c1);
        labelCaja.setFont(Main.defaultFont18);
        labelCaja.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCaja.setText("Caja");
        labelCaja.setOpaque(true);

        jLabel3.setFont(Main.defaultFont18);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("TURNO");

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane1.setFocusable(false);

        textObservacion.setColumns(20);
        textObservacion.setFont(Main.defaultFont14);
        textObservacion.setRows(4);
        textObservacion.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(textObservacion);

        jTabbedPane1.addTab("OBSERVACIONES", jScrollPane2);

        jTable1.setFont(Main.defaultFont14);
        jTable1.setRowHeight(24);
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("MONTOS CALCULADOS", jScrollPane1);

        labelTurno.setBackground(Main.c1);
        labelTurno.setFont(Main.defaultFont18);
        labelTurno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTurno.setText("1");
        labelTurno.setOpaque(true);

        jLabel8.setFont(Main.defaultFont18);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("FECHA");

        labelFecha.setBackground(Main.c1);
        labelFecha.setFont(Main.defaultFont18);
        labelFecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelFecha.setText("17/03/2014");
        labelFecha.setOpaque(true);

        jButton3.setFont(Main.defaultFont18);
        jButton3.setText("PROCEDER");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jComboBox1.setBackground(Main.c1);
        jComboBox1.setFont(Main.defaultFont18);
        jComboBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel4.setFont(Main.defaultFont18);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("ACCIÓN");

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 24));

        jLabel5.setFont(Main.defaultFont14);
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("EFECTIVO");
        jLabel5.setMinimumSize(new java.awt.Dimension(44, 16));

        textEfectivo.setFont(Main.defaultFont14);
        textEfectivo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textEfectivo.setMargin(new java.awt.Insets(4, 4, 4, 4));
        textEfectivo.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        textEfectivo.setMinimumSize(new java.awt.Dimension(0, 24));
        textEfectivo.setPreferredSize(new java.awt.Dimension(120, 22));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textEfectivo, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2);

        jPanel4.setMaximumSize(new java.awt.Dimension(2147483647, 24));

        jLabel11.setFont(Main.defaultFont14);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("CHEQUES");
        jLabel11.setMinimumSize(new java.awt.Dimension(44, 16));

        textCheques.setFont(Main.defaultFont14);
        textCheques.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textCheques.setMargin(new java.awt.Insets(4, 4, 4, 4));
        textCheques.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        textCheques.setMinimumSize(new java.awt.Dimension(0, 24));
        textCheques.setPreferredSize(new java.awt.Dimension(120, 22));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textCheques, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textCheques, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel4);

        jPanel5.setMaximumSize(new java.awt.Dimension(2147483647, 24));

        jLabel12.setFont(Main.defaultFont14);
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("TRANSFERENCIAS");
        jLabel12.setMinimumSize(new java.awt.Dimension(44, 16));

        textTransferencias.setFont(Main.defaultFont14);
        textTransferencias.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textTransferencias.setMargin(new java.awt.Insets(4, 4, 4, 4));
        textTransferencias.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        textTransferencias.setMinimumSize(new java.awt.Dimension(0, 24));
        textTransferencias.setPreferredSize(new java.awt.Dimension(120, 22));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textTransferencias, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textTransferencias, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel5);

        jPanel3.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
        );

        labelUsuario.setBackground(Main.c1);
        labelUsuario.setFont(Main.defaultFont18);
        labelUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUsuario.setText("Usuario");
        labelUsuario.setOpaque(true);

        jLabel10.setFont(Main.defaultFont18);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("USUARIO");

        jLabel13.setFont(Main.defaultFont18);
        jLabel13.setText("HORA");

        jLabel14.setBackground(Main.c1);
        jLabel14.setFont(Main.defaultFont18);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("  :  ");
        jLabel14.setOpaque(true);

        jLabel1.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE))))
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String sel = (String) jComboBox1.getSelectedItem();
        switch (sel) {
            case "Abrir Caja":
                try {
                    Integer efectivo = Integer.parseInt(textEfectivo.getText().trim().replaceAll("\\.|,", ""));
                    try {
                        String observaciones = textObservacion.getText().trim();
                        DB.instance.insertTurno.executeUpdate(caja, time, turno, username, 'A', 0, 0, 0, observaciones, efectivo, 0, 0);
                        Long result = (Long) DB.instance.getLastId.executeFirstQuery()[0];
                        DB.instance.auditoria("Abre caja", "ID:" + result + " $" + Main.formatter.format(efectivo));
                        JOptionPane.showMessageDialog(this, caja + " Abierta con $" + Main.formatter.format(efectivo));
                        setVisible(false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar todos montos contabilizados", "ERROR", JOptionPane.ERROR_MESSAGE);
                    textEfectivo.requestFocus();
                }
                break;
            case "Cerrar Caja":
                try {
                    String observaciones = textObservacion.getText().trim();
                    int efectivo = 0;
                    try {
                        efectivo = Integer.parseInt(textEfectivo.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    int cheques = 0;
                    try {
                        cheques = Integer.parseInt(textCheques.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    int transferencias = 0;
                    try {
                        transferencias = Integer.parseInt(textTransferencias.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    if (observaciones.length() > 0 || (efectivoCalculado == efectivo && chequeCalculado == cheques && transferenciaCalculado == transferencias)) {
                        try {
                            DB.instance.insertTurno.executeUpdate(caja, time, turno, username, 'C', efectivoCalculado, chequeCalculado, transferenciaCalculado, observaciones, efectivo, cheques, transferencias);
                            Long result = (Long) DB.instance.getLastId.executeFirstQuery()[0];

                            if ((efectivoCalculado == efectivo && chequeCalculado == cheques && transferenciaCalculado == transferencias)) {
                                DB.instance.auditoria("Cierra caja", "ID:" + result + " $" + Main.formatter.format(efectivo) + "(efectivo) $" + Main.formatter.format(cheques) + "(cheques) $" + Main.formatter.format(transferencias) + "(transferencias)");
                            } else {
                                DB.instance.auditoria("Cierra caja con diferencias", "ID:" + result + " $"
                                        + Main.formatter.format(efectivo) + " -> " + Main.formatter.format(efectivoCalculado) + "(efectivo) $"
                                        + Main.formatter.format(cheques) + " -> " + Main.formatter.format(chequeCalculado) + "(cheques) $"
                                        + Main.formatter.format(transferencias) + " -> " + Main.formatter.format(transferenciaCalculado) + "(transferencias) " + observaciones);
                            }
                            JOptionPane.showMessageDialog(this, caja + " Cerrada");
                            setVisible(false);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No coincide lo calculado con lo ingresado, debe ingresar una observación", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar todos montos contabilizados", "ERROR", JOptionPane.ERROR_MESSAGE);
                    textEfectivo.requestFocus();
                }

                break;
            case "Ingreso":
                try {
                    int efectivo = 0;
                    try {
                        efectivo = Integer.parseInt(textEfectivo.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    int cheques = 0;
                    try {
                        cheques = Integer.parseInt(textCheques.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    int transferencias = 0;
                    try {
                        transferencias = Integer.parseInt(textTransferencias.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    try {
                        String observaciones = textObservacion.getText().trim();
                        DB.instance.insertTurno.executeUpdate(caja, time, turno, username, 'I', 0, 0, 0, observaciones, efectivo, cheques, transferencias);
                        Long result = (Long) DB.instance.getLastId.executeFirstQuery()[0];
                        DB.instance.auditoria("Ingreso de caja", "ID:" + result + " $" + Main.formatter.format(efectivo) + "(efectivo) $" + Main.formatter.format(cheques) + "(cheques) $" + Main.formatter.format(transferencias) + "(transferencias)");
                        JOptionPane.showMessageDialog(this, "Ingreso guardado");
                        setVisible(false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar todos montos contabilizados", "ERROR", JOptionPane.ERROR_MESSAGE);
                    textEfectivo.requestFocus();
                }

                break;
            case "Egreso":
                try {
                    int efectivo = 0;
                    try {
                        efectivo = Integer.parseInt(textEfectivo.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    int cheques = 0;
                    try {
                        cheques = Integer.parseInt(textCheques.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    int transferencias = 0;
                    try {
                        transferencias = Integer.parseInt(textTransferencias.getText().trim().replaceAll("\\.|,", ""));
                    } catch (Exception e) {
                    }
                    if (efectivoCalculado >= efectivo && chequeCalculado >= cheques && transferenciaCalculado >= transferencias) {
                        try {
                            String observaciones = textObservacion.getText().trim();
                            DB.instance.insertTurno.executeUpdate(caja, time, turno, username, 'E', 0, 0, 0, observaciones, -efectivo, -cheques, -transferencias);
                            Long result = (Long) DB.instance.getLastId.executeFirstQuery()[0];
                            DB.instance.auditoria("Egreso de caja", "ID:" + result + " $" + Main.formatter.format(efectivo) + "(efectivo) $" + Main.formatter.format(cheques) + "(cheques) $" + Main.formatter.format(transferencias) + "(transferencias)");
                            JOptionPane.showMessageDialog(this, "Egreso guardado");
                            setVisible(false);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No puede sacar mas de los que hay en la caja", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar todos montos contabilizados", "ERROR", JOptionPane.ERROR_MESSAGE);
                    textEfectivo.requestFocus();
                }

                break;
            case "Ejecutar Movimiento":
                break;
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private String caja = null;
    private String username = null;
    private int time = 0;
    private int turno = 0;
    long efectivoCalculado, chequeCalculado, transferenciaCalculado;

    public void audit(int id) {
        Object[] result = null;
        try {
            result = DB.instance.getTurnoAudit.executeFirstQuery(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {

            textEfectivo.setDocument(new PlainDocument());
            textCheques.setDocument(new PlainDocument());
            textTransferencias.setDocument(new PlainDocument());
            textObservacion.setEditable(true);

            labelCaja.setText(result[1].toString());
            labelUsuario.setText(result[0] == null ? "" : result[0].toString());
            try {
                labelFecha.setText(DB.sdf.format(Main.sdf2.parse(result[2].toString()).getTime()));
            } catch (Exception e) {
                labelFecha.setText(e.getMessage());
            }
            labelTurno.setText(result[3].toString());

            String estado = result[4].toString();
            if ("A".equals(estado)) {
                estado = "Abrir Caja";
                if (jTabbedPane1.getComponentCount() == 2) {
                    jTabbedPane1.remove(1);
                }
            } else if ("C".equals(estado)) {
                estado = "Cerrar Caja";
                if (jTabbedPane1.getComponentCount() == 1) {
                    jTabbedPane1.add(jTable1, 1);
                    jTabbedPane1.setTitleAt(1, "MONTOS CALCULADOS");
                }
            } else if ("I".equals(estado)) {
                estado = "Ingreso";
                 if (jTabbedPane1.getComponentCount() == 2) {
                    jTabbedPane1.remove(1);
                }
            } else if ("E".equals(estado)) {
                estado = "Egreso";
                 if (jTabbedPane1.getComponentCount() == 2) {
                    jTabbedPane1.remove(1);
                }
            } else {
                estado = "No definido";
                if (jTabbedPane1.getComponentCount() == 1) {
                    jTabbedPane1.add(jTable1, 1);
                    jTabbedPane1.setTitleAt(1, "MONTOS CALCULADOS");
                }
            }
            jComboBox1.setModel(new DefaultComboBoxModel(new Object[]{estado}));

            result[8]=result[8] != null ? result[8] : 0;
            textEfectivo.setText(Main.formatter.format(result[8]));
            result[9]=result[9] != null ? result[9] : 0;
            textCheques.setText(Main.formatter.format(result[9]));
            result[10]=result[10] != null ? result[10] : 0;
            textTransferencias.setText(Main.formatter.format(result[10]));

            jButton3.setVisible(false);
          
            textObservacion.setEditable(false);
            jPanel2.setVisible(true);
            jPanel4.setVisible(true);
            jPanel5.setVisible(true);

            KTable table = (KTable) jTable1;
            while (table.getRowCount() > 0) {
                table.removeRow(0);
            }

            table.addRow(new Object[]{"Efectivo Calculado", (int) result[5]});
            table.addRow(new Object[]{"Cheques Calculado", (int) result[6]});
            table.addRow(new Object[]{"Transferencias Calculado", (int) result[7]});

            String clob = (String) result[11];
            try {
                textObservacion.setText(clob);
            } catch (Exception e) {
                textObservacion.setText(e.getMessage());
                e.printStackTrace();
            }
            jLabel14.setText(Main.sdfh2.format(result[12]));
            textEfectivo.requestFocus();
            textCheques.requestFocus();
            textTransferencias.requestFocus();
            
            jComboBox1.requestFocus();
            textEfectivo.setEditable(false);
            textCheques.setEditable(false);
            textTransferencias.setEditable(false);
            setVisible(true);

        }
    }

    public void updateData(boolean cerrada, String name, String usuario, String username, int dia, int turno) {
        this.caja = name;
        this.username = username;
        this.time = dia;
        this.turno = turno;
        KSwingTools.setIntegerField(textEfectivo);
        KSwingTools.setIntegerField(textCheques);
        KSwingTools.setIntegerField(textTransferencias);
        textEfectivo.setEditable(true);
        textCheques.setEditable(true);
        textTransferencias.setEditable(true);
        textEfectivo.setText("");
        textCheques.setText("");
        textTransferencias.setText("");
        textObservacion.setText("");
        textObservacion.setEditable(true);
        jButton3.setVisible(true);
        labelCaja.setText(name);
        labelUsuario.setText(usuario);
        labelTurno.setText("" + turno);
        try {
            labelFecha.setText(DB.sdf.format(Main.sdf2.parse("" + this.time)));
        } catch (ParseException e) {

        }
        if (cerrada) {//Caja cerrada... solo permite la apertura...
            jComboBox1.setModel(new DefaultComboBoxModel(new Object[]{"Abrir Caja"}));
            if (jTabbedPane1.getComponentCount() == 2) {
                jTabbedPane1.remove(1);
            }
            jPanel4.setVisible(false);
            jPanel5.setVisible(false);
        } else {//Caja abierta...
            jComboBox1.setModel(new DefaultComboBoxModel(new Object[]{"Cerrar Caja", "Ingreso", "Egreso"}));

            try {
                List<Object[]> lista = DB.instance.getCuadratura.executeQuery(dia, caja, turno);
                KTable table = (KTable) jTable1;
                while (table.getRowCount() > 0) {
                    table.removeRow(0);
                }
                efectivoCalculado = 0;
                chequeCalculado = 0;
                transferenciaCalculado = 0;
                for (Object[] o : lista) {
                    if ("EFECTIVO".equals(o[0])) {
                        efectivoCalculado = efectivoCalculado + (Long) o[1];
                    }
                    if ("CHEQUE".equals(o[0])) {
                        chequeCalculado = chequeCalculado + (Long) o[1];
                    }
                    if ("TRANSFERENCIA".equals(o[0])) {
                        transferenciaCalculado = transferenciaCalculado + (Long) o[1];
                    }
                }
                Object[] result = DB.instance.getTurnoApertura.executeFirstQuery(dia, caja, turno);
                int apertura = 0;
                if (result != null) {
                    apertura = (Integer) result[0];
                }
                efectivoCalculado = efectivoCalculado + apertura;
                result = DB.instance.getIngresoEgreso.executeFirstQuery(dia, caja, turno);
                if (result != null) {
                    try {
                        efectivoCalculado = efectivoCalculado + (Long) result[0];
                    } catch (Exception e) {
                    }
                    try {
                        chequeCalculado = chequeCalculado + (Long) result[1];
                    } catch (Exception e) {
                    }
                    try {
                        transferenciaCalculado = transferenciaCalculado + (Long) result[2];
                    } catch (Exception e) {
                    }
                }
                table.addRow(new Object[]{"Efectivo Calculado", (int) efectivoCalculado});
                table.addRow(new Object[]{"Cheques Calculado", (int) chequeCalculado});
                table.addRow(new Object[]{"Transferencias Calculado", (int) transferenciaCalculado});
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jTabbedPane1.getComponentCount() == 1) {
                jTabbedPane1.add(jTable1, 1);
                jTabbedPane1.setTitleAt(1, "MONTOS CALCULADOS");
            }
            jPanel4.setVisible(true);
            jPanel5.setVisible(true);
        }
        jTabbedPane1.setSelectedIndex(0);
        setVisible(true);
        textEfectivo.requestFocus();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    public javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JTable jTable1;
    protected javax.swing.JLabel labelCaja;
    private javax.swing.JLabel labelFecha;
    private javax.swing.JLabel labelTurno;
    protected javax.swing.JLabel labelUsuario;
    private javax.swing.JTextField textCheques;
    public javax.swing.JTextField textEfectivo;
    private javax.swing.JTextArea textObservacion;
    private javax.swing.JTextField textTransferencias;
    // End of variables declaration//GEN-END:variables
}
