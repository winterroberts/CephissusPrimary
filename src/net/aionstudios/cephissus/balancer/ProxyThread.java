package net.aionstudios.cephissus.balancer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ProxyThread extends Thread {
    
	private Socket remote = null;
	private int port;
    public ProxyThread(Socket remote, int port) {
        super("ProxyThread");
        this.remote = remote;
        this.port = port;
    }

    public void run() {
    	try {
        	final byte[] request = new byte[1024];
        	final byte[] reply = new byte[4096];
            OutputStream remOut = remote.getOutputStream();
            InputStream remIn = remote.getInputStream();
            
            UpstreamTarget ut = UpstreamTargets.findNextTarget();
			Socket upstream = new Socket(ut==null?"127.0.0.1":ut.getIp(),port);
			OutputStream upOut = upstream.getOutputStream();
			InputStream upIn = upstream.getInputStream();
			new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        while ((bytes_read = remIn.read(request)) != -1) {
                            upOut.write(request, 0, bytes_read);
                            upOut.flush();
                        }
                    } catch (IOException e) {
                    }
                }
            }.start();
            
            int bytes_read;
            try {
                while (!upstream.isClosed() && (bytes_read = upIn.read(reply)) != -1) {
                    remOut.write(reply, 0, bytes_read);
                    remOut.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (upstream != null) {
                        upstream.close();
                    }
                    if (remote != null) {
                    	remote.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            remOut.close();
            remote.close();
            upstream.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
}