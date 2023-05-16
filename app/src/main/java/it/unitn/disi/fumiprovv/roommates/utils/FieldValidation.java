package it.unitn.disi.fumiprovv.roommates.utils;

public class FieldValidation {

    public static boolean checkPasswordRequirements(String password) {
        // At least one digit, one lowercase, one uppercase, one special character, no whitespaces, at least 8 characters
        //String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        // At least 8 characters
        String regex = "^.{8,}$";
        return password.matches(regex);
    }

    public static boolean checkEmailRequirements(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}
