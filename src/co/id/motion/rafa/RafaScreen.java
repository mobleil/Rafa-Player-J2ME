/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package co.id.motion.rafa;

import java.util.Vector;

/**
 *
 * @author andrias
 */
public class RafaScreen {
    private String appID = null;
    private Vector meItem = new Vector();

    public String getAppID() {
        return appID;
    }

    public void setAppID(String me) {
        appID = me;
    }

    public void addItem(Object me) {
        meItem.addElement(me);
    }

    public Object getItem(int index) {
        Object item = null;
        if (meItem.size()>0) {
            item = meItem.elementAt(index);
        }
        return item;
    }

    public void removeAll() {
        if (meItem.size()>0) {
            appID = null;
            for (int i=0; i<meItem.size(); i++) {
                String str = meItem.elementAt(i).getClass().getName();
                if (str.equals("RafaList")) {
                    RafaList item = (RafaList) meItem.elementAt(i);
                    item.removeAll();
                } else if (str.equals("RafaForm")) {
                    RafaForm item = (RafaForm) meItem.elementAt(i);
                    item.removeAll();
                }
            }
            meItem.removeAllElements();
        }
    }

    public int elementCount() {
        return meItem.size();
    }

    public RafaForm getForm(String name) {
        RafaForm retval = null;
        String className = null;
        for (int i=0; i<meItem.size(); i++) {
            className = meItem.elementAt(i).getClass().getName();
            if (className.indexOf("RafaForm")>0) {
                RafaForm item = (RafaForm) meItem.elementAt(i);
                if (item.getName().equals(name)) {
                    retval = (RafaForm) meItem.elementAt(i);
                }
                item = null;
            }
        }
        return retval;
    }
}
