package xxl;

import java.util.ArrayList;

/**
 * Represents a user in the application.
 */
public class User extends Calculator{
    private String _username;
    private ArrayList <Spreadsheet> _spreadsheets;

    public User(String username) {
        _spreadsheets = new ArrayList<>();
        _username = username;
    }

    public void addSpreadsheet(Spreadsheet spreadsheet) {
        _spreadsheets.add(spreadsheet);
        spreadsheet.addUser(this); // Notify the spreadsheet about the user
    }

    public void removeSpreadsheet(Spreadsheet spreadsheet) {
        _spreadsheets.remove(spreadsheet);
        spreadsheet.removeUser(this); // Notify the spreadsheet about the user removal
    }
}
