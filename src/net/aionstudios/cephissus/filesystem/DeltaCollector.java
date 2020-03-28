package net.aionstudios.cephissus.filesystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DeltaCollector {
	
	public static Map<String, FileDelta> deltas = new HashMap<>();
	
	public static void updateDelta(FileDelta d) {
		deltas.put(d.getPath().toString(), d);
	}
	
	public static List<FileDelta> compileDeltas(long time){
		List<FileDelta> compile = new LinkedList<>();
		for(Entry<String, FileDelta> d : deltas.entrySet()) {
			if(d.getValue().getDeltaTime()>time) {
				compile.add(d.getValue());
			}
		}
		Collections.sort(compile);
		return compile;
	}

}
