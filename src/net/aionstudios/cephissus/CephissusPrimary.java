package net.aionstudios.cephissus;

import java.io.IOException;

import net.aionstudios.api.API;
import net.aionstudios.api.context.ContextManager;
import net.aionstudios.api.error.ErrorManager;
import net.aionstudios.cephissus.balancer.BalancerServer;
import net.aionstudios.cephissus.console.CephissusConsole;
import net.aionstudios.cephissus.console.CreateReplicaCommand;
import net.aionstudios.cephissus.context.FileSystemContext;
import net.aionstudios.cephissus.context.PeerContext;
import net.aionstudios.cephissus.errors.FileNotFoundError;
import net.aionstudios.cephissus.filesystem.RecursiveWatcherService;
import net.aionstudios.cephissus.filesystem.action.FileSystemDeltasAction;
import net.aionstudios.cephissus.filesystem.action.FileSystemRetrieveAction;
import net.aionstudios.cephissus.peer.action.PeerHeartbeatAction;

public class CephissusPrimary {
	
	public static FileNotFoundError fileNotFoundError;
	
	public static void main(String[] args) throws IOException {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		API.initAPI("Cephissus", 26723, true, "Cephissus Primary", 26723, false);
		
		RecursiveWatcherService rws = new RecursiveWatcherService();
		rws.init();
		
		/* File System */
		FileSystemContext fsc = new FileSystemContext();
		fsc.registerAction(new FileSystemDeltasAction());
		fsc.registerAction(new FileSystemRetrieveAction());
		
		/* Peer */
		PeerContext pc = new PeerContext();
		pc.registerAction(new PeerHeartbeatAction());
		
		ContextManager.registerContext(fsc);
		ContextManager.registerContext(pc);
		
		/* Console */
		new CreateReplicaCommand();
		CephissusConsole.getInstance().startConsoleThread();
		
		/* Error */
		fileNotFoundError = new FileNotFoundError();
		ErrorManager.registerError(fileNotFoundError);
		
		BalancerServer balancerServer = new BalancerServer(443, 443);
	}

}
