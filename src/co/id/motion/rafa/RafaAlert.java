/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public class RafaAlert {
    private String method = null;
    private String uuid = null;
    private String textID = null;
    private String textValue = null;

    public String getMethod() {
        return method;
    }

    public void setMethod(String value) {
        method = value;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String value) {
        uuid = value;
    }

    public String getTextID() {
        return textID;
    }

    public void setTextID(String value) {
        textID = value;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String value) {
        textValue = value;
    }

}
