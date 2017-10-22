package net.teamfruit.usefulbuilderswand.meta.lore;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class NestedStringUtils {
	/**
	 * <p>Gets the String that is nested in between two Strings.
	 * Only the first match is returned.</p>
	 *
	 * <p>A {@code null} input String returns {@code null}.
	 * A {@code null} open/close returns {@code null} (no match).
	 * An empty ("") open and close returns an empty string.</p>
	 *
	 * <pre>
	 * NestedStringUtils.substringNested("wx[b]yz", "[", "]")      = "b"
	 * NestedStringUtils.substringNested("w[x[b]y]z", "[", "]")    = "x[b]y"
	 * NestedStringUtils.substringNested("wx[[b]yz", "[", "]")     = null
	 * NestedStringUtils.substringNested("wx[b]y]z", "[", "]")     = "b"
	 * NestedStringUtils.substringNested(null, *, *)               = null
	 * NestedStringUtils.substringNested(*, null, *)               = null
	 * NestedStringUtils.substringNested(*, *, null)               = null
	 * NestedStringUtils.substringNested("", "", "")               = ""
	 * NestedStringUtils.substringNested("", "", "]")              = null
	 * NestedStringUtils.substringNested("", "[", "]")             = null
	 * NestedStringUtils.substringNested("yabcz", "", "")          = ""
	 * NestedStringUtils.substringNested("yabcz", "y", "z")        = "abc"
	 * NestedStringUtils.substringNested("yabczyabcz", "y", "z")   = "abc"
	 * </pre>
	 *
	 * @param str  the String containing the substring, may be null
	 * @param open  the String before the substring, may be null
	 * @param close  the String after the substring, may be null
	 * @return the substring, {@code null} if no match
	 */
	public static String substringNested(final String str, final String open, final String close) {
		return substringNested(str, open, close, null, null);
	}

	/**
	 * <p>Gets the String that is nested in between two Strings.
	 * Only the first match is returned.</p>
	 *
	 * <p>A {@code null} input String returns {@code null}.
	 * A {@code null} open/close returns {@code null} (no match).
	 * An empty ("") open and close returns an empty string.</p>
	 *
	 * <pre>
	 * NestedStringUtils.substringNested("wx[b]yz", "[", "]", null, null)      = "b"
	 * NestedStringUtils.substringNested("w[x[b]y]z", "[", "]", null, null)    = "x[b]y"
	 * NestedStringUtils.substringNested("wx[[b]yz", "[", "]", null, null)     = null
	 * NestedStringUtils.substringNested("wx[b]y]z", "[", "]", null, null)     = "b"
	 * NestedStringUtils.substringNested("wx[b$]y]z", "[", "]", null, "$")     = "b$]y"
	 * NestedStringUtils.substringNested("w[x^[by]z", "[", "]", "^", null)     = "x^[by"
	 * NestedStringUtils.substringNested("w[x%[b&]y]z", "[", "]", "%", "&")    = "x%[b&]y"
	 * NestedStringUtils.substringNested("j%[w[xb&]y]z", "[", "]", "%", "&")   = "xb&]y"
	 * NestedStringUtils.substringNested(null, *, *, null, null)               = null
	 * NestedStringUtils.substringNested(*, null, *, null, null)               = null
	 * NestedStringUtils.substringNested(*, *, null, null, null)               = null
	 * NestedStringUtils.substringNested("", "", "", null, null)               = ""
	 * NestedStringUtils.substringNested("", "", "]", null, null)              = null
	 * NestedStringUtils.substringNested("", "[", "]", null, null)             = null
	 * NestedStringUtils.substringNested("yabcz", "", "", null, null)          = ""
	 * NestedStringUtils.substringNested("yabcz", "y", "z", null, null)        = "abc"
	 * NestedStringUtils.substringNested("yabczyabcz", "y", "z", null, null)   = "abc"
	 * </pre>
	 *
	 * @param str  the String containing the substring, may be null
	 * @param open  the String before the substring, may be null
	 * @param close  the String after the substring, may be null
	 * @param escopen  escape sequence of open, it ignores open sequence just behind , may be null
	 * @param escclose  escape sequence of close, it ignores close sequence just behind, may be null
	 * @return the substring, {@code null} if no match
	 */
	public static String substringNested(final String str, final String open, final String close, String openesc, String closeesc) {
		if (str==null||open==null||close==null)
			return null;
		if (openesc==null)
			openesc = "";
		if (closeesc==null)
			closeesc = "";
		final int openlength = open.length();
		final int closelength = close.length();
		final int openesclength = openesc.length();
		final int closeesclength = closeesc.length();
		int start = StringUtils.INDEX_NOT_FOUND;
		int stack = 0;
		int current = 0;
		do {
			final int currentstart = str.indexOf(open, current);
			final int currentend = str.indexOf(close, current);
			if (currentend==StringUtils.INDEX_NOT_FOUND)
				return null;
			else if (currentstart==StringUtils.INDEX_NOT_FOUND||currentstart>currentend) {
				current = currentend+closelength;
				if (closeesclength<=0||!str.startsWith(closeesc, currentend-closeesclength))
					if (stack>0)
						stack--;
			} else {
				current = currentstart+openlength;
				if (openesclength<=0||!str.startsWith(openesc, currentstart-openesclength))
					if (stack>0)
						stack++;
					else {
						stack = 1;
						start = currentstart;
					}
			}
		} while (stack>0||start==StringUtils.INDEX_NOT_FOUND);
		return str.substring(start+openlength, current-closelength);
	}

	public static String substringBeforeNested(final String str, final String open) {
		return substringBeforeNested(str, open, null);
	}

	public static String substringBeforeNested(final String str, final String open, String openesc) {
		if (str==null||open==null)
			return null;
		if (openesc==null)
			openesc = "";
		final int openlength = open.length();
		final int openesclength = openesc.length();
		int current = 0;
		while (true) {
			final int currentstart = str.indexOf(open, current);
			current = currentstart+openlength;
			if (currentstart==StringUtils.INDEX_NOT_FOUND)
				return str;
			else if (openesclength<=0||!str.startsWith(openesc, currentstart-openesclength))
				return str.substring(0, currentstart);
		}
	}

	public static String substringAfterNested(final String str, final String open, final String close) {
		return substringAfterNested(str, open, close, null, null);
	}

	public static String substringAfterNested(final String str, final String open, final String close, String openesc, String closeesc) {
		if (str==null||open==null||close==null)
			return null;
		if (openesc==null)
			openesc = "";
		if (closeesc==null)
			closeesc = "";
		final int openlength = open.length();
		final int closelength = close.length();
		final int openesclength = openesc.length();
		final int closeesclength = closeesc.length();
		int start = StringUtils.INDEX_NOT_FOUND;
		int stack = 0;
		int current = 0;
		do {
			final int currentstart = str.indexOf(open, current);
			final int currentend = str.indexOf(close, current);
			if (currentend==StringUtils.INDEX_NOT_FOUND)
				return "";
			else if (currentstart==StringUtils.INDEX_NOT_FOUND||currentstart>currentend) {
				current = currentend+closelength;
				if (closeesclength<=0||!str.startsWith(closeesc, currentend-closeesclength))
					if (stack>0)
						stack--;
			} else {
				current = currentstart+openlength;
				if (openesclength<=0||!str.startsWith(openesc, currentstart-openesclength))
					if (stack>0)
						stack++;
					else {
						stack = 1;
						start = currentstart;
					}
			}
		} while (stack>0||start==StringUtils.INDEX_NOT_FOUND);
		return str.substring(current);
	}

	public static String[] splitOutsideNested(final String str, final String open, final String close, String openesc, String closeesc, final String seperate, String seperateesc) {
		if (str==null||open==null||close==null||seperate==null)
			return null;
		if (openesc==null)
			openesc = "";
		if (closeesc==null)
			closeesc = "";
		if (seperateesc==null)
			seperateesc = "";
		final int openlength = open.length();
		final int openesclength = openesc.length();
		final int closelength = close.length();
		final int closeesclength = closeesc.length();
		final int seperatelength = seperate.length();
		final int seperateesclength = seperateesc.length();
		int stack = 0;
		int current = 0;
		final List<String> seps = Lists.newArrayList();
		int lastsep = 0;
		boolean flag = false;
		while (true) {
			final int currentstart = str.indexOf(open, current);
			final int currentend = str.indexOf(close, current);
			final int currentseperate = str.indexOf(seperate, current);
			if (currentseperate==StringUtils.INDEX_NOT_FOUND)
				break;
			else if (currentend==StringUtils.INDEX_NOT_FOUND||currentend>currentseperate) {
				current = currentseperate+seperatelength;
				if (str.startsWith(seperate, currentseperate)&&str.startsWith(seperateesc, currentseperate+seperatelength))
					flag = true;
				else if (flag)
					flag = false;
				if (seperateesclength<=0||!str.startsWith(seperateesc, currentseperate-seperateesclength))
					if (stack<=0) {
						seps.add(str.substring(lastsep, currentseperate));
						lastsep = current;
					}
			} else if (currentstart==StringUtils.INDEX_NOT_FOUND||currentstart>currentend) {
				current = currentend+closelength;
				if (closeesclength<=0||!str.startsWith(closeesc, currentend-closeesclength))
					if (stack>0)
						stack--;
			} else if (currentstart!=StringUtils.INDEX_NOT_FOUND) {
				current = currentstart+openlength;
				if (openesclength<=0||!str.startsWith(openesc, currentstart-openesclength))
					if (stack>0)
						stack++;
					else
						stack = 1;
			} else
				break;
		}
		seps.add(str.substring(lastsep));
		return seps.toArray(new String[seps.size()]);
	}
}
