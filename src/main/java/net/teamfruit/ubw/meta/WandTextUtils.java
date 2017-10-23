package net.teamfruit.ubw.meta;

import static net.teamfruit.ubw.meta.WandMetaUtils.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class WandTextUtils {
	private static final Pattern p = Pattern.compile("\\$\\{(.*?)\\}");

	private static String resolve(final Deque<String> stack, final IWandMeta data, final String str) {
		if (stack.contains(str))
			return null;
		stack.push(str);

		final Matcher m = p.matcher(str);

		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			final String wrappedkey = m.group();
			final String key = StringUtils.substringBetween(wrappedkey, "${", "}");
			final String value = toStringOrNull(get(data, key));
			String res = null;
			c: {
				if (value==null) {
					final String valueif = toStringOrNull(get(data, key+".if"));
					if (valueif!=null) {
						final String resif = resolve(stack, data, valueif);
						final Boolean resifbool = BooleanUtils.toBooleanObject(resif);
						if (resifbool==null) {
							res = "(if:"+valueif+")";
							break c;
						}
						final String valuebool = toStringOrNull(get(data, key+(resifbool ? ".true" : ".false")));
						if (valuebool!=null)
							res = resolve(stack, data, valuebool);
						else
							res = "";
						break c;
					}

					final String valueeval = toStringOrNull(get(data, key+".eval"));
					if (valueeval!=null) {
						final Evals evtype = Evals.evals.get(valueeval);
						if (evtype!=null) {
							final List<String> vargs = Lists.newArrayList();
							String valuearg;
							for (int i = 0; (valuearg = toStringOrNull(get(data, key+".arg"+i)))!=null; i++) {
								final String resarg = resolve(stack, data, valuearg);
								if (resarg!=null)
									vargs.add(resarg);
							}
							res = evtype.eval(vargs);
							break c;
						}
					}
				} else {
					res = resolve(stack, data, value);
					break c;
				}
			}
			if (res==null)
				res = "(err:"+key+")";
			m.appendReplacement(sb, Matcher.quoteReplacement(res));
		}
		m.appendTail(sb);

		stack.pop();
		return sb.toString();
	}

	public static String resolve(final IWandMeta data, final String str) {
		return resolve(new ArrayDeque<String>(), data, str);
	}

	public static @Nullable String toStringOrNull(final @Nullable Object value) {
		return value!=null ? value.toString() : null;
	}
}
