/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package co.id.motion.frame;

import co.id.motion.poltektelkom.FlexClock;
import co.id.motion.poltektelkom.FlexConnection;
import co.id.motion.poltektelkom.FlexConnectionListener;
import co.id.motion.poltektelkom.FlexDB;
import co.id.motion.poltektelkom.FlexLog;
import co.id.motion.rafa.RafaAlert;
import co.id.motion.rafa.RafaForm;
import co.id.motion.rafa.RafaFormChoice;
import co.id.motion.rafa.RafaFormChoiceElement;
import co.id.motion.rafa.RafaFormElement;
import co.id.motion.rafa.RafaFormLabel;
import co.id.motion.rafa.RafaList;
import co.id.motion.rafa.RafaListener;
import co.id.motion.rafa.RafaParser;
import co.id.motion.rafa.RafaRedirect;
import co.id.motion.rafa.RafaScreen;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.lcdui.Image;
import org.kalmeo.kuix.core.*;
import org.kalmeo.kuix.widget.*;
import org.kalmeo.kuix.widget.Menu.MenuPopup;
import org.kalmeo.util.frame.*;

/**
 *
 * @author andrias
 */
public class CodeFrame implements Frame, RafaListener {
    // static instance of MainFrame
    public static CodeFrame instance = new CodeFrame();

    // Initilize Object
    private Screen screen = null;
    private int activeScreen = 0;
    private RafaParser rafaParser = new RafaParser();
    private FlexDB flexDB = new FlexDB();
    private boolean onAuthenticate = false;
    private boolean onLoading = false;
    
    public boolean onMessage(Object identifier, Object[] arguments) {
        if ((onLoading) && (!identifier.equals("askExit")))
            return true;
        if (identifier.equals("doLogin")) {
            doLogin();
            return false;
        } else if (identifier.equals("askExit")) {
            // show askQuit box wich call onMessage method with "quit" name and null arguments
            Kuix.alert(	Kuix.getMessage("ASK_QUIT"),
                KuixConstants.ALERT_QUESTION | KuixConstants.ALERT_YES | KuixConstants.ALERT_NO,
                "!exit",
                null);
            return false;
        } else if (identifier.equals("menuMarket")) {
            String URL = "/catalog";
            doOpen(URL);
            return false;
        } else if (identifier.equals("showMyapp")) {
            String URL = "/userpoltek/app/"+flexDB.getUsername();
            doOpen(URL);
            return false;
        } else if (identifier.equals("addMyapp")) {
            String URL = "/userpoltek/addapp/"+
                    flexDB.getUsername()+"/"+rafaParser.getAppID();
            doOpen(URL);
            return false;
        } else if (identifier.equals("askRun")) {
            PopupBox pb = new PopupBox();
            pb.setId("poprun");
            pb.add(new Text().setText(Kuix.getMessage("RUN")));
            TextField tf = new TextField();
            tf.setId("appid");
            pb.add(tf);
            MenuItem mi1 = pb.getFirstMenuItem();
            mi1.add(new Text().setText(Kuix.getMessage("VALIDATE")));
            mi1.setOnAction("doRun(#appid.text)");
            pb.getSecondMenuItem().add(new Text().setText(Kuix.getMessage("CANCEL")));
            Kuix.getCanvas().getDesktop().addPopup(pb);
            return false;
        } else if (identifier.equals("doHelp")) {
            doHelp();
            return false;
        } else if (identifier.equals("doAbout")) {
            doAbout();
            return false;
        } else if (identifier.equals("doRun")) {
            doRun(arguments);
            return false;
        } else if (identifier.equals("doForm")) {
            doForm(arguments);
            return false;
        } else if (identifier.equals("doList")) {
            doList(arguments);
            return false;
        } else if (identifier.equals("doBack")) {
            doBack();
            return false;
        } else if (identifier.equals("doAdvertise")) {
            doAdvertise(arguments);
            return false;
        }
        return true;
    }

    public void onAdded() {
        // Initialize RMS;
        flexDB.loadDB();
        // Initialize screen
        initScreen();
        // Initialize splash
        Widget splash = Kuix.loadWidget("splash.xml", null);
        Kuix.splash(2000, splash, "doLogin");
        // Initialize parser
        rafaParser.setListener(this);
        // Init state is authenticate
        onAuthenticate = true;
    }

    public void onRemoved() {
        System.out.println("Removed...");
    }

    public void onStart() {
        onLoading = true;
        Screen.ScreenBar screenBar = getScreen().getBottomBar();
        buildPopupbox();
    }

    public void onError(String message) {
        // Check if loading popup active
        Widget popup = getScreen().getWidget("loading");
        if (popup != null)
            popup.remove();
        onLoading = false;
        Kuix.alert(message);
    }

    public void onFinishUI(RafaScreen rafaScreen) {
        // Check if loading popup active
        Widget popup = getScreen().getWidget("loading");
        if (popup != null)
            popup.remove();
        if ((onAuthenticate) && (!rafaParser.getAppID().equals("userpoltek"))) {
            flexDB.saveDB();
            onAuthenticate = false;
        }
        flipScreen();
        render(rafaScreen);
        renderScreen("right");
        onLoading = false;
    }

    public void onFinishNonUI(Object obj) {
        // Check if loading popup active
        Widget popup = getScreen().getWidget("loading");
        if (popup != null)
            popup.remove();
        String str = obj.getClass().getName();
        if (str.indexOf("RafaAlert")>0) {
            RafaAlert meAlert = (RafaAlert) obj;
            renderAlert(meAlert);
        } else if (str.indexOf("RafaRedirect")>0) {
            RafaRedirect meRedirect = (RafaRedirect) obj;
            renderRedirect(meRedirect);
        }
        onLoading = false;
    }

    /**
     * RAFA Listener
     * @param uuid
     */

    public ScrollPane buildScreen(String type) {
        // Default title screen
        String appID = rafaParser.getAppID();
        if (appID == null)
            getScreen().setTitle("PoltekTelkom");
        else {
            getScreen().setTitle(appID);
        }

        ScrollPane sp = new ScrollPane();
        sp.setId("content");
        getScreen().add(sp);
        // Add menu
        getScreen().getFirstMenu().add(new Text().setText(Kuix.getMessage("START")));
        // Add clock
        /*
        FlexClock clock = new FlexClock();
        getScreen().getBottomBar().add(clock);
        clock.start();
        */
        
        MenuPopup menu = getScreen().getFirstMenu().getPopup();
        // Check if screen initialize
        if (type.equals("init")) {
            MenuItem menu1 = new MenuItem();
            menu1.add(new Text().setText(Kuix.getMessage("LOGIN")));
            menu1.setOnAction("doLogin");
            menu.add(menu1);
            // Add menu separatort
            Widget separator = new Widget();
            separator.setStyleClass("separator");
            menu.add(separator);
        } else if (type.equals("normal")) {
            MenuItem menu1 = new MenuItem();
            menu1.setOnAction("menuMarket");
            menu1.add(new Text().setText(Kuix.getMessage("MARKET")));
            menu.add(menu1);
            MenuItem menu3 = new MenuItem();
            menu3.setOnAction("askRun");
            menu3.add(new Text().setText(Kuix.getMessage("RUN")));
            menu.add(menu3);
            // Add menu separatort
            Widget separator = new Widget();
            separator.setStyleClass("separator");
            menu.add(separator);
            // Create second menu back only if cache more than one
            if (rafaParser.elementCount()>1) {
                getScreen().getSecondMenu().add(
                        new Text().setText(Kuix.getMessage("BACK")));
                getScreen().getSecondMenu().setOnAction("doBack");
            }
        } else if (type.equals("market")) {
            MenuItem menu3 = new MenuItem();
            menu3.setOnAction("askRun");
            menu3.add(new Text().setText(Kuix.getMessage("RUN")));
            menu.add(menu3);
            // Add menu separatort
            Widget separator = new Widget();
            separator.setStyleClass("separator");
            menu.add(separator);
            // Create second menu back
            getScreen().getSecondMenu().add(
                    new Text().setText(Kuix.getMessage("BACK")));
            getScreen().getSecondMenu().setOnAction("doBack");
        } else if (type.equals("myapp")) {
            MenuItem menu1 = new MenuItem();
            menu1.setOnAction("menuMarket");
            menu1.add(new Text().setText(Kuix.getMessage("MARKET")));
            menu.add(menu1);
            MenuItem menu3 = new MenuItem();
            menu3.setOnAction("askRun");
            menu3.add(new Text().setText(Kuix.getMessage("RUN")));
            menu.add(menu3);
            // Add menu separatort
            Widget separator = new Widget();
            separator.setStyleClass("separator");
            menu.add(separator);
            // Create second menu back
            getScreen().getSecondMenu().add(
                    new Text().setText(Kuix.getMessage("BACK")));
            getScreen().getSecondMenu().setOnAction("doBack");
        } else if (type.equals("adv")) {
            // Create second menu back
            getScreen().getSecondMenu().add(
                    new Text().setText(Kuix.getMessage("BACK")));
            getScreen().getSecondMenu().setOnAction("doBack");
        }

        MenuItem menu5 = new MenuItem();
        menu5.setOnAction("doHelp");
        menu5.add(new Text().setText(Kuix.getMessage("HELP")));
        menu.add(menu5);
        MenuItem menu6 = new MenuItem();
        menu6.setOnAction("doAbout");
        menu6.add(new Text().setText(Kuix.getMessage("ABOUT")));
        menu.add(menu6);
        MenuItem menu4 = new MenuItem();
        menu4.setOnAction("askExit");
        menu4.add(new Text().setText(Kuix.getMessage("SHUTDOWN")));
        menu.add(menu4);
        return sp;
    }

    public Widget buildPopupbox() {
        //PopupBox pb = new PopupBox();
        Widget container = new Widget();
        //pb.add(container);
        container.setId("loading");
        container.add(new Text().setText(Kuix.getMessage("LOADING")));
        Picture pic = new Picture();
        try {
            pic.setImage(Image.createImage("/img/loading.png"));
        } catch (IOException ex) {
            Kuix.alert(ex.getMessage());
        }
        pic.setFrameHeight(16);
        pic.setFrameWidth(16);
        container.add(pic);
        return container;
    }

    /**
     * Get active screen
     * @return
     */
    public Screen getScreen() {
        if (screen==null) {
            screen = new Screen();
        }
        return screen;
    }

    /*
    public void removeScreen() {
        screen[activeScreen].removeAll();
        screen[activeScreen].remove();
        screen[activeScreen] = null;
    }
     *
     */

    public void flipScreen() {
        if (screen != null) {
            screen = null;
            screen = new Screen();
        }
    }


    public void renderScreen(String slide) {
        getScreen().parseAuthorStyle("transition:slide("+slide+")");
        getScreen().setCurrent();
        //Kuix.getCanvas().repaintNextFrame();
    }

    public void initScreen() {
        ScrollPane sp = buildScreen("init");
        getScreen().setCurrent();
    }

    public void render(RafaScreen rafaScreen) {
        ScrollPane sp = null;
        // If onAuthenticate screen without back button
        if (onAuthenticate)
            sp = buildScreen("auth");
        else
            sp = buildScreen("normal");
        for (int i=0; i<rafaScreen.elementCount(); i++) {
            String className = rafaScreen.getItem(i).getClass().getName();
            if (className.indexOf("RafaList")>0) {
                RafaList obj = (RafaList) rafaScreen.getItem(i);
                renderList(obj, sp);
            } else if (className.indexOf("RafaForm")>0) {
                RafaForm obj = (RafaForm) rafaScreen.getItem(i);
                renderForm(obj, sp);
            }
        }
    }

    public void renderList(RafaList meList, ScrollPane sp) {
        // Check clear cache
        if (meList.getClear()) rafaParser.clearCache();
        if (meList.getTitle() != null) {
            Text head = new Text();
            head.setId("listtitle");
            head.setText(meList.getTitle());
            sp.add(head);
        }
        List list = new List();
        sp.add(list);
        boolean isExpand = false;
        String doExpand = "padding: 0 6 0 0";
        for (int i=0; i<meList.elementCount(); i++) {
            ActionWidget listItem = new ActionWidget("listitem");
            if (meList.getItem(i).getValue() != null) {
                String arguments = meList.getMethod()+","+meList.getItem(i).getValue();
                listItem.setOnAction("doList("+arguments+")");
            }
            if (meList.getItem(i).getType() != null) {
                if (meList.getItem(i).getType().equals("adv")) {
                    String arguments = meList.getItem(i).getValue();
                    listItem.setId("adv");
                    listItem.setOnAction("doAdvertise("+arguments+")");
                }
            // Arrow icon on the right
            } else if (meList.getItem(i).getValue() != null) {
                listItem.setId("expand");
                isExpand = true;
            }
            // List item title
            Text title = new Text();
            title.setId("itemtitle");
            title.setText(meList.getItem(i).getName());
            listItem.add(title);
            // List item description
            if (meList.getItem(i).getDescription() != null) {
                TextArea desc = new TextArea();
                desc.setId("itemdesc");
                desc.setText(meList.getItem(i).getDescription());
                if (isExpand)
                    desc.parseAuthorStyle(doExpand);
                listItem.add(desc);
            }
            if (meList.getItem(i).getSnipset() != null) {
                Text item2 = new Text();
                item2.setId("itemsnipset");
                item2.setText(meList.getItem(i).getSnipset());
                if (isExpand)
                    item2.parseAuthorStyle(doExpand);
                listItem.add(item2);
            }
            list.add(listItem);
            /*
            if (meList.getItem(i).getImg() != null) {
                Widget container = new Widget();
                list.add(container);
                FlexConnection conn = new FlexConnection(container);
                conn.setListener( new FlexConnectionListener() {

                    public void onError(String message) {
                    }

                    public void onStart(Object adapter) {
                        Widget container = (Widget) adapter;
                        Picture pic = new Picture();
                        pic.setId("img");
                        try {
                            pic.setImage(Image.createImage("/img/loading.png"));
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        pic.setFrameHeight(16);
                        pic.setFrameWidth(16);
                        container.add(pic);
                    }

                    public void onFinish(Object adapter, InputStream content) {
                        Widget container = (Widget) adapter;
                        Picture pic = (Picture) container.getWidget("img");
                        pic.setFrameWidth(0);
                        pic.setFrameHeight(0);
                        try {
                            pic.setImage(Image.createImage(content));
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }

                });
                int mWidth = Kuix.getCanvas().getWidth() - 21;
                FlexLog.Write("CodeFrame", "Width: " + Integer.toString(mWidth));
                String URL = "/image/load/" + meList.getItem(i).getImg() + "/" + Integer.toString(mWidth);
                conn.Connect(URL, null);
            }
            */
        }
        if (meList.getHelp() != null) {
            TextArea help = new TextArea();
            help.setId("listhelp");
            help.setText(meList.getHelp());
            sp.add(help);
        }
    }

    public void renderForm(RafaForm meForm, ScrollPane sp) {
        if (meForm.getTitle() != null) {
            Text head = new Text();
            head.setText(meForm.getTitle());
            head.setId("listtitle");
            sp.add(head);
        }
        List list = new List();
        list.setId("form");
        sp.add(list);
        for (int i=0; i<meForm.elementCount(); i++) {
            RafaFormElement meElement = meForm.getItem(i);
            if (meElement.getElementType().equals("RafaFormCheck")) {
                CheckBox cb = new CheckBox();
                cb.setId(meElement.getName());
                cb.add(new Text().setText(meElement.getLabel()));
                if (meElement.getValue() != null)
                    if (meElement.getValue().equals("1"))
                        cb.setSelected(true);
                list.add(cb);
            } else if (meElement.getElementType().equals("RafaFormTextField")) {
                Widget item = new Widget();
                item.parseAuthorStyle("layout:gridlayout(1,2)");
                Text txt = new Text();
                txt.setText(meElement.getLabel());
                txt.parseAuthorStyle("font-style: bold");
                item.add(txt);
                TextField tf = new TextField();
                tf.setId(meElement.getName());
                if (meElement.getValue() != null)
                    tf.setText(meElement.getValue());
                if (meElement.getType() != null)
                    if (meElement.getType().equals("pwd"))
                        tf.setConstraints(javax.microedition.lcdui.TextField.PASSWORD);
                item.add(tf);
                list.add(item);
            } else if (meElement.getElementType().equals("RafaFormChoice")) {
                Widget item = new Widget();
                item.parseAuthorStyle("layout:gridlayout(1,2)");
                list.add(item);
                Text txt = new Text();
                txt.setText(meElement.getLabel());
                txt.parseAuthorStyle("font-style: bold");
                item.add(txt);
                RafaFormChoice meChoice = (RafaFormChoice) meForm.getItem(i);
                Choice choice = new Choice();
                choice.setId(meChoice.getName());
                item.add(choice);
                RadioGroup rg = choice.getRadioGroup();
                RafaFormChoiceElement choiceElement = null;
                for (int j=0; j<meChoice.ElementCount(); j++) {
                    choiceElement = meChoice.getItem(j);
                    RadioButton rb = new RadioButton();
                    rb.setValue(choiceElement.getValue());
                    rb.add(new Text().setText(choiceElement.getLabel()));
                    if (choiceElement.getSelected())
                        rb.setSelected(true);
                    rg.add(rb);
                }
            } else if (meElement.getElementType().equals("RafaFormLabel")) {
                boolean isExpand = false;
                String doExpand = "padding: 0 6 0 0";
                RafaFormLabel meLabel = (RafaFormLabel) meForm.getItem(i);
                ActionWidget listItem = new ActionWidget("listitem");
                if (meLabel.getType() != null) {
                    if (meLabel.getType().equals("adv")) {
                        String arguments = meLabel.getValue();
                        listItem.setId("adv");
                        listItem.setOnAction("doAdvertise("+arguments+")");
                    }
                // Arrow icon on the right
                } else if (meLabel.getValue() != null) {
                    listItem.setId("expand");
                    isExpand = true;
                }
                list.add(listItem);
                // List item title
                Text title = new Text();
                title.setId("itemtitle");
                title.setText(meLabel.getName());
                listItem.add(title);
                // List item description
                if (meLabel.getDescription() != null) {
                    TextArea desc = new TextArea();
                    desc.setId("itemdesc");
                    desc.setText(meLabel.getDescription());
                    if (isExpand) desc.parseAuthorStyle(doExpand);
                    listItem.add(desc);
                }
                if (meLabel.getSnipset() != null) {
                    Text item2 = new Text();
                    item2.setId("itemsnipset");
                    item2.setText(meLabel.getSnipset());
                    if (isExpand) item2.parseAuthorStyle(doExpand);
                    listItem.add(item2);
                }                
            }
        }
        // Create submit button
        ActionWidget listItem = new ActionWidget("listitem");
        listItem.setId("expand");
        listItem.setOnAction("doForm("+meForm.getName()+")");
        list.add(listItem);
        // List item title
        Text title = new Text();
        title.setId("itemtitle");
        title.setText(Kuix.getMessage("SUBMIT"));
        listItem.add(title);
        if (meForm.getHelp() != null) {
            TextArea help = new TextArea();
            help.setText(meForm.getHelp());
            help.setId("listhelp");
            sp.add(help);
        }
    }

    public void renderAlert(RafaAlert meAlert) {
        if (meAlert.getMethod().equals("back")) {
            Kuix.alert(meAlert.getTextValue());
        } else {
            String URL = "/"+meAlert.getUUID()+"/"+meAlert.getMethod();
            if (meAlert.getTextID() != null)
                URL += "/"+meAlert.getTextID();
            doOpen(URL);
        }
    }

    public void renderRedirect(RafaRedirect meRedirect) {
        String URL = "/"+meRedirect.getUUID()+"/"+meRedirect.getMethod();
        doOpen(URL);
    }

    public void doLogin() {
        // Default tukuya in authentication mode (signin)
        onAuthenticate = true;
        // if user already registered, automatically login
        String URL = null;
        if (flexDB.checkDB()) {
            URL = "/userpoltek/signin/"+flexDB.getUsername()
                    +"/"+flexDB.getPassword();
        } else {
            URL = "/userpoltek";
        }
        doOpen(URL);
    }

    public void doHelp() {
        String URL = "/help";
        doOpen(URL);
    }

    public void doAbout() {
        String msg = "<rafa version=\"1.2\">";
        msg += "<head><uuid>about</uuid></head><body>";
        msg += "<rule name=\"list\"><method>index</method>";
        msg += "<title>Tentang Kami</title><elements>";
        msg += "<item><name>PoltekTelkom</name></item>";
        msg += "<item><name>PT. Mobile Solutions</name></item>";
        msg += "<item><name>Version 1.1</name></item>";
        msg += "</elements></rule></body></rafa>";
        System.out.println("MSG: "+msg);
        doOpenLocal(msg);
    }

    public void doRun(Object[] arguments) {
        // Remove pupyp
        Widget pb = Kuix.getCanvas().getDesktop().getWidget("poprun");
        if (pb != null)
            pb.remove();
        if (arguments.length>0) {
            String id = (String) arguments[0];
            id = id.toLowerCase();
            id = id.trim();
            System.out.println(id);
            String URL = "/catalog/run/"+id;
            doOpen(URL);
        }
    }

    public void doForm(Object[] arguments) {
        if (arguments.length>0) {
            String formName = (String) arguments[0];
            String postData = "";
            String value;
            RafaForm item = rafaParser.getForm(formName);
            for (int i=0; i<item.elementCount(); i++) {
                RafaFormElement actItem = item.getItem(i);
                value = null;
                if (!actItem.getElementType().equals("RafaFormLabel")) {
                    if (actItem.getElementType().equals("RafaFormCheck")) {
                        CheckBox widget = (CheckBox) getScreen().getWidget(actItem.getName());
                        if (widget.isSelected())
                            value = "1";
                        else
                            value = "0";
                    } else if (actItem.getElementType().equals("RafaFormTextField")) {
                        TextField widget = (TextField) getScreen().getWidget(actItem.getName());
                        if (widget.getText() != null)
                            value = widget.getText();
                    } else if (actItem.getElementType().equals("RafaFormChoice")) {
                        Choice widget = (Choice) getScreen().getWidget(actItem.getName());
                        value = widget.getRadioGroup().getValue();
                    }
                    if (value != null)
                        postData += "&"+actItem.getName()+"="+value;
                    // Save user and password to database is onAuthenticate mode
                    // Before send to server
                    if (onAuthenticate) {
                        if (actItem.getName().equals("user")) {
                            if (value != null)
                                flexDB.setUsername(value);
                        } else if (actItem.getName().equals("pwd")) {
                            if (value != null)
                                flexDB.setPassword(value);
                        }
                    }
                }
            }
            String URL = "/"+rafaParser.getAppID()+"/"+item.getMethod();
            if (postData.length()>0) {
                // Delete first character '&'
                postData = postData.substring(1);
                doOpen(URL, postData);
            } else {
                doOpen(URL, null);
            }
        }
    }

    public void doList(Object[] arguments) {
        if (arguments.length>1) {
            String method = (String) arguments[0];
            String value = (String) arguments[1];
            String URL = "/"+rafaParser.getAppID()+"/"+method+"/"+value;
            doOpen(URL);
        }
    }

    public void doBack() {
        RafaScreen rafaScreen = rafaParser.backScreen();
        flipScreen();
        render(rafaScreen);
        renderScreen("left");
    }

    public void doAdvertise(Object[] arguments) {
        if (arguments.length>0) {
            String id = (String) arguments[0];
            String URL = "/advertise/go/"+id;
            doOpen(URL);
        }
    }

    public void doOpen(String url) {
        if (flexDB.getUsername() != null) {
            String postData = "u="+flexDB.getUsername();
            rafaParser.Parse(url, postData);
        } else {
            rafaParser.Parse(url, null);
        }
    }

    public void doOpenLocal(String rafa) {
        InputStream in;
        try {
            in = new ByteArrayInputStream(rafa.getBytes("UTF-8"));
            rafaParser.localParse(in);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }       
    }

    public void doOpen(String url, String post) {
        if (flexDB.getUsername() != null) {
            post += "&u="+flexDB.getUsername();
        }
        rafaParser.Parse(url, post);
    }

}
