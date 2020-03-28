package net.aionstudios.cephissus.filesystem;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;

public class FileDelta implements Comparable<FileDelta> {
	
	private Path path;
	private String kind;
	private long deltaTime = 0;
	private boolean directory;

	public FileDelta(Path path, String kind, long deltaTime, boolean directory) {
		this.path = path;
		this.kind = kind;
		this.deltaTime = deltaTime;
		this.directory = directory;
		DeltaCollector.updateDelta(this);
	}
	
	public String getKind() {
		return kind;
	}

	public long getDeltaTime() {
		return deltaTime;
	}

	public Path getPath() {
		return path;
	}
	
	public boolean isDirectory() {
		return directory;
	}
	
	public String toString() {
		return deltaTime + " " + getKind() + " " + (directory?"DIR":"FILE") + " " + path.toString();
	}

	@Override
	public int compareTo(FileDelta other) {
		long diff = deltaTime-other.getDeltaTime();
		if(diff==0) {
			return path.toString().compareTo(other.getPath().toString());
		}
		return diff < Integer.MIN_VALUE?-1:diff > Integer.MAX_VALUE?1:(int)diff;
	}
	
}
