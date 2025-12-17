package utils.report;

public class TestNameHolder {
    private static final ThreadLocal<String> currentTestName = new ThreadLocal<>();

    public static void setTestName(String name) {
        currentTestName.set(name);
    }

    public static String getTestName() {
        return currentTestName.get();
    }

    public static void clear() {
        currentTestName.remove();
    }
}
