package net.aionstudios.cephissus.balancer;

import java.util.LinkedList;
import java.util.Queue;

public class UpstreamTargets {
	
	private static Queue<UpstreamTarget> targets = new LinkedList<>();
	
	public static UpstreamTarget findNextTarget() {
		UpstreamTarget d = targets.poll();
		if(d==null) {
			return null;
		}
		while(d.getTime()<System.currentTimeMillis()-90000) {
			System.out.println("Balancer lost "+d.getIp());
			d = targets.poll();
		}
		targets.add(d);
		return d;
	}

	public static void setTarget(String ip, long time) {
		LinkedList<UpstreamTarget> t = (LinkedList<UpstreamTarget>) targets;
		for(UpstreamTarget d : t) {
			if(d.getIp().equals(ip)) {
				d.setTime(time);
				return;
			}
		}
		targets.add(new UpstreamTarget(ip, time));
		System.out.println("Balancer got "+ip);
	}
	
}
