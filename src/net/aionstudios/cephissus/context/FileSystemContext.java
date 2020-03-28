package net.aionstudios.cephissus.context;

import org.json.JSONException;

import net.aionstudios.api.context.Context;
import net.aionstudios.api.response.Response;

public class FileSystemContext extends Context {

	public FileSystemContext() {
		super("fileSystem");
	}

	@Override
	public void contextDefault(Response response, String requestContext) throws JSONException {
		
	}

}
