/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.rafa;

import co.id.motion.poltektelkom.FlexCache;
import co.id.motion.poltektelkom.FlexConnectionListener;
import co.id.motion.poltektelkom.FlexConnection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.kxml2.io.*;
import org.xmlpull.v1.*;

/**
 *
 * @author andrias
 */
public class RafaParser implements FlexConnectionListener {
    protected RafaListener rafaListener = null;
    private final FlexConnection flexConnection = new FlexConnection();
    private final FlexCache flexCache = new FlexCache();
    private boolean isUI = false;
    private String appOldID = "";

    public void Parse(final String URL, final String URLPost) {
        if (flexConnection.getListener() == null)
            flexConnection.setListener(this);
        flexConnection.Connect(URL, URLPost);
    }
    
    public void setListener(RafaListener listener) {
        rafaListener = listener;
    }

    private void blockParse(InputStream in) throws IOException, XmlPullParserException {
        KXmlParser parser = new KXmlParser();
        parser.setInput(new InputStreamReader(in));
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "rafa");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "head");
        parseHead(parser);
        parser.require(XmlPullParser.END_TAG, null, "head");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "body");
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "rule");
            // Read type of design protocol
            String appType = parser.getAttributeValue(0);
            if (appType.equals("list")) {
                parseList(parser);
            } else if (appType.equals("alert")) {
                parseAlert(parser);
            } else if (appType.equals("redirect")) {
                parseRedirect(parser);
            } else if (appType.equals("form")) {
                parseForm(parser);
            }
            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "rule");
        }
        parser.require(XmlPullParser.END_TAG, null, "body");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "rafa");
    }

    private void parseHead(KXmlParser parser) throws IOException, XmlPullParserException {
        String appUUID = null;
        boolean isClear = false;
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            if (parser.getName().equals("uuid")) {
                parser.require(XmlPullParser.START_TAG, null, "uuid");
                appUUID = parser.nextText();
            } else if (parser.getName().equals("clear")) {
                parser.require(XmlPullParser.START_TAG, null, "clear");
                String temp = parser.nextText();
                if (temp.equals("1")) isClear = true;
            }
        }
        doHead(appUUID, isClear);
    }

    private void parseList(KXmlParser parser) throws IOException, XmlPullParserException {
        RafaList meList = new RafaList();

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "method");
        meList.setMethod(parser.nextText());
        parser.nextTag();
        while (!parser.getName().equals("elements")) {
            if (parser.getName().equals("title")) {
                parser.require(XmlPullParser.START_TAG, null, "title");
                meList.setTitle(parser.nextText());
            } else if (parser.getName().equals("help")) {
                parser.require(XmlPullParser.START_TAG, null, "help");
                meList.setHelp(parser.nextText());
            }
            parser.nextTag();
        }
        parser.require(XmlPullParser.START_TAG, null, "elements");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            RafaListElement item = new RafaListElement();
            parser.require(XmlPullParser.START_TAG, null, "item");
            for (int i=0; i<parser.getAttributeCount(); i++) {
                if (parser.getAttributeName(i).equals("value")) {
                    item.setValue(parser.getAttributeValue(i));
                } else if (parser.getAttributeName(i).equals("type")) {
                    item.setType(parser.getAttributeValue(i));
                }
            }
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "name");
            item.setName(parser.nextText());
            while (parser.nextTag()!=XmlPullParser.END_TAG) {
                if (parser.getName().equals("desc")) {
                    parser.require(XmlPullParser.START_TAG, null, "desc");
                    item.setDescription(parser.nextText());
                } else if (parser.getName().equals("snip")) {
                    parser.require(XmlPullParser.START_TAG, null, "snip");
                    item.setSnipset(parser.nextText());
                } else if (parser.getName().equals("img")) {
                    parser.require(XmlPullParser.START_TAG, null, "img");
                    item.setImg(parser.nextText());
                }
            }
            parser.require(XmlPullParser.END_TAG, null, "item");
            meList.addItem(item);
        }
        parser.require(XmlPullParser.END_TAG, null, "elements");
        doList(meList);
    }

    private void parseForm(KXmlParser parser) throws IOException, XmlPullParserException {
        RafaForm meRule = new RafaForm();

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "method");
        meRule.setMethod(parser.nextText());
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "name");
        meRule.setName(parser.nextText());
        parser.nextTag();
        if (parser.getName().equals("title")) {
            parser.require(XmlPullParser.START_TAG, null, "title");
            meRule.setTitle(parser.nextText());
            parser.nextTag();
            if (parser.getName().equals("help")) {
                parser.require(XmlPullParser.START_TAG, null, "help");
                meRule.setHelp(parser.nextText());
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.START_TAG, null, "elements");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            String formType = parser.getName();
            if (formType.equals("itext")) {
                meRule.addItem(parseFormTextField(parser));
            } else if (formType.equals("icheck")) {
                meRule.addItem(parseFormCheck(parser));
            } else if (formType.equals("ichoice")) {
                meRule.addItem(parseFormChoice(parser));
            } else if (formType.equals("item")) {
                meRule.addItem(parseFormLabel(parser));
            }
        }
        parser.require(XmlPullParser.END_TAG, null, "elements");
        doForm(meRule);
    }

    private RafaFormLabel parseFormLabel(KXmlParser parser) throws IOException, XmlPullParserException {
            RafaFormLabel item = new RafaFormLabel();
            parser.require(XmlPullParser.START_TAG, null, "item");
            for (int i=0; i<parser.getAttributeCount(); i++) {
                if (parser.getAttributeName(i).equals("value")) {
                    item.setValue(parser.getAttributeValue(i));
                } else if (parser.getAttributeName(i).equals("type")) {
                    item.setType(parser.getAttributeValue(i));
                }
            }
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "name");
            item.setName(parser.nextText());
            while (parser.nextTag()!=XmlPullParser.END_TAG) {
                if (parser.getName().equals("desc")) {
                    parser.require(XmlPullParser.START_TAG, null, "desc");
                    item.setDescription(parser.nextText());
                } else if (parser.getName().equals("snip")) {
                    parser.require(XmlPullParser.START_TAG, null, "snip");
                    item.setSnipset(parser.nextText());
                }
            }
            parser.require(XmlPullParser.END_TAG, null, "item");
            return item;
    }

    private RafaFormTextField parseFormTextField(KXmlParser parser) throws IOException, XmlPullParserException {
        String name = null;
        String value = null; // Optional
        String type = null; // Optional
        String label = null;
        
        parser.require(XmlPullParser.START_TAG, null, "itext");
        for (int i=0; i<parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equals("name")) {
                name = parser.getAttributeValue(i);
            } else if (parser.getAttributeName(i).equals("value")) {
                value = parser.getAttributeValue(i);
            } else if (parser.getAttributeName(i).equals("type")) {
                type = parser.getAttributeValue(i);
            }
        }
        label = parser.nextText();
        RafaFormTextField result = new RafaFormTextField(name, label);
        if (value != null)
            result.setValue(value);
        if (type != null)
            result.setType(type);
        return result;
    }

    private RafaFormCheck parseFormCheck(KXmlParser parser) throws IOException, XmlPullParserException {
        String name = null;
        String value = null;
        String label = null;

        parser.require(XmlPullParser.START_TAG, null, "icheck");
        for (int i=0; i<parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equals("name")) {
                name = parser.getAttributeValue(i);
            } else if (parser.getAttributeName(i).equals("value")) {
                value = parser.getAttributeValue(i);
            }
        }
        label = parser.nextText();
        RafaFormCheck result = new RafaFormCheck(name, label);
        if (value != null)
            result.setValue(value);
        return result;
    }

    private RafaFormChoice parseFormChoice(KXmlParser parser) throws IOException, XmlPullParserException {
        String name = null;
        String label = null;
        RafaFormChoice result = null;
        String itemValue = null;
        String itemLabel = null;
        String itemType = null;

        parser.require(XmlPullParser.START_TAG, null, "ichoice");
        for (int i=0; i<parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equals("name")) {
                name = parser.getAttributeValue(i);
            } else if (parser.getAttributeName(i).equals("label")) {
                label = parser.getAttributeValue(i);
            }
        }
        result = new RafaFormChoice(name, label);
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "item");
            itemValue = null;
            itemType = null;
            for (int i=0; i<parser.getAttributeCount(); i++) {
                if (parser.getAttributeName(i).equals("value")) {
                    itemValue = parser.getAttributeValue(i);
                } else if (parser.getAttributeName(i).equals("type")) {
                    itemType = parser.getAttributeValue(i);
                }
            }
            itemLabel = parser.nextText();
            result.addItem(itemLabel, itemValue, itemType);
        }
        parser.require(XmlPullParser.END_TAG, null, "ichoice");
        return result;
    }

    private void parseAlert(KXmlParser parser) throws IOException, XmlPullParserException {
        RafaAlert meAlert = new RafaAlert();
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "method");
        if (parser.getAttributeCount()>0) {
            meAlert.setUUID(parser.getAttributeValue(0));
        }
        meAlert.setMethod(parser.nextText());
        parser.require(XmlPullParser.END_TAG, null, "method");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "text");
        meAlert.setTextID(parser.getAttributeValue(0));
        meAlert.setTextValue(parser.nextText());
        parser.require(XmlPullParser.END_TAG, null, "text");
        doAlert(meAlert);
    }

    private void parseRedirect(KXmlParser parser) throws IOException, XmlPullParserException {
        RafaRedirect meRedirect = new RafaRedirect();

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "method");
        meRedirect.setUUID(parser.getAttributeValue(0));
        meRedirect.setMethod(parser.nextText());
        parser.require(XmlPullParser.END_TAG, null, "method");
        doRedirect(meRedirect);
    }

    public void receivedStream(InputStream in) {
        try {
            this.blockParse(in);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void onError(String message) {
        this.rafaListener.onError(message);
    }

    public void onStart(Object adapter) {
        this.rafaListener.onStart();
    }

    public void onFinish(Object adapter, InputStream content) {
        this.localParse(content);
    }

    public void localParse(InputStream content) {
        try {
            // Default reply content is non ui
            isUI = false;
            // Inside blockparse will create rafascreen
            blockParse(content);
            // Save rafascreen only for ui
            if (isUI) {
                flexCache.saveScreen();
                rafaListener.onFinishUI(flexCache.getItem());
            }
        } catch (IOException ie) {
            rafaListener.onError("Rafa language incorrect");
        } catch (XmlPullParserException xe) {
            rafaListener.onError("Rafa language incorrect");
        }
    }

    private void doHead(String uuid, boolean isClear) {
        // Check if uuid is not core application
        if ((uuid.equals("user")==false) && (uuid.equals("catalog")==false) &&
                (uuid.equals("advertise")==false) && (uuid.equals("help")==false)) {
            // Check if application change
            if ((uuid.compareTo(appOldID)!=0) || (isClear)) {
                flexCache.removeAll();
            }
            appOldID = uuid;
        }
        flexCache.addHead(uuid);
    }

    private void doList(RafaList meList) {
        isUI = true;
        flexCache.addList(meList);
    }

    private void doForm(RafaForm meForm) {
        isUI = true;
        flexCache.addForm(meForm);
    }

    private void doAlert(RafaAlert meAlert) {
        isUI = false;
        rafaListener.onFinishNonUI(meAlert);
    }

    private void doRedirect(RafaRedirect meRedirect) {
        isUI = false;
        rafaListener.onFinishNonUI(meRedirect);
    }

    public RafaScreen backScreen() {
        return flexCache.getItemBack();
    }

    public RafaForm getForm(String name) {
        return flexCache.getItem().getForm(name);
    }

    public String getAppID() {
        String retval = null;
        if (flexCache.getItem() != null)
            retval = flexCache.getItem().getAppID();
       return retval;
    }

    public void clearCache() {
        flexCache.removeAll();
    }

    public int elementCount() {
        return flexCache.elementCount();
    }
}
