package net.aionstudios.cephissus.filesystem.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.InternalErrors;
import net.aionstudios.api.file.MultipartFile;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.service.AccountServices;
import net.aionstudios.cephissus.CephissusPrimary;

public class FileSystemRetrieveAction extends Action {

	public FileSystemRetrieveAction() {
		super("retrieve");
		this.setGetRequiredParams("file");
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
		String file = "replicate/"+getQuery.get("file");
		if(!new File(file).exists()) {
			response.putErrorResponse(CephissusPrimary.fileNotFoundError, "File not found.");
			return;
		}
		Path path = Paths.get(file);
        try {
            byte[] blob = Files.readAllBytes(path);
            response.putData("blob", Base64.getEncoder().encodeToString(blob));
            response.putDataResponse(ResponseStatus.SUCCESS, "Retrieved file");
        } catch (IOException e) {
            e.printStackTrace();
        } 
		
	}

}
