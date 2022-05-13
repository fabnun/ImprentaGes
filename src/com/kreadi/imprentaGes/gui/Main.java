package com.kreadi.imprentaGes.gui;

import com.kreadi.imprentaGes.DB;
import com.kreadi.imprentaGes.SimpleDateFormatThreadSafe;
import com.kreadi.swing.KPlainDocument;
import com.kreadi.swing.KSwingTools;
import com.kreadi.swing.KTable;
import com.michaelbaranov.microba.calendar.CalendarPane;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;
import jxl.write.WritableWorkbook;
import jxl.write.WritableCellFormat;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.NumberFormats;

public class Main extends javax.swing.JFrame {

    private CajaGui caja = null;//Dialog de caja
    private PreciosGui precios = null;//Dialog de precios
    private OrdenTrabajoGui ot = null;//Dialog de OT
    private ConfigGui config = null;//Dialog de configuracion
    private boolean login = false;//true cuando esta logeado el usuario
    private boolean app = false;//true cuando ha iniciado la aplicacion
    private Thread timeThread = null;//thread que actualiza la hora
    public static final Font defaultFont14 = KSwingTools.getTTFFont(Main.class.getResource("DroidSansMono.ttf"), 0, 14);
    public static final Font defaultFont18 = KSwingTools.getTTFFont(Main.class.getResource("DroidSansMono.ttf"), 1, 15);
    public static final Color c1 = new Color(220, 232, 255);//Color Botones
    public static final Color c2 = new Color(248, 255, 248);//Color Fondo
    public static final Color c3 = new Color(128, 192, 250);//Color Seleccion
    public static final Color c4 = new Color(194, 252, 221);//Color de Autocompletado
    public static final SimpleDateFormatThreadSafe sdf2 = new SimpleDateFormatThreadSafe("yyyyMMdd");
    public static final SimpleDateFormatThreadSafe sdfh2 = new SimpleDateFormatThreadSafe("HH:mm:ss");
    public static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static boolean cajaAbierta = false;
    private static String usuario, nombreUsuario;
    public static boolean permisoPrecio, permisoConfig, permisoCaja, permisoOt;
    private final ImageIcon i0 = new ImageIcon(getClass().getResource("png/cash.png"));
    private final ImageIcon i1 = new ImageIcon(getClass().getResource("png/cash2.png"));
    public static final JFileChooser jfc = new JFileChooser();

    public static void exportTable2PDF(Component c, String title, String filename, KTable table, int colWidth) throws IOException, WriteException {
        jfc.setSelectedFile(new File(jfc.getCurrentDirectory(), filename));
        if (jfc.showSaveDialog(c) == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".xls")) {
                f = new File(f.getParent(), f.getName() + ".xls");
            }
            WritableWorkbook workbook = Workbook.createWorkbook(f);
            WritableSheet sheet = workbook.createSheet(title, 0);
            int cols = table.getColumnCount();
            for (int i = 0; i < cols; i++) {
                sheet.addCell(new Label(i, 0, table.getColumnName(i)));
            }
            WritableCellFormat integerFormat = new WritableCellFormat(NumberFormats.INTEGER);
            WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT);
            for (int j = 0; j < table.getRowCount(); j++) {
                for (int i = 0; i < cols; i++) {
                    Class cls = table.getModel().getColumnClass(i);
                    Object o = table.getValueAt(j, i);
                    if (o != null) {
                        if (cls == Integer.class || cls == Long.class || cls == Double.class || cls == Float.class) {
                            String s = o.toString();
                            if (s.trim().length() > 0) {
                                if (cls == Integer.class || cls == Long.class) {
                                    sheet.addCell(new Number(i, j + 1, Double.parseDouble(s), integerFormat));
                                } else {
                                    sheet.addCell(new Number(i, j + 1, Double.parseDouble(s), floatFormat));
                                }
                            }
                        } else {
                            Object val = o;
                            sheet.addCell(new Label(i, j + 1, val != null ? val.toString() : ""));
                        }
                    }
                    sheet.getSettings().setDefaultColumnWidth(colWidth);
                }
            }
            sheet.getSettings().setFitWidth(240);
            workbook.write();
            workbook.close();
            if (JOptionPane.showConfirmDialog(c, "Desea Abrir el archivo " + f, "Abrir Excel", JOptionPane.YES_NO_OPTION) == 0) {
                Desktop.getDesktop().browse(f.toURI());
            }
        }
    }

    public void login() {
        String username = jTextField5.getText().trim().toLowerCase();

        String password = new String(jPasswordField1.getPassword()).trim().toLowerCase();
        try {
            if (DB.instance.login.executeFirstQuery(username, password) != null && DB.instance.existCaja.executeFirstQuery(DB.instance.caja) != null) {
                Object[] result = DB.instance.userInfo.executeFirstQuery(username);
                permisoConfig = (Boolean) result[2];

                boolean found = false;
                if (!permisoConfig) {//Si no es un admnistrador... chequeCalculado si es usuario de la caja
                    Object[] o = DB.instance.getUsuariosCaja.executeFirstQuery(DB.instance.caja);
                    if (o != null) {
                        String[] usuarios = ((String) o[0]).trim().toLowerCase().split("\\s+");
                        if (usuarios.length == 1 && usuarios[0].length() == 0) {
                            found = true;
                        } else {
                            for (String s : usuarios) {
                                if (username.equals(s)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    found = true;
                }
                if (found) {
                    clearSearch();
                    jComboBox1.setSelectedIndex(0);
                    DB.instance.usuario = username;
                    usuario = username;
                    nombreUsuario = (String) result[0];
                    permisoPrecio = (Boolean) result[1];

                    permisoCaja = (Boolean) result[3];
                    permisoOt = (Boolean) result[4];
                    jDialog1.setVisible(false);
                    caja = new CajaGui(this, true);
                    precios = new PreciosGui(this, true);
                    ot = new OrdenTrabajoGui(this, true);
                    config = new ConfigGui(this, true);
                    try {
                        restoreBounds();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    KSwingTools.addEscapeListener(caja);
                    KSwingTools.addEscapeListener(jDialog3);
                    KSwingTools.addEscapeListener(precios);
                    KSwingTools.addEscapeListener(ot);
                    KSwingTools.addEscapeListener(ot.jDialog1);
                    KSwingTools.addEscapeListener(ot.jDialog2);
                    KSwingTools.addEscapeListener(config);
                    KSwingTools.addEscapeListener(config.jDialog1);
                    KSwingTools.addEscapeListener(config.jDialog2);
                    ActionListener escListener = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (login) {
                                jDialog1.setVisible(false);
                            }
                        }
                    };

                    jDialog1.getRootPane().registerKeyboardAction(escListener,
                            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                            JComponent.WHEN_IN_FOCUSED_WINDOW);

                    //KSwingTools.addEscapeListener(this);
                    jDialog1.setTitle("Imprenta Ges 1.0");
                    jButton7.setVisible(permisoPrecio);
                    jButton5.setVisible(permisoCaja);
                    jButton1.setVisible(permisoOt);
                    jButton8.setVisible(permisoConfig);
                    jLabel8.setText(nombreUsuario);

                    try {
                        getTurno();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    jButton6.setVisible(true);
                    jButton4.setVisible(true);
                    login = true;
                    app = true;
                    if (timeThread == null) {
                        timeThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    Date d = DB.instance.getDate();
                                    jLabel7.setText(DB.sdfh.format(d));
                                    if (caja.textEfectivo.isEditable()) {
                                        caja.jLabel14.setText(sdfh2.format(d));
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                    }
                                }
                            }
                        });
                        timeThread.start();
                    }
                    setVisible(true);
                } else {
                    jDialog1.setTitle("ERROR USUARIO SIN ACCESO A CAJA");
                    logout();
                }
            } else {
                jDialog1.setTitle("ERROR DE ACCESO");
            }
        } catch (SQLException e) {
            System.err.println("Error al hacer login de usuario");
            e.printStackTrace();
        }
    }

    public static BufferedImage getScreenShot(Component component) {
        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        component.paint(image.getGraphics());
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0.5f));
        g.fillRect(0, 0, component.getWidth(), component.getHeight());
        return image;
    }

    public void setModalView(boolean modalView) {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        if (modalView) {
            jLabel10.setIcon(new ImageIcon(getScreenShot(jPanel6)));
            cl.show(getContentPane(), "modal");
        } else {
            cl.show(getContentPane(), "panel");
        }
    }

    private void backupBounds() {
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
        File f = new File(System.getProperty("user.home")+File.separator+"imprentaGes","imprentaGesBounds.dat");
        if (f.exists()) {
            try (FileInputStream fos = new FileInputStream(f); ObjectInputStream oos = new ObjectInputStream(fos)) {
                map = (HashMap<String, HashMap<String, Integer>>) oos.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            map.put("main", KSwingTools.getMapProperties(this));
            map.put("main.table1", KSwingTools.getMapProperties(jTable1));
            map.put("main.table2", KSwingTools.getMapProperties(jTable2));
            map.put("main.table3", KSwingTools.getMapProperties(jTable3));
            //TODO Solo actualizar si se abrio la ventana OT
            if (OrdenTrabajoGui.open1) {
                map.put("ot", KSwingTools.getMapProperties(ot));
                map.put("ot.table1", KSwingTools.getMapProperties(ot.tablaDetalle));
            }
            if (OrdenTrabajoGui.open2) {
                map.put("ot.pago", KSwingTools.getMapProperties(ot.jDialog1));
                map.put("ot.pago.table", KSwingTools.getMapProperties(ot.jTable2));
            }
            //TODO Solo actualizar si se abrio la ventana Caja
            if (CajaGui.open1) {
                map.put("caja", KSwingTools.getMapProperties(caja));
                map.put("caja.table1", KSwingTools.getMapProperties(caja.jTable1));
            }
            //TODO Solo actualizar si se habrio la ventana Options... y ver que pasa con el dialog interior
            if (PreciosGui.open1) {
                map.put("precios", KSwingTools.getMapProperties(precios));
                map.put("precios.table1", KSwingTools.getMapProperties(precios.jTable1));
            }
            if (ConfigGui.open1) {
                map.put("config", KSwingTools.getMapProperties(config));
                map.put("config.table1", KSwingTools.getMapProperties(config.jTable1));
                map.put("config.table2", KSwingTools.getMapProperties(config.jTable3));
                map.put("config.split1", KSwingTools.getMapProperties(config.jSplitPane1));
            }
            if (ConfigGui.open3) {
                map.put("config.param", KSwingTools.getMapProperties(config.jDialog1));
            }
            if (ConfigGui.open2) {
                map.put("config.formato", KSwingTools.getMapProperties(config.jDialog2));
                map.put("config.param.table1", KSwingTools.getMapProperties(config.jTable2));
            }
            try (FileOutputStream fos = new FileOutputStream(new File(System.getProperty("user.home")+File.separator+"imprentaGes","imprentaGesBounds.dat")); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(map);
            }
        } catch (IOException e) {
        }
    }

    private void restoreBounds() throws FileNotFoundException, IOException, ClassNotFoundException {
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
        File f = new File(System.getProperty("user.home")+File.separator+"imprentaGes","imprentaGesBounds.dat");
        if (f.exists()) {
            try (FileInputStream fos = new FileInputStream(f); ObjectInputStream oos = new ObjectInputStream(fos)) {
                map = (HashMap<String, HashMap<String, Integer>>) oos.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                config.setLocationRelativeTo(this);
                caja.setLocationRelativeTo(this);
                precios.setLocationRelativeTo(this);
                ot.setLocationRelativeTo(this);
            }
        }
        HashMap<String, Integer> map0;
        int i;
        TableColumnModel tcm;
        try {
            map0 = map.get("main");
            this.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            this.setLocationRelativeTo(null);
        }
        try {
            map0 = map.get("ot");
            ot.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            ot.setLocationRelativeTo(this);
        }
        try {
            map0 = map.get("ot.pago");
            ot.jDialog1.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            Dimension dim = new Dimension(800, 420);
            ot.jDialog1.setSize(dim);
            ot.jDialog1.setMinimumSize(dim);
            ot.jDialog1.setPreferredSize(dim);
            ot.jDialog1.setLocationRelativeTo(this);
        }
        try {
            map0 = map.get("caja");
            caja.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            caja.setLocationRelativeTo(this);
        }
        try {
            map0 = map.get("precios");
            precios.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            precios.setLocationRelativeTo(this);
        }
        try {
            map0 = map.get("config");
            config.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            config.setLocationRelativeTo(this);
        }
        try {
            map0 = map.get("config.param");
            config.jDialog1.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            config.jDialog1.setLocationRelativeTo(this);
        }

        try {
            map0 = map.get("config.formato");
            config.jDialog2.setBounds(map0.get("x"), map0.get("y"), map0.get("width"), map0.get("height"));
        } catch (Exception e) {
            Dimension dim = new Dimension(520, 420);
            config.jDialog2.setSize(dim);
            config.jDialog2.setMinimumSize(dim);
            config.jDialog2.setPreferredSize(dim);
            config.jDialog2.setLocationRelativeTo(this);
        }
        try {
            map0 = map.get("config.table1");
            i = 0;
            tcm = config.jTable1.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("config.param.table1");
            i = 0;
            tcm = config.jTable2.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("config.table2");
            i = 0;
            tcm = config.jTable3.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("caja.table1");
            i = 0;
            tcm = caja.jTable1.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("precios.table1");
            i = 0;
            tcm = precios.jTable1.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("ot.table1");
            i = 0;
            tcm = ot.tablaDetalle.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("main.table1");
            i = 0;
            tcm = jTable1.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("main.table2");
            i = 0;
            tcm = jTable2.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("main.table3");
            i = 0;
            tcm = jTable3.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("ot.pago.table");
            i = 0;
            tcm = ot.jTable2.getColumnModel();
            while (map0.containsKey("width." + i)) {
                tcm.getColumn(i).setPreferredWidth(map0.get("width." + i));
                i++;
            }
        } catch (Exception e) {
        }
        try {
            map0 = map.get("config.split1");
            config.jSplitPane1.setDividerLocation(map0.get("splitPos"));
        } catch (Exception e) {
        }
    }
    CalendarPane cal, cal2;

    public Main() {
        initComponents();
        jfc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isFile() && f.getName().toLowerCase().endsWith(".xls");
            }

            @Override
            public String getDescription() {
                return "XLS EXCEL";
            }
        });
        jButton10.setVisible(false);
        jDialog3.setSize(303, 160);
        jDialog3.setIconImage(new ImageIcon("icono.png").getImage());
//        ((KTable) jTable1).getCellRenderer(1).setHorizontalAlignment(KCellRenderer.CENTER);
//        ((KTable) jTable1).getCellRenderer(2).setHorizontalAlignment(KCellRenderer.CENTER);
//        ((KTable) jTable1).getCellRenderer(3).setHorizontalAlignment(KCellRenderer.CENTER);
//        ((KTable) jTable1).getCellRenderer(4).setHorizontalAlignment(KCellRenderer.CENTER);
//        ((KTable) jTable1).getCellRenderer(5).setHorizontalAlignment(KCellRenderer.CENTER);
//        ((KTable) jTable2).getCellRenderer(1).setHorizontalAlignment(KCellRenderer.CENTER);
//        ((KTable) jTable2).getCellRenderer(2).setHorizontalAlignment(KCellRenderer.CENTER);
        Date dat = DB.instance.getDate();
        String s = DB.sdf.format(dat);
        jLabel2.setText(s);
        jLabel3.setText(s);
        cal = KSwingTools.calendar(jLabel2, DB.sdf, false, DB.timezone);
        cal2 = KSwingTools.calendar(jLabel3, DB.sdf, false, DB.timezone);
        try {
            cal.setDate(dat);
            cal2.setDate(dat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        jButton1.setFont(defaultFont14);
        setIconImage(new ImageIcon("icono.png").getImage());
        jDialog1.setIconImage(new ImageIcon("icono.png").getImage());
        jButton4.setVisible(false);
        jButton6.setVisible(false);
        jDialog1.setTitle("ImprentaGes [Acceso a " + DB.instance.caja + "]");
        jDialog1.setSize(360, 150);
        jDialog1.setLocationRelativeTo(null);
        jDialog1.toFront();
        jTextField5.requestFocus();
        jDialog1.setVisible(true);
    }

    private void exit() {
        if (app) {
            backupBounds();
        }
        System.exit(0);
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    private Integer[] getTurno() throws SQLException {
        int dia = Integer.parseInt(sdf2.format(DB.instance.getDate()));
        Object[] state = (Object[]) DB.instance.getCajaTurno.executeFirstQuery(DB.instance.caja);
        if (state == null) {
            state = new Integer[]{1, 0, dia};
        } else {
            dia = (Integer) state[1] == 1 ? (int) state[2] : dia;
            if (dia != (int) state[2]) {
                state[0] = 1;
            }
        }
        cajaAbierta = (Integer) state[1] == 1;
        jLabel9.setText(DB.instance.caja + ((Integer) state[1] == 1 ? " Abierta" : " Cerrada"));
        jButton5.setIcon((Integer) state[1] == 1 ? i1 : i0);
        jButton1.setEnabled((Integer) state[1] == 1);
        Integer[] result = new Integer[]{Integer.parseInt(state[0].toString()), Integer.parseInt(state[1].toString()), dia};
        if (result[1] == 1) {
            result[0]--;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new JDialog(null, ModalityType.TOOLKIT_MODAL)
        ;
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jDialog3 = new javax.swing.JDialog();
        jLabel15 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jLabel16 = new javax.swing.JLabel();
        jPasswordField3 = new javax.swing.JPasswordField();
        jButton13 = new javax.swing.JButton();
        jPasswordField4 = new javax.swing.JPasswordField();
        jLabel17 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel71 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new com.kreadi.swing.KTable(
            new String[]{"OT","FACTURA / BOLETA","FECHA SOLICITUD","FECHA ENTREGA","CLIENTE","ESTADO","TOTAL","PAGADO"},
            new Class[]{Integer.class, String.class,String.class,String.class,String.class,String.class, Integer.class, Integer.class},
            new boolean[]{false, false, false, false, false,false,false, false},
            new int[]{0,0,0,0,0,0,0,0},
            new String[]{null,null,null, null,null,null,null,null}   
        );
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new com.kreadi.swing.KTable(
            new String[]{"TIEMPO","CAJA","USUARIO","ACCION","DATOS"},
            new Class[]{String.class, String.class,String.class,String.class,String.class},
            new boolean[]{false, false, false, false, false},
            new int[]{0,0,0,0,0},
            new String[]{null,null,null,null,null}   
        );
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new com.kreadi.swing.KTable(
            new String[]{"TIEMPO","CAJA","USUARIO","TIPO","EFECTIVO","CHEQUE","TRANSFERENCIA"},
            new Class[]{String.class, String.class, String.class, String.class, Integer.class, Integer.class, Integer.class},
            new boolean[]{false, false, false, false, false, false, false},
            new int[]{0,0,0,0,0,0,0},
            new String[]{null,null,null,null,null,null,null}   
        );
        jButton10 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();

        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialog1.setTitle("Imprenta Ges 1.0");
        jDialog1.setModal(true);
        jDialog1.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog1.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jDialog1.setResizable(false);
        jDialog1.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialog1WindowClosing(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/icono.png"))); // NOI18N

        jTextField5.setFont(defaultFont14);
        jTextField5.setMargin(new java.awt.Insets(0, 4, 0, 4));
        jTextField5.setMinimumSize(new java.awt.Dimension(4, 24));
        jTextField5.setPreferredSize(new java.awt.Dimension(11, 24));
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel5.setFont(defaultFont14);
        jLabel5.setText("Usuario");

        jLabel6.setFont(defaultFont14);
        jLabel6.setText("Clave");

        jPasswordField1.setFont(defaultFont14);
        jPasswordField1.setMargin(new java.awt.Insets(0, 4, 0, 4));
        jPasswordField1.setMinimumSize(new java.awt.Dimension(4, 24));
        jPasswordField1.setPreferredSize(new java.awt.Dimension(11, 24));
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });

        jButton3.setFont(defaultFont14);
        jButton3.setText("Ingresar");
        jButton3.setToolTipText("");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(defaultFont14);
        jButton4.setText("Salir");
        jButton4.setToolTipText("Desconectarse");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/set.png"))); // NOI18N
        jButton6.setToolTipText("Modificar clave");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setMaximumSize(new java.awt.Dimension(22, 22));
        jButton6.setMinimumSize(new java.awt.Dimension(22, 22));
        jButton6.setPreferredSize(new java.awt.Dimension(22, 22));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jLabel11.setText(" ");
        jLabel11.setPreferredSize(new java.awt.Dimension(8, 16));

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8))))
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDialog3.setTitle("Modificación de Clave");
        jDialog3.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jDialog3.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jDialog3.setResizable(false);
        jDialog3.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialog3WindowClosing(evt);
            }
        });

        jLabel15.setFont(Main.defaultFont14);
        jLabel15.setText("Clave Nueva");

        jPasswordField2.setFont(Main.defaultFont14);

        jLabel16.setFont(Main.defaultFont14);
        jLabel16.setText("Clave Nueva");

        jPasswordField3.setFont(Main.defaultFont14);

        jButton13.setFont(Main.defaultFont14);
        jButton13.setText("Guardar y Cerrar");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jPasswordField4.setFont(Main.defaultFont14);

        jLabel17.setFont(Main.defaultFont14);
        jLabel17.setText("Clave actual");

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPasswordField4, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(jPasswordField3)
                    .addComponent(jPasswordField2))
                .addContainerGap())
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jPasswordField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jPasswordField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Imprenta Ges 1.0");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        jPanel2.setFocusable(false);
        jPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 54));

        jLabel1.setFont(defaultFont14);
        jLabel1.setText("Buscar Por ");
        jLabel1.setFocusable(false);

        jComboBox1.setBackground(Main.c1);
        jComboBox1.setFont(defaultFont14);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Fechas", "Nombre o RUT (sin dv)", "Números OT", "Números Factura", "Números Boleta", "Auditoria Fechas", "Auditoria Texto", "Cajas Diario" }));
        jComboBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBox1.setFocusable(false);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jPanel1.setFocusable(false);
        jPanel1.setLayout(new java.awt.CardLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(673, 22));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jComboBox3.setBackground(new java.awt.Color(255, 255, 255));
        jComboBox3.setFont(Main.defaultFont14);
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "caja1" }));
        jComboBox3.setFocusable(false);
        jComboBox3.setMaximumSize(new java.awt.Dimension(120, 25));
        jComboBox3.setMinimumSize(new java.awt.Dimension(120, 25));
        jComboBox3.setPreferredSize(new java.awt.Dimension(120, 25));
        jPanel4.add(jComboBox3);

        jLabel71.setText("  ");
        jPanel4.add(jLabel71);

        jLabel70.setText("  Desde  ");
        jPanel4.add(jLabel70);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(Main.defaultFont14);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel2.setMaximumSize(new java.awt.Dimension(1024, 28));
        jLabel2.setOpaque(true);
        jPanel4.add(jLabel2);

        jLabel69.setText("   hasta   ");
        jPanel4.add(jLabel69);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(Main.defaultFont14);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(Main.c1, 4));
        jLabel3.setMaximumSize(new java.awt.Dimension(1024, 28));
        jLabel3.setOpaque(true);
        jPanel4.add(jLabel3);

        jPanel1.add(jPanel4, "card1");

        jTextField1.setFont(Main.defaultFont14);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1, "card2");

        jComboBox2.setBackground(Main.c1);
        jComboBox2.setFont(defaultFont14);
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sin Filtro", "Terminadas", "Pendientes", "Anuladas" }));
        jComboBox2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBox2.setFocusable(false);

        jButton2.setBackground(Main.c4);
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/search.png"))); // NOI18N
        jButton2.setToolTipText("Realiza la busqueda");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton2.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton2.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel3.setLayout(new java.awt.CardLayout());

        jScrollPane1.setFocusable(false);

        jTable1.setFont(Main.defaultFont14);
        jTable1.setFocusable(false);
        jTable1.setRowHeight(22);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel3.add(jScrollPane1, "otData");

        jScrollPane2.setFont(defaultFont14);

        jTable2.setFont(Main.defaultFont14);
        jTable2.setRowHeight(22);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable2MousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel3.add(jScrollPane2, "auditoriaData");

        jScrollPane3.setFont(defaultFont14);

        jTable3.setFont(Main.defaultFont14);
        jTable3.setRowHeight(22);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable3MousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jPanel3.add(jScrollPane3, "pagosData");

        jButton10.setBackground(Main.c4);
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/excel.png"))); // NOI18N
        jButton10.setToolTipText("Exporta a Excel");
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.setFocusable(false);
        jButton10.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton10.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton10.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218), 2));

        jLabel7.setFont(defaultFont14);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("29/06/2014 22:34");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel8.setFont(defaultFont14);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(" ");

        jLabel9.setFont(defaultFont14);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(" ");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9))
        );

        jButton9.setFont(defaultFont14);
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/user.png"))); // NOI18N
        jButton9.setText("Acceso");
        jButton9.setToolTipText("Acceso o Desconección");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setMaximumSize(new java.awt.Dimension(69, 64));
        jButton9.setMinimumSize(new java.awt.Dimension(69, 64));
        jButton9.setPreferredSize(new java.awt.Dimension(69, 64));
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton1.setFont(defaultFont14);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/newOT.png"))); // NOI18N
        jButton1.setText("OT");
        jButton1.setToolTipText("Ordenes de Trabajo");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMaximumSize(new java.awt.Dimension(82, 64));
        jButton1.setMinimumSize(new java.awt.Dimension(82, 64));
        jButton1.setPreferredSize(new java.awt.Dimension(82, 64));
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setFont(defaultFont14);
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/cash.png"))); // NOI18N
        jButton5.setText("Caja");
        jButton5.setToolTipText("Operaciones de Caja");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setMaximumSize(new java.awt.Dimension(82, 64));
        jButton5.setMinimumSize(new java.awt.Dimension(82, 64));
        jButton5.setPreferredSize(new java.awt.Dimension(82, 64));
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setFont(defaultFont14);
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/price.png"))); // NOI18N
        jButton7.setText("Precios");
        jButton7.setToolTipText("Lista de Precios");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setMaximumSize(new java.awt.Dimension(82, 64));
        jButton7.setMinimumSize(new java.awt.Dimension(82, 64));
        jButton7.setPreferredSize(new java.awt.Dimension(82, 64));
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(defaultFont14);
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kreadi/imprentaGes/gui/png/config.png"))); // NOI18N
        jButton8.setText("Config");
        jButton8.setToolTipText("Configuracion Avanzada");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setMaximumSize(new java.awt.Dimension(82, 64));
        jButton8.setMinimumSize(new java.awt.Dimension(82, 64));
        jButton8.setPreferredSize(new java.awt.Dimension(82, 64));
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel6, "panel");

        jPanel8.setLayout(new java.awt.CardLayout());
        jPanel8.add(jLabel10, "card2");

        getContentPane().add(jPanel8, "modal");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jDialog1WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog1WindowClosing
        if (!app) {
            exit();
        } else {
            if (login) {
                jDialog1.setVisible(false);
            } else {
                exit();
            }
        }
    }//GEN-LAST:event_jDialog1WindowClosing

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        login();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        login();
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        login();
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        setModalView(true);
        try {
            Integer[] turno = getTurno();
            caja.updateData(turno[1] == 0, DB.instance.caja, nombreUsuario, usuario, turno[2], turno[0]);
            getTurno();
            CajaGui.open1 = true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar vista de caja...");
            e.printStackTrace();
        }
        setModalView(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        setModalView(true);
        try {
            precios.updateData();
            precios.setVisible(true);
            PreciosGui.open1 = true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar datos de precios...");
            e.printStackTrace();
        }
        setModalView(false);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setModalView(true);
        ot.updateData(0);
        ot.setVisible(true);
        OrdenTrabajoGui.open1 = true;
        setModalView(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        setModalView(true);
        try {
            config.updateData();
            config.setVisible(true);
            ConfigGui.open1 = true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar datos de configuración...");
            e.printStackTrace();
        }
        setModalView(false);
    }//GEN-LAST:event_jButton8ActionPerformed


    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        clearSearch();
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        int idx = jComboBox1.getSelectedIndex();
        if (idx == 1 || idx == 6) {
            jTextField1.setDocument(new KPlainDocument(64, "toUpper"));
        } else if (idx == 2 || idx == 3 || idx == 4) {
            jTextField1.setDocument(new KPlainDocument(64, "(\\d| )*"));
        }
        String informe = "card" + ((idx == 5 || idx == 7) ? "1" : (Math.min(1, idx) + 1));

        cl.show(jPanel1, informe);
        cl = (CardLayout) jPanel3.getLayout();
        if (Math.min(1, idx) == 1) {
            jTextField1.setText("");
            jTextField1.requestFocus();
        }
        if (idx == 5 || idx == 6) {
            jComboBox2.setVisible(false);
            cl.show(jPanel3, "auditoriaData");
        } else if (idx == 7) {
            jComboBox2.setVisible(false);
            cl.show(jPanel3, "pagosData");
        } else {
            jComboBox2.setVisible(true);
            cl.show(jPanel3, "otData");
        }

        boolean idx7 = idx != 7;

        jComboBox3.setVisible(!idx7);
        jLabel70.setVisible(idx7);
        jLabel3.setVisible(idx7);
        jLabel69.setVisible(idx7);

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        setModalView(true);
        jTextField5.setText("");
        jPasswordField1.setText("");
        jDialog1.setTitle("Cambio de usuario en caja");
        jDialog1.setLocationRelativeTo(null);
        jTextField5.requestFocus();
        try {
            getTurno();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jDialog1.setVisible(true);
        setModalView(false);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jDialog1.setTitle("Usuario desconectado");
        logout();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void logout() {
        login = false;
        jDialog1.setVisible(false);
        jLabel8.setText("");
        jButton4.setVisible(false);
        jButton6.setVisible(false);
        jTextField5.setText("");
        jPasswordField1.setText("");
        jDialog1.setSize(360, 150);
        jDialog1.setLocationRelativeTo(null);
        jTextField5.requestFocus();
        jDialog1.setVisible(true);
    }


    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        backupBounds();
        exit();
    }//GEN-LAST:event_formWindowClosing

    private void clearSearch() {

        while (jTable1.getRowCount() > 0) {
            ((KTable) jTable1).removeRow(0);
        }

        while (jTable2.getRowCount() > 0) {
            ((KTable) jTable2).removeRow(0);
        }

        while (jTable3.getRowCount() > 0) {
            ((KTable) jTable3).removeRow(0);
        }

        jButton10.setVisible(false);
        informeTable = null;
    }

    private KTable informeTable = null;
    private String filename;

    public void search() {
        int idx = jComboBox1.getSelectedIndex();
        if (idx == 0 || idx == 5) {
            filename = (idx == 0 ? "ot." : "auditoria.") + Main.sdf2.format(cal.getDate()) + "." + Main.sdf2.format(cal2.getDate());
        } else if (idx == 7) {
            filename = "caja." + Main.sdf2.format(cal.getDate());
        } else {
            filename = idx == 1 ? "cliente." : idx == 2 ? "ot." : idx == 3 ? "factura." : idx == 4 ? "boleta." : "auditoria.";
        }
        if (idx < 5 || idx == 6) {
            filename = filename + jTextField1.getText().replaceAll("\\s+", ".").replace("[^\\w]+", "").toLowerCase()
                    + (jComboBox2.getSelectedIndex() > 0 ? ("." + jComboBox2.getSelectedItem().toString().toLowerCase()) : "");
        }
        clearSearch();
        if (idx >= 5) {
            if (idx == 5) {
                long t0 = cal.getDate().getTime();
                long t1 = cal2.getDate().getTime();
                if (t0 <= t1) {
                    try {
                        List<Object[]> result = DB.instance.getAuditoriaDiaria.executeQuery(t0, t1);
                        KTable table = (KTable) jTable2;
                        for (Object[] o : result) {
                            o[0] = DB.sdfh.format(o[0]);
                            table.addRow(o);
                        }
                        jButton10.setVisible(table.getRowCount() > 0);
                        informeTable = table;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "La fecha inicial es mayor que la fecha final");
                }
            } else if (idx == 6) {
                try {
                    StringBuilder sb = new StringBuilder();
                    String ss = jTextField1.getText().toLowerCase().trim().replace("\\s+", "%");
                    if (ss.length() > 0) {
                        ss = "%" + ss + "%";
                        sb.append("SELECT tiempo, caja, b.nombre, texto, subtexto FROM auditoria a, usuarios b where a.usuario=b.usuario and (lower(texto) like '")
                                .append(ss).append("' or lower(subtexto) like '").append(ss).append("' or lower(b.nombre) like '")
                                .append(ss).append("' or lower(caja) like '").append(ss).append("') order by 1 desc;");
                        List<Object[]> result = DB.instance.executeQueryList(sb.toString());
                        KTable table = (KTable) jTable2;
                        for (Object[] o : result) {
                            o[0] = DB.sdfh.format(o[0]);
                            table.addRow(o);
                        }
                        jButton10.setVisible(table.getRowCount() > 0);
                        informeTable = table;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (idx == 7) {
                long date = cal.getDate().getTime();
                try {
                    List<Object[]> result = DB.instance.getPagosDiario.executeQuery(date, date);
                    KTable table = (KTable) jTable3;
                    int sum0 = 0, sum1 = 0, sum2 = 0;
                    boolean cierreClose = true;
                    int i = 0;
                    for (Object[] o : result) {
                        o[0] = DB.sdfh.format(o[0]);
                        if (o[3] != null && ((String) o[3]).contains("CIERRE CAJA")) {
                            cierreClose = true;
                            if (i != result.size() - 1) {
                                sum0 = 0;
                                sum1 = 0;
                                sum2 = 0;
                            }
                            String[] split = ((String) o[3]).split("\\s+");
                            Integer efectivo = Integer.parseInt(split[3]);
                            Integer cheques = Integer.parseInt(split[4]);
                            Integer transferencias = Integer.parseInt(split[5]);
                            o[3] = split[0] + " CIERRE CAJA INGRESADO";
                            table.addRow(o);
                            table.addRow(new Object[]{o[0], o[1], o[2], split[0] + " CIERRE CAJA CALCULADO", efectivo, cheques, transferencias});

                            int efectivo1 = (Integer) o[4];
                            int cheques1 = (Integer) o[5];
                            int transferencias1 = (Integer) o[6];
                            if (efectivo1 != efectivo || cheques1 != cheques1 || transferencias != transferencias1) {
                                table.addRow(new Object[]{o[0], o[1], o[2], split[0] + " CIERRE CAJA DIFERENCIA", efectivo1 - efectivo, cheques1 - cheques, transferencias1 - transferencias});
                            }
                            table.addRow(new Object[]{});
                        } else {
                            cierreClose = false;
                            sum0 = sum0 + (Integer) o[4];
                            sum1 = sum1 + (Integer) o[5];
                            sum2 = sum2 + (Integer) o[6];
                            table.addRow(o);
                        }
                        i++;
                    }

                    if (!cierreClose) {
                        table.addRow(new Object[]{});
                    }
                    table.addRow(new Object[]{" ", " ", " ", "TOTAL CAJA CALCULADO $" + Main.formatter.format(sum0 + sum1 + sum2), sum0, sum1, sum2});

                    jButton10.setVisible(table.getRowCount() > 0);
                    informeTable = table;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (idx == 0) {
            long t = cal.getDate().getTime();
            long t2 = cal2.getDate().getTime();
            if (t <= t2) {
                try {
                    int filter = jComboBox2.getSelectedIndex();
                    List<Object[]> result = DB.instance.getOTDiaria.executeQuery(t, t2, t, t2);
                    KTable table = (KTable) jTable1;
                    for (Object[] o : result) {
                        Integer doc = (Integer) o[1];
                        o[1] = doc == null ? "" : doc > 0 ? "Factura " + Main.formatter.format(doc) : "Boleta " + Main.formatter.format(-doc);
                        o[2] = DB.sdf.format(o[2]);
                        o[3] = DB.sdf.format(o[3]);
                        boolean add = (filter == 0 || (filter == 1 && "T".equals(o[5])) || (filter == 2 && "P".equals(o[5])) || (filter == 3 && "A".equals(o[5])));
                        o[5] = "A".equals(o[5]) ? "ANULADA" : "T".equals(o[5]) ? "TERMINADA" : "PENDIENTE";
                        o[7] = o[7] == null ? "" : new Integer(o[7].toString());
                        if (add) {
                            table.addRow(o);
                        }
                    }
                    jButton10.setVisible(table.getRowCount() > 0);
                    informeTable = table;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "La fecha inicial es mayor que la fecha final");
            }
        } else if (idx > 0 || idx < 5) {
            String s = jTextField1.getText().trim();
            String search[] = s.split("\\s+");
            try {
                int filter = jComboBox2.getSelectedIndex();
                List<Object[]> result;
                if (idx == 1) {
                    Integer rut = null;
                    try {
                        rut = Integer.parseInt(search[0]);
                    } catch (Exception e) {

                    }
                    StringBuilder sb = new StringBuilder("SELECT a.numero, documento, fecha_solicitud,  fecha_entrega, a.razon, estado, total,  \n"
                            + "(select sum(c.monto) from pagos c where c.ot_numero=a.numero and c.aprobado)\n"
                            + "FROM ordenes a where ");

                    if (s.length() == 0) {
                        sb.append("lower(a.razon) ='' or ");
                    } else {
                        for (String ss : search) {
                            sb.append("lower(a.razon) like '%").append(ss.toLowerCase()).append("%' or ");
                        }
                    }
                    sb.delete(sb.length() - 3, sb.length());
                    if (rut != null) {
                        sb.append("or  a.rut='").append(rut).append("'");
                    }
                    sb.append("\ngroup by a.numero order by 1 desc;");
                    result = DB.instance.executeQueryList(sb.toString());
                } else if (idx == 2) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("SELECT a.numero, documento, fecha_solicitud,  fecha_entrega, a.razon, estado, total,  \n"
                                + "(select sum(c.monto) from pagos c where c.ot_numero=a.numero and c.aprobado)\n"
                                + "FROM ordenes a where  a.numero in (");
                        for (String ss : search) {
                            sb.append(ss).append(",");
                        }
                        sb.delete(sb.length() - 1, sb.length());
                        sb.append(") group by a.numero\n"
                                + "order by 1 desc;");

                        result = DB.instance.executeQueryList(sb.toString());
                    } catch (NumberFormatException | SQLException e) {
                        result = new LinkedList<>();
                    }
                } else if (idx == 3) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("SELECT a.numero, documento, fecha_solicitud,  fecha_entrega, a.razon, estado, total,  \n"
                                + "(select sum(c.monto) from pagos c where c.ot_numero=a.numero and c.aprobado)\n"
                                + "FROM ordenes a where a.documento in (");
                        for (String ss : search) {
                            sb.append(ss).append(",");
                        }
                        sb.delete(sb.length() - 1, sb.length());
                        sb.append(") group by a.numero\n"
                                + "order by 1 desc;");
                        result = DB.instance.executeQueryList(sb.toString());
                    } catch (NumberFormatException | SQLException e) {
                        result = new LinkedList<>();
                    }
                } else if (idx == 4) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("SELECT a.numero, documento, fecha_solicitud,  fecha_entrega, a.razon, estado, total,  \n"
                                + "(select sum(c.monto) from pagos c where c.ot_numero=a.numero and c.aprobado)\n"
                                + "FROM ordenes a where a.documento in (");
                        for (String ss : search) {
                            sb.append("-").append(ss).append(",");
                        }
                        sb.delete(sb.length() - 1, sb.length());
                        sb.append(") group by a.numero\n"
                                + "order by 1 desc;");
                        result = DB.instance.executeQueryList(sb.toString());
                    } catch (NumberFormatException | SQLException e) {
                        result = new LinkedList<>();
                    }
                } else {
                    result = new LinkedList<>();
                }
                KTable table = (KTable) jTable1;
                for (Object[] o : result) {
                    Integer doc = (Integer) o[1];
                    o[1] = doc == null ? "" : doc > 0 ? "Factura " + Main.formatter.format(doc) : "Boleta " + Main.formatter.format(-doc);
                    o[2] = DB.sdf.format(o[2]);
                    o[3] = DB.sdf.format(o[3]);
                    boolean add = (filter == 0 || (filter == 1 && "T".equals(o[5])) || (filter == 2 && "P".equals(o[5])) || (filter == 3 && "A".equals(o[5])));
                    o[5] = "A".equals(o[5]) ? "ANULADA" : "T".equals(o[5]) ? "TERMINADA" : "PENDIENTE";
                    o[7] = o[7] == null ? "" : new Integer(o[7].toString());
                    if (add) {
                        table.addRow(o);
                    }
                }
                jButton10.setVisible(table.getRowCount() > 0);
                informeTable = table;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        search();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            int idx = jTable1.getSelectedRow();
            if (idx != -1) {

                ot.ot = (Integer) jTable1.getValueAt(idx, 0);
                setModalView(true);
                ot.updateData(2);
                ot.setVisible(true);
                setModalView(false);
            }
        }
    }//GEN-LAST:event_jTable1MousePressed

    private void jTable2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MousePressed
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            int idx = jTable2.getSelectedRow();
            if (idx != -1) {
                String val = (String) jTable2.getValueAt(idx, 4);
                if (val != null) {
                    if (val.startsWith("OT:")) {
                        ot.ot = Integer.parseInt(val.substring(3, val.indexOf(" ")).replaceAll("\\.", ""));
                        setModalView(true);
                        ot.updateData(2);
                        ot.setVisible(true);
                        setModalView(false);
                    } else if (val.startsWith("ID:")) {
                        int id = Integer.parseInt(val.substring(3, val.indexOf(" ")).replaceAll("\\.", ""));
                        setModalView(true);
                        caja.audit(id);
                        setModalView(false);
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable2MousePressed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        search();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        String pass1 = new String(jPasswordField2.getPassword());
        String pass2 = new String(jPasswordField3.getPassword());
        String oldpass = new String(jPasswordField4.getPassword());
        String user = Main.usuario;
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(jDialog3, "Debe repetir la clave y esta tiene no puede estar vacia.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                int upd = DB.instance.setPass.executeUpdate(pass1, user, oldpass);
                if (upd == 1) {
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

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (login) {
            jDialog1.setVisible(false);
            jPasswordField2.setText("");
            jPasswordField3.setText("");
            jPasswordField4.setText("");
            jDialog3.setLocationRelativeTo(jButton6);
            jDialog3.setVisible(true);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jDialog3WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog3WindowClosing
        jDialog3.setVisible(false);
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jDialog3WindowClosing

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        try {

            exportTable2PDF(this, jComboBox1.getSelectedItem().toString(), filename, informeTable, 13);
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTable3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MousePressed
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            int idx = jTable3.getSelectedRow();
            if (idx != -1) {
                String val = (String) jTable3.getValueAt(idx, 3);
                if (val != null) {
                    if (val.startsWith("OT:")) {
                        ot.ot = Integer.parseInt(val.substring(3, val.indexOf(" ")).replaceAll("\\.", ""));
                        setModalView(true);
                        ot.updateData(2);
                        ot.setVisible(true);
                        setModalView(false);
                    } else if (val.startsWith("ID:")) {
                        int id = Integer.parseInt(val.substring(3, val.indexOf(" ")).replaceAll("\\.", ""));
                        setModalView(true);
                        caja.audit(id);
                        setModalView(false);
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable3MousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPasswordField jPasswordField3;
    private javax.swing.JPasswordField jPasswordField4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

}
