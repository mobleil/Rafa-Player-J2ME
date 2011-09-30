/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

/**
 *
 * @author andrias
 */
public interface RafaListener {
    public void onError(String message);
    public void onStart();
    public void onFinishUI(RafaScreen rafaScreen);
    public void onFinishNonUI(Object obj);
}
