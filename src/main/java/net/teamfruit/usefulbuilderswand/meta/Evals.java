package net.teamfruit.usefulbuilderswand.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

public interface Evals {
	public static Map<String, Evals> evals = new HashMap<String, Evals>() {
		private void puts(final Evals o, final String... keys) {
			for (final String key : keys)
				put(key, o);
		}

		{
			puts(new AbstractEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a==b;
				}
			}, "eq", "==");
			puts(new AbstractEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a!=b;
				}
			}, "ne", "!=");
			puts(new AbstractEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a>b;
				}
			}, "gt", ">");
			puts(new AbstractEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a>=b;
				}
			}, "ge", ">=");
			puts(new AbstractEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a<b;
				}
			}, "lt", "<");
			puts(new AbstractEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a<=b;
				}
			}, "le", "<=");
			puts(new AbstractEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a+b;
				}
			}, "add", "+");
			puts(new AbstractEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a-b;
				}
			}, "sub", "-");
			puts(new AbstractEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a*b;
				}
			}, "mul", "*");
			puts(new AbstractEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a/b;
				}
			}, "div", "/");
			puts(new AbstractEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a%b;
				}
			}, "mod", "%");
		}
	};

	public static abstract class AbstractEvals<T> implements Evals {
		@Override
		public String eval(final List<String> args) {
			if (args.size()>=2) {
				final String sa = args.get(0), sb = args.get(1);
				if (NumberUtils.isNumber(sa)&&NumberUtils.isNumber(sb)) {
					final int a = NumberUtils.toInt(sa);
					final int b = NumberUtils.toInt(sb);
					return String.valueOf(eval(a, b));
				}
			}
			return null;
		}

		public abstract T eval(int a, int b);
	}

	String eval(List<String> args);
}
