/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kreadi.imprentaGes.gui;

import com.kreadi.imprentaGes.DB;
import com.kreadi.swing.KAutoComplete;
import com.kreadi.swing.KCellRenderer;
import com.kreadi.swing.KPlainDocument;
import com.kreadi.swing.KSwingTools;
import com.kreadi.swing.KTable;
import com.kreadi.swing.KTableCellListener;
import com.michaelbaranov.microba.calendar.CalendarPane;
import com.roncemer.barcode.BarCodeRenderer;
import com.roncemer.barcode.UPCABarCodeRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Maria Jose
 */
public class OrdenTrabajoGui extends javax.swing.JDialog {

    public static boolean open1 = false;
    public static boolean open2 = false;
    private boolean facturaDatos = false;
    private String[] facturaDatosText = null;
    private String[] otText = null;

    CalendarPane calFechaSolicitud, calFechaEntrega, calFechaDocumento;

    Integer doc = null;
    int pagado, tot, neto, iva;
    int ot, dia, turno, oldOT, searchState = 0;
    String rut_factura, rut, dv, razon, direccion, comuna, ciudad, telefono, celular, mail, sii, web, fantasia, giro, sucursal, observaciones;
    Long turno_estado;
    String estado = null;

    public static char dv(int numero) {
        int M = 0, S = 1;
        for (; numero != 0; numero /= 10) {
            S = (S + numero % 10 * (9 - M++ % 6)) % 11;
        }
        return ((char) (S != 0 ? S + 47 : 75));
    }
    private boolean[] guardarPago = null;
    private long[] idPago = null;

    private boolean valida2() {

        facturaDatosText = new String[]{
            tRut.getText(), tDv.getText(), tRazon.getText(), tDireccion.getText(),
            tComuna.getText(), tCiudad.getText(), tTelefonos.getText(), tCelulares.getText(),
            tMail.getText(), tSii.getText(), tWeb.getText(), tFantasia.getText(), Tgiro.getText(), tSucursal.getText()};

        rut = facturaDatosText[0].replaceAll("\\.", "").trim();
        dv = facturaDatosText[1].trim();
        try {
            if (rut.length() == 0 || dv.length() == 0 || dv(Integer.parseInt(rut)) != dv.charAt(0)) {
                JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO RUT INVALIDO");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO RUT INVALIDO");
            if (facturaDatos) {
                tRut.requestFocus();
            }
            return false;
        }

        razon = facturaDatosText[2].trim();
        if (razon.length() == 0) {
            JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO RAZON INVALIDO");
            if (facturaDatos) {
                tRazon.requestFocus();
            }
            return false;
        }

        direccion = facturaDatosText[3].trim();
        if (direccion.length() == 0) {
            JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO DIRECCION INVALIDO");
            if (facturaDatos) {
                tDireccion.requestFocus();
            }
            return false;
        }
        comuna = facturaDatosText[4].trim();
        if (comuna.length() == 0) {
            JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO COMUNA INVALIDO");
            if (facturaDatos) {
                tComuna.requestFocus();
            }
            return false;
        }
        ciudad = facturaDatosText[5].trim();
        if (ciudad.length() == 0) {
            JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO CIUDAD INVALIDO");
            if (facturaDatos) {
                tCiudad.requestFocus();
            }
            return false;
        }

        telefono = facturaDatosText[6].trim();
        celular = facturaDatosText[7].trim();
        mail = facturaDatosText[8].trim();

        sii = facturaDatosText[9].trim();
        web = facturaDatosText[10].trim();
        fantasia = facturaDatosText[11].trim();
        giro = facturaDatosText[12].trim();
        if (giro.length() == 0) {
            JOptionPane.showMessageDialog(this, "FACTURACION: CAMPO GIRO INVALIDO");
            if (facturaDatos) {
                Tgiro.requestFocus();
            }
            return false;
        }
        sucursal = facturaDatosText[13].trim();
        return true;
    }

    private boolean valida() {
        try {
            tablaDetalle.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        Date dent = calFechaEntrega.getDate();
        Date dsol = calFechaSolicitud.getDate();
        if (dent.compareTo(dsol) < 0) {
            JOptionPane.showMessageDialog(this, "LA FECHA DE ENTREGA DEBE SER IGUAL O SUPERIOR A LA FECHA DE SOLICITUD");
            return false;
        }

        rut = tRut.getText().replaceAll("\\.", "").trim();
        dv = tDv.getText().trim();
        try {
            if (rut.length() > 0 && (dv.length() == 0 || dv(Integer.parseInt(rut)) != dv.charAt(0))) {
                JOptionPane.showMessageDialog(this, "CAMPO RUT INVALIDO");
                tRut.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "CAMPO RUT INVALIDO");
            tRut.requestFocus();
            return false;
        }

        razon = tRazon.getText().trim();
        direccion = tDireccion.getText().trim();
        comuna = tComuna.getText().trim();
        ciudad = tCiudad.getText().trim();
        telefono = tTelefonos.getText().trim();
        celular = tCelulares.getText().trim();
        mail = tMail.getText().trim();
        sii = tSii.getText().trim();
        web = tWeb.getText().trim();
        fantasia = tFantasia.getText().trim();
        giro = Tgiro.getText().trim();
        sucursal = tSucursal.getText().trim();
        observaciones = tObservaciones.getText().trim();

        boolean haveDesc = false;
        for (int i = 0; i < DB.lineas; i++) {
            Object o = tablaDetalle.getValueAt(i, 0);
            if (o != null && o.toString().trim().length() > 0) {
                haveDesc = true;
                break;
            }
        }
        if (!haveDesc) {
            JOptionPane.showMessageDialog(this, "DEBE INGRESAR UN PRODUCTO");
            return false;
        }

        if (lTotal.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "EL TOTAL NO PUEDE SER CERO");
            return false;
        }
        try {
            Integer rt = rut.length() > 0 ? Integer.parseInt(rut) : null;
            Object[] result = DB.instance.getRutFromRazon.executeFirstQuery(razon);
            if (rt != null && result != null && !result[0].equals(rt)) {//Si el nombre esta asociado a otro rut
                JOptionPane.showMessageDialog(this, razon + " TIENE ASIGNADO EL RUT " + result[0] + ", SOLUCIONE EL CONFLICTO");
                return false;
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void saveOT(int otReq) {

        if (valida()) {

            int totalRead = Integer.parseInt(lTotal.getText().replaceAll("\\.", ""));
            int ivaRead = Integer.parseInt(lIva.getText().replaceAll("\\.", ""));
            int netoRead = Integer.parseInt(lNeto.getText().replaceAll("\\.", ""));

            try {

                Object[] result = DB.instance.getLastTurno2.executeFirstQuery(DB.instance.caja);//Obtiene datos del ultimo turno procesado por esta caja
                dia = (Integer) result[0];//Obtiene el dia 
                turno = (Integer) result[1];//Obtiene el turno
                turno_estado = (Long) result[2];//Obtiene el estado del turno

                if (otReq == -1) {
                    Object[] res = DB.instance.getLastOT.executeFirstQuery();
                    ot = res == null || res[0] == null ? DB.otStart : (int) res[0];
                    if (res != null && res[0] != null) {
                        if (ot < DB.otStart) {
                            ot = DB.otStart;
                        } else {
                            ot = ot + 1;
                        }
                    }
                    long l0 = calFechaSolicitud.getDate().getTime();
                    long l1 = calFechaEntrega.getDate().getTime();
                    DB.instance.insertOT.executeUpdate(ot,
                            rut.length() > 0 ? Integer.parseInt(rut) : null, l0, l1, "P",
                            observaciones, totalRead, netoRead, ivaRead, Main.getUsuario(), DB.instance.caja, dia, turno, turno_estado, razon, direccion, comuna,
                            ciudad, sii, telefono, celular, mail, web, fantasia, giro, sucursal);
                    jLabel2.setText("" + ot);
                } else {
                    DB.instance.updateOT.executeUpdate(
                            //rut=?, fecha_solicitud=?, fecha_entrega=?, estado=?,  observaciones=?, total=?, neto=?, iva=?, usuario=?,  caja=?, dia=?, turno=?, turno_estado=? 
                            rut.length() > 0 ? Integer.parseInt(rut) : null, calFechaSolicitud.getDate().getTime(), calFechaEntrega.getDate().getTime(), "P",
                            observaciones, totalRead, netoRead, ivaRead, Main.getUsuario(), razon, direccion, comuna, ciudad, sii, telefono, celular, mail, web, fantasia, giro, sucursal, ot);
                    DB.instance.delDetalle.executeUpdate(ot);
                }
                DB.instance.auditoria("Genera nueva orden de trabajo ", "OT:" + ot + " para " + razon);
                Integer rt = rut.length() > 0 ? Integer.parseInt(rut) : null;
                if (rt != null) {
                    if (DB.instance.existCliente.executeFirstQuery(rt) == null) {//No existe el cliente
                        DB.instance.insertCliente.executeUpdate(rt, razon, direccion, comuna, ciudad, sii, telefono, celular, mail, web, fantasia, giro, sucursal);
                    } else {//existe
                        DB.instance.updateCliente.executeUpdate(razon, direccion, comuna, ciudad, sii, telefono, celular, mail, web, fantasia, giro, sucursal, rt);
                    }
                }

                for (int i = 0; i < DB.lineas; i++) {
                    Object[] row = ((KTable) tablaDetalle).getRow(i);
                    row[0] = (row[0] != null && row[0].toString().trim().length() == 0) ? null : row[0];
                    if (row[0] != null || row[1] != null || row[2] != null || row[3] != null || row[4] != null) {
                        DB.instance.insertDetalle.executeUpdate(ot, i, row[0], row[1], row[2], row[3], row[4]);
                    }
                }
                updateData(2);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Estado 0 En Blanco, 2 Guardada, 1 Guardada en Edicion
     *
     * @param estado
     */
    public void updateData(int estado) {
        try {
            tablaDetalle.clearSelection();//Limpia la seleccion de la tabla
        } catch (Exception e) {
        }
        try {
            tablaDetalle.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        int cursor = estado == 1 || estado == 2 ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR;
        jLabel24.setCursor(new Cursor(cursor));
        jLabel25.setCursor(new Cursor(cursor));
        if (estado == 0) {//nueva en edicion
            bOtMenu1.setVisible(false);
            jPanel6.setBackground(Main.c1);
            jLabel22.setBackground(Main.c1);
            ot = -1;
            bPagar.setText("PAGAR");
            searchState = 0;
            tRut.setEditable(true);
            tDv.setEditable(true);
            tRazon.setEditable(true);
            tDireccion.setEditable(true);
            tComuna.setEditable(true);
            tCiudad.setEditable(true);
            tTelefonos.setEditable(true);
            tCelulares.setEditable(true);
            tMail.setEditable(true);
            tSii.setEditable(true);
            tWeb.setEditable(true);
            tFantasia.setEditable(true);
            Tgiro.setEditable(true);
            tSucursal.setEditable(true);
            tablaDetalle.setEnabled(true);
            tablaDetalle.setBackground(Color.white);
            tObservaciones.setBackground(Color.white);
            tObservaciones.setEditable(true);
            calFechaEntrega.setVisible(true);
            calFechaSolicitud.setVisible(true);
            bAddRow.setVisible(true);
            bDelRow.setVisible(true);
            jSeparator1.setVisible(true);
            bPrev.setVisible(true);
            bNext.setVisible(false);
            bGuardar.setVisible(true);
            bOtMenu.setVisible(false);

            Date dat = DB.instance.getDate();
            String day = DB.sdf.format(dat);
            jLabel2.setText("");//OT
            jLabel22.setText("NUEVA");//ESTADO
            jLabel23.setText("");//FACTURA
            jLabel24.setText(day);//SOLICITUD
            jLabel25.setText(day);//ENTREGA
            try {
                calFechaEntrega.setDate(dat);
                calFechaSolicitud.setDate(dat);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tRut.setText("");//RUT
            tDv.setText("");//DV
            tRazon.setText("");//RAZON

            tDireccion.setText("");//DIRECCION
            tComuna.setText("");//COMUNA
            tCiudad.setText("");//CIUDAD

            tTelefonos.setText("");//TELEFONO
            tCelulares.setText("");//CELULAR
            tMail.setText("");//MAIL

            tSii.setText("");//SII
            tWeb.setText("");//WEB
            tFantasia.setText("");//FANTASI
            Tgiro.setText("");//GIRO
            tSucursal.setText("");//SUCURSAL

            tObservaciones.setText("");//OBSERVACIONES

            for (int i = 0; i < tablaDetalle.getRowCount(); i++) {
                ((KTable) tablaDetalle).setRow(new Object[]{"", null, null, null, null}, i);
            }
            tablaDetalle.clearSelection();

            bImprimir1.setVisible(false);
            bImprimir.setVisible(false);
            bPagar.setVisible(false);

            lNeto.setText("");
            lIva.setText("");
            lTotal.setText("");

            tRut.requestFocus();

        } else if (estado == 1 || estado == 2) {

            tRut.setEditable(estado == 1);
            tDv.setEditable(estado == 1);
            tRazon.setEditable(estado == 1);
            tDireccion.setEditable(estado == 1);
            tComuna.setEditable(estado == 1);
            tCiudad.setEditable(estado == 1);
            tTelefonos.setEditable(estado == 1);
            tCelulares.setEditable(estado == 1);
            tMail.setEditable(estado == 1);
            tSii.setEditable(estado == 1);
            tWeb.setEditable(estado == 1);
            tFantasia.setEditable(estado == 1);
            Tgiro.setEditable(estado == 1);
            tSucursal.setEditable(estado == 1);
            tablaDetalle.setEnabled(estado == 1);
            tablaDetalle.setBackground(estado == 1 ? Color.white : new Color(242, 242, 242));
            tObservaciones.setBackground(estado == 1 ? Color.white : new Color(242, 242, 242));
            tObservaciones.setEditable(estado == 1);
            calFechaEntrega.setVisible(estado == 1);
            calFechaSolicitud.setVisible(estado == 1);
            bAddRow.setVisible(estado == 1);
            bDelRow.setVisible(estado == 1);
            jSeparator1.setVisible(estado == 1);

            if (estado == 1) {

                jPanel6.setBackground(Main.c2);
                jLabel22.setBackground(Main.c2);
                if (ot == -1) {
                    bOtMenu1.setVisible(false);

                    this.estado = null;
                    jLabel22.setText("NUEVA");//ESTADO EN EDICIÓN
                    jLabel2.setText("");
                    Date dat = DB.instance.getDate();
                    jLabel24.setText(DB.sdf.format(dat));//SOLICITUD
                    jLabel25.setText(DB.sdf.format(dat));//ENTREGA
                    try {
                        calFechaEntrega.setDate(dat);
                        calFechaSolicitud.setDate(dat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    bOtMenu1.setVisible(false);
                    jLabel22.setText("EDICIÓN");//ESTADO EN EDICIÓN
                }
            }

            bPrev.setVisible(true);
            bNext.setVisible(true);
            bGuardar.setVisible(estado == 1 && Main.cajaAbierta);
            bImprimir.setVisible(estado == 2 && Main.cajaAbierta);
            bImprimir1.setVisible(estado == 2 && Main.cajaAbierta);
            bOtMenu.setVisible(estado == 2 && Main.cajaAbierta);
            bPagar.setVisible(estado == 2 && Main.cajaAbierta);

            if (estado == 2) {
                loadOT();
                bPrev.setVisible(bPrev.isVisible() && Main.permisoOt);
                bNext.setVisible(bNext.isVisible() && Main.permisoOt);
                bOtMenu.setVisible(bOtMenu.isVisible() && Main.permisoOt);
                bOtMenu1.setVisible(Main.permisoOt || rut_factura != null);
                bImprimir.setVisible(bImprimir.isVisible() && Main.permisoOt);
                bImprimir1.setVisible(bImprimir1.isVisible() && Main.permisoCaja);
                bPagar.setVisible(bPagar.isVisible() && Main.permisoCaja);
            } else {
                bOtMenu1.setVisible(false);
            }
            jMenuItem6.setText(Main.permisoOt && this.estado != null && "P".equals(this.estado) ? "Guardar" : "OT");
            jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("png/" + (Main.permisoOt && "P".equals(this.estado) ? "save" : "clone2") + ".png")));
            jMenuItem7.setVisible(Main.permisoOt && "P".equals(this.estado));
        }
    }

    private boolean updatePagos() {
        try {
            Long total = (Long) DB.instance.getPagoTotal.executeFirstQuery(ot)[0];
            if ("A".equals(estado)) {
                bPagar.setText("PAGOS");
            } else {
                bPagar.setText((total != null ? ((tot - total == 0) ? ("P".equals(estado) ? "TERMINAR OT Y VER PAGOS" : "VER PAGOS") : ("PAGAR " + Main.formatter.format(tot - total))) : "PAGAR " + Main.formatter.format(tot)));
            }
            return total != null ? (tot - total == 0) : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadOT() {
        try {
            Object[] result;
            if (ot == -1) {
                result = DB.instance.getLastOT.executeFirstQuery();
                if (result == null || result[0] == null) {
                    ot = DB.otStart;
                    return false;
                }
                ot = (Integer) result[0];
            }
            result = DB.instance.selectOT.executeFirstQuery(ot, ot);
            if (result != null) {
                facturaDatos = false;
                facturaDatosText = null;
                otText = null;
                rut_factura = rut = result[22] != null ? result[22].toString() : null;
                bOtMenu1.setBackground(rut_factura != null ? Main.c4 : Main.c2);
                estado = (String) result[0];

                jLabel2.setText("" + ot);//OT
                switch (estado) {
                    case "P":
                        bOtMenu1.setVisible(true && Main.permisoOt);
                        jMenuItem5.setVisible(!facturaDatos);
                        jMenuItem6.setVisible(facturaDatos);
                        jMenuItem7.setVisible(facturaDatosText != null);
                        jLabel22.setText("PENDIENTE");
                        jPanel6.setBackground(Color.cyan);
                        jLabel22.setBackground(Color.cyan);
                        break;
                    case "A":
                        bOtMenu1.setVisible(false);
                        jLabel22.setText("ANULADA");
                        jPanel6.setBackground(Color.orange);
                        jLabel22.setBackground(Color.orange);
                        break;
                    case "T":
                        bOtMenu1.setVisible(false);
                        jLabel22.setText("TERMINADA");
                        jPanel6.setBackground(Color.green);
                        jLabel22.setBackground(Color.green);
                        jLabel23.setText(Main.formatter.format(result[1]));
                        break;
                }
                doc = (Integer) result[1];

                if (doc != null) {
                    if (Main.cajaAbierta) {
                        bImprimir1.setVisible(true);
                    }
                    if (doc < 0) {
                        bImprimir1.setText("IMP BOL");
                        jLabel20.setText("N° BOLETA");
                        jLabel23.setText(Main.formatter.format(-doc));
                    } else {
                        bImprimir1.setText("IMP FAC");
                        jLabel20.setText("N° FACTURA");
                        jLabel23.setText(Main.formatter.format(doc));
                    }
                } else {
                    if (Main.cajaAbierta) {
                        bImprimir1.setVisible(false);
                    }
                    jLabel20.setText("N° -");
                    jLabel23.setText("-");
                }
                Date d2 = new Date((long) result[2]);
                Date d3 = new Date((long) result[3]);

                String fsol = DB.sdf.format(d2);
                String fent = DB.sdf.format(d3);

                jLabel24.setText(fsol);//SOLICITUD
                jLabel25.setText(fent);//ENTREGA

                try {
                    calFechaEntrega.setDate(d3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    calFechaSolicitud.setDate(d2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                neto = (Integer) result[5];
                iva = (Integer) result[6];
                tot = (Integer) result[7];

                lNeto.setText(Main.formatter.format(neto));
                lIva.setText(Main.formatter.format(iva));
                lTotal.setText(Main.formatter.format(tot));

                rut = result[4] != null ? result[4].toString() : "";
                tRut.setText("" + rut);//RUT
                if (rut.length() > 0) {
                    tDv.setText("" + dv((Integer) result[4]));//DV
                } else {
                    tDv.setText("");//DV
                }
                tRazon.setText(result[9].toString());//RAZON

                tDireccion.setText(result[10].toString());//DIRECCION
                tComuna.setText(result[11].toString());//COMUNA
                tCiudad.setText(result[12].toString());//CIUDAD

                tTelefonos.setText(result[14].toString());//TELEFONO
                tCelulares.setText(result[15].toString());//CELULAR
                tMail.setText(result[16].toString());//MAIL

                tSii.setText(result[13].toString());//SII
                tWeb.setText(result[17].toString());//WEB
                tFantasia.setText(result[18].toString());//FANTASI
                Tgiro.setText(result[19].toString());//GIRO
                tSucursal.setText(result[20].toString());//SUCURSAL
                tObservaciones.setText(result[8].toString());//OBSERVACIONES

                for (int i = 0; i < tablaDetalle.getRowCount(); i++) {
                    ((KTable) tablaDetalle).setRow(new Object[]{"", null, null, null, null}, i);
                }
                tablaDetalle.clearSelection();

                List<Object[]> det = DB.instance.selectDetalle.executeQuery(ot);
                for (Object[] row : det) {
                    ((KTable) tablaDetalle).setRow(new Object[]{(String) row[1], (Integer) row[2], (Integer) row[3], (Integer) row[4], (Integer) row[5]}, (Integer) row[0]);
                }
                updatePagos();
                jMenuItem1.setText("A".equals(estado) ? "DES-ANULAR OT" : "ANULAR OT");
                jMenuItem1.setForeground("A".equals(estado) ? Color.red : new Color(51, 51, 51));
                Long pagos = (Long) result[21];
                jMenuItem3.setVisible("P".equals(estado) && pagos == 0);
                updateFacturaDatos();
                return true;
            } else {
                if (searchState == 2 && Main.cajaAbierta) {
                    updateData(0);
                    jPanel6.setBackground(Main.c2);
                    jLabel22.setBackground(Main.c2);
                } else {
                    ot = oldOT;
                    updateFacturaDatos();
                }

                return false;
            }
        } catch (SQLException e) {
            updateFacturaDatos();
            return false;
        }

    }

    private void updateClient(int rut, String razon, String direccion, String comuna, String ciudad, String sii, String telefonos, String celulares, String mail, String web, String fantasia, String giro, String sucursal) {
        tRut.setText("" + rut);
        tDv.setText("" + dv(rut));
        tRazon.setText(razon);

        tDireccion.setText(direccion);//DIRECCION
        tComuna.setText(comuna);//COMUNA
        tCiudad.setText(ciudad);//CIUDAD

        tTelefonos.setText(telefonos);//TELEFONO
        tCelulares.setText(celulares);//CELULAR
        tMail.setText(mail);//MAIL

        tSii.setText(sii);//SII
        tWeb.setText(web);//WEB
        tFantasia.setText(fantasia);//FANTASIA
        Tgiro.setText(giro);//GIRO
        tSucursal.setText(sucursal);//SUCURSAL

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////                                                                      //////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates new form OT
     *
     * @param parent
     * @param modal
     */
    public OrdenTrabajoGui(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTextField16.setDocument(new KPlainDocument(64));
        jTextField17.setDocument(new KPlainDocument(2, "\\d?\\d?"));
        jTextField19.setDocument(new KPlainDocument(64, "toUpper"));
        jDialog1.setIconImage(new ImageIcon("icono.png").getImage());
        KSwingTools.setIntegerField(jTextField1);
        KSwingTools.setIntegerField(jTextField2);
        KSwingTools.setIntegerField(jTextField3);

        for (int i = 0; i < 8; i++) {
            if (i != 5) {
                KCellRenderer render = (KCellRenderer) ((KTable) jTable2).getCellRenderer(0, i);
                //render.setHorizontalAlignment(JLabel.CENTER);
            }
        }

        tRut.setDocument(new KPlainDocument(9, "\\d*"));//RUT
        KSwingTools.setIntegerField(tRut);
        tDv.setDocument(new KPlainDocument(1, "toUpper(\\d|K)?"));//DV
        tRazon.setDocument(new KPlainDocument(128, "toUpper"));//RAZON

        tDireccion.setDocument(new KPlainDocument(128, "toUpper"));//DIRECCION
        tComuna.setDocument(new KPlainDocument(128, "toUpper"));//COMUNA
        tCiudad.setDocument(new KPlainDocument(128, "toUpper"));//CIUDAD

        tTelefonos.setDocument(new KPlainDocument(128, "toUpper"));//TELEFONO
        tCelulares.setDocument(new KPlainDocument(128, "toUpper"));//CELULAR
        tMail.setDocument(new KPlainDocument(128, "toUpper"));//MAIL

        tSii.setDocument(new KPlainDocument(128, "toUpper"));//SII
        tWeb.setDocument(new KPlainDocument(128, "toUpper"));//WEB
        tFantasia.setDocument(new KPlainDocument(128, "toUpper"));//FANTASIA
        Tgiro.setDocument(new KPlainDocument(1024, "toUpper"));//GIRO
        tSucursal.setDocument(new KPlainDocument(1024, "toUpper"));//SUCURSAL

        setIconImage(new ImageIcon("icono.png").getImage());
        calFechaSolicitud = KSwingTools.calendar(jLabel24, DB.sdf, false, DB.timezone);
        calFechaEntrega = KSwingTools.calendar(jLabel25, DB.sdf, false, DB.timezone);
        calFechaDocumento = KSwingTools.calendar(jLabel32, DB.sdf, false, DB.timezone);

        DefaultTableModel dtm = (DefaultTableModel) tablaDetalle.getModel();
        for (int i = 0; i < DB.lineas; i++) {
            dtm.addRow(new Object[]{"", null, null, null, null});
        }
        //Autocomplete tado de productos y precios
        new KAutoComplete((KTable) tablaDetalle, 0) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select producto, precio  from precios where");
                for (String val : values) {
                    sb.append(" lower(producto) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                try {
                    List<Object[]> list = DB.instance.executeQueryList(sb.toString());
                    List<Object[]> top = new LinkedList<>();
                    for (Object[] o : list) {
                        String s = ((String) o[0]).toLowerCase().replaceAll("\\s+", " ");
                        for (int i = 0; i < values.length - 1; i++) {
                            String t = values[i] + " " + values[i + 1];
                            if (s.contains(t)) {
                                top.add(0, o);
                                break;
                            }
                        }
                    }
                    for (Object[] o : top) {
                        list.remove(o);
                        list.add(0, o);
                    }
                    return list;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public void select(String s) {
                try {
                    Object[] result = DB.instance.getPrecio.executeFirstQuery(s);
                    if (result != null) {
                        this.field.setText(s);
                        int row = tablaDetalle.getSelectedRow();
                        tablaDetalle.getModel().setValueAt(s, row, 0);
                        tablaDetalle.getModel().setValueAt(result[0], row, 3);
                        updateRow();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.setBackground(Main.c4);

        //Autocompletado del RUT
        new KAutoComplete(tRut) {

            @Override
            public List<Object[]> find(String text) {
                try {
                    return DB.instance.existCliente.executeQuery(Integer.parseInt(text));
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {

                try {
                    int rut = Integer.parseInt(text);
                    Object[] client = DB.instance.selectClienteRut.executeFirstQuery(rut);
                    updateClient(rut, (String) client[0], (String) client[1], (String) client[2], (String) client[3], (String) client[4], (String) client[5], (String) client[6], (String) client[7], (String) client[8], (String) client[9], (String) client[10], (String) client[11]);
                } catch (NumberFormatException | SQLException e) {
                }

            }

        }.setBackground(Main.c4);

        //Autocompletado de la razon social
        new KAutoComplete(tRazon) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.trim().toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select razon from clientes where");
                for (String val : values) {
                    sb.append(" lower(razon) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                sb.append("order by 1");
                try {
                    return DB.instance.executeQueryList(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {
                try {
                    Object[] client = DB.instance.selectClienteRazon.executeFirstQuery(text);
                    updateClient((Integer) client[0], text, (String) client[1], (String) client[2], (String) client[3], (String) client[4], (String) client[5], (String) client[6], (String) client[7], (String) client[8], (String) client[9], (String) client[10], (String) client[11]);
                } catch (NumberFormatException | SQLException e) {
                }
            }

        }.setBackground(Main.c4);

        //Autocompletado de la Comuna
        new KAutoComplete(tComuna) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.trim().toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select distinct comuna from clientes where");
                for (String val : values) {
                    sb.append(" lower(comuna) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                sb.append("order by 1");
                try {
                    return DB.instance.executeQueryList(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {
            }

        }.setBackground(Main.c4);

        //Autocompletado de la Comuna
        new KAutoComplete(jTextField19) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.trim().toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select distinct banco from pagos where");
                for (String val : values) {
                    sb.append(" lower(banco) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                sb.append("order by 1");
                try {
                    return DB.instance.executeQueryList(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {
            }

        }.setBackground(Main.c4);

        //Autocompletado de la Ciudad
        new KAutoComplete(tCiudad) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.trim().toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select distinct ciudad from clientes where");
                for (String val : values) {
                    sb.append(" lower(ciudad) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                sb.append("order by 1");
                try {
                    return DB.instance.executeQueryList(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {
            }

        }.setBackground(Main.c4);

        //Autocompletado del SII
        new KAutoComplete(tSii) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.trim().toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select distinct sii from clientes where");
                for (String val : values) {
                    sb.append(" lower(sii) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                sb.append("order by 1");
                try {
                    return DB.instance.executeQueryList(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {
            }

        }.setBackground(Main.c4);

        //Autocompletado del Giro
        new KAutoComplete(Tgiro) {

            @Override
            public List<Object[]> find(String text) {
                String[] values = text.trim().toLowerCase().split("\\s+");
                StringBuilder sb = new StringBuilder("select distinct giro from clientes where");
                for (String val : values) {
                    sb.append(" lower(giro) like '%").append(val.toLowerCase()).append("%' and");
                }
                sb.delete(sb.length() - 3, sb.length());
                sb.append("order by 1");
                try {
                    return DB.instance.executeQueryList(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void select(String text) {
            }

        }.setBackground(Main.c4);

        DocumentListener vueltoListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                vuelto();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                vuelto();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                vuelto();
            }

            public void vuelto() {
                try {
                    int pago = Integer.parseInt(jTextField2.getText().replaceAll("\\.", ""));
                    String s = jTextField3.getText();
                    int ingre = s.length() == 0 ? pago : Integer.parseInt(s.replaceAll("\\.", ""));
                    int vuelto = ingre - pago;

                    jLabel40.setText(Main.formatter.format(vuelto));
                } catch (NumberFormatException e) {
                    jLabel40.setText("0");
                }

            }
        };
        jTextField2.getDocument().addDocumentListener(vueltoListener);
        jTextField3.getDocument().addDocumentListener(vueltoListener);

        new KTableCellListener(tablaDetalle, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateRow();
            }
        });

    }

    private void updateRow() {
        int row = tablaDetalle.getSelectedRow();
        neto = 0;
        if (row > -1) {
            Integer cant = (Integer) tablaDetalle.getValueAt(row, 2);
            cant = cant == null ? 1 : cant;
            Integer prec = (Integer) tablaDetalle.getValueAt(row, 3);
            if (prec != null) {
                tablaDetalle.setValueAt(cant * prec, row, 4);
            } else {
                tablaDetalle.setValueAt(null, row, 4);
            }
            for (int i = 0; i < tablaDetalle.getRowCount(); i++) {
                Integer gt = (Integer) tablaDetalle.getValueAt(i, 4);
                if (gt != null) {
                    neto = neto + gt;
                }
            }
        }
        lNeto.setText(Main.formatter.format(neto));
        iva = (int) Math.round(DB.iva * (double) neto);
        lIva.setText(Main.formatter.format(iva));
        tot = neto + iva;
        lTotal.setText(Main.formatter.format(tot));
    }

    private void loadPagos() {
        boolean show = "P".equals(estado);

        jComboBox1.setSelectedIndex(0);
        int total = Integer.parseInt(lTotal.getText().replaceAll("\\.", ""));
        jLabel31.setText(Main.formatter.format(total));
        jTextField2.setText("");
        jTextField3.setText("");

        jLabel36.setVisible(false);
        jTextField16.setVisible(false);
        jTextField17.setVisible(false);
        jLabel38.setVisible(false);
        jTextField19.setVisible(false);
        jTextField16.setText("");
        jTextField17.setText("");
        jTextField19.setText("");
        jTextField2.setText("");
        jLabel43.setText((doc != null ? doc > 0 ? "FACTURA " + Main.formatter.format(doc) : "BOLETA " + Main.formatter.format(-doc) : "") + ("A".equals(estado) ? " ANULADA" : "T".equals(estado) ? " TERMINADA" : ""));

        pagado = 0;
        boolean todosPagosAprobados = true;
        try {
            List<Object[]> list = DB.instance.getPagos.executeQuery(ot);

            KTable kt = (KTable) jTable2;
            while (kt.getRowCount() > 0) {
                kt.removeRow(0);
            }
            int i = 0;
            guardarPago = new boolean[list.size()];
            idPago = new long[list.size()];

            for (Object[] o : list) {
                Long d = (Long) o[0];
                o[0] = DB.sdfh.format(d);
                kt.addRow(o);
                pagado = pagado + (Integer) o[5];
                guardarPago[i] = (Boolean) o[8];
                if (todosPagosAprobados && !guardarPago[i]) {
                    todosPagosAprobados = false;
                }
                idPago[i] = (Long) o[9];
                i++;
            }
            kt.updateUI();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        jPanel5.setVisible(show && tot != pagado);
        jPanel4.setVisible(show && tot != pagado);
        if (show) {
            jButton17.setText(("P".equals(estado) && tot == pagado) ? (!todosPagosAprobados ? "GUARDAR Y TERMINAR OT" : "TERMINAR OT") : "GUARDAR");
        }
        jButton17.setVisible(show);

        jLabel42.setText(Main.formatter.format(total - pagado));
        if (show && tot != pagado) {
            jTextField2.requestFocus();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new JDialog(this, true);
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new KTable(
            new String[]{"Fecha", "Tipo", "Número", "SubPago","Banco", "Monto", "Caja", "Atendido"}, 
            new Class[]{String.class, String.class, String.class, String.class, String.class, Integer.class,  String.class, String.class},
            new boolean[]{false, false, false,false, false, false, false, false, false},
            new int[]{0,0,0,0,0,0,0,0}, new String[]{ null,  null, null, null, null, null, null, null}
        ){

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (guardarPago!=null && guardarPago[row]){
                    c.setBackground(Color.lightGray);
                    c.setForeground(Color.blue);
                } else {
                    c.setBackground(Color.white);
                    c.setForeground(Color.black);
                }
                return c;
            }

        };
        ;
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel43 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton16 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField17 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jDialog2 = new JDialog(this, true);
        jTextField1 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        togleFactura = new javax.swing.JToggleButton();
        togleBoleta = new javax.swing.JToggleButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDetalle = new com.kreadi.swing.KTable(
            new String[]{"PRODUCTO","FOLIO","CANTIDAD","PRECIO","TOTAL"},
            new Class[]{String.class, Integer.class,Integer.class,Integer.class,Integer.class},
            new boolean[]{true, true, true, true, false},
            new int[]{64,10,9,9,9},
            new String[]{"toUpper","\\d*","\\d*","\\d*","\\d*"}   
        );
        jScrollPane2 = new javax.swing.JScrollPane();
        tObservaciones = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tRut = new JTextField(){

            public void requestFocus(){
                super.requestFocus();
            }

        };
        tDv = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tRazon = new javax.swing.JTextField();
        tDireccion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tComuna = new javax.swing.JTextField();
        tCiudad = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tSii = new javax.swing.JTextField();
        tMail = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tCelulares = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        tTelefonos = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tWeb = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        tFantasia = new javax.swing.JTextField();
        Tgiro = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        tSucursal = new javax.swing.JTextField();
        bOtMenu1 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        bNext = new javax.swing.JButton();
        bPrev = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        bAddRow = new javax.swing.JButton();
        bDelRow = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        bGuardar = new javax.swing.JButton();
        bPagar = new javax.swing.JButton();
        bImprimir = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        lNeto = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lIva = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lTotal = new javax.swing.JLabel();
        bImprimir1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        bOtMenu = new javax.swing.JButton();

        jDialog1.setTitle("Pagos");
        jDialog1.setMinimumSize(null);
        jDialog1.setModal(true);
        jDialog1.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog1.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jDialog1.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialog1WindowClosing(evt);
            }
        });

        jScrollPane3.setPreferredSize(new java.awt.Dimension(454, 64));

        jTable2.setFont(Main.defaultFont14);
        jTable2.setRowHeight(24);
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable2MousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable2);

        jLabel30.setFont(Main.defaultFont18);
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("TOTAL");

        jLabel31.setBackground(Main.c1);
        jLabel31.setFont(Main.defaultFont18);
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("0");
        jLabel31.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jLabel31.setOpaque(true);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel43.setFont(Main.defaultFont18);
        jLabel43.setForeground(new java.awt.Color(255, 0, 0));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));

        jButton17.setFont(Main.defaultFont18);
        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/save.png"))); // NOI18N
        jButton17.setText("GUARDAR");
        jButton17.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton17.setFocusable(false);
        jButton17.setMaximumSize(new java.awt.Dimension(128, 26));
        jButton17.setMinimumSize(new java.awt.Dimension(128, 26));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jLabel39.setFont(Main.defaultFont18);
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("VUELTO");

        jLabel40.setBackground(Main.c1);
        jLabel40.setFont(Main.defaultFont18);
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("0");
        jLabel40.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jLabel40.setOpaque(true);

        jLabel35.setFont(Main.defaultFont18);
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("INGRESO");

        jTextField3.setFont(Main.defaultFont18);
        jTextField3.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jLabel34.setFont(Main.defaultFont18);
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("PAGO");

        jTextField2.setFont(Main.defaultFont18);
        jTextField2.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jLabel41.setFont(Main.defaultFont18);
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("SALDO");

        jLabel42.setBackground(Main.c1);
        jLabel42.setFont(Main.defaultFont18);
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("0");
        jLabel42.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel42.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel42.setOpaque(true);
        jLabel42.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel42MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton16.setFont(Main.defaultFont18);
        jButton16.setText("INGRESAR PAGO");
        jButton16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton16.setFocusable(false);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel38.setFont(Main.defaultFont18);
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("BANCO");

        jTextField19.setFont(Main.defaultFont18);
        jTextField19.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jLabel36.setFont(Main.defaultFont18);
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("NÚMERO");

        jTextField16.setFont(Main.defaultFont18);
        jTextField16.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jLabel37.setFont(Main.defaultFont18);
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("TIPO");

        jComboBox1.setFont(Main.defaultFont18);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EFECTIVO", "CHEQUE", "TRANSFERENCIA" }));
        jComboBox1.setFocusable(false);
        jComboBox1.setMinimumSize(new java.awt.Dimension(200, 25));
        jComboBox1.setPreferredSize(new java.awt.Dimension(200, 25));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jTextField17.setFont(Main.defaultFont18);
        jTextField17.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField17.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jLabel44.setFont(Main.defaultFont18);
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 141, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jTextField16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator4)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenuItem2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem2.setFont(Main.defaultFont18);
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/clonar.png"))); // NOI18N
        jMenuItem2.setText("Clonar OT");
        jMenuItem2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem2.setMinimumSize(new java.awt.Dimension(0, 28));
        jMenuItem2.setPreferredSize(new java.awt.Dimension(164, 32));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem1.setFont(Main.defaultFont18);
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/anular.png"))); // NOI18N
        jMenuItem1.setText("Anular OT");
        jMenuItem1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem1.setMinimumSize(new java.awt.Dimension(0, 28));
        jMenuItem1.setPreferredSize(new java.awt.Dimension(164, 32));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem3.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem3.setFont(Main.defaultFont18);
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/right.png"))); // NOI18N
        jMenuItem3.setText("EDITAR OT");
        jMenuItem3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem3.setMinimumSize(new java.awt.Dimension(0, 28));
        jMenuItem3.setPreferredSize(new java.awt.Dimension(164, 32));
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);

        jMenuItem4.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem4.setFont(Main.defaultFont14);
        jMenuItem4.setText("Eliminar Pago");
        jMenuItem4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem4.setMinimumSize(new java.awt.Dimension(0, 28));
        jMenuItem4.setPreferredSize(new java.awt.Dimension(164, 32));
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem4);

        jDialog2.setTitle(" GENERAR FACTURA O BOLETA");
        jDialog2.setModal(true);
        jDialog2.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog2.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jDialog2.setResizable(false);
        jDialog2.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                jDialog2WindowActivated(evt);
            }
        });

        jTextField1.setFont(Main.defaultFont18);
        jTextField1.setMargin(new java.awt.Insets(4, 4, 4, 4));

        jButton3.setFont(Main.defaultFont18);
        jButton3.setText("GENERAR");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        togleFactura.setBackground(new java.awt.Color(255, 255, 255));
        togleFactura.setFont(Main.defaultFont18);
        togleFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/factura.png"))); // NOI18N
        togleFactura.setText("FACTURA");
        togleFactura.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        togleFactura.setFocusable(false);
        togleFactura.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togleFactura.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togleFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togleFacturaActionPerformed(evt);
            }
        });

        togleBoleta.setBackground(new java.awt.Color(255, 255, 255));
        togleBoleta.setFont(Main.defaultFont18);
        togleBoleta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/boleta.png"))); // NOI18N
        togleBoleta.setText("BOLETA");
        togleBoleta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        togleBoleta.setFocusable(false);
        togleBoleta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togleBoleta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togleBoleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togleBoletaActionPerformed(evt);
            }
        });

        jLabel7.setFont(Main.defaultFont14);
        jLabel7.setText("Número");

        jLabel29.setFont(Main.defaultFont14);
        jLabel29.setText("Fecha ");

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(togleBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(togleFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(togleBoleta)
                    .addComponent(togleFactura))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBox1.setText("jCheckBox1");

        jMenuItem5.setBackground(Main.c4);
        jMenuItem5.setFont(Main.defaultFont14);
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/clone2.png"))); // NOI18N
        jMenuItem5.setText("Facturación");
        jMenuItem5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem5.setMinimumSize(new java.awt.Dimension(0, 26));
        jMenuItem5.setPreferredSize(new java.awt.Dimension(140, 26));
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem5);

        jMenuItem6.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem6.setFont(Main.defaultFont14);
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/save.png"))); // NOI18N
        jMenuItem6.setText("Guardar");
        jMenuItem6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem6.setMinimumSize(new java.awt.Dimension(0, 26));
        jMenuItem6.setPreferredSize(new java.awt.Dimension(140, 26));
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem6);

        jMenuItem7.setBackground(new java.awt.Color(255, 255, 204));
        jMenuItem7.setFont(Main.defaultFont14);
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/remove.png"))); // NOI18N
        jMenuItem7.setText("Borrar");
        jMenuItem7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem7.setMinimumSize(new java.awt.Dimension(0, 26));
        jMenuItem7.setPreferredSize(new java.awt.Dimension(140, 26));
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem7);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Orden de Trabajo");
        setMinimumSize(new java.awt.Dimension(640, 480));

        jLabel1.setFont(Main.defaultFont18);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("N°");

        jLabel2.setBackground(Main.c1);
        jLabel2.setFont(Main.defaultFont14);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setToolTipText("DobleClick para cambiar número");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.setOpaque(true);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
        });

        jLabel4.setFont(Main.defaultFont18);
        jLabel4.setText("FEC ENT");

        jLabel5.setFont(Main.defaultFont18);
        jLabel5.setText("FEC  SOL");

        jLabel6.setFont(Main.defaultFont18);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("ESTADO ");

        jLabel20.setFont(Main.defaultFont18);
        jLabel20.setText("N° -");

        jLabel22.setBackground(Main.c1);
        jLabel22.setFont(Main.defaultFont14);
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel22.setOpaque(true);

        jLabel23.setBackground(Main.c1);
        jLabel23.setFont(Main.defaultFont14);
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel23.setOpaque(true);

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(Main.defaultFont14);
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel24.setOpaque(true);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(Main.defaultFont14);
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel25.setOpaque(true);

        jTabbedPane1.setFocusable(false);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        tablaDetalle.setFont(Main.defaultFont14);
        tablaDetalle.setRowHeight(22);
        tablaDetalle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaDetalle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaDetalleKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaDetalle);

        jTabbedPane1.addTab("DETALLE", jScrollPane1);

        tObservaciones.setColumns(20);
        tObservaciones.setFont(Main.defaultFont14);
        tObservaciones.setRows(3);
        tObservaciones.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(tObservaciones);

        jTabbedPane1.addTab("OBSERVACIONES", jScrollPane2);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setToolTipText("");

        jLabel8.setFont(Main.defaultFont14);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("RUT");

        tRut.setFont(Main.defaultFont14);
        tRut.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tRut.setMargin(new java.awt.Insets(0, 4, 0, 4));

        tDv.setFont(Main.defaultFont14);
        tDv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tDv.setText(" ");
        tDv.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel3.setFont(Main.defaultFont14);
        jLabel3.setText("RAZON");

        tRazon.setFont(Main.defaultFont14);
        tRazon.setMargin(new java.awt.Insets(0, 4, 0, 4));

        tDireccion.setFont(Main.defaultFont14);
        tDireccion.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel9.setFont(Main.defaultFont14);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("DIRECCIÓN");

        jLabel10.setFont(Main.defaultFont14);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("COMUNA");

        tComuna.setFont(Main.defaultFont14);
        tComuna.setMargin(new java.awt.Insets(0, 4, 0, 4));

        tCiudad.setFont(Main.defaultFont14);
        tCiudad.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel11.setFont(Main.defaultFont14);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("CIUDAD");

        jLabel16.setFont(Main.defaultFont14);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("SII");

        tSii.setFont(Main.defaultFont14);
        tSii.setMargin(new java.awt.Insets(0, 4, 0, 4));

        tMail.setFont(Main.defaultFont14);
        tMail.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel14.setFont(Main.defaultFont14);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("MAIL");

        tCelulares.setFont(Main.defaultFont14);
        tCelulares.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel13.setFont(Main.defaultFont14);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("CELULARES");

        tTelefonos.setFont(Main.defaultFont14);
        tTelefonos.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel12.setFont(Main.defaultFont14);
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("TELÉFONOS");

        jLabel15.setFont(Main.defaultFont14);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("WEB");

        tWeb.setFont(Main.defaultFont14);
        tWeb.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel17.setFont(Main.defaultFont14);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("NOMBRE FANTASIA");

        tFantasia.setFont(Main.defaultFont14);
        tFantasia.setMargin(new java.awt.Insets(0, 4, 0, 4));

        Tgiro.setFont(Main.defaultFont14);
        Tgiro.setMargin(new java.awt.Insets(0, 4, 0, 4));

        jLabel18.setFont(Main.defaultFont14);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("GIRO");

        jLabel21.setFont(Main.defaultFont14);
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("SUCURSAL");

        tSucursal.setFont(Main.defaultFont14);
        tSucursal.setMargin(new java.awt.Insets(0, 4, 0, 4));

        bOtMenu1.setFont(Main.defaultFont14);
        bOtMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/set.png"))); // NOI18N
        bOtMenu1.setToolTipText("Editar Datos de Facturación");
        bOtMenu1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bOtMenu1.setComponentPopupMenu(jPopupMenu1);
        bOtMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bOtMenu1.setFocusable(false);
        bOtMenu1.setMaximumSize(new java.awt.Dimension(10, 18));
        bOtMenu1.setMinimumSize(new java.awt.Dimension(10, 18));
        bOtMenu1.setPreferredSize(new java.awt.Dimension(10, 18));
        bOtMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOtMenu1ActionPerformed(evt);
            }
        });

        jLabel27.setText("  ");
        jLabel27.setPreferredSize(new java.awt.Dimension(8, 16));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tWeb, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tFantasia))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tTelefonos, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tComuna, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tCelulares)
                            .addComponent(tCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tSii, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                            .addComponent(tMail)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tRut, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tDv, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tRazon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bOtMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tDireccion)
                    .addComponent(Tgiro)
                    .addComponent(tSucursal))
                .addGap(0, 0, 0)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tRut, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tDv, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tRazon, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bOtMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tComuna, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(tCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(tSii, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tTelefonos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(jLabel13))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tCelulares, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(tMail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(tWeb, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(tFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(Tgiro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap())
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/right.png"))); // NOI18N
        bNext.setToolTipText("Orden Siguiente");
        bNext.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bNext.setFocusable(false);
        bNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNextActionPerformed(evt);
            }
        });

        bPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/left.png"))); // NOI18N
        bPrev.setToolTipText("Orden Anterior");
        bPrev.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bPrev.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bPrev.setFocusable(false);
        bPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPrevActionPerformed(evt);
            }
        });

        bAddRow.setFont(Main.defaultFont18);
        bAddRow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/addrow.png"))); // NOI18N
        bAddRow.setToolTipText("Insertar Linea");
        bAddRow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bAddRow.setFocusable(false);
        bAddRow.setMaximumSize(new java.awt.Dimension(32, 32));
        bAddRow.setMinimumSize(new java.awt.Dimension(32, 32));
        bAddRow.setPreferredSize(new java.awt.Dimension(32, 32));
        bAddRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddRowActionPerformed(evt);
            }
        });

        bDelRow.setFont(Main.defaultFont18);
        bDelRow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/delrow.png"))); // NOI18N
        bDelRow.setToolTipText("Eliminar Linea");
        bDelRow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bDelRow.setFocusable(false);
        bDelRow.setMaximumSize(new java.awt.Dimension(32, 32));
        bDelRow.setMinimumSize(new java.awt.Dimension(32, 32));
        bDelRow.setPreferredSize(new java.awt.Dimension(32, 32));
        bDelRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDelRowActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        bGuardar.setBackground(Main.c4);
        bGuardar.setFont(Main.defaultFont18);
        bGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/save.png"))); // NOI18N
        bGuardar.setText("GUARDAR");
        bGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bGuardar.setFocusable(false);
        bGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bGuardarActionPerformed(evt);
            }
        });

        bPagar.setBackground(Main.c4);
        bPagar.setFont(Main.defaultFont18);
        bPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/pagar.png"))); // NOI18N
        bPagar.setText("PAGOS");
        bPagar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bPagar.setFocusable(false);
        bPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPagarActionPerformed(evt);
            }
        });

        bImprimir.setBackground(Main.c4);
        bImprimir.setFont(Main.defaultFont18);
        bImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/print.png"))); // NOI18N
        bImprimir.setText("IMP OT");
        bImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bImprimir.setFocusable(false);
        bImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bImprimirActionPerformed(evt);
            }
        });

        jLabel28.setFont(Main.defaultFont18);
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("NETO");

        lNeto.setBackground(Main.c1);
        lNeto.setFont(Main.defaultFont18);
        lNeto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lNeto.setOpaque(true);

        jLabel26.setFont(Main.defaultFont18);
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("IVA");

        lIva.setBackground(Main.c1);
        lIva.setFont(Main.defaultFont18);
        lIva.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lIva.setOpaque(true);

        jLabel19.setFont(Main.defaultFont18);
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("TOTAL");

        lTotal.setBackground(Main.c1);
        lTotal.setFont(Main.defaultFont18);
        lTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lTotal.setOpaque(true);

        bImprimir1.setBackground(Main.c4);
        bImprimir1.setFont(Main.defaultFont18);
        bImprimir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/print.png"))); // NOI18N
        bImprimir1.setText("IMP DOC");
        bImprimir1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bImprimir1.setFocusable(false);
        bImprimir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bImprimir1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bAddRow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bDelRow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(bGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bPagar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bImprimir1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lNeto, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lIva, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lIva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lNeto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bAddRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bDelRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(bImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(bPagar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bImprimir1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel6.setPreferredSize(new java.awt.Dimension(0, 6));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        bOtMenu.setFont(Main.defaultFont14);
        bOtMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/set.png"))); // NOI18N
        bOtMenu.setToolTipText("Menú OT");
        bOtMenu.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bOtMenu.setComponentPopupMenu(jPopupMenu1);
        bOtMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bOtMenu.setFocusable(false);
        bOtMenu.setMaximumSize(new java.awt.Dimension(10, 18));
        bOtMenu.setMinimumSize(new java.awt.Dimension(10, 18));
        bOtMenu.setPreferredSize(new java.awt.Dimension(10, 18));
        bOtMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOtMenuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(bPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(bNext, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(bOtMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bPrev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bOtMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPagarActionPerformed
        open2 = true;
        try {
            DB.instance.removeNoAprovPago.executeUpdate(ot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPagos();
        jDialog1.setVisible(true);
    }//GEN-LAST:event_bPagarActionPerformed

    private void bGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGuardarActionPerformed
        saveOT(ot);
    }//GEN-LAST:event_bGuardarActionPerformed

    private void bAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddRowActionPerformed
        int row = tablaDetalle.getSelectedRow();
        if (row != -1) {
            try {
                tablaDetalle.getCellEditor().stopCellEditing();
            } catch (Exception e) {
            }
            for (int i = DB.lineas - 1; i > row; i--) {
                for (int j = 0; j < tablaDetalle.getColumnCount(); j++) {
                    tablaDetalle.setValueAt(tablaDetalle.getValueAt(i - 1, j), i, j);
                }
            }
            ((KTable) tablaDetalle).setRow(new Object[]{"", null, null, null, null}, row);
            updateRow();
        }
    }//GEN-LAST:event_bAddRowActionPerformed

    private void bDelRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDelRowActionPerformed
        int row = tablaDetalle.getSelectedRow();
        if (row != -1) {
            try {
                tablaDetalle.getCellEditor().stopCellEditing();
            } catch (Exception e) {
            }
            for (int i = row; i < DB.lineas - 1; i++) {
                for (int j = 0; j < tablaDetalle.getColumnCount(); j++) {
                    tablaDetalle.setValueAt(tablaDetalle.getValueAt(i + 1, j), i, j);
                }
            }
            ((KTable) tablaDetalle).setRow(new Object[]{"", null, null, null, null}, DB.lineas - 1);
            updateRow();
        }
    }//GEN-LAST:event_bDelRowActionPerformed

    private void tablaDetalleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaDetalleKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setVisible(false);
        }
    }//GEN-LAST:event_tablaDetalleKeyPressed

    private void bPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPrevActionPerformed
        oldOT = ot;
        searchState = 1;
        if (ot == -1) {
            try {
                Object[] result = DB.instance.getLastOT.executeFirstQuery();
                if (result != null && result[0] != null) {
                    ot = (Integer) result[0];
                } else {
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Object[] result = DB.instance.getPrevOT.executeFirstQuery(ot);
                if (result != null && result[0] != null) {
                    ot = (Integer) result[0];
                } else {
                    return;
                }
            } catch (Exception e) {
            }
        }
        updateData(2);
    }//GEN-LAST:event_bPrevActionPerformed

    private void bNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNextActionPerformed
        oldOT = ot;
        searchState = 2;

        try {
            Object[] result = DB.instance.getNextOT.executeFirstQuery(ot);
            if (result != null && result[0] != null) {
                ot = (Integer) result[0];
            } else {
                ot++;
            }
        } catch (Exception e) {
        }

        updateData(2);
    }//GEN-LAST:event_bNextActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        boolean detVisible = jTabbedPane1.getSelectedIndex() == 0;
        bAddRow.setEnabled(detVisible);
        bDelRow.setEnabled(detVisible);
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        try {
            Object[] result = DB.instance.getLastTurno2.executeFirstQuery(DB.instance.caja);//Obtiene datos del turno
            dia = (Integer) result[0];//Obtiene el dia 
            turno = (Integer) result[1];//Obtiene el turno
            turno_estado = (Long) result[2];//Obtiene el estado del turno

            String s = jLabel42.getText();
            int saldo = s.length() == 0 ? 0 : Integer.parseInt(s.replaceAll("\\.", ""));
            s = jTextField2.getText();
            int pago = s.length() == 0 ? 0 : Integer.parseInt(s.replaceAll("\\.", ""));
            s = jTextField3.getText();
            int ingre = s.length() == 0 ? pago : Integer.parseInt(s.replaceAll("\\.", ""));
            int vuelto = ingre - pago;

            String tipo = jComboBox1.getSelectedItem().toString();
            boolean efectivo = "EFECTIVO".equals(tipo);
            String numero = jTextField16.getText().trim();
            String subpago = jTextField17.getText().trim();
            String banco = jTextField19.getText().trim();
            numero = numero.length() == 0 ? null : numero;
            banco = banco.length() == 0 ? null : banco;

            if (vuelto < 0) {
                jLabel43.setText("ERROR: PAGO MAYOR QUE INGRESO");
                return;
            } else if (pago <= 0) {
                jLabel43.setText("ERROR: PAGO CERO");
                return;
            } else if (pago > saldo) {
                jLabel43.setText("ERROR: PAGO MAYOR QUE SALDO");
                return;
            } else if (!efectivo && (numero == null || banco == null)) {
                jLabel43.setText("ERROR: DEBE INGRESAR EL NUMERO Y EL BANCO");
                return;
            } else {
                jLabel43.setText("");
            }
            if (!efectivo) {
                Object[] exist = DB.instance.existDocumentoBancario.executeFirstQuery(tipo, numero, subpago, banco);
                if (exist != null) {
                    jLabel43.setText("ERROR: DOCUMENTO BANCARIO EN OT " + Main.formatter.format(exist[0]));
                    return;
                }
            }

            DB.instance.insertPago.executeUpdate(ot, tipo, numero, subpago, banco, pago, DB.instance.caja, dia, turno, Main.getUsuario(), turno_estado, false);

            loadPagos();

        } catch (SQLException e) {
            jDialog1.setVisible(false);
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR al guardar pago", JOptionPane.ERROR_MESSAGE);
            jDialog1.setVisible(false);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            jLabel40.setText("0");
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void bOtMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOtMenuActionPerformed
        jPopupMenu1.show(bOtMenu, 0, 0);
    }//GEN-LAST:event_bOtMenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        boolean anulado = "A".equals(estado);
        if (JOptionPane.showConfirmDialog(this, (anulado ? "Desea des-anular orden de compra " : "Desea anular orden de compra ") + ot, "Anular", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                if (anulado) {
                    if (doc != null) {
                        DB.instance.setOTESTADO.executeUpdate("T", ot);
                    } else {
                        DB.instance.setOTESTADO.executeUpdate("P", ot);
                    }
                    DB.instance.auditoria("DES-Anula orden de compra ", "OT:" + ot + " " + (doc == null ? "" : ("-> " + Main.formatter.format(Math.abs(doc)))));
                } else {
                    DB.instance.setOTESTADO.executeUpdate("A", ot);
                    DB.instance.auditoria("Anula orden de compra ", "OT:" + ot + " " + (doc == null ? "" : ("-> " + Main.formatter.format(Math.abs(doc)))));
                }
                updateData(2);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        ot = -1;
        updateData(1);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jLabel42MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel42MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            jTextField2.setText(jLabel42.getText().replaceAll("\\.", ""));
            jTextField2.requestFocus();
        }
    }//GEN-LAST:event_jLabel42MouseClicked

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged

    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        boolean is = jComboBox1.getSelectedIndex() > 0;
        jLabel36.setVisible(is);
        jTextField16.setVisible(is);
        jTextField17.setVisible(is);
        jLabel38.setVisible(is);
        jTextField19.setVisible(is);
        if (is) {
            jTextField16.requestFocus();
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked

    }//GEN-LAST:event_jTable2MouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        int idx = jTable2.getSelectedRow();
        if (idx != -1) {
            try {
                DB.instance.delPago.executeUpdate(idPago[idx]);
                DB.instance.auditoria("Elimina pago de orden de trabajo ", "OT:" + ot + " " + jTable2.getValueAt(idx, 1)
                        + " " + jTable2.getValueAt(idx, 2) + " " + jTable2.getValueAt(idx, 3) + " " + jTable2.getValueAt(idx, 4)
                        + " " + jTable2.getValueAt(idx, 5) + " " + jTable2.getValueAt(idx, 6) + " " + jTable2.getValueAt(idx, 7)
                );
                loadPagos();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        try {
            List<Object[]> list = DB.instance.aprovPagoPrev.executeQuery(ot);
            if (list.size() > 0 || pagado == tot) {
                for (Object[] o : list) {
                    String tipo = (String) o[1];
                    String numero = (String) o[2];
                    String subpago = (String) o[3];
                    String banco = (String) o[4];
                    DB.instance.auditoria("Recibe pago orden de trabajo ", "OT:" + ot + " $" + Main.formatter.format(o[5]) + " Tipo:" + tipo + (!"EFECTIVO".equals(tipo) ? (" Nº:" + numero + " sub:" + subpago + " banco:" + banco) : ""));
                }
                DB.instance.aprovPago.executeUpdate(ot);
                loadPagos();
                if (pagado == tot) {
                    Dimension dim = new Dimension(306, 276);
                    jDialog2.setPreferredSize(dim);
                    jDialog2.setSize(dim);
                    jDialog2.setLocationRelativeTo(jDialog1);
                    jTextField1.setText("");
                    jDialog1.setVisible(false);
                    jDialog2.setVisible(true);
                } else {
                    jDialog1.setVisible(false);
                }
                loadOT();
            } else if (pagado != tot) {
                jDialog1.setVisible(false);
                JOptionPane.showMessageDialog(this, "Debe ingresar un pago primero");
                jDialog1.setVisible(true);
            }
        } catch (SQLException | HeadlessException e) {
            e.printStackTrace();
        }
        togleFactura.setSelected(false);
        togleBoleta.setSelected(false);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jTable2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MousePressed
        Point p = evt.getPoint();
        int idx = jTable2.rowAtPoint(p);
        if (idx != -1) {
            jTable2.getSelectionModel().setSelectionInterval(idx, idx);
        }
        if (evt.getButton() == MouseEvent.BUTTON3) {
            idx = jTable2.getSelectedRow();
            if (idx != -1 && guardarPago != null && !guardarPago[idx]) {
                jPopupMenu2.show(jTable2, evt.getX(), evt.getY());
            }
        }

    }//GEN-LAST:event_jTable2MousePressed

    private void jDialog1WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog1WindowClosing
        try {
            DB.instance.removeNoAprovPago.executeUpdate(ot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadOT();
    }//GEN-LAST:event_jDialog1WindowClosing

    private void togleFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togleFacturaActionPerformed
        togleFactura.setSelected(true);
        togleBoleta.setSelected(false);
        if (DB.facturaAuto) {
            try {
                Integer next = (Integer) DB.instance.nextFactura.executeFirstQuery()[0];
                if (next == null || next < DB.facturaStart) {
                    next = DB.facturaStart - 1;
                }
                jTextField1.setText("" + (next + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            jTextField1.setText("");
        }
        jTextField1.requestFocus();
    }//GEN-LAST:event_togleFacturaActionPerformed

    private void togleBoletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togleBoletaActionPerformed
        togleFactura.setSelected(false);
        togleBoleta.setSelected(true);
        if (DB.boletaAuto) {
            try {
                Integer next = (Integer) DB.instance.nextBoleta.executeFirstQuery()[0];
                if (next == null || next < DB.boletaStart) {
                    next = DB.boletaStart - 1;
                }
                jTextField1.setText("" + (next + 1));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            jTextField1.setText("");
        }
        jTextField1.requestFocus();
    }//GEN-LAST:event_togleBoletaActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        boolean boleta = togleBoleta.isSelected();
        boolean factura = togleFactura.isSelected();
        if (boleta || factura) {
            try {
                doc = Integer.parseInt(jTextField1.getText().replaceAll("\\.", ""));
                if (boleta) {
                    doc = doc * -1;
                }
                Object[] res = DB.instance.existDocumento.executeFirstQuery(doc);
                if (res == null) {
                    DB.instance.setDocumento.executeUpdate(doc, calFechaDocumento.getDate().getTime(), ot);
                    DB.instance.setOTESTADO.executeUpdate("T", ot);
                    DB.instance.auditoria("Genera " + (boleta ? "boleta" : "factura"), "OT:" + ot + " " + (doc == null ? "" : "-> " + Main.formatter.format(Math.abs(doc))));
                    jDialog2.setVisible(false);
                    jDialog1.setVisible(false);
                    loadOT();
                } else {
                    JOptionPane.showMessageDialog(jDialog2, "Documento ya usado en ot " + Main.formatter.format(res[0]));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(jDialog2, "Ingrese un número");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(jDialog2, "Seleccione Factura o Boleta");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        updateData(1);
        DB.instance.auditoria("Edita orden de compra ", "OT:" + ot + " ");
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MousePressed
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            String resp = JOptionPane.showInputDialog(this, "Numero de OT?");
            try {
                Object[] exist = DB.instance.existeOT.executeFirstQuery(Integer.parseInt(resp));
                if (exist != null) {
                    ot = Integer.parseInt(resp);
                    updateData(2);
                } else {
                    JOptionPane.showMessageDialog(this, "No existe la OT " + resp, "NO EXISTE", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException | SQLException e) {
            }
        }
    }//GEN-LAST:event_jLabel2MousePressed

    private void bImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bImprimirActionPerformed
        try {
            Object[] result = DB.instance.selectCaja.executeFirstQuery(DB.instance.caja);
            String val = (String) result[2];
            val = val == null ? "" : val;
            String imp[] = val.trim().split("\\s+");
            if (imp.length != 2) {
                JOptionPane.showMessageDialog(this, "ERROR EN IMPRESION DE OT:\nDebe configurar impresora y formato");
            } else {
                result = DB.instance.getFormato.executeFirstQuery(imp[1]);
                if (result != null) {
                    String clob = (String) result[4];
                    print(imp[0], clob, (String) result[2], (String) result[3]);
                } else {
                    JOptionPane.showMessageDialog(this, "ERROR EN IMPRESION DE OT:\nDebe configurar impresora y formato");
                }
            }
        } catch (SQLException | HeadlessException e) {
            e.printStackTrace();
        }
        DB.instance.auditoria("Imprime orden de compra ", "OT:" + ot + " " + (doc == null ? "" : "-> " + Main.formatter.format(Math.abs(doc))));
    }//GEN-LAST:event_bImprimirActionPerformed

    private void bImprimir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bImprimir1ActionPerformed
        try {
            
            Object[] result = DB.instance.selectCaja.executeFirstQuery(DB.instance.caja);
            String val = (String) result[bImprimir1.getText().endsWith("BOL") ? 1 : 0];
            val = val == null ? "" : val;
            String imp[] = val.trim().split("\\s+");
            if (imp.length != 2) {
                JOptionPane.showMessageDialog(this, "ERROR EN IMPRESION DE " + (bImprimir1.getText().endsWith("BOL") ? "BOLETA" : "FACTURA") + ":\nDebe configurar impresora y formato");
            } else {
                result = DB.instance.getFormato.executeFirstQuery(imp[1]);
                if (result != null) {
                    String clob = (String) result[4];
                    if (rut_factura != null) {
                        showFacturacion(false);
                    }
                    print(imp[0], clob, (String) result[2], (String) result[3]);
                } else {
                    JOptionPane.showMessageDialog(this, "ERROR EN IMPRESION DE " + (bImprimir1.getText().endsWith("BOL") ? "BOLETA" : "FACTURA") + ":\nDebe configurar impresora y formato");
                }
            }

        } catch (SQLException | HeadlessException e) {
            e.printStackTrace();
        } 
        DB.instance.auditoria("Imprime orden de compra ", "OT:" + ot + " " + (doc == null ? "" : "-> " + Main.formatter.format(Math.abs(doc))));
    }//GEN-LAST:event_bImprimir1ActionPerformed

    private void bOtMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOtMenu1ActionPerformed
        jPopupMenu3.show(bOtMenu1, -60, 0);
    }//GEN-LAST:event_bOtMenu1ActionPerformed

    public void updateFacturaDatos() {
        jMenuItem5.setVisible(!facturaDatos);
        jMenuItem6.setVisible(facturaDatos);
        if (facturaDatos) {
            if (Main.permisoOt && editFact && "P".equals(estado)) {
                tRut.setEditable(true);//RUT
                tDv.setEditable(true);//DV
                tRazon.setEditable(true);//RAZON
                tDireccion.setEditable(true);//DIRECCION
                tComuna.setEditable(true);//COMUNA
                tCiudad.setEditable(true);//CIUDAD
                tTelefonos.setEditable(true);//TELEFONO
                tCelulares.setEditable(true);//CELULAR
                tMail.setEditable(true);//MAIL
                tSii.setEditable(true);//SII
                tWeb.setEditable(true);//WEB
                tFantasia.setEditable(true);//FANTASI
                Tgiro.setEditable(true);//GIRO
                tSucursal.setEditable(true);//SUCURSAL
            }
            jPanel1.setBackground(Main.c4);
            jLabel8.setText("RUT FAC");
            if (facturaDatosText != null) {
                tRut.setText(facturaDatosText[0]);//RUT
                tDv.setText(facturaDatosText[1]);//DV

                tRazon.setText(facturaDatosText[2]);//RAZON

                tDireccion.setText(facturaDatosText[3]);//DIRECCION
                tComuna.setText(facturaDatosText[4]);//COMUNA
                tCiudad.setText(facturaDatosText[5]);//CIUDAD

                tTelefonos.setText(facturaDatosText[6]);//TELEFONO
                tCelulares.setText(facturaDatosText[7]);//CELULAR
                tMail.setText(facturaDatosText[8]);//MAIL

                tSii.setText(facturaDatosText[9]);//SII
                tWeb.setText(facturaDatosText[10]);//WEB
                tFantasia.setText(facturaDatosText[11]);//FANTASI
                Tgiro.setText(facturaDatosText[12]);//GIRO
                tSucursal.setText(facturaDatosText[13]);//SUCURSAL
            }
        } else {
            jPanel1.setBackground(Main.c2);
            jLabel8.setText("RUT");
            tRut.setEditable(false);//RUT
            tDv.setEditable(false);//DV
            tRazon.setEditable(false);//RAZON
            tDireccion.setEditable(false);//DIRECCION
            tComuna.setEditable(false);//COMUNA
            tCiudad.setEditable(false);//CIUDAD
            tTelefonos.setEditable(false);//TELEFONO
            tCelulares.setEditable(false);//CELULAR
            tMail.setEditable(false);//MAIL
            tSii.setEditable(false);//SII
            tWeb.setEditable(false);//WEB
            tFantasia.setEditable(false);//FANTASI
            Tgiro.setEditable(false);//GIRO
            tSucursal.setEditable(false);//SUCURSAL     
            if (otText != null) {
                tRut.setText(otText[0]);//RUT
                tDv.setText(otText[1]);//DV

                tRazon.setText(otText[2]);//RAZON

                tDireccion.setText(otText[3]);//DIRECCION
                tComuna.setText(otText[4]);//COMUNA
                tCiudad.setText(otText[5]);//CIUDAD

                tTelefonos.setText(otText[6]);//TELEFONO
                tCelulares.setText(otText[7]);//CELULAR
                tMail.setText(otText[8]);//MAIL

                tSii.setText(otText[9]);//SII
                tWeb.setText(otText[10]);//WEB
                tFantasia.setText(otText[11]);//FANTASI
                Tgiro.setText(otText[12]);//GIRO
                tSucursal.setText(otText[13]);//SUCURSAL
            }
        }

    }

    private boolean editFact = false;

    private void showFacturacion(boolean edit) {
        editFact = edit;
        facturaDatos = true;
        otText = new String[]{
            tRut.getText(), tDv.getText(), tRazon.getText(), tDireccion.getText(),
            tComuna.getText(), tCiudad.getText(), tTelefonos.getText(), tCelulares.getText(),
            tMail.getText(), tSii.getText(), tWeb.getText(), tFantasia.getText(), Tgiro.getText(), tSucursal.getText()
        };
        if (facturaDatosText == null) {
            try {
                Integer rt = (Integer) DB.instance.getRutFacturacion.executeFirstQuery(ot)[0];
                if (rt == null) {
                    facturaDatosText = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", ""};
                } else {
                    Object[] res = DB.instance.getCliente.executeFirstQuery(rt);
                    facturaDatosText
                            = new String[]{"" + rt, "" + dv(rt), "" + res[0], "" + res[1], "" + res[2], "" + res[3], "" + res[4], "" + res[5], "" + res[6],
                                "" + res[7], "" + res[8], "" + res[9], "" + res[10], "" + res[11]};
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR AL CARGAR DATOS DE FACTURACION", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        updateFacturaDatos();
    }

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        showFacturacion(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        if (!Main.permisoOt && "P".equals(estado)) {
            facturaDatos = false;
            updateFacturaDatos();
        } else if (valida2()) {
            if (otText[0].equals(facturaDatosText[0])) {
                JOptionPane.showMessageDialog(this, "El rut de facturacion debe\nser diferente al de la orden de trabajo.");
            } else {
                try {
                    Integer rt = rut.length() > 0 ? Integer.parseInt(rut) : null;
                    if (DB.instance.existCliente.executeFirstQuery(rt) == null) {//No existe el cliente
                        DB.instance.insertCliente.executeUpdate(rt, razon, direccion, comuna, ciudad, sii, telefono, celular, mail, web, fantasia, giro, sucursal);
                    } else {//existe
                        DB.instance.updateCliente.executeUpdate(razon, direccion, comuna, ciudad, sii, telefono, celular, mail, web, fantasia, giro, sucursal, rt);
                    }
                    DB.instance.updateOTFacturacion.executeUpdate(rt, ot);
                    facturaDatos = false;
                    updateFacturaDatos();
                    bOtMenu1.setBackground(Main.c4);
                } catch (NumberFormatException | SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR AL GUARDAR DATOS DE FACTURACION", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed

        try {
            DB.instance.deleteOTFacturacion.executeUpdate(ot);
            facturaDatos = false;
            facturaDatosText = null;
            updateFacturaDatos();
            bOtMenu1.setBackground(Main.c2);
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR AL ELIMINAR DATOS DE FACTURACION", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jDialog2WindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog2WindowActivated
        try {
            calFechaDocumento.setDate(DB.instance.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }//GEN-LAST:event_jDialog2WindowActivated

    public void print(String printURL, String format, String begin, String end) {
        String lines[] = format.split("\n");
        Pattern pat = Pattern.compile("\\[(\\%|\\$)?\\w+[^\\]]*\\]");
        try {
            FileOutputStream os = new FileOutputStream(printURL);
            try (PrintStream ps = new PrintStream(os, true, "ISO-8859-1")) {
                int cantidadCount = 0;
                int detalleCount = 0;
                int precioCount = 0;
                int folioCount = 0;
                int totalCount = 0;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb1 = new StringBuilder();
                String toTextNum = null;
                for (String s : lines) {
                    Matcher m = pat.matcher(s);
                    int i0 = 0;
                    boolean update = false;
                    while (m.find()) {
                        int i1 = m.start();
                        if (i1 > i0) {
                            sb.append(s.substring(i0, i1));
                        }

                        String match = m.group();
                        boolean isnum = (match.charAt(1) == '$');
                        boolean isTextNum = (match.charAt(1) == '%');

                        String id = match.substring((isnum || isTextNum) ? 2 : 1, match.length() - 1).trim();
                        boolean isFull = (id.endsWith("+"));
                        if (isFull) {
                            id = id.substring(0, id.length() - 1).trim();
                        }
                        Object replace = null;
                        int len = m.group().length();
                        try {
                            Object result[]=DB.instance.selectOT.executeFirstQuery(ot);//TODO Imprimir con estos datos... no desde getText de componentes
                            //estado,  documento,  fecha_solicitud, fecha_entrega, rut, neto, iva, total, observaciones, razon, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal, (SELECT count(*)  FROM PAGOS WHERE ot_numero=?), rut_factura 
                            Date fechaDoc=result[23]!=null?new Date((long)result[23]):DB.instance.getDate();
                            switch (id.toLowerCase()) {
                                case "numero":
                                    replace = "" + ot;
                                    break;
                                case "barcode":
                                    replace = "[barcode]";
                                    break;
                                case "doc":
                                    replace = "" + jLabel23.getText();
                                    break;
                                case "dia":
                                    
                                    replace = "" + fechaDoc.getDate();
                                    break;
                               
                                case "mes":
                                    String meses[] = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
                                    replace = meses[fechaDoc.getMonth()];
                                    break;
                               
                                case "año":
                                    replace = "" + (1900 + fechaDoc.getYear());
                                    break;
                               
                                case "razon":
                                    replace = tRazon.getText();
                                    break;
                                case "dir":
                                    replace = tDireccion.getText();
                                    break;
                                case "comuna":
                                    replace = tComuna.getText();
                                    break;
                                case "ciudad":
                                    replace = tCiudad.getText();
                                    break;
                                case "fono":
                                    replace = tTelefonos.getText();
                                    break;
                                case "cel":
                                    replace = tCelulares.getText();
                                    break;
                                case "rut":
                                    try {
                                        replace = Main.formatter.format(Integer.parseInt(tRut.getText().replaceAll("\\.", ""))) + "-" + tDv.getText();
                                    } catch (Exception e) {
                                        replace = "";
                                    }
                                    break;
                                case "fechasol":
                                    try {
                                        replace = jLabel24.getText();
                                    } catch (Exception e) {
                                        replace = "";
                                    }
                                    break;
                                case "sii":
                                    try {
                                        replace = tSii.getText();
                                    } catch (Exception e) {
                                        replace = "";
                                    }
                                    break;
                                case "fechaent":
                                    try {
                                        replace = jLabel24.getText();
                                    } catch (Exception e) {
                                        replace = "";
                                    }
                                    break;
                                case "fantasia":
                                    replace = tFantasia.getText();
                                    break;
                                case "sucursales":
                                    replace = tSucursal.getText();
                                    break;
                                case "mail":
                                    replace = tMail.getText();
                                    break;
                                case "web":
                                    replace = tWeb.getText();
                                    break;
                                case "giro":
                                    replace = Tgiro.getText();
                                    break;
                                //---------------------- "PRODUCTO","FOLIO","CANTIDAD","PRECIO","TOTAL"
                                case "cant":
                                    replace = tablaDetalle.getValueAt(cantidadCount, 2);
                                    cantidadCount++;
                                    break;
                                case "det":
                                    replace = tablaDetalle.getValueAt(detalleCount, 0);
                                    detalleCount++;
                                    break;
                                case "folio":
                                    replace = tablaDetalle.getValueAt(folioCount, 1);
                                    if (replace != null) {
                                        replace = " Folio " + replace;
                                    }
                                    folioCount++;
                                    break;
                                case "precio":
                                    replace = tablaDetalle.getValueAt(precioCount, 3);
                                    precioCount++;
                                    break;
                                case "total":
                                    replace = tablaDetalle.getValueAt(totalCount, 4);
                                    totalCount++;
                                    break;
                                //---------------------- "PRODUCTO","FOLIO","CANTIDAD","PRECIO","TOTAL"
                                case "neto":
                                    replace = lNeto.getText();
                                    break;
                                case "iva":
                                    replace = lIva.getText();
                                    break;
                                case "totfinal":
                                    replace = lTotal.getText();
                                    break;
                                case "abono":
                                    Long abono = (Long) DB.instance.getAbonoSaldo.executeFirstQuery(ot)[0];
                                    if (abono != null) {
                                        replace = "" + abono;
                                    }
                                    break;
                                case "saldo":
                                    abono = (Long) DB.instance.getAbonoSaldo.executeFirstQuery(ot)[0];
                                    if (abono != null) {
                                        replace = "" + (Long.parseLong(lTotal.getText().replaceAll("\\.", "")) - abono);
                                    }
                                    break;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            JOptionPane.showMessageDialog(this, "ERROR al imprimir, verifique cantidad de items", "ERROR de impresión", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                            return;
                        }
                        if (replace != null) {
                            int num = -1;
                            if (isnum || isTextNum) {
                                try {
                                    num = Integer.parseInt(replace.toString().replaceAll("\\.", ""));
                                } catch (Exception e) {
                                    replace = "";
                                }
                            }
                            if (isnum) {
                                replace = Main.formatter.format(num);
                            } else if (isTextNum) {
                                if (toTextNum == null) {
                                    toTextNum = NumberLiteral.N2L(num) + " pesos";
                                }
                                replace = toTextNum.trim();
                                while (replace.toString().length() > len && replace.toString().lastIndexOf(" ") > -1) {
                                    replace = replace.toString().substring(0, replace.toString().lastIndexOf(" ")).trim();
                                }
                                toTextNum = toTextNum.substring(replace.toString().length());
                            }
                            if (isFull) {
                                sb.append(replace);
                            } else if (replace.toString().length() > len) {
                                sb.append(replace.toString().substring(0, len));
                            } else if (replace.toString().length() <= len) {
                                if (!isnum) {
                                    sb.append(replace);
                                }
                                for (int i = 0; i < len - replace.toString().length(); i++) {
                                    sb.append(' ');
                                }
                                if (isnum) {
                                    sb.append(replace);
                                }
                            }
                            update = true;
                        } else {
                            for (int i = 0; i < len; i++) {
                                sb.append(' ');
                            }
                        }
                        i0 = i1 + len;
                    }
                    sb1.append(update ? 1 : 0);
                    if (i0 < s.length()) {
                        sb.append(s.substring(i0));
                    }
                    sb.append("\n");
                }
                String lineas[] = sb.toString().split("\n");

                int idx = 0;
                String[] code = begin.split(" ");
                for (String c : code) {
                    if (c.length() > 0) {
                        ps.print((char) Integer.parseInt(c));
                    }
                }
                for (String s : lineas) {
                    if (s.trim().equals("[barcode]")) {
                        try {
                            printBarCode("" + ot, 62, 32, 2, printURL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!s.endsWith("*") || (s.substring(0, s.length() - 1).trim().length() > 0 && sb1.charAt(idx) == '1')) {
                        if (s.endsWith("*")) {
                            s = s.substring(0, s.length() - 1);
                        }
                        ps.println(s);
                    }
                    idx++;
                }
                code = end.split(" ");
                for (String c : code) {
                    if (c.length() > 0) {
                        ps.print((char) Integer.parseInt(c));
                    }
                }
            }

        } catch (FileNotFoundException | SQLException | NumberFormatException | HeadlessException | UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR AL IMPRIMIR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static PrintService getPrintService(String printerName) {
        PrintService service = null;
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (int index = 0; service == null && index < services.length; index++) {
            if (services[index].getName().equalsIgnoreCase(printerName)) {
                service = services[index];
            }
        }
        return service;
    }

    private static PrinterJob getPrinterJob(String printerName) throws Exception {

        PrintService printService = getPrintService(printerName);
        if (printService == null) {
            throw new IllegalStateException("Unrecognized Printer Service \"" + printerName + '"');
        }
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintService(printService);
        return printerJob;
    }

    private static BufferedImage bar2Img(String code, int width, int height, int amp) throws IOException {
        final BarCodeRenderer barCodeRenderer = new UPCABarCodeRenderer();
        final BufferedImage image = new BufferedImage(width * amp, height * amp, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width * amp, height * amp);
        g.setColor(Color.black);
        g.scale(amp, amp);
        barCodeRenderer.render(g, code, 1, width, height);
        ImageIO.write(image, "jpg", new File(code + ".jpg"));
        return image;
    }

    private static void printBarCode(String code, final int width, final int height, final int amp, String printer) throws Exception {
        final BufferedImage image = bar2Img(code, width, height, amp);
        PrinterJob printJob = getPrinterJob(printer);
        printJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex != 0) {
                    return NO_SUCH_PAGE;
                }
                graphics.drawImage(image, 0, 0, width * amp, height * amp, null);
                return PAGE_EXISTS;
            }
        });
        try {
            printJob.print();
        } catch (PrinterException e1) {
            e1.printStackTrace();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Tgiro;
    private javax.swing.JButton bAddRow;
    private javax.swing.JButton bDelRow;
    private javax.swing.JButton bGuardar;
    private javax.swing.JButton bImprimir;
    private javax.swing.JButton bImprimir1;
    private javax.swing.JButton bNext;
    private javax.swing.JButton bOtMenu;
    private javax.swing.JButton bOtMenu1;
    private javax.swing.JButton bPagar;
    private javax.swing.JButton bPrev;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    public javax.swing.JDialog jDialog1;
    public javax.swing.JDialog jDialog2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JLabel lIva;
    private javax.swing.JLabel lNeto;
    private javax.swing.JLabel lTotal;
    private javax.swing.JTextField tCelulares;
    private javax.swing.JTextField tCiudad;
    private javax.swing.JTextField tComuna;
    private javax.swing.JTextField tDireccion;
    private javax.swing.JTextField tDv;
    private javax.swing.JTextField tFantasia;
    private javax.swing.JTextField tMail;
    private javax.swing.JTextArea tObservaciones;
    private javax.swing.JTextField tRazon;
    private javax.swing.JTextField tRut;
    private javax.swing.JTextField tSii;
    private javax.swing.JTextField tSucursal;
    private javax.swing.JTextField tTelefonos;
    private javax.swing.JTextField tWeb;
    public javax.swing.JTable tablaDetalle;
    private javax.swing.JToggleButton togleBoleta;
    private javax.swing.JToggleButton togleFactura;
    // End of variables declaration//GEN-END:variables

}
