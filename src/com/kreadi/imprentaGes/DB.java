package com.kreadi.imprentaGes;

import com.kreadi.imprentaGes.gui.Main;
import static com.kreadi.imprentaGes.gui.Main.c1;
import static com.kreadi.imprentaGes.gui.Main.c2;
import static com.kreadi.imprentaGes.gui.Main.c3;
import com.kreadi.jdbc.DBManager;
import com.kreadi.jdbc.SQL;
import com.kreadi.swing.KConfig;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

@SuppressWarnings("UseSpecificCatch")
public class DB extends DBManager {

    public static DB instance;
    public final String caja;
    public String usuario;
    public static long timeSync;

    //Prepared statements definidos en DB.sql
    public SQL createDB, insertUsuario, updateUsuario, selectUsuarios, existUsuario, login, userInfo,
            selectCajas, selectCaja, updateCaja, insertCaja, existCaja, getUsuariosCaja, setPass, setPass2,
            getPrecios, insertPrecios, delPrecios, getPrecio, getParametros, updateParam, getParametro, nextBoleta,
            insertTurno, getCajaTurno, getDBTime, selectOT, setOTENTREGA, countParams, setParam, getLastId, getPagosDiario,
            insertOT, updateOT, getLastOT, getLastTurno2, insertCliente, updateCliente, getCliente, existCliente, getRutFromRazon,
            insertDetalle, delDetalle, selectDetalle, selectClienteRut, selectClienteRazon, getAbonoSaldo, getTurnoAudit, aprovPagoPrev, getRutFacturacion,
            setOTESTADO, insertPago, getPagos, delPago, aprovPago, removeNoAprovPago, getPagoTotal, getPrevOT, getNextOT, updateOTFacturacion,
            nextFactura, setDocumento, existDocumentoBancario, existeOT, insertAuditoria, getAuditoriaDiaria, getOTDiaria, existDocumento, deleteOTFacturacion,
            getCuadratura, getTurnoApertura, getIngresoEgreso, getFormatoNames, renameFormato, newFormato, removeFormato, getFormato, updateFormato;

    public static int lineas;
    public static double iva;
    public static int otStart;
    public static int facturaStart;
    public static int boletaStart;
    public static boolean facturaAuto;
    public static boolean boletaAuto;
    public static SimpleDateFormatThreadSafe sdf;//formato de fecha
    public static SimpleDateFormatThreadSafe sdfh;//Formato fecha hora

    /**
     * Inicia la el dbmanager indicando la clave
     *
     * @param servidor
     * @param puerto
     * @param usuario
     * @param clave
     * @param caja
     * @throws SQLException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public DB(String servidor, int puerto, String usuario, String clave, String caja) throws SQLException, IOException, IllegalArgumentException, IllegalAccessException {
        //super("org.h2.Driver", "sa", clave, new String[]{"jdbc:h2:.\\data\\db;DB_CLOSE_ON_EXIT=FALSE"}, null, true);
        //super("org.h2.Driver", "sa", clave, new String[]{"jdbc:h2:" + (ssl ? "ssl" : "tcp") + "://" + servidor + ":" + puerto + "/.\\db;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE"}, null, false);
        super("org.postgresql.Driver", usuario, clave, new String[]{"jdbc:postgresql://" + servidor + ":" + puerto + "/impges"}, null, true, 2);

        this.caja = caja;
        delPrecios.getConnection().setAutoCommit(false);
        long serverTime = ((BigDecimal) getDBTime.executeFirstQuery()[0]).longValue();
        long clientTime = System.currentTimeMillis();
        timeSync = clientTime - serverTime;//Al iniciar se sincroniza calcula la diferencia de time con el servidor
        try (ResultSet rs = executeQuery("SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'usuarios'")) {
            if (!rs.next()) {
                createDB.execute();
            }
        }
    }

    public void auditoria(String texto, String subtexto) {
        try {
            insertAuditoria.executeUpdate(usuario, caja, texto, subtexto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna la fecha syncronizada inicialmente con el servidor
     *
     * @return
     */
    public Date getDate() {
        return new Date(System.currentTimeMillis() - timeSync);
    }

    private static byte[] encrypt(String password, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        byte[] salt = {
            (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
            (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
        };

        int count = 20;
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(String password, byte[] encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        byte[] salt = {
            (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
            (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
        };

        int count = 20;
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

        return cipher.doFinal(encryptedData);
    }

    private final static File configFile = new File("configClient.dat");

    public static final KConfig getConfig(boolean exit) {
        return new KConfig("install.png", 128, 380, 320, exit, "Instalación Imprenta Ges", "icono.png", " Guardar Configuración Local ", configFile, 3, 16,
                new String[]{"Servidor", "Puerto", "Usuario", "Clave", "Caja"},
                new String[]{"Nombre o IP Servidor", "Puerto DB", "Usuario DB", "Clave DB", "Nombre de la caja"},
                new Class[]{String.class, Integer.class, String.class, char[].class, String.class},
                new Object[]{"127.0.0.1", 5432, "postgres", new char[0], "caja1"}
        );
    }

    static {
        try {
            File logDir=new File("logs");
            if (!logDir.exists()){
                logDir.mkdirs();
            }
            SimpleDateFormatThreadSafe sdf=new SimpleDateFormatThreadSafe("yyyy-MM-dd_hh-mm");
            File _outFile = new File(logDir,"out.log");
            File _errFile = new File(logDir,"err.log");
            if (_outFile.exists()){
                String name="out."+sdf.format(new Date(Files.readAttributes(_outFile.toPath(), BasicFileAttributes.class).creationTime().toMillis()))+".log";
                _outFile.renameTo(new File(logDir,name));
            }
            if (_errFile.exists()){
                String name="err."+sdf.format(new Date(Files.readAttributes(_errFile.toPath(), BasicFileAttributes.class).creationTime().toMillis()))+".log";
                _errFile.renameTo(new File(logDir,name));    
            }
            File outFile = new File(logDir,"out.log");
            File errFile = new File(logDir,"err.log");
            outFile.createNewFile();
            PrintStream outPs = new PrintStream(outFile);
            System.setOut(outPs);
            errFile.createNewFile();
            PrintStream errPs = new PrintStream(errFile);
            System.setErr(errPs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        KConfig kc;
        boolean ok = false;
        while (!ok) {
            try {
                if (configFile.exists()) {
                    kc = new KConfig(configFile, 16);
                } else {
                    kc = getConfig(false);
                    kc.setVisible(true);
                }
                kc.waitForValues();
                Object[] par = kc.getValues();
                instance = new DB((String) par[0], (int) par[1], (String) par[2], new String((char[]) par[3]), (String) par[4]);
                ok = true;
            } catch (Exception e) {
                System.err.println(e);
                if (e.getClass()==org.apache.commons.dbcp.SQLNestedException.class){
                    JOptionPane.showMessageDialog(null, "Error de acceso a datos\nDebe iniciar el servidor", "Error ", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                } else {
                JOptionPane.showMessageDialog(null, "Error de acceso a datos\nDebe reconfigurar el acceso a los datos", "Error ", JOptionPane.ERROR_MESSAGE);
                configFile.delete();
                }
            }
        }

        //-----------------------------------------------------------------------------------------------------------------------------------
        loadParams();

    }

    public static void loadParams() {

        try {
            lineas = Integer.parseInt(DB.instance.getParametro.executeFirstQuery("ITEMS ORDEN")[0].toString());
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            iva = Double.parseDouble(DB.instance.getParametro.executeFirstQuery("IVA")[0].toString()) / 100d;
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            otStart = Integer.parseInt(DB.instance.getParametro.executeFirstQuery("OT INICIO")[0].toString());
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            facturaStart = Integer.parseInt(DB.instance.getParametro.executeFirstQuery("FACTURA INICIO")[0].toString());
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            boletaStart = Integer.parseInt(DB.instance.getParametro.executeFirstQuery("BOLETA INICIO")[0].toString());
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            boletaAuto = DB.instance.getParametro.executeFirstQuery("BOLETA AUTOMATICO")[0].toString().toLowerCase().trim().equals("v");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            facturaAuto = DB.instance.getParametro.executeFirstQuery("FACTURA AUTOMATICO")[0].toString().toLowerCase().trim().equals("v");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            String s = DB.instance.getParametro.executeFirstQuery("FORMATO FECHA")[0].toString();
            sdf = new SimpleDateFormatThreadSafe(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String s = DB.instance.getParametro.executeFirstQuery("FORMATO FECHA HORA")[0].toString();
            sdfh = new SimpleDateFormatThreadSafe(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TimeZone timezone = TimeZone.getDefault();

    public static void main(String args[]) {
        TimeZone.setDefault(timezone);
        //SwingTools.setNimbusLookAndFeel(c1, c2, c3);
        UIDefaults uiDefaults = UIManager.getDefaults();
        uiDefaults.put("Panel.background", c2);
        uiDefaults.put("Button.background", c1);
        uiDefaults.put("Button.select", c3);
        uiDefaults.put("Viewport.background", Color.white);
        uiDefaults.put("TableHeader.background", c1);
        uiDefaults.put("Table.background", c2);
        uiDefaults.put("TabbedPane.selected", c3);
        uiDefaults.put("TabbedPane.unselectedBackground", c1);
        uiDefaults.put("Table.selectionBackground", c3);
        uiDefaults.put("ScrollBar.shadow", c3);
        uiDefaults.put("ScrollBar.background", c2);
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

}
