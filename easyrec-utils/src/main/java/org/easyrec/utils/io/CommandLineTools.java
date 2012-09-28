/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.utils.io;

import java.io.*;

/**
 * Utility methods for CLI's that handle command line parameter parsing,
 * output formatting and other stuff.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2005</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class CommandLineTools {
    public static boolean isParamSet(String args[], int position) {
        return getParam(args, position) != null;
    }

    public static String getParam(String args[], int position) {
        if (args == null) {
            return null;
        }
        if (args.length <= position) {
            return null;
        }
        return args[position];
    }

    public static int getIntParam(String args[], int position) {
        return Integer.parseInt(getParam(args, position));
    }

    public static float getFloatParam(String args[], int position) {
        return Float.parseFloat(getParam(args, position));
    }

    public static double getDoubleParam(String args[], int position) {
        return Double.parseDouble(getParam(args, position));
    }

    public static long getLongParam(String args[], int position) {
        return Long.parseLong(getParam(args, position));
    }

    public static boolean getBooleanParam(String args[], int position) {
        return Boolean.valueOf(getParam(args, position)).booleanValue();
    }

    /**
     * check for given flag in option array, e.g. '-h'
     *
     * @param args
     * @param option the option char
     * @return
     */
    public static boolean isShortOptionSet(String[] args, char option) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].charAt(1) == option) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check for --[option] in args. e.g. "--help"
     *
     * @param args
     * @param option the option string (without "--")
     * @return
     */
    public static boolean isExtendedOptionSet(String[] args, String option) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(option) || args[i].equals("--" + option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check for given flag in option array, e.g. '-h', with an alternative
     * extended option
     *
     * @param args
     * @param option the option char
     * @return
     */
    public static boolean isOptionSet(String[] args, char option, String extendedOption) {
        return isShortOptionSet(args, option) || isExtendedOptionSet(args, extendedOption);
    }

    public static String getParamForOption(String[] args, char shortOption, String extendedOption) {
        String paramShort = getParamForShortOption(args, shortOption);
        String paramExtended = getParamForExtendedOption(args, extendedOption);
        if (paramShort != null && paramExtended != null) {
            throw new IllegalArgumentException(
                    "short and extended option have parameters. (short:" + paramShort + " , ext: " + paramExtended +
                            ")");
        }
        if (paramShort != null) {
            return paramShort;
        }
        return paramExtended;
    }

    public static String getParamForExtendedOption(String[] args, String option) {
        option = option.startsWith("--") ? option : "--" + option;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(option) && i < args.length - 1) {
                //assume that parameter is in next string
                //check if it is another option, and return null if thats the case
                if (args[i + 1].charAt(0) == '-') {
                    return null;
                }
                return args[i + 1];
            }
            if (args[i].startsWith(option)) {
                return args[i].substring(option.length());
            }
        }
        return null;
    }

    public static String getParamForShortOption(String[] args, char option) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].charAt(1) == option) {
                    if (args[i].length() > 2) {
                        //parameter for option is in same string. like -oBlabla
                        String param = args[i].substring(2).trim();
                        return param;
                    } else if (i < args.length - 1) {
                        //assume that parameter is in next string
                        //check if it is another option, and return null if thats the case
                        if (args[i + 1].charAt(0) == '-') {
                            return null;
                        }
                        return args[i + 1];
                    }
                }
            }
        }
        return null;
    }

    public static int getIntParamForShortOption(String[] args, char option) throws NumberFormatException {
        String param = getParamForShortOption(args, option);
        return Integer.parseInt(param);
    }

    public static long getLongParamForShortOption(String[] args, char option) throws NumberFormatException {
        String param = getParamForShortOption(args, option);
        return Long.parseLong(param);
    }

    public static double getDoubleParamForShortOption(String[] args, char option) throws NumberFormatException {
        String param = getParamForShortOption(args, option);
        return Double.parseDouble(param);
    }

    public static float getFloatParamForShortOption(String[] args, char option) throws NumberFormatException {
        String param = getParamForShortOption(args, option);
        return Float.parseFloat(param);
    }

    public static boolean getBooleanParamForShortOption(String[] args, char shortOption) {
        String param = getParamForShortOption(args, shortOption);
        return Boolean.valueOf(param).booleanValue();
    }

    public static int getIntParamForOption(String[] args, char shortOption, String extendedOption)
            throws NumberFormatException {
        String param = getParamForOption(args, shortOption, extendedOption);
        return Integer.parseInt(param);
    }

    public static long getLongParamForOption(String[] args, char shortOption, String extendedOption)
            throws NumberFormatException {
        String param = getParamForOption(args, shortOption, extendedOption);
        return Long.parseLong(param);
    }

    public static double getDoubleParamForOption(String[] args, char shortOption, String extendedOption)
            throws NumberFormatException {
        String param = getParamForOption(args, shortOption, extendedOption);
        return Double.parseDouble(param);
    }

    public static float getFloatParamForOption(String[] args, char shortOption, String extendedOption)
            throws NumberFormatException {
        String param = getParamForOption(args, shortOption, extendedOption);
        return Float.parseFloat(param);
    }

    public static boolean getBooleanParamForOption(String[] args, char shortOption, String extendedOption) {
        String param = getParamForOption(args, shortOption, extendedOption);
        return Boolean.valueOf(param).booleanValue();
    }

    public static int getIntParamForExtendedOption(String[] args, String option) throws NumberFormatException {
        String param = getParamForExtendedOption(args, option);
        return Integer.parseInt(param);
    }

    public static long getLongParamForExtendedOption(String[] args, String option) throws NumberFormatException {
        String param = getParamForExtendedOption(args, option);
        return Long.parseLong(param);
    }

    public static double getDoubleParamForExtendedOption(String[] args, String option) throws NumberFormatException {
        String param = getParamForExtendedOption(args, option);
        return Double.parseDouble(param);
    }

    public static float getFloatParamForExtendedOption(String[] args, String option) throws NumberFormatException {
        String param = getParamForExtendedOption(args, option);
        return Float.parseFloat(param);
    }

    public static boolean getBooleanParamForExtendedOption(String[] args, String extendedOption) {
        String param = getParamForExtendedOption(args, extendedOption);
        return Boolean.valueOf(param).booleanValue();
    }

    public static String prompt(String message, InputStream in, OutputStream out) throws Exception {
        PrintWriter writer = new PrintWriter(out);
        writer.println(message);
        writer.flush();
        String answer = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        answer = reader.readLine();
        return answer;
    }

    /**
     * confirm dialog for text-oriented interfaces.
     *
     * @param message            the message to display prior to user input
     * @param confirmationString what the user has to type in order to confirm
     * @param confirmAnswer      the text that is displayed if the user confirms
     * @param cancelAnswer       the text that is displayed if the user doesn't confirm
     * @param in                 an InputStream to read from
     * @param out                an OutputStream to write to
     * @return a boolean indicating if the user has confirmed
     * @throws Exception
     */
    public static boolean confirm(String message, String confirmationString, String confirmAnswer, String cancelAnswer,
                                  InputStream in, OutputStream out) throws Exception {
        assert message != null;
        assert confirmationString != null;
        String reply = prompt(message + "\nType '" + confirmationString + "' to confirm.", in, out);

        if (confirmationString.equals(reply)) {
            if (confirmAnswer != null) {
                outputLine(confirmAnswer, out);
            }
            return true;
        }
        if (cancelAnswer != null) {
            outputLine(cancelAnswer, out);
        }
        return false;
    }

    public static String pad(double data, int length) {
        return pad(new Double(data), length);
    }

    public static String pad(int data, int length) {
        return pad(new Integer(data), length);
    }

    public static String pad(Object data, int length) {
        if (data == null) {
            return pad((String) null, length);
        }
        return pad(data.toString(), length);
    }

    public static String pad(String data, int length, char filler) {
        if (data == null) {
            data = "null";
        }
        if (data.length() >= length) {
            return data.substring(0, length);
        }
        StringBuilder ret = new StringBuilder();
        ret.append(data);
        for (int i = data.length(); i < length; i++) {
            ret.append(filler);
        }
        return ret.toString();
    }

    public static String pad(String data, int length) {
        return pad(data, length, ' ');
    }

    public static void outputLine(String line, OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        writer.println(line);
        writer.flush();
    }

    public static void output(String text, OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        writer.print(text);
        writer.flush();
    }

    public static void outputLine(String line) {
        System.out.println(line);
    }

    public static void output(String str) {
        System.out.print(str);
    }
}
