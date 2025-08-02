package com.framework.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtil {

    // Returns the LocalDate of the next Friday from today
    public static LocalDate getNextFriday() {
        LocalDate today = LocalDate.now();
        int daysUntilFriday = DayOfWeek.FRIDAY.getValue() - today.getDayOfWeek().getValue();
        if (daysUntilFriday <= 0) {
            // If today is Friday or after, move to next week's Friday
            daysUntilFriday += 7;
        }
        return today.plusDays(daysUntilFriday);
    }

}
