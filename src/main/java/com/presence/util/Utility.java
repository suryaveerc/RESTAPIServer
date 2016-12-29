/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presence.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

/**
 *
 * @author chauh
 */
public class Utility {
    //Reference: https://github.com/fangyidong/json-simple/blob/master/src/main/java/org/json/simple/JSONValue.java

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utility.class);
    public final static String SELECT = "select";
    public final static String FILTER = "filter";
    private final static Map<String, String> replacements;
    private final static Map<String, String> filterReplacements;
    private final static String regexp = "\":|,\"|\\{\"|\\}";
    private final static String regexpForSelectKeys = "\":1|,\"|\\{\"|\\}";
    private final static String regexForFilter = "\":|,\"|\\{\"|\\}";
    private final static Pattern p = Pattern.compile(regexp);
    private final static Pattern selectP = Pattern.compile(regexpForSelectKeys);
    private final static Pattern filterP = Pattern.compile(regexForFilter);

    static {
        replacements = new HashMap<String, String>() {
            {
                put("\":", "=");
                put(",\"", ",");
                put("{\"", "");
                put("}", "");
                put("\":1", "");
            }
        };
        filterReplacements = new HashMap<String, String>() {
            {
                put("\":", "=");
                put(",\"", " AND ");
                put("{\"", "");
                put("}", "");
            }

        };

    }
//Ref: http://stackoverflow.com/a/7661573/3275095

    public static String resultsetToJson(ResultSet rs, String[] columns) throws SQLException {
        long start = System.nanoTime();
        StringBuilder outputJson = new StringBuilder(500);
        try {

            int counter = 0;
            int keysCount = 0;

            ResultSetMetaData rsmd = rs.getMetaData();
            if (columns != null) {
                keysCount = columns.length;
            } else {
                keysCount = rsmd.getColumnCount();
                columns = new String[keysCount];
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = rsmd.getColumnLabel(i + 1);

                }
            }
            int[] columnTypes = new int[keysCount];
            for (int i = 0; i < columns.length; i++) {
                columnTypes[i] = rsmd.getColumnType(i + 1);
            }

    //        logger.debug("Columns count: ", keysCount);

            outputJson.append("[");//start array

            while (rs.next()) {
                outputJson.append("{"); //start object
                for (int i = 1; i <= keysCount; i++) {
      //              System.out.println("counter ***********" + counter + " " + columns[i - 1] + " type: " + rsmd.getColumnType(i));
                    outputJson.append("\"").append(columns[i - 1]).append("\":");

                    switch (columnTypes[i-1]) {
                        case Types.INTEGER:
                            outputJson.append(rs.getInt(i)).append(",");
                            break;
                        case Types.LONGVARBINARY:
                            String val = rs.getString(i);
                            if (val != null) {
                                outputJson.append("\"").append(Utility.escape(val)).append("\",");
                            } else {
                                outputJson.append("\"\",");
                            }
                            break;
                        default:
                            val = rs.getString(i);
                            if (val != null) {
                                outputJson.append("\"").append(rs.getString(i)).append("\",");
                            } else {
                                outputJson.append("\"\",");
                            }
                    }
/*
                    if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
                        outputJson.append(rs.getInt(i)).append(",");
                    } else if (rsmd.getColumnType(i) == java.sql.Types.LONGVARBINARY) {
                        String val = rs.getString(i);
                        if (val != null) {
                            outputJson.append("\"").append(Utility.escape(val)).append("\",");
                        } else {
                            outputJson.append("\"\",");
                        }
                    } else {
                        String val = rs.getString(i);
                        if (val != null) {
                            outputJson.append("\"").append(rs.getString(i)).append("\",");
                        } else {
                            outputJson.append("\"\",");
                        }
                    }
*/
                }
                outputJson.setCharAt(outputJson.lastIndexOf(","), '}'); //remove last ',' and close object
                outputJson.append(','); //add ',' for another object
            }
            outputJson.setCharAt(outputJson.lastIndexOf(","), ']'); //remove last ',' and close array
        //    logger.debug("Generated JSON is: \n{}", outputJson);
    logger.debug("resultsetToJSON time: {}",(float)(System.nanoTime() - start)/1000000);
            return outputJson.toString();
        } catch (SQLException ex) {
            logger.error("Error while creating JSON from resultset ", ex);
            throw ex;
        }

    }

    public static String convertJSONToSQL(String input, String type) {
    //long start = System.nanoTime();
    
        Matcher m;
        if (SELECT.equalsIgnoreCase(type)) {
            if (input == null) {
      //                      logger.debug("convertJSONToSQL time: {}",(float)(System.nanoTime() - start)/1000000);

                return "*";
            }
            m = selectP.matcher(input);
            logger.debug("Building select keys");
        } else if (FILTER.equalsIgnoreCase(type)) {
            if (input == null) {
        //        logger.debug("convertJSONToSQL time: {}",(float)(System.nanoTime() - start)/1000000);
                return null;
            }
            m = filterP.matcher(input);
            logger.debug("Building filters");
        } else {
            logger.debug("Building others");
            m = p.matcher(input);
        }
        StringBuffer sb = new StringBuffer(input.length());
        while (m.find()) {
            m.appendReplacement(sb, FILTER.equalsIgnoreCase(type) ? filterReplacements.get(m.group()) : replacements.get(m.group()));
        }
        m.appendTail(sb);
        logger.debug("JSON to SQL sub string: {}", sb.toString());
        //logger.debug("convertJSONToSQL time: {}",(float)(System.nanoTime() - start)/1000000);
        return sb.toString();
    }

    public static List<String> mapToList(Map<String, String> keyValues) {
        List<String> kv = new ArrayList<>();
        StringBuilder keys = new StringBuilder(500).append('(');
        StringBuilder values = new StringBuilder(500).append('(');
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            keys.append(entry.getKey()).append(',');
            values.append(entry.getValue()).append(',');
        }
        keys.setCharAt(keys.lastIndexOf(","), ')'); //remove last',' and close bracket
        values.setCharAt(values.lastIndexOf(","), ')'); //remove last',' and close bracket
        kv.add(keys.toString());
        kv.add(values.toString());
//        logger.debug("Generated keys string: {}", keys);
//        logger.debug("Generated values string: {}", values);
        return kv;
    }

    public static Map<String, String> tokenizeSingleJsonArray(String json) {
        Map<String, String> jsonMap = new HashMap<>();
        //json = json.replaceAll("[{|}]", "");
    
        json = json.substring(json.indexOf('{') + 1, json.lastIndexOf('}'));
        String str = null;
        String key = null;
        String val = null;
        StringTokenizer fullJson = new StringTokenizer(json, ",");
        int firstIndexOfColon = 0;
        while (fullJson.hasMoreElements()) {
            str = fullJson.nextToken();
            firstIndexOfColon = str.indexOf("\":");
            key = str.substring(1, firstIndexOfColon);
            //val = str.substring(2 + firstIndexOfColon); //skip ":
            val = str.substring(firstIndexOfColon + 2); //taking values with quotes, easy to create insert string.
            //val = str.charAt(1 + firstIndexOfColon) == '"' ? str.substring(3 + firstIndexOfColon, str.lastIndexOf("\"")) : str.substring(2 + firstIndexOfColon);
            //val = ((firstIndexOfColon + 1) < str.length()) ? str.substring(1 + firstIndexOfColon) : "";
            //System.out.println(key + " " + val);

            jsonMap.put(key, val);
        }
        return jsonMap;
    }
//Ref: http://stackoverflow.com/questions/1030479/most-efficient-way-of-converting-string-to-integer-in-java/30732429#30732429

    public static int parseInt(final String s) {
        // Check for a sign.
        int num = 0;
        int sign = -1;
        final int len = s.length();
        final char ch = s.charAt(0);
        if (ch == '-') {
            sign = 1;
        } else {
            num = '0' - ch;
        }

        // Build the number.
        int i = 1;
        while (i < len) {
            num = num * 10 + '0' - s.charAt(i++);
        }

        return sign * num;
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    static void escape(String s, StringBuffer sb) {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }//for
    }

}
