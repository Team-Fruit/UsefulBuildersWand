package net.teamfruit.usefulbuilderswand;

import org.apache.commons.lang.StringUtils;

public class NestedStringUtils {
	public static String substringNested(final String str, final String open, final String close) {
		if (str==null||open==null||close==null)
			return null;
		final int openlength = open.length();
		final int closelength = close.length();
		int start = StringUtils.INDEX_NOT_FOUND;
		int stack = 0;
		int current = 0;
		do {
			final int currentstart = str.indexOf(open, current);
			final int currentend = str.indexOf(close, current);
			if (currentend==StringUtils.INDEX_NOT_FOUND)
				return null;
			else if (currentstart==StringUtils.INDEX_NOT_FOUND||currentstart>currentend) {
				if (stack>0)
					stack--;
				current = currentend+closelength;
			} else {
				if (stack>0)
					stack++;
				else {
					stack = 1;
					start = currentstart;
				}
				current = currentstart+openlength;
			}
		} while (stack>0||start==StringUtils.INDEX_NOT_FOUND);
		return str.substring(start+openlength, current-closelength);
	}

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
				if (closeesclength<=0||!str.startsWith(closeesc, currentend-closeesclength))
					if (stack>0)
						stack--;
				current = currentend+closelength;
			} else {
				;
				if (openesclength<=0||!str.startsWith(openesc, currentstart-openesclength))
					if (stack>0)
						stack++;
					else {
						stack = 1;
						start = currentstart;
					}
				current = currentstart+openlength;
			}
		} while (stack>0||start==StringUtils.INDEX_NOT_FOUND);
		return str.substring(start+openlength, current-closelength);
	}
}
