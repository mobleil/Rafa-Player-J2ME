/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

import java.util.Vector;

/**
 *
 * @author andrias
 */
public class RafaList {
    private String method = null;
    private String title = null;
    private String help = null;
    private boolean isClear = false;
    private Vector listItems = new Vector();
    public final static String className = "RafaList";

    public String getMethod() {
        return method;
    }

    public void setMethod(String value) {
        method = value;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String value) {
        title = value;
    }
    
    public String getHelp() {
        return help;
    }
    
    public void setHelp(String value) {
        help = value;
    }

    public void setClear(boolean value) {
        isClear = value;
    }
    
    public boolean getClear() {
        return isClear;
    }

    public void addItem(RafaListElement item) {
        listItems.addElement(item);
    }

    public RafaListElement getItem(int index) {
        return (RafaListElement) listItems.elementAt(index);
    }

    public int elementCount() {
        return listItems.size();
    }

    public void removeAll() {
        if (listItems.size()>0)
            listItems.removeAllElements();
    }
}
