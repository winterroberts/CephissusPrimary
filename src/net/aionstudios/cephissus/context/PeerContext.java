package net.aionstudios.cephissus.context;

import org.json.JSONException;

import net.aionstudios.api.context.Context;
import net.aionstudios.api.response.Response;

public class PeerContext extends Context {

	public PeerContext() {
		super("peer");
	}

	@Override
	public void contextDefault(Response response, String requestContext) throws JSONException {
		
	}

}
