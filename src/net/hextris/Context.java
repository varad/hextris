package net.hextris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.awt.event.KeyEvent;

/**
 * an application context
 * handler for all kinda things 
 * at the moment only programm properties are kept/loaded/saved
 * @author frank
 *
 */
public class Context extends Hashtable<Object, Object> {

    public enum HexSize {

        NORMAL, BIG
    }
    
    static final long serialVersionUID = 493518487136L;
    static final String cfgFileString = "hextris.cfg";
    static final String KEY_MOVE_LEFT = "key.move.left";
    static final String KEY_MOVE_RIGHT = "key.move.right";
    static final String KEY_MOVE_DOWN = "key.move.down";
    static final String KEY_FALL_DOWN = "key.fall.down";
    static final String KEY_ROTATE_LEFT = "key.rotate.left";
    static final String KEY_ROTATE_RIGHT = "key.rotate.right";
    static final String HEX_SIZE = "hex.size";
    static final String LAST_NAME = "last.name";
    static Context ctx = null;
    static String path = null;
    private static final int[] keys = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_R, KeyEvent.VK_SPACE, KeyEvent.VK_DOWN};
    private static final HexSize DEFAULT_HEX_SIZE = HexSize.NORMAL;

    /**
     * returns the context for this application
     * if neccessary a new context is created and initialized
     * @return
     */
    public static Context getContext() {
        if (ctx == null) {
            try {
                Properties prop = System.getProperties();
                path = prop.getProperty("user.home") + System.getProperty("file.separator") + ".hextris";
                File f = new File(path);
                if (!f.exists()) {
                    f.mkdirs();
                }

                FileInputStream istream = new FileInputStream(path +
                        System.getProperty("file.separator") + cfgFileString);
                ObjectInputStream p = new ObjectInputStream(istream);
                Object o = p.readObject();
                istream.close();
                ctx = (Context) o;
                //initialize keys
                readKeys();
            } catch (Exception ex) {
                System.out.println("could not load context create new one");
                ctx = new Context();
            }
        }
        //System.out.println(ctx);
        return ctx;
    }

    /**
     * read key occupation from hashtable into keys[]-buffer
     *
     */
    public static void readKeys() {
        if (ctx == null) {
            return;
        }

        Object conf = ctx.get(KEY_MOVE_LEFT);
        if (conf != null) {
            keys[0] = ((Integer) conf).intValue();
        }
        conf = ctx.get(KEY_MOVE_RIGHT);
        if (conf != null) {
            keys[1] = ((Integer) conf).intValue();
        }
        conf = ctx.get(KEY_ROTATE_LEFT);
        if (conf != null) {
            keys[2] = ((Integer) conf).intValue();
        }
        conf = ctx.get(KEY_ROTATE_RIGHT);
        if (conf != null) {
            keys[3] = ((Integer) conf).intValue();
        }
        conf = ctx.get(KEY_MOVE_DOWN);
        if (conf != null) {
            keys[4] = ((Integer) conf).intValue();
        }
        conf = ctx.get(KEY_FALL_DOWN);
        if (conf != null) {
            keys[5] = ((Integer) conf).intValue();
        }
    }

    public int[] getKeys() {
        return keys;
    }

    /**
     * save property-hashtble to disk
     *
     */
    public void savePersistProp() {
        try {
            FileOutputStream ostream = new FileOutputStream(path +
                    System.getProperty("file.separator") + cfgFileString);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(ctx);
            p.flush();
            ostream.close();
        } catch (Exception ex) {
            System.out.println("could not save config");
        }
    }



    public HexSize getHexSize() {
        Object val = get(HEX_SIZE);
        if (val != null) {
            HexSize size = HexSize.valueOf(val.toString());
            if (size != null) {
                return size;
            }
        }
        return DEFAULT_HEX_SIZE;
    }

    public String getLastName() {
        Object val = get(LAST_NAME);
        if (val == null) {
            return "";
        }
        return (String)val;
    }

    public void setProperty(String property, Object value) {
        put(property, value);
        savePersistProp();
    }

    //TODO:refaktor this class, add enum
}
