/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public class RafaListElement {
    private String name = null;
    private String value = null;
    private String description = null;
    private String snipset = null;
    private String type = null;
    private String img = null;

    public String getName() {
        return this.name;
    }

    public void setName(String me) {
        this.name = me;
    }

    public String getType() {
        return type;
    }

    public void setType(String me) {
        type = me;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String me) {
        this.value = me;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String me) {
        this.description = me;
    }

    public String getSnipset() {
        return snipset;
    }

    public void setSnipset(String value) {
        snipset = value;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String value) {
        img = value;
    }

}
