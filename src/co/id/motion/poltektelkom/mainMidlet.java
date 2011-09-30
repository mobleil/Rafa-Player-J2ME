package co.id.motion.poltektelkom;

import co.id.motion.frame.CodeFrame;
import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixMIDlet;
import org.kalmeo.kuix.widget.Desktop;

/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

/**
 * @author andrias
 */
public class mainMidlet extends KuixMIDlet {

    public void initDesktopStyles() {
        Kuix.loadCss("style.css");
    }

    public void initDesktopContent(Desktop dsktp) {
        Kuix.getFrameHandler().pushFrame(CodeFrame.instance);
    }

}
