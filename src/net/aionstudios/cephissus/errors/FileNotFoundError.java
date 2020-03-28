package net.aionstudios.cephissus.errors;

import net.aionstudios.api.error.AOSError;

public class FileNotFoundError extends AOSError {

	public FileNotFoundError() {
		super("FileNotFoundError", 404, "File not available.");
	}

}
