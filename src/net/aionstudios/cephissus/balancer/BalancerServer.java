package net.aionstudios.cephissus.balancer;

import java.io.IOException;
import java.net.ServerSocket;

public class BalancerServer {

	public BalancerServer(int port, int upstreamPort) throws IOException {
		ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Balancer server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (listening) {
            new ProxyThread(serverSocket.accept(), upstreamPort).start();
        }
        serverSocket.close();
	}
	
}
