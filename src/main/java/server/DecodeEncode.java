/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author stuart
 */
public class DecodeEncode {
        private static char[] encodeChars = new char[]{'\'', '@', '%', '+', '-', '"'};
    private static String[] encodeCodes = new String[]{"%27", "%40", "%25", "%2B", "%2D", "%22"};

    public static String encode(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            boolean notReplaced = true;
            if (c <= ' ') {
                sb.append("%20");
                notReplaced = false;
            } else {
                for (int i = 0; i < encodeChars.length; i++) {
                    if (encodeChars[i] == c) {
                        sb.append(encodeCodes[i]);
                        notReplaced = false;
                        break;
                    }
                }
            }
            if (notReplaced) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int[] hexValues = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15};

    public static String decode(String s) {
        StringBuilder sb = new StringBuilder();
        int state = 0;
        int val = 0;
        for (char c : s.toCharArray()) {
            if (c == '%') {
                state = 1;
            }
            switch (state) {
                case 0:
                    sb.append(c);
                    break;
                case 1:
                    state = 2;
                    break;
                case 2:
                    val = hexValues[(c - '0')] * 16;
                    state = 3;
                    break;
                case 3:
                    val = val + hexValues[(c - '0')];
                    state = 0;
                    sb.append((char) val);
                    break;
            }
        }
        return sb.toString();
    }

}
