/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public abstract class RafaFormElement {
    String ElementType = "RafaFormElement";

    // All abstarct method
    public abstract String getName();
    public abstract String getLabel();
    public abstract String getValue();
    public abstract String getType();

    protected void setElementType(String me) {
        this.ElementType = me;
    }

    public String getElementType() {
        return this.ElementType;
    }

}
