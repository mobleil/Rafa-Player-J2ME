/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public class RafaFormChoiceElement {
    private String label = null;
    private String value = null;
    private boolean selected = false;

    public RafaFormChoiceElement(String me, String va) {
        this.label = me;
        this.value = va;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setSelected(boolean me) {
        this.selected = me;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String me) {
        this.label = me;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String me) {
        this.value = me;
    }
}
