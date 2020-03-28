package net.aionstudios.cephissus.console;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.api.account.AccessLevel;
import net.aionstudios.api.account.AppType;
import net.aionstudios.api.aos.AOSInfo;
import net.aionstudios.api.service.AccountServices;
import net.aionstudios.api.service.ResponseServices;
import net.aionstudios.api.util.DatabaseUtils;

public class CreateReplicaCommand extends Command {

	public CreateReplicaCommand() {
		super("createreplica");
	}
	
	private String countReplicas = "SELECT COUNT(*) as c FROM `AOSAccounts` WHERE `appName` LIKE 'REPLICA %';";

	@Override
	public void execute(String... args) {
		long c = (Long) DatabaseUtils.prepareAndExecute(countReplicas, false).get(0).getResults().get(0).get("c");
		String[] credentials = AccountServices.createAccountQuery("CEPHISSUS REPLICA", "REPLICA "+c, AppType.PARTNER, null, "CEPHISSUS", null, null, null, null, AccessLevel.SYSTEM).split("\\.");
		JSONObject out = ResponseServices.getLinkedJsonObject();
		try {
			out.put("cp_primary_host", "127.0.0.1");
			out.put("cp_key", credentials[0]);
			out.put("cp_secret", credentials[1]);
			AOSInfo.writeConfig(out, new File("./replica.json"));
			System.out.println("Created replica, see file './replica.json'.");
		} catch (JSONException e) {
			System.err.println("Replica creation failed, see error...");
			e.printStackTrace();
		}
	}

	@Override
	public String getHelp() {
		return "Creates credentials for a Cephissus Replica and stores them in 'replica.json'.\r\n"
				+ "    USAGE: createreplica";
	}

}
