package net.aionstudios.cephissus.peer.action;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.file.MultipartFile;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.service.AccountServices;
import net.aionstudios.cephissus.balancer.UpstreamTargets;

public class PeerHeartbeatAction extends Action {

	public PeerHeartbeatAction() {
		super("heartbeat");
		this.setPostRequiredParams("apiToken");
	}

	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery, List<MultipartFile> multipartFiles) throws JSONException {
		String apiKey = AccountServices.getApiKeyFromToken(postQuery.get("apiToken"));
		if(apiKey.length()<8) {
			response.putErrorResponse(InternalErrors.invalidSessionError, "Couldn't accept heartbeat.");
			return;
		}
		long time = System.currentTimeMillis();
		response.putData("time_now", time);
		response.putData("api_key", apiKey);
		UpstreamTargets.setTarget(response.getRequestIP(), time);
		response.putDataResponse(ResponseStatus.SUCCESS, "Hearbeat accepted.");
	}

}
