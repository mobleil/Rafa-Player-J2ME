/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package co.id.motion.poltektelkom;

import java.util.Calendar;
import java.util.Date;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.TextWidget;

/**
 *
 * @author andrias
 */
public class FlexClock extends Text implements Runnable {

    public void run() {
        while (true) {
            this.refreshClock();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                FlexLog.Write("FlexClock", e.getMessage());
            }
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        try {
            thread.start();
        } catch (Exception e) {
            FlexLog.Write("FlexClock", e.getMessage());
        }
    }

    public void refreshClock() {
        String time = getTime();
        FlexLog.Write("FlexClock", time);
        super.setText(time);
    }
    
    private String addZero(int i, int size) {
        String s = "0000" + i;
        return s.substring(s.length() - size, s.length());
    }

    private String getTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        String time = addZero(c.get(Calendar.HOUR_OF_DAY), 2) + ":" + addZero(c.get(Calendar.MINUTE), 2) + ":" + addZero(c.get(Calendar.SECOND), 2);
        return time;
    }
}
