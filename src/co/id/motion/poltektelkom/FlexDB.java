/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.poltektelkom;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author andrias
 */
public class FlexDB {
    public static final FlexDB instance = new FlexDB();

    private RecordStore rs = null;
    private String username = null;
    private String password = null;
    private boolean isloaded = false;

    public FlexDB() {
        try {
            rs = RecordStore.openRecordStore("flexapps", true);
        } catch (RecordStoreException ex) {
            System.out.println("FlexCache::RecordStore "+ex.getMessage());
        }
    }

    /**
     * Check if username already save on local database
     * @return true if found, false if not found
     */
    public boolean checkDB() {
        if (!this.isloaded) {
            this.loadDB();
        }
        if (username != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Read from database
     * @return true if successfully, false if not
     */
    public boolean loadDB() {
        boolean retval = false;
        try {
            if (rs.getNumRecords()>0) {
                byte b[] = rs.getRecord(1);
                if (b.length>0) {
                    this.username = new String(b, 0, b.length);
                    byte c[] = rs.getRecord(2);
                    if (c.length>0) {
                        this.password = new String(c, 0, c.length);
                        retval = true;
                        this.isloaded = true;
                    }
                }
            }
        } catch (RecordStoreException e) {
            System.out.println("FlexCache::RecordStore "+e.getMessage());
        }
        return retval;
    }

    /**
     * Save to database
     * @return true if successfully, false if not
     */
    public boolean saveDB() {
        boolean retval = false;
        int recId = 0;
        try {
            if (username != null) {
                byte b[] = this.username.getBytes();
                recId = rs.addRecord(b, 0, b.length);
                if (password != null) {
                    byte c[] = this.password.getBytes();
                    recId = rs.addRecord(c, 0, c.length);
                    retval = true;
                }
            }
        } catch (RecordStoreException e) {
            System.out.println("FlexCache::RecordStore "+e.getMessage());
        }
        return retval;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String value) {
        this.password = value;
    }
}
