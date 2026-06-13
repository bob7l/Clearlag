package me.minebuilders.clearlag;

public class RAMUtil {

	private static final long MB = 1024 * 1024;
	
	public static long getTotalMemory() {
		return (Runtime.getRuntime().totalMemory() / MB);
	}

	public static long getFreeMemory() {
		return (Runtime.getRuntime().freeMemory() / MB);
	}

	public static long getUsedMemory() {
		return ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB);
	}

	public static long getMaxMemory() {
		return (Runtime.getRuntime().maxMemory() / MB);
	}

	public static int toMB(long memory) {
		return (int) (memory / MB);
	}
}
