/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public class RafaFormLabel extends RafaFormElement {
    private String name = null;
    private String description = null;
    private String value = null;
    private String type = null;
    private String snipset = null;

    public RafaFormLabel() {
        super.setElementType("RafaFormLabel");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String me) {
        this.name = me;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String me) {
        description = me;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String me) {
        this.value = me;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String me) {
        this.type = me;
    }

    public String getLabel() {
        return null;
    }

    public void setSnipset(String me) {
        snipset = me;
    }

    public String getSnipset() {
        return snipset;
    }

}
