package at.ac.univie.hci.clouddeclutterG4;

import java.util.HashMap;
import java.util.Map;

public class LoginData {
    private static Map<String, String> loginData = new HashMap<>();
    private static String currentLogin = "";

    public static String getLogin() {
        return currentLogin;
    }

    public static boolean addLogin(String email, String password) {
        if (loginData.containsKey(email)) return false;

        loginData.put(email, password);
        return true;
    }

    public static boolean changePassword(String password, String newPassword) {
        if (currentLogin.isEmpty() || password.isEmpty() || newPassword.isEmpty() || !checkPassword(currentLogin, password)) return false;

        loginData.put(currentLogin, newPassword);
        return true;
    }

    public static boolean changeEmail(String email) {
        if (currentLogin.isEmpty() || email.isEmpty()) return false;

        loginData.put(email, loginData.get(currentLogin));
        loginData.remove(currentLogin);
        currentLogin = email;
        return true;
    }

    public static boolean checkPassword(String email, String password) {
        String pwd = loginData.get(email);
        if (pwd == null) return false;
        return pwd.equals(password);
    }

    public static boolean login(String email, String password) {
        if (checkPassword(email, password)) {
            currentLogin = email;
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean register(String email, String password) {
        if (!addLogin(email, password)) return false;
        currentLogin = email;
        return true;
    }

    public static void logout() {
        currentLogin = "";
    }

    public static void deleteAccount() {
        loginData.remove(currentLogin);
        logout();
    }
}
