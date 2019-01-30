package org.odk.collect.android.utilities;

import android.content.Context;
import android.os.Build;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.chrono.IslamicChronology;
import org.odk.collect.android.R;
import org.odk.collect.android.logic.DatePickerDetails;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bikramsambat.BikramSambatDate;
import bikramsambat.BsCalendar;
import bikramsambat.BsException;
import bikramsambat.BsGregorianDate;
import timber.log.Timber;

public class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static String getDateTimeLabel(Date date, DatePickerDetails datePickerDetails, boolean containsTime, Context context) {
        if (datePickerDetails.isGregorianType()) {
            return getGregorianDateTimeLabel(date, datePickerDetails, containsTime, null);
        } else {
            return getCustomDateTimeLabel(date, datePickerDetails, containsTime, context);
        }
    }

    private static String getGregorianDateTimeLabel(Date date, DatePickerDetails datePickerDetails, boolean containsTime, Locale locale) {
        DateFormat dateFormatter;
        locale = locale == null ? Locale.getDefault() : locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String format = android.text.format.DateFormat.getBestDateTimePattern(locale, getDateTimeSkeleton(containsTime, datePickerDetails));
            dateFormatter = new SimpleDateFormat(format, locale);
        } else {
            dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
        }
        return dateFormatter.format(date);
    }

    private static String getCustomDateTimeLabel(Date date, DatePickerDetails datePickerDetails, boolean containsTime, Context context) {
        String gregorianDateText = getGregorianDateTimeLabel(date, datePickerDetails, containsTime, Locale.getDefault());

        DateTime customDate;
        String[] monthArray;
        if (datePickerDetails.isEthiopianType()) {
            customDate = new DateTime(date).withChronology(EthiopicChronology.getInstance());
            monthArray = context.getResources().getStringArray(R.array.ethiopian_months);
        } else if (datePickerDetails.isCopticType()) {
            customDate = new DateTime(date).withChronology(CopticChronology.getInstance());
            monthArray = context.getResources().getStringArray(R.array.coptic_months);
        } else if (datePickerDetails.isIslamicType()) {
            customDate = new DateTime(date).withChronology(IslamicChronology.getInstance());
            monthArray = context.getResources().getStringArray(R.array.islamic_months);
        } else {
            customDate = new DateTime(date);
            monthArray = BsCalendar.MONTH_NAMES.toArray(new String[BsCalendar.MONTH_NAMES.size()]);
        }
        String customDateText = "";

        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (datePickerDetails.isBikramSambatType()) {
            BikramSambatDate bikramSambatDate;
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                bikramSambatDate = BsCalendar.getInstance().toBik(new BsGregorianDate(
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH) + 1,
                                        calendar.get(Calendar.DAY_OF_MONTH)));
                String day = datePickerDetails.isSpinnerMode() ? bikramSambatDate.day + " " : "";
                String month = datePickerDetails.isSpinnerMode() || datePickerDetails.isMonthYearMode() ? monthArray[bikramSambatDate.month - 1] + " " : "";

                if (containsTime) {
                    customDateText = day + month + bikramSambatDate.year + ", " + df.format(customDate.toDate());
                } else {
                    customDateText = day + month + bikramSambatDate.year;
                }
            } catch (BsException e) {
                Timber.e(e);
            }
        } else {
            String day = datePickerDetails.isSpinnerMode() ? customDate.getDayOfMonth() + " " : "";
            String month = datePickerDetails.isSpinnerMode() || datePickerDetails.isMonthYearMode() ? monthArray[customDate.getMonthOfYear() - 1] + " " : "";

            if (containsTime) {
                customDateText = day + month + customDate.getYear() + ", " + df.format(customDate.toDate());
            } else {
                customDateText = day + month + customDate.getYear();
            }
        }
        return String.format(context.getString(R.string.custom_date), customDateText, gregorianDateText);
    }

    private static String getDateTimeSkeleton(boolean containsTime, DatePickerDetails datePickerDetails) {
        String dateSkeleton;
        if (containsTime) {
            dateSkeleton = "yyyyMMMdd HHmm";
        } else {
            dateSkeleton = "yyyyMMMdd";
        }
        if (datePickerDetails.isMonthYearMode()) {
            dateSkeleton = "yyyyMMM";
        } else if (datePickerDetails.isYearMode()) {
            dateSkeleton = "yyyy";
        }
        return dateSkeleton;
    }

    public static LocalDateTime skipDaylightSavingGapIfExists(LocalDateTime date) {
        final DateTimeZone dtz = DateTimeZone.getDefault();

        if (dtz != null) {
            while (dtz.isLocalDateTimeGap(date)) {
                date = date.plusMinutes(1);
            }
        }
        return date;
    }

    public static DatePickerDetails getDatePickerDetails(String appearance) {
        DatePickerDetails.DatePickerType datePickerType = DatePickerDetails.DatePickerType.GREGORIAN;
        DatePickerDetails.DatePickerMode datePickerMode = DatePickerDetails.DatePickerMode.CALENDAR;
        if (appearance != null) {
            appearance = appearance.toLowerCase(Locale.US);
            if (appearance.contains("ethiopian")) {
                datePickerType = DatePickerDetails.DatePickerType.ETHIOPIAN;
                datePickerMode = DatePickerDetails.DatePickerMode.SPINNERS;
            } else if (appearance.contains("coptic")) {
                datePickerType = DatePickerDetails.DatePickerType.COPTIC;
                datePickerMode = DatePickerDetails.DatePickerMode.SPINNERS;
            } else if (appearance.contains("islamic")) {
                datePickerType = DatePickerDetails.DatePickerType.ISLAMIC;
                datePickerMode = DatePickerDetails.DatePickerMode.SPINNERS;
            } else if (appearance.contains("bikram-sambat")) {
                datePickerType = DatePickerDetails.DatePickerType.BIKRAM_SAMBAT;
                datePickerMode = DatePickerDetails.DatePickerMode.SPINNERS;
            } else if (appearance.contains("no-calendar")) {
                datePickerMode = DatePickerDetails.DatePickerMode.SPINNERS;
            }

            if (appearance.contains("month-year")) {
                datePickerMode = DatePickerDetails.DatePickerMode.MONTH_YEAR;
            } else if (appearance.contains("year")) {
                datePickerMode = DatePickerDetails.DatePickerMode.YEAR;
            }
        }

        return new DatePickerDetails(datePickerType, datePickerMode);
    }
}
