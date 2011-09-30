/*
 * RAFA Player for J2ME, Copyright 2011-2012 PT. Mobile Solution
 * Written and Supervised by Andrias Hardinata
 */

package co.id.motion.poltektelkom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import org.kalmeo.util.GZIP;

/**
 *
 * @author andrias
 */
public class FlexConnection {

    protected FlexConnectionListener flexConnectionListener = null;
    private int replyCode;
    private String connection;
    private String contentType;
    private int contentLength;
    private String serverTime;
    private Object adapter;

    public FlexConnection() {
        adapter = null;
    }

    public FlexConnection(Object item) {
        adapter = item;
    }

    public void setListener(FlexConnectionListener listener) {
        this.flexConnectionListener = listener;
    }

    public FlexConnectionListener getListener() {
        return flexConnectionListener;
    }

    public void Connect(final String URL, final String URLData) {
        flexConnectionListener.onStart(adapter);
        new Thread() {
            public void run() {
                //connectSocket(URL, URLData);
                connectHttp(URL, URLData);
            }
        }.start();
    }

    public void connectSocket(String serverUrl, String postData) {
        SocketConnection httpConn = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            httpConn = (SocketConnection)Connector.open("socket://"+FlexConstants.SITE_HOST+":3000");
            os = httpConn.openOutputStream();
            // Check if user post data
            if (postData!=null) {
                os.write(("POST "+serverUrl+" HTTP/1.0\r\n").getBytes());
                os.write(("Accept-Encoding: gzip,deflate\r\n").getBytes());
                os.write(("Content-Type: application/x-www-form-urlencoded\r\n").getBytes());
                os.write(("Content-Length: "+postData.length()+"\r\n").getBytes());
                os.write(("\r\n").getBytes());
                os.write(postData.getBytes());
                os.write(("\r\n").getBytes());
                //os.flush();
            } else {
                // Get method
                os.write(("GET "+serverUrl+" HTTP/1.0\r\n").getBytes());
                os.write(("Accept-Encoding: gzip,deflate\r\n").getBytes());
                os.write(("\r\n").getBytes());
            }
            is = httpConn.openInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringBuffer bf = new StringBuffer();
            int data = 0;
            int olddata = 0;
            int i = 0;
            boolean isData = false;
            while ((data = is.read()) != -1) {
                i++;
                if ((olddata==0x0d) && (data==0x0a) && (!isData)) {
                    if (i==2) {
                        isData = true;
                    } else {
                        bf.deleteCharAt(bf.length()-1);
                        String token = bf.toString().toLowerCase();
                        parseResponse(token);
                        bf.setLength(0);
                    }
                    i = 0;
                } else if (isData) {
                    //if (i<=contentLength) on get there's no conntent
                    baos.write(data);
                } else {
                    bf.append((char) data);
                    olddata = data;
                }
            }
            byte bais[] = GZIP.inflate(baos.toByteArray());
            InputStream readData = new ByteArrayInputStream(bais);
            flexConnectionListener.onFinish(adapter, readData);
            baos.close();
        } catch(IOException t){
            flexConnectionListener.onError("Cannot connect to server");
        }
        finally{
            try{
                if (is != null)
                    is.close();
            } catch(Throwable t){
                flexConnectionListener.onError("Exception occurred while closing input " +
                        "stream.");
            }
            try{
                if (os != null)
                    os.close();
            } catch(Throwable t){
                flexConnectionListener.onError("Exception occurred while closing output " +
                        "stream.");
            }
            try{
                if(httpConn != null)
                    httpConn.close();
            } catch(Throwable t){
                flexConnectionListener.onError("Exception occurred while closing network.");
            }
        }
    }

    public void connectHttp(String serverUrl, String postData) {
        HttpConnection httpConn = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            String url = "http://"+FlexConstants.SITE_HOST+serverUrl;
            FlexLog.Write("Connection", "Connecting URL="+url);
            httpConn = (HttpConnection)Connector.open(url);
            httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            // Check if user post data
            if (postData!=null) {
                httpConn.setRequestMethod(HttpConnection.POST);
                httpConn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                httpConn.setRequestProperty("Content-Length", String.valueOf(postData.length()));
                os = httpConn.openOutputStream();
                os.write(postData.getBytes());
                //os.flush();
            }
            if((httpConn.getResponseCode() == HttpConnection.HTTP_OK)){
                int length = (int)httpConn.getLength();
                is = httpConn.openInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int data = 0;
                while ((data = is.read())!=-1) {
                    baos.write(data);
                }
                InputStream readData;
                if (httpConn.getEncoding() == null) {
                    readData = new ByteArrayInputStream(baos.toByteArray());
                } else {
                    byte bais[] = GZIP.inflate(baos.toByteArray());
                    readData = new ByteArrayInputStream(bais);
                }
                flexConnectionListener.onFinish(adapter, readData);
            } else{
                flexConnectionListener.onError("Server reply unknown language");
            }
        } catch(IOException t){
            System.out.println("Error lieur: "+t.getMessage());
            flexConnectionListener.onError("Cannot connect to server"+t.getMessage());
        }
    //Since only limited number of network objects can be in open state
    //it is necessary to clean them up as soon as we are done with them.
        finally{//Networking done. Clean up the network objects
            try{
                if (is != null)
                    is.close();
            } catch(Throwable t){
                flexConnectionListener.onError("Exception occurred while closing input " +
                        "stream.");
            }
            try{
                if (os != null)
                    os.close();
            } catch(Throwable t){
                flexConnectionListener.onError("Exception occurred while closing output " +
                        "stream.");
            }
            try{
                if(httpConn != null)
                    httpConn.close();
            } catch(Throwable t){
                flexConnectionListener.onError("Exception occurred "+t.toString());
            }
        }
    }

    public static String getLocalIP() {
        String retval = null;
        try {
            ServerSocketConnection ssc = null;
            try {
               ssc = (ServerSocketConnection) Connector.open("socket://:1234");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
                retval = ssc.getLocalAddress();
        } catch (IOException ex) {
                ex.printStackTrace();
        }
        return retval;
    }

    private void parseResponse(String token) {
        int space = token.indexOf(" ");
        if (space > 0) {
            String name = null;
            String value = token.substring(space+1);
            if (token.charAt(space-1)==":".charAt(0)) {
                name = token.substring(0, space-1);
            } else {
                name = token.substring(0, space);
            }
            if (name.equals("connection")) {
                connection = value;
            } else if (name.equals("content-type")) {
                contentType = value;
            } else if (name.equals("content-length")) {
                contentLength = Integer.parseInt(value);
            } else if (name.equals("date")) {
                serverTime = value;
            } else if (name.startsWith("http")) {
                int c = value.indexOf(" ");
                replyCode = Integer.parseInt(value.substring(0, c));
            }
        }
    }
}
