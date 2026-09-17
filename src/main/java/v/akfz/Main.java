package v.akfz;

public class Main {
	static {
		boolean loaded = NativeLoader.load("mytest");
		if (loaded) {
			System.out.println("Native library loaded successfully!");
		} else {
			System.err.println("Native library not loaded! Error: " + NativeLoader.getLastError());
		}
	}

	public static void main(String[] args) {
		if (NativeLoader.isLoaded()) {
			NativeTest test = new NativeTest();
			int result = test.add(40, 2);
			System.out.println("Native add(40, 2) = " + result);
		} else {
			System.out.println("Falling back to Java implementation");
			System.out.println("Java add(40, 2) = " + (40 + 2));
		}
	}
}