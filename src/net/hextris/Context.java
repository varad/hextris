package net.hextris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.awt.event.KeyEvent;

/**
 * An application context where program properties are kept/loaded/saved.
 * @author frank
 * @author Radek Varbuchta
 */
public class Context extends Hashtable<Context.IProperty, Object> {

    public enum HexSize {

        NORMAL, BIG
    }

    public interface IProperty {
    }

    public enum Property implements IProperty {

        HEX_SIZE, LAST_NAME
    }

    public enum Key implements IProperty {

        MOVE_LEFT(KeyEvent.VK_LEFT),
        MOVE_RIGHT(KeyEvent.VK_RIGHT),
        MOVE_DOWN(KeyEvent.VK_SPACE),
        FALL_DOWN(KeyEvent.VK_DOWN),
        ROTATE_LEFT(KeyEvent.VK_UP),
        ROTATE_RIGHT(KeyEvent.VK_R);
        private int intValue;

        Key(int intValue) {
            this.intValue = intValue;
        }

        private int intValue() {
            return intValue;
        }
    }
    static final long serialVersionUID = 493518487136L;
    private static Context ctx = null;
    private static String DIR_PATH;
    private static final String CFG_FILE_NAME = "hextris.cfg";
    private static final HexSize DEFAULT_HEX_SIZE = HexSize.NORMAL;
    private static final String DEFAULT_LAST_NAME = "";
    private boolean access;

    /**
     * Creates a new config file with default values.
     * @throws java.io.IOException
     */
    private Context() throws IOException {
        try {
            System.getProperty("user.home");
            access = true;
        } catch (SecurityException ex) {
            access = false;
        }

        if (access) {
            File file = Context.getConfigFile();
            if (!file.exists()) {
                file.createNewFile();
            }
        }

        for (Key key : Key.values()) {
            super.put(key, key.intValue());
        }
        for (Property p : Property.values()) {
            switch (p) {
                case HEX_SIZE:
                    super.put(Property.HEX_SIZE, DEFAULT_HEX_SIZE);
                    break;
                case LAST_NAME:
                    super.put(Property.LAST_NAME, DEFAULT_LAST_NAME);
                    break;
            }
        }

        if (access) {
            save();
        }
    }

    /**
     * Returns file that represents config file. If a directory in which
     * the file resides doesn't exist, it's created.
     * @return
     */
    private static File getConfigFile() {
        File dir = new File(DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(DIR_PATH + System.getProperty("file.separator") + CFG_FILE_NAME);
    }

    /**
     * Returns the context for this application.
     * If neccessary a new context is created and initialized.
     * @return context
     */
    public static Context getContext() {
        if (ctx == null) {
            try {
                DIR_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + ".hextris";
                FileInputStream istream = new FileInputStream(getConfigFile());
                ObjectInputStream p = new ObjectInputStream(istream);
                Object o = p.readObject();
                istream.close();
                ctx = (Context) o;
            } catch (Exception ex) {
                System.out.println("could not load context create new one");
                try {
                    ctx = new Context();
                } catch (IOException ex1) {
                    System.out.println("cannot create config file");
                }
            }
        }
        return ctx;
    }

    /**
     * Saves properties to the disk.
     */
    private void save() {
        if (!access) {
            return;
        }

        try {
            FileOutputStream ostream = new FileOutputStream(getConfigFile());
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(this);
            p.flush();
            ostream.close();
        } catch (Exception ex) {
            System.out.println("could not save config");
        }

    }

    /**
     * Returs hexagons size.
     * @return hexagons size
     */
    public HexSize getHexSize() {
        Object val = get(Property.HEX_SIZE);
        if (val != null) {
            HexSize size = HexSize.valueOf(val.toString());
            if (size != null) {
                return size;
            }

        }
        return DEFAULT_HEX_SIZE;
    }

    /**
     * Returns the last name which got to the high score list.
     * @return
     */
    public String getLastName() {
        Object val = get(Property.LAST_NAME);
        if (val == null) {
            return "";
        }

        return (String) val;
    }

    /**
     * Returns integer representation of the given key.
     * @param key
     * @return integer representaion of the given key
     */
    public int getKeyValue(Key key) {
        return Integer.valueOf(get(key).toString());
    }

    /**
     * Puts key-value property into the context and permanently saves changes.
     * {@inheritDoc}
     */
    @Override
    public synchronized Object put(IProperty property, Object value) {
        Object retVal = super.put(property, value);
        if (access) {
            save();
        }
        return retVal;
    }
}
