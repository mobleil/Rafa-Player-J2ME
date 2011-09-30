/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.poltektelkom;

import co.id.motion.rafa.RafaForm;
import co.id.motion.rafa.RafaList;
import co.id.motion.rafa.RafaScreen;
import java.util.Vector;

/**
 *
 * @author andrias
 */
public class FlexCache {
    private Vector cache = new Vector();
    private RafaScreen screen;

    public void removeAll() {
        if (cache.size()>0) {
            cache.removeAllElements();
            screen = null;
        }
    }

    public void newScreen() {
        screen = new RafaScreen();
    }

    public void addHead(String name) {
        if (screen == null)
            newScreen();
        screen.setAppID(name);
    }

    public void addList(RafaList me) {
        if (screen == null)
            newScreen();
        screen.addItem(me);
    }

    public void addForm(RafaForm me) {
        if (screen == null)
            newScreen();
        screen.addItem(me);
    }

    public void saveScreen() {
        cache.addElement(screen);
        screen = null;
    }

    public RafaScreen getItem() {
        RafaScreen obj = null;
        if (cache.size()>0)
            obj = (RafaScreen) cache.lastElement();
        return obj;
    }

    public RafaScreen getItemBack() {
        RafaScreen retval = null;
        if (cache != null) {
            if (cache.size()==1) {
                retval = getItem();
            } else {
                int index = cache.size() - 1;
                // Erase all screen element
                RafaScreen item = (RafaScreen) cache.lastElement();
                item.removeAll();
                cache.removeElementAt(index);
                retval = getItem();
            }
        }
        return retval;
    }

    public int elementCount() {
        return cache.size();
    }
}
