package com.jason.experiment.somecoinapp.logging;

import android.util.Log;

import com.jason.experiment.somecoinapp.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logg
 * Created by jason.
 */
public class Logg {
    private static final int    MAX_LOG_LENGTH     = 4000;
    private static final int    MAX_TAG_LENGTH     = 23;
    private static final int    CALL_STACK_INDEX   = 4;
    private static final String TOP_LEFT_CORNER    = "┌";
    private static final String MIDDLE_CORNER      = "├";
    private static final String BOTTOM_LEFT_CORNER = "└";
    private static final String HORIZONTAL_LINE    = "│";
    private static final String DOUBLE_DIVIDER     = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER     = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER         = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER      = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER      = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private static boolean SHOW_THREAD = true;
    private static boolean ENABLE_LOGS = BuildConfig.DEBUG;
    private static String  BASE_TAG    = "+_";
    private static boolean PRINT_FANCY = false;

    private static ArrayList<WeakReference<LogTracker>> logTrackers = new ArrayList<>();

    //region:--<Logg Configuration...>
    public static void setEnableLogs(boolean enableLogs) {
        ENABLE_LOGS = enableLogs;
    }

    public static void setPrintFancy(boolean printFancy){
        PRINT_FANCY = printFancy;
    }

    public static void setShowThread(boolean showThread){
        SHOW_THREAD = showThread;
    }

    public static void setBaseTag(String baseTag){
        if(baseTag.length() > MAX_TAG_LENGTH){
            BASE_TAG = baseTag.substring(0, MAX_TAG_LENGTH);
        } else if(baseTag == null){
            BASE_TAG = "";
        } else {
            BASE_TAG = baseTag;
        }
    }

    public static void addLogTracker(LogTracker logTracker) {
        Iterator<WeakReference<LogTracker>> iterator = logTrackers.iterator();
        while (iterator.hasNext()) {
            WeakReference<LogTracker> tracker = iterator.next();
            if (tracker == null || tracker.get() == null) {
                iterator.remove();
            }
        }

        logTrackers.add(new WeakReference<>(logTracker));
    }

    public static void removeLogTracker(LogTracker logTracker) {
        Iterator<WeakReference<LogTracker>> iterator = logTrackers.iterator();
        while (iterator.hasNext()) {
            WeakReference<LogTracker> tracker = iterator.next();
            if (tracker == null || tracker.get() == null) {
                iterator.remove();
            } else if (tracker.get() == logTracker) {
                iterator.remove();
            }
        }
    }
    //endregion

    //region:--<Logging...>
    public static void e(String tag, String message, Throwable e) {
        prepareLog(Log.ERROR, tag, message, e);
    }

    public static void e(String message, Throwable e) {
        prepareLog(Log.ERROR, null, message, e);
    }

    public static void e(String message) {
        prepareLog(Log.ERROR, null, message, null);
    }

    public static void e(Throwable e){
        prepareLog(Log.ERROR, null, e.getMessage(), e);
    }

    public static void d(String tag, String message, Throwable e) {
        prepareLog(Log.DEBUG, tag, message, e);
    }

    public static void d(String message, Throwable e) {
        prepareLog(Log.DEBUG, null, message, e);
    }

    public static void d(String message) {
        prepareLog(Log.DEBUG, null, message, null);
    }

    public static void d(Throwable e){
        prepareLog(Log.DEBUG, null, e.getMessage(), e);
    }
    //endregion

    //region:--<Log Preparation...>
    private static void prepareLog(int priority, String tag, String message, Throwable throwable) {
        if (!ENABLE_LOGS) {
            return;
        }
        if (PRINT_FANCY) {
            prepareFancyLog(priority, tag, message, throwable);
        } else {
            prepareSimpleLog(priority, tag, message, throwable);
        }
        for (WeakReference<LogTracker> tracker : logTrackers) {
            if(tag == null){
                tag = BASE_TAG;
            }
            if (tracker != null && tracker.get() != null) {
                tracker.get().trackLog(priority, tag, message, throwable);
            }
        }
    }

    private static void prepareSimpleLog(int priority, String tag, String message, Throwable throwable) {
        String finalMessage = message;
        if(tag == null){
            tag = "";
        }
        if(SHOW_THREAD){
            finalMessage += '\n' + "Thread: " + Thread.currentThread().getName();
        }
        if (throwable != null) {
            finalMessage += '\n' + Log.getStackTraceString(throwable);
        }
        while (finalMessage.length() > MAX_LOG_LENGTH){
            Log.println(priority, BASE_TAG + tag, finalMessage.substring(0, 4000));
            finalMessage = finalMessage.substring(4000, finalMessage.length());
        }
        Log.println(priority, BASE_TAG + tag, finalMessage);
    }

    private static void prepareFancyLog(int priority, String tag, String message, Throwable throwable) {
        String msg      = message;
        String finalTag = tag;
        if (tag == null) {
            finalTag = getEnhancedTag();
        }

        if (msg == null || msg.isEmpty()) {
            if (throwable == null) {
                return;
            }
            msg = Log.getStackTraceString(throwable);
        } else {
            if (throwable != null) {
                msg += "\n" + Log.getStackTraceString(throwable);
            }
        }
        if (msg.length() > MAX_LOG_LENGTH) {
            msg = msg.substring(0, MAX_LOG_LENGTH);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(BuildConfig.APPLICATION_ID).append("\n");
        sb.append(getLogChunk(priority, TOP_BORDER))
                .append(getLogContent(priority, finalTag));
        if (SHOW_THREAD) {
            sb.append(getLogChunk(priority, MIDDLE_BORDER));
            sb.append(getLogContent(priority, "Thread: " + Thread.currentThread().getName()));
        }
        sb.append(getLogChunk(priority, MIDDLE_BORDER))
                .append(getLogContent(priority, msg))
                .append(getLogChunk(priority, BOTTOM_BORDER));
        Log.println(priority, BASE_TAG, sb.toString());
    }

    public static String translatePriority(int priority) {
        switch (priority) {
            case Log.ASSERT:
                return "ASSERT ";
            case Log.ERROR:
                return "ERROR  ";
            case Log.WARN:
                return "WARN   ";
            case Log.INFO:
                return "INFO   ";
            case Log.DEBUG:
                return "DEBUG  ";
            case Log.VERBOSE:
                return "VERBOSE";
            default:
                return "NO PRIO";
        }
    }

    private static String getLogChunk(int priority, String chunk) {
        return translatePriority(priority) + ": " + chunk + '\n';
    }

    private static String getLogContent(int priority, String content) {
        StringBuilder     output = new StringBuilder();
        ArrayList<String> lines  = new ArrayList<>(Arrays.asList(content.split(System.getProperty("line.separator"))));
        for (String line : lines) {
            output.append(getLogChunk(priority, HORIZONTAL_LINE + line));
        }
        return output.toString();
    }

    private static String getEnhancedTag() {
        StackTraceElement[] ste = new Throwable().getStackTrace();

        if (ste.length <= CALL_STACK_INDEX) {
            return BASE_TAG;
        } else {
            String oneLevelUp   = createStackElementTag(ste[CALL_STACK_INDEX + 1]);
            String currentLevel = createStackElementTag(ste[CALL_STACK_INDEX]);
            return " " + TOP_LEFT_CORNER + '─' + currentLevel + '\n' + oneLevelUp;
        }
    }

    private String getBasicTag() {
        StackTraceElement[] ste = new Throwable().getStackTrace();

        if (ste.length <= CALL_STACK_INDEX) {
            return BASE_TAG;
        } else {
            return createStackElementTag(ste[CALL_STACK_INDEX]);
        }
    }

    private static String createStackElementTag(StackTraceElement element) {
        String  tag            = element.getClassName();
        Pattern anonymousClass = Pattern.compile("(\\$\\d+)+$");
        Matcher m              = anonymousClass.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1);
        tag = tag + "." + element.getMethodName() + "()";
        if (!PRINT_FANCY) {
            tag = BASE_TAG + tag;
        }
        return tag;
    }

    private static String getTag() {
        StackTraceElement element = new Throwable().getStackTrace()[CALL_STACK_INDEX - 1];
        return createStackElementTag(element);
    }
    //endregion

}
