package com.friendlypos.application.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {

    public static void addLink(TextView textView, String patternToMatch,
                               final String link) {
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return link;
            }
        };
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
    }

    private static char[] hextable = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String byteArrayToHex(byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            int di = (array[i] + 256) & 0xFF; // Make it unsigned
            s = s + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
        }
        return s;
    }

    public static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return s;
        }

        m.update(s.getBytes(), 0, s.length());
        return byteArrayToHex(m.digest());
    }

    public static String md5(String s) {
        return digest(s, "MD5");
    }

   /* public static double sGetDecimalStringAnyLocaleAsDouble(String value) {
        return Double.parseDouble(value);
    }
*/

    public static double sGetDecimalStringAnyLocaleAsDouble(String value) {

        Locale theLocale = Locale.getDefault();
        NumberFormat numberFormat = DecimalFormat.getInstance(theLocale);
        Number theNumber;
        try {
            theNumber = numberFormat.parse(value);
            return theNumber.doubleValue();
        } catch (ParseException e) {
            String valueWithDot = value.replaceAll(",", ".");
            return Double.valueOf(valueWithDot);
        }
    }
    /*public static double sGetDecimalStringAnyLocaleAsDouble(String value) {

        Locale theLocale = Locale.getDefault();
        theLocale = Locale.US;
        NumberFormat numberFormat = DecimalFormat.getInstance(theLocale);
        Number theNumber;
        try {
            theNumber = numberFormat.parse(value);
            return theNumber.doubleValue();
        } catch (ParseException e) {
            String valueWithDot = value.replaceAll(",", ".");
            return Double.valueOf(valueWithDot);
        }
    }
*/
    public static double round(double num, int multipleOf) {
        return Math.ceil((num / multipleOf)) * multipleOf;
    }

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Calendar cal = Calendar.getInstance();
        return (dateFormat.format(cal.getTime())); //16:00:22
    }

    public static String get24Time() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return (dateFormat.format(cal.getTime())); //16:00:22
    }


    public static String getTime(String date) {
        String rdate = "";
        try {
            SimpleDateFormat regular = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat spanish = new SimpleDateFormat("hh:mm:ss a");
            Date regdate = regular.parse(date);
            rdate = spanish.format(regdate);
        } catch (Exception e) {
        }
        return rdate;
    }

    public static String getTime24() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return (dateFormat.format(cal.getTime())); //16:00:22
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(cal.getTime());
    }

    public static String getDateSpanishF(String date) {
        String rdate = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormatSpanish = new SimpleDateFormat("dd-MM-yyyy");
            Date regdate = dateFormat.parse(date);
            rdate = dateFormatSpanish.format(regdate);
        } catch (Exception e) {
        }
        return rdate;
    }

    public static String getDateTimeSpanishF(String date) {
        String rdate = "";
        try {

            Date date1 = new Date();
            DateFormat hourFormat, dateFormat;
            hourFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            hourFormat.format(date1);
            dateFormat.format(date1);

          /*  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dateFormatSpanish = new SimpleDateFormat("dd/MM/yyyy");
            Date regdate = dateFormat.parse(date);*/
            rdate = dateFormat.format(date1) + " " + hourFormat.format(date1);;
        } catch (Exception e) {
        }
        return rdate;
    }

    public static String getDateCredit(String date, int add) {
        String untildate = date;//can take any date in current format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(untildate));
        } catch (Exception e) {
        }
        cal.add(Calendar.DATE, add);
        return dateFormat.format(cal.getTime());
    }

    public static String getDateCreditSpanish(String date, int add) {
        String untildate = date;//can take any date in current format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(untildate));
        } catch (Exception e) {
        }
        cal.add(Calendar.DATE, add);
        return dateFormat.format(cal.getTime());
    }

    public static String getDateEnglishF(String date) {
        String rdate = "";
        try {
            SimpleDateFormat regular = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat spanish = new SimpleDateFormat("yyyy-MM-dd");
            Date regdate = regular.parse(date);
            rdate = spanish.format(regdate);
        } catch (Exception e) {
        }
        return rdate;
    }

    public static void CreateMessage(Context context, String Tittle, String Message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(Tittle);
        builder1.setMessage(Message);
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static String paddigTabs(long tabs) {
        String formtat = "";
        for (int c = 0; c < tabs; c += 2) {
            formtat += "\t";
        }
        return formtat;
    }

    public static String numbreFormat(double d) {
        return String.format("%,.2f", d);
    }

    public static String getVesionNaveCode(Context context) {
        String send = "";
        try {
            send = "Version: " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {

        }
        return send;
    }

}
