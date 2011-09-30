/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

import co.id.motion.rafa.RafaFormTextField;
import co.id.motion.rafa.RafaFormCheck;
import java.util.Vector;

/**
 *
 * @author andrias
 */
public class RafaForm {
    private String method = null;
    private String name = null;
    private String title = null;
    private String help = null;
    private Vector listItems = new Vector();
    public final static String className = "RafaForm";

    public String getMethod() {
        return method;
    }

    public void setMethod(String value) {
        method = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
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
    
    public void addItem(RafaFormTextField item) {
        listItems.addElement(item);
    }

    public void addItem(RafaFormCheck item) {
        listItems.addElement(item);
    }

    public void addItem(RafaFormChoice item) {
        listItems.addElement(item);
    }

    public void addItem(RafaFormLabel item) {
        listItems.addElement(item);
    }

    public RafaFormElement getItem(int index) {
        return (RafaFormElement) listItems.elementAt(index);
    }

    public RafaFormElement getLastItem() {
        return (RafaFormElement) listItems.lastElement();
    }

    public int elementCount() {
        return listItems.size();
    }

    public void removeAll() {
        for (int i=0; i<listItems.size(); i++) {
            String str = listItems.elementAt(i).getClass().getName();
            if (str.equals("RafaFormChoice")) {
                RafaFormChoice item = (RafaFormChoice) listItems.elementAt(i);
                item.removeAll();
            }
            listItems.removeAllElements();
        }
    }
}
