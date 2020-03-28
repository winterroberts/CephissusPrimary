package net.aionstudios.cephissus.filesystem.action;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.file.MultipartFile;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.service.AccountServices;
import net.aionstudios.api.service.ResponseServices;
import net.aionstudios.cephissus.filesystem.DeltaCollector;
import net.aionstudios.cephissus.filesystem.FileDelta;

public class FileSystemDeltasAction extends Action {

	public FileSystemDeltasAction() {
		super("deltas");
		this.setGetRequiredParams("deltaTime");
		this.setPostRequiredParams("apiToken");
	}

	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery, List<MultipartFile> multipartFiles) throws JSONException {
		String apiKey = AccountServices.getApiKeyFromToken(postQuery.get("apiToken"));
		if(apiKey.length()<8) {
			response.putErrorResponse(InternalErrors.invalidSessionError, "Couldn't provide deltas.");
			return;
		}
		long deltaTime;
		try {
			deltaTime = Long.parseLong(getQuery.get("deltaTime"));
			JSONArray dJA = new JSONArray();
			for(FileDelta d : DeltaCollector.compileDeltas(deltaTime)) {
				JSONObject dO = ResponseServices.getLinkedJsonObject();
				dO.put("path", d.getPath().toString());
				dO.put("kind", d.getKind());
				dO.put("time", d.getDeltaTime());
				dO.put("dir", d.isDirectory());
				dJA.put(dO);
			}
			response.putData("deltas", dJA);
			response.putDataResponse(ResponseStatus.SUCCESS, "Provided deltas");
		} catch (NumberFormatException e) {
			
		}
	}

}
