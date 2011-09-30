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
public class RafaFormChoice extends RafaFormElement {
    private String name;
    private String label;
    private Vector listItems = new Vector();
    private int selectedIndex = 0;

    public RafaFormChoice() {
        super.setElementType("RafaFormChoice");
    }

    public RafaFormChoice(String me, String lbl) {
        this();
        this.name = me;
        label = lbl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String me) {
        this.name = me;
    }

    public String getLabel() {
        return label;
    }
    
    public void setLabel(String me) {
        label = me;
    }

    public void addItem(String label, String value, String type) {
        RafaFormChoiceElement item = new RafaFormChoiceElement(label, value);
        if (type != null)
            if (type.equals("act")) {
                item.setSelected(true);
            }
        listItems.addElement(item);
    }

    public RafaFormChoiceElement getItem(int index) {
        return (RafaFormChoiceElement) listItems.elementAt(index);
    }

    public RafaFormChoiceElement getLastItem() {
        return (RafaFormChoiceElement) listItems.lastElement();
    }

    public int ElementCount() {
        return listItems.size();
    }

    public void setSelectedIndex(int index) {
        if ((index<listItems.size()) && (listItems.size()>0)) {
            // Unselected choice element before
            this.getItem(selectedIndex).setSelected(false);
            this.getItem(index).setSelected(true);
            this.selectedIndex = index;
        }
    }

    public int getSelectedIndex() {
        if (listItems.size()>0) {
            return this.selectedIndex;
        } else {
            return -1;
        }
    }

    public String getValue() {
        return null;
    }

    public String getType() {
        return null;
    }

    public void removeAll() {
        if (listItems.size()>0)
            listItems.removeAllElements();
    }
}
