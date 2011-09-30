/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.poltektelkom;

import java.io.InputStream;

/**
 *
 * @author andrias
 */
public interface FlexConnectionListener {
    public void onError(String message);
    public void onStart(Object adapter);
    public void onFinish(Object adapter, InputStream content);
}
