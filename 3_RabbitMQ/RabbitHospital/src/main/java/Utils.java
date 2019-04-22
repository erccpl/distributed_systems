
public class Utils {

    public static String validateArguments(String message) {
        String[] msgComponents = message.split(" ");
        if (msgComponents.length != 3) {
            System.out.println("Invalid input format\nFormat: <patient name> <examination>");
        }
        return msgComponents[2];
    }

}
