import interfaces.DateValidator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidatorUsingDateFormat implements DateValidator {
    private final String dateFormat;

    public DateValidatorUsingDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public boolean isValid(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
        dateFormat.setLenient(false); // make the format check NOT LENIENT, which means it has to be an EXACT match
        // Try parsing the dateString into a date based on the given format
        // if it throws an exception we return false, if it doesn't throw an exception we return true
        try {
            dateFormat.parse(dateString);
        } catch (ParseException exception) {
            return false;
        }
        return true;
    }
}
