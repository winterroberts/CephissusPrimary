package net.aionstudios.cephissus.balancer;

public class UpstreamTarget {
	
	private long time;
	private String ip;
	
	public UpstreamTarget(String ip, long time) {
		this.ip = ip;
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getIp() {
		return ip;
	}

}
