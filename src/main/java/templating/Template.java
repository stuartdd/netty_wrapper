/*
 * Template.java
 *
 * Created on 20 August 2007, 15:26
 *
 */
package templating;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * This class manages Templates of any text file type.<br/>
 * It reads the template fragments via a URL which makes it easier to manage
 * HTML and web resources. <br/>
 * All substitution parameters are provided via a <b>java.util.Map</b> object.
 * The Map object is searched<br/>
 * for the parameter. The object returned us substituted via the getString()
 * method.<br/>
 * <br/>
 * <h2>A complete example:</h2> For this example a template called file_005.txt
 * is stored in the templates directory
 * <p/>
 * <
 * pre>
 * <code><b>
 * Name value = %{name} Date = %{date} Num = %{num} Java Vendor String =
 * %{java.vm.vendor}. Note this is not provided via the data object. --- Now the
 * multiple lines %{repeat#file_005_1.txt} --- End the multiple lines Num =
 * %{num}
 * </b>
 * </pre>
 * <p/>
 * </code> And a sub (included) template called file_005_1.txt is stored in the
 * same templates directory
 * <p/>
 * <
 * pre>
 * <code><b>
 * Line %{num} of %{numberOfLines} : %{lineText}. Note an EOL is at the end of
 * this file
 *
 * </b>
 * </pre>
 * <p/>
 * </code> The following java segment will tailor file file_005.txt.
 * <p/>
 * <
 * pre>
 * <code><b>
 * HashMap data = new HashMap();
 *
 *       // Set up the template. It is at templates/file_005.txt. Note sub template
 * file_005_1.txt is also there. Template template = new Template("templates",
 * "file_005.txt");
 *
 *       // Simple Object/String sibstitution data.put("name", "Stuart Davies"); //
 * Simple String based substitution. Replace %{name} data.put("date", new
 * Date((long)1234567894)); // Substitute via the toString() method. Replace
 * %{date} data.put("num", 12345); // Java wraps the number value obtained via
 * toString(). Replace %{num}
 *
 *       // Repeated template inclusion int numberOfLines = 5;
 * data.put("numberOfLines",numberOfLines); // Map data included in each line.
 * ArrayList list = new ArrayList(); // Repeated Section for (int i=0;
 * i&lt;numberOfLines; i++) { // Include n instances of a template. HashMap m =
 * new HashMap(); // Create 1 map per template instance m.put("num", i+1); //
 * add some data. Note this num overrides the data num inside repeat
 * m.put("lineText", "Some text from line "+i+1); list.add(m); // Add the Maps
 * to the list } data.put("file_005_1.txt", list); // Add the list to the data.
 * Note the template name. String s = template.parse(data));
 * </b>
 * </pre>
 * <p/>
 * </code> The value assigned to s is as follows:
 * <p/>
 * <
 * pre>
 * <code><b>
 * Name value = Stuart Davies Date = Thu Jan 15 07:56:07 GMT 1970 Num = 12345
 * Java Vendor String = Sun Microsystems Inc.. Note this is not provided via the
 * data object. --- Now the multiple lines Line 1 of 5 : Some text from line 01.
 * Note an EOL is at the end of this file Line 2 of 5 : Some text from line 11.
 * Note an EOL is at the end of this file Line 3 of 5 : Some text from line 21.
 * Note an EOL is at the end of this file Line 4 of 5 : Some text from line 31.
 * Note an EOL is at the end of this file Line 5 of 5 : Some text from line 41.
 * Note an EOL is at the end of this file
 *
 * --- End the multiple lines Num = 12345
 * </b>
 * </pre>
 * <p/>
 * </code> <h2>Template control tags:</h2> <h3>The following tags result in
 * additional text from other templates being inserted in to the output</h3>
 * <b>template#</b> - eg: %{template#file_006.html} Include template
 * file_006.html at this point.<br/>
 * <b>template?</b> - eg: %{template?varName} Include the template named in
 * property varName at this point.<br/>
 * <b>repeat#</b> - eg: %{repeat#file_006.html} Repeatedly include template
 * file_006.html at this point. See above example.<br/>
 * <h3>The following tags will not add text to the output. They manipulate the
 * existing data set.</h3> <b>set#</b> - eg: %{set#abc=123} Create value abc and
 * set it's value to 123. For definition inline<br/>
 * <b>set?</b> - eg: %{set?abc=123} Create value named by the value of abc and
 * set it's value to 123. For definition inline<br/>
 * <b>unSet#</b> - eg: %{set?abc=123} remove the value abc. For removal
 * inline<br/>
 * <b>bundle#</b> - eg: %{bundle#file_006.properties} This includes the set of
 * name value pairs in the properties file.<br/>
 * <b>bundle?</b> - eg: %{bundle?varName} This includes the set of name value
 * pairs in the properties file defined in varName.<br/>
 * <b>Notes on using bundles</b><br/>
 * - Bundles are within the scope of a template. A bundle loaded in the top
 * templete will provide values in ALL included (sub) templates.<br/>
 * A bundle included in a sub template overrides the higher level bundle and
 * will provide values in ALL included (sub/sub) templates. ETC.<br/>
 * - Do not load bundles in <b>repeat#</b> templates as they will be loaded each
 * time.<br/>
 * - Only ONE bundle per template can be loaded. A second bundle will replace
 * the first.<br/>
 * - Bundles are useful for String constants that can be defined once outside
 * the template.<br/>
 * <h3>The following tags are flow control tags</h3> <b>ifDef#</b> - eg:
 * %{ifDef#abc} The text following this tag, up to %{fi#} is included if abc is
 * defined<br/>
 * <b>ifUnDef#</b> - eg: %{ifUnDef#abc} The text following this tag, up to
 * %{fi#} is included if abc is NOT defined<br/>
 * <b>fi#</b> - eg: %{fi#} closes the ifDef and idUnDef tags. Dont forget the #
 * on this control tag<br/>
 * <br/>
 * <b>NOTE THAT ifDef/fi and ifUnDef/fi can be nested to any (reasonable)
 * depth</b><br/>
 * <p/>
 * <
 * pre>
 * <code>
 * 1 <b>%{ifDef#abc}</b>abc is defined and contains (<b>%{abc}</b>) so this is
 * included.<b>%{fi#}</b>
 * 2 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * <b>%{set#xyz=123}</b>3 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * <b>%{unSet#xyz}</b>4 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * <b>%{unSet#abc}</b>5 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * </pre>
 * <p/>
 * </code> If abc is defined in a data map before tailoring begins then this
 * will be the outcome:<br/>
 * <p/>
 * <
 * pre>
 * <code>
 * 1 abc is defined and contains (--abc--) so this is included.
 * 2 abc is defined so this is included.
 * 3 abc and xyz are both defined so this is included.
 * 4 abc is defined so this is included.
 * 5
 * </pre>
 * <p/>
 * </code> The following tags is for debugging<br/>
 * <b>listData</b> - eg: %{listData} Will list ALL data in the HashMap passed to
 * the parse method<br/>
 *
 * @author - Stuart Davies Davies (802996013)
 * @version $Rev: $ $Date: $
 */
public class Template {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final String TEMPLATE_STR = "Template";
    private static final String BUNDLE_STR = "Bundle:";
    private static final String IN_STR_MODE = " in String mode";
    private static final String CANNOT_USE = "Cannot use ";
    private static final String NOT_FOUND = ":notFound";
    private static final String SUB_NOT_FOUND = "' SUBSTITUTION VAR NOT FOUND";
    public static final String ERROR_PREFIX = "{{+++ERROR: ";
    public static final String ERROR_SUFFIX = " +++}}";
    public static final String REPEAT_TP = "Repeat template property:";
    private static String NL = System.getProperty("line.separator");
    private static char ID_CHAR = '%';
    private static String ID_STRING = "%";
    private static String INC = "template#";
    private static String INC_VAR = "template?";
    private static String BUNDLE_REF = "bundle#";
    private static String BUNDLE_VAR = "bundle?";
    private static String REPEAT = "repeat#";
    private static String IF_DEF = "ifDef#";
    private static String IF_UN_DEF = "ifUnDef#";
    private static String FI = "fi#";
    private static String SET_VAR = "set#";
    private static String SET_VAR_VAR = "set?";
    private static String UN_SET_VAR = "unSet#";
    private static String LIST_DATA = "listData";
    private byte[] fragmentStr;
    private int fragmentStrPos = 0;
    private int maxIndex;
    private final Deque appendToTemplateStack = new LinkedList();
    private boolean appendToTemplate = true;
    private String fileUrl = null;
    private String templateName = null;
    private Template parent;
    private Properties bundle;
    private boolean loadViaUrl = true;
    private boolean cannotUseInclude = false;

    private Template() {
    }

    public Template(String fileName) throws TemplateException {
        if (fileName == null) {
            throw new InvalidParameterException("Parameter fileName is null");
        }
        File template = new File(fileName);
        if (!template.exists()) {
            throw new InvalidParameterException(TEMPLATE_STR + " ["
                    + template.getAbsolutePath() + "] does not exist");
        }
        if (!template.isFile()) {
            throw new InvalidParameterException(TEMPLATE_STR + " ["
                    + template.getAbsolutePath() + "] is not a file");
        }
        this.fileUrl = template.getParent();
        this.templateName = template.getName();
        this.parent = null;
        this.loadViaUrl = false;
        this.fragmentStr = load(this.templateName);
        this.maxIndex = this.fragmentStr.length - 1;
    }

    /**
     * Creates a new instance of Fragment
     */
    public Template(String fileUrl, String templateName) throws TemplateException {
        this(fileUrl, true, templateName, null);
    }

    private Template(String fileUrl, boolean loadViaUrl, String templateName,
            Template parent) throws TemplateException {
        if (templateName == null) {
            throw new InvalidParameterException(
                    "Parameter templateName is null");
        }
        this.parent = parent;
        this.templateName = templateName;
        this.fileUrl = fileUrl;
        this.loadViaUrl = loadViaUrl;
        this.fragmentStr = load(this.templateName);
        this.maxIndex = this.fragmentStr.length - 1;
    }

    /**
     * @param s The string to be parsed (containing %{?} tags)
     * @param map The map containing the name value pairs
     * @return The resultant text (without %{?} values)
     */
    public static String parse(String s, Map map) {
        return parse(s, map, false);
    }

    /**
     * @param s The string to be parsed (containing %{?} tags)
     * @param map The map containing the name value pairs
     * @param ignoreUnresolvedSubs True to leave values that cannot be resolved
     * as they are
     * @return The resultant text (without %{?} values)
     */
    public static String parse(String s, Map map, boolean ignoreUnresolvedSubs) {
        Template t = new Template();
        t.fragmentStr = s.getBytes(UTF_8);
        t.maxIndex = t.fragmentStr.length - 1;
        t.fragmentStrPos = 0;
        t.fileUrl = null;
        t.templateName = null;
        t.parent = null;
        t.loadViaUrl = false;
        t.cannotUseInclude = false;
        return t.parse(map, ignoreUnresolvedSubs);
    }

    private static byte[] extendArray(byte[] subject, int newLen) {
        byte[] newByte = new byte[newLen];
        System.arraycopy(subject, 0, newByte, 0, subject.length);
        return newByte;
    }

    private static String[] split(char ch, String s) {
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ch) {
                sb0 = sb;
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        String[] returnVal = new String[]{
            sb0.toString(), sb.toString()
        };
        return returnVal;
    }

    public Map getAditionalData(String templateName) {
        return null;
    }

    public String getTemplateText() {
        return new String(fragmentStr);
    }

    public String parse(Map data1) {
        return parseDual(data1, null, false);
    }

    public String parse(Map data1, boolean ignoreUnresolvedSubs) {
        return parseDual(data1, null, ignoreUnresolvedSubs);
    }

    @Override
    public String toString() {
        return TEMPLATE_STR + ":" + templateName;
    }

    private void pushSuspend(boolean newVal) {
        if (appendToTemplate) {
            appendToTemplateStack.push(appendToTemplate);
            appendToTemplate = newVal;
        } else {
            appendToTemplateStack.push(Boolean.FALSE);
        }
    }

    private void popSuspend() {
        if (appendToTemplateStack.isEmpty()) {
            appendToTemplate = true;
        } else {
            appendToTemplate = (boolean) appendToTemplateStack.pop();
        }
    }

    private String parseDual(Map data1, Map data2, boolean ignoreUnresolvedSubs) {
        int pos = 0;
        byte c;
        StringBuilder sbx = new StringBuilder();
        if (pos > maxIndex) {
            c = 0;
        } else {
            c = fragmentStr[pos];
            pos++;
        }
        while (c != 0) {
            if (c == ID_CHAR) {
                if (pos > maxIndex) {
                    if (appendToTemplate) {
                        sbx.append((char) c);
                    }
                } else {
                    c = fragmentStr[pos];
                    pos++;
                    if (c == '{') {
                        String var = parseVarName(pos);
                        if (var == null) {
                            sbx.append(ERROR_PREFIX
                                    + "SUBSTITUTION VAR NOT TERMINATED"
                                    + ERROR_SUFFIX);
                            return sbx.toString();
                        } else {
                            pos = pos + var.length() + 1;
                            String val = lookUpVar(var, data1, data2, ignoreUnresolvedSubs);
                            if (appendToTemplate) {
                                sbx.append(val);
                            }
                        }
                    } else {
                        if (c == ID_CHAR) {
                            if (appendToTemplate) {
                                sbx.append(ID_CHAR);
                            }
                        } else {
                            if (appendToTemplate) {
                                sbx.append(ID_CHAR);
                            }
                            if (appendToTemplate) {
                                sbx.append((char) c);
                            }
                        }
                    }
                }
            } else {
                if (appendToTemplate) {
                    sbx.append((char) c);
                }
            }
            if (pos > maxIndex) {
                c = 0;
            } else {
                c = fragmentStr[pos];
                pos++;
            }

        }
        return sbx.toString();
    }

    private String lookUpVar(String name, Map data1, Map data2, boolean ignoreUnresolvedSubs) {
        if (name == null || name.length() == 0) {
            return ERROR_PREFIX + "SUBSTITUTION VAR IS EMPTY" + ERROR_SUFFIX;
        }

        if (name.startsWith(BUNDLE_VAR)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + BUNDLE_VAR
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String incName = name.substring(BUNDLE_VAR.length());
                Object incNameValue = getSubVar(incName, data1, data2);
                if (incNameValue == null) {
                    if (ignoreUnresolvedSubs) {
                        return ID_STRING + '{' + incName + '}';
                    } else {
                        return ERROR_PREFIX + "'" + INC_VAR + incName
                                + SUB_NOT_FOUND + ERROR_SUFFIX;
                    }
                }
                try {
                    bundle = loadBundle(incNameValue.toString());
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + BUNDLE_STR + incNameValue
                            + NOT_FOUND + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(BUNDLE_REF)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + BUNDLE_REF
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String bundleName = name.substring(BUNDLE_REF.length());
                try {
                    bundle = loadBundle(bundleName);
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + BUNDLE_STR + bundleName + NOT_FOUND
                            + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(REPEAT)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + REPEAT
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                StringBuilder sb = new StringBuilder();
                String repeatTemplateName = name.substring(REPEAT.length());
                Object o = getSubVar(repeatTemplateName, data1, data2);
                if (o != null) {
                    if (o instanceof List) {
                        List list = (List) o;
                        for (int i = 0; i < list.size(); i++) {
                            Object listMember = list.get(i);
                            if (listMember instanceof Map) {
                                try {
                                    Template f = new Template(fileUrl,
                                            loadViaUrl, repeatTemplateName, this);
                                    sb.append(f.parseDual(data1,
                                            (Map) listMember, ignoreUnresolvedSubs));
                                } catch (TemplateException ex) {
                                    return ignoreException(ERROR_PREFIX + TEMPLATE_STR + ':'
                                            + repeatTemplateName + NOT_FOUND
                                            + ERROR_SUFFIX, ex);
                                }
                            } else {
                                return ERROR_PREFIX
                                        + REPEAT_TP
                                        + repeatTemplateName
                                        + " must only contain java.util.Map objects"
                                        + ERROR_SUFFIX;
                            }
                        }
                        return sb.toString();
                    } else {
                        return ERROR_PREFIX + REPEAT_TP
                                + repeatTemplateName
                                + " must be of type java.util.List" + ERROR_SUFFIX;
                    }
                } else {
                    return ERROR_PREFIX + REPEAT_TP
                            + repeatTemplateName + NOT_FOUND + ERROR_SUFFIX;
                }
            }
            return "";
        }

        if (name.startsWith(INC)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + INC + IN_STR_MODE
                        + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String incName = name.substring(INC.length());
                try {
                    Template f = new Template(fileUrl, loadViaUrl, incName,
                            this);
                    return f.parseDual(data1, data2, ignoreUnresolvedSubs);
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + TEMPLATE_STR + ':' + incName + NOT_FOUND + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(INC_VAR)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + INC_VAR
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String incName = name.substring(INC_VAR.length());
                Object incNameValue = getSubVar(incName, data1, data2);
                if (incNameValue == null) {
                    if (ignoreUnresolvedSubs) {
                        return ID_STRING + '{' + incName + '}';
                    } else {
                        return ERROR_PREFIX + "'" + INC_VAR + incName
                                + SUB_NOT_FOUND + ERROR_SUFFIX;
                    }
                }
                try {
                    Template f = new Template(fileUrl, loadViaUrl,
                            incNameValue.toString(), this);
                    return f.parseDual(data1, data2, ignoreUnresolvedSubs);
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + TEMPLATE_STR + ':' + incNameValue + NOT_FOUND + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(IF_UN_DEF)) {
            String ifName = name.substring(IF_UN_DEF.length());
            Object ifVal = getSubVar(ifName, data1, data2);
            if (ifVal != null) {
                if (ifVal.toString().startsWith(ERROR_PREFIX)) {
                    ifVal = null;
                }
            }
            pushSuspend(ifVal == null);
            return "";
        }

        if (name.startsWith(IF_DEF)) {
            String ifName = name.substring(IF_DEF.length());
            Object ifVal = getSubVar(ifName, data1, data2);
            if (ifVal != null) {
                if (ifVal.toString().startsWith(ERROR_PREFIX)) {
                    ifVal = null;
                }
            }
            pushSuspend(ifVal != null);
            return "";
        }

        if (name.startsWith(FI)) {
            popSuspend();
            return "";
        }

        if (name.startsWith(SET_VAR)) {
            if (appendToTemplate) {
                String exp = name.substring(SET_VAR.length());
                String[] vals = split('=', exp);
                if (vals.length != 2) {
                    return ERROR_PREFIX + SET_VAR
                            + " expression " + exp + " is invalid" + ERROR_SUFFIX;
                }
                data1.put(vals[0], vals[1]);
            }
            return "";
        }

        if (name.startsWith(SET_VAR_VAR)) {
            if (appendToTemplate) {
                String exp = name.substring(SET_VAR_VAR.length());
                String[] vals = split('=', exp);
                if (vals.length != 2) {
                    return ERROR_PREFIX + SET_VAR_VAR
                            + " expression " + exp + " is invalid" + ERROR_SUFFIX;
                }
                Object var = getSubVar(vals[1], data1, data2);
                if (var == null) {
                    if (ignoreUnresolvedSubs) {
                        return ID_STRING + '{' + vals[1] + '}';
                    } else {
                        return ERROR_PREFIX + "'" + INC_VAR + vals[1]
                                + SUB_NOT_FOUND + ERROR_SUFFIX;
                    }
                }
                data1.put(vals[0], var);
            }
            return "";
        }

        if (name.startsWith(UN_SET_VAR)) {
            if (appendToTemplate) {
                String exp = name.substring(UN_SET_VAR.length());
                data1.remove(exp);
            }
            return "";
        }

        if (name.equals(LIST_DATA)) {
            return (stringData(data1, data2));
        }

        Object var = getSubVar(name, data1, data2);
        if (var == null) {
            if (ignoreUnresolvedSubs) {
                return ID_STRING + '{' + name + '}';
            } else {
                return ERROR_PREFIX + "'" + INC_VAR + name
                        + SUB_NOT_FOUND + ERROR_SUFFIX;
            }
        }
        return var.toString();
    }

    protected Object getBundleProperty(String name) {
        if (bundle == null) {
            if (parent == null) {
                return null;
            }
            return parent.getBundleProperty(name);
        }
        Object v = bundle.get(name);
        if (v == null) {
            if (parent != null) {
                return parent.getBundleProperty(name);
            }
        }
        return v;
    }

    public Object getSubVar(String varName, Map data1, Map data2) {
        Object o = null;
        Map additional = getAditionalData(templateName);
        if (additional != null) {
            o = additional.get(varName);
        }
        if (o == null) {
            if (data2 != null) {
                o = data2.get(varName);
            }
            if (o == null) {
                if (data1 != null) {
                    o = data1.get(varName);
                }
                if (o == null) {
                    o = getBundleProperty(varName);
                    if (o == null) {
                        o = System.getProperties().get(varName);
                        if (o == null) {
                            return null;
                        }
                    }
                }
            }
        }
        return o;
    }

    private String parseVarName(int pos) {
        byte c = 0;
        StringBuilder sb = new StringBuilder();
        if (pos > maxIndex) {
            c = 0;
        } else {
            c = fragmentStr[pos];
            pos++;

        }

        while (c != 0) {
            if (c == '}') {
                return sb.toString();
            } else {
                if (c > 31) {
                    sb.append((char) c);
                } else {
                    sb.append('_');
                }

            }
            if (pos > maxIndex) {
                c = 0;
            } else {
                c = fragmentStr[pos];
                pos++;

            }

        }
        return null;
    }

    private String stringData(Map data1, Map data2) {
        StringBuilder sb = new StringBuilder();
        if (data2 != null) {
            sb.append("Data set 2 ---------------- Overrides Data set 1.").append(NL);
            sb.append("Data size = ").append(data2.size()).append(NL);
            for (Iterator it = data2.entrySet().iterator(); it.hasNext();) {
                Object elem = it.next();
                sb.append(elem).append(NL);
            }
        }
        if (data1 != null) {
            sb.append("Data set 1 ----------------").append(NL);
            sb.append("Data size = ").append(data1.size()).append(NL);
            for (Iterator it = data1.entrySet().iterator(); it.hasNext();) {
                Object elem = it.next();
                sb.append(elem).append(NL);
            }
        }
        String ret = sb.toString().trim();
        if (ret.length() == 0) {
            return "Data set is EMPTY";
        }
        return ret;
    }

    private Properties loadBundle(String localBundleName)
            throws TemplateException {
        Properties p = new Properties();
        String fileName = null;
        URL url = null;
        try {
            if (loadViaUrl) {
                fileName = fileUrl + "/" + localBundleName;
                url = new URL(fileName);
                p.load(url.openStream());
            } else {
                if (fileUrl != null) {
                    fileName = fileUrl + File.separator + localBundleName;
                } else {
                    fileName = localBundleName;
                }
                FileInputStream fis = new FileInputStream(fileName);
                p.load(fis);
                fis.close();
            }
        } catch (IOException ex) {
            throw new TemplateException(ignoreException("Failed to load bundle at [" + fileName + "] " + ex.getMessage(), ex));
        }
        if (p.isEmpty()) {
            return null;
        }
        return p;
    }

    private byte[] load(String localTemplateName) throws TemplateException {
        InputStream fis = null;
        URL url = null;
        String fileName = null;
        byte[] sb = new byte[500];
        fragmentStrPos = 0;
        try {
            if (loadViaUrl) {
                fileName = fileUrl + "/" + localTemplateName;
                url = new URL(fileName);
                fis = url.openStream();
            } else {
                if (fileUrl != null) {
                    fileName = fileUrl + File.separator + localTemplateName;
                } else {
                    fileName = localTemplateName;
                }
                fis = new FileInputStream(fileName);
            }
            byte[] buffer = new byte[500];
            int bytesRead = fis.read(buffer);
            while (bytesRead > 0) {
                if ((fragmentStrPos + bytesRead) > sb.length) {
                    sb = extendArray(sb, sb.length + bytesRead);
                }
                System.arraycopy(buffer, 0, sb, 0, bytesRead);
                bytesRead = fis.read(buffer);
            }
            if ((fragmentStrPos + 1) > sb.length) {
                // Java 1.5 sb = Arrays.copyOf(sb, sb.length + 1);
                sb = extendArray(sb, sb.length + 1);
            }
            sb[fragmentStrPos] = (byte) 0;
            fis.close();
            return sb;
        } catch (IOException ex) {
            throw new TemplateException(ignoreException("Failed to load template at [" + fileName + "] " + ex.getMessage(), ex));
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                throw new TemplateException(ignoreException("Failed to close file [" + fileName + "] " + ex.getMessage(), ex));
            }
        }
    }

    protected static final String ignoreException(String message, Exception ex) {
        return ex.getClass().getSimpleName() + ':' + message;
    }
}
