/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public class RafaFormCheck extends RafaFormElement {
    private String name = null;
    private String label = null;
    private String value = null;

    public RafaFormCheck() {
        super.setElementType("RafaFormCheck");
    }

    public RafaFormCheck(String me, String va) {
        this();
        this.name = me;
        this.label = va;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String me) {
        this.name = me;
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

    public String getType() {
        return null;
    }
}
