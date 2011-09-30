/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package co.id.motion.poltektelkom;

/**
 *
 * @author andrias
 */
public class FlexLog {
    public final static String state = "INFO";

    public static void Write(String name, String msg) {
        if (state.equals("INFO")) {
            System.out.println(name+": "+msg);
        }
    }
}
