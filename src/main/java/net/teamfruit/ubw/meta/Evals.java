package net.teamfruit.ubw.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import net.teamfruit.ubw.PlayerUUID;

public interface Evals {
	public static Map<String, Evals> evals = new HashMap<String, Evals>() {
		private void puts(final Evals o, final String... keys) {
			for (final String key : keys)
				put(key, o);
		}

		{
			puts(new AbstractDoubleNumberEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a==b;
				}
			}, "eq", "==");
			puts(new AbstractDoubleNumberEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a!=b;
				}
			}, "ne", "!=");
			puts(new AbstractDoubleNumberEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a>b;
				}
			}, "gt", ">");
			puts(new AbstractDoubleNumberEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a>=b;
				}
			}, "ge", ">=");
			puts(new AbstractDoubleNumberEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a<b;
				}
			}, "lt", "<");
			puts(new AbstractDoubleNumberEvals<Boolean>() {
				@Override
				public Boolean eval(final int a, final int b) {
					return a<=b;
				}
			}, "le", "<=");
			puts(new AbstractDoubleNumberEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a+b;
				}
			}, "add", "+");
			puts(new AbstractDoubleNumberEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a-b;
				}
			}, "sub", "-");
			puts(new AbstractDoubleNumberEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a*b;
				}
			}, "mul", "*");
			puts(new AbstractDoubleNumberEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a/b;
				}
			}, "div", "/");
			puts(new AbstractDoubleNumberEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a%b;
				}
			}, "mod", "%");
			puts(new AbstractDoubleNumberEvals<Integer>() {
				@Override
				public Integer eval(final int a, final int b) {
					return a%b;
				}
			}, "mod", "%");
			puts(new AbstractDoubleBooleanEvals<Boolean>() {
				@Override
				public Boolean eval(final boolean a, final boolean b) {
					return a&&b;
				}
			}, "and", "&&");
			puts(new AbstractDoubleBooleanEvals<Boolean>() {
				@Override
				public Boolean eval(final boolean a, final boolean b) {
					return a||b;
				}
			}, "or", "||");
			puts(new AbstractDoubleBooleanEvals<Boolean>() {
				@Override
				public Boolean eval(final boolean a, final boolean b) {
					return BooleanUtils.xor(new boolean[] { a, b });
				}
			}, "xor");
			puts(new AbstractSingleBooleanEvals<Boolean>() {
				@Override
				public Boolean eval(final boolean a) {
					return !a;
				}
			}, "not", "!");
			puts(new AbstractDoubleEvals<Boolean>() {
				@Override
				public Boolean eval(final String sa, final String sb) {
					return StringUtils.equals(sa, sb);
				}
			}, "equals");
			puts(new AbstractSingleEvals<Boolean>() {
				@Override
				public Boolean eval(final String str) {
					return StringUtils.isEmpty(str);
				}
			}, "empty");
			puts(new AbstractSingleEvals<String>() {
				@Override
				public String eval(final String uuid) {
					return PlayerUUID.getName(uuid).orElse("");
				}
			}, "name");
		}
	};

	public static abstract class AbstractSingleEvals<T> implements Evals {
		@Override
		public String eval(final List<String> args) {
			if (args.size()>=1) {
				final String sa = args.get(0);
				return String.valueOf(eval(sa));
			}
			return null;
		}

		public abstract T eval(String str);
	}

	public static abstract class AbstractSingleBooleanEvals<T> extends AbstractSingleEvals<T> {
		@Override
		public T eval(final String sa) {
			final boolean a = BooleanUtils.toBoolean(sa);
			return eval(a);
		}

		public abstract T eval(boolean a);
	}

	public static abstract class AbstractDoubleEvals<T> implements Evals {
		@Override
		public String eval(final List<String> args) {
			if (args.size()>=2) {
				final String sa = args.get(0), sb = args.get(1);
				return String.valueOf(eval(sa, sb));
			}
			return null;
		}

		public abstract T eval(String sa, String sb);
	}

	public static abstract class AbstractDoubleNumberEvals<T> extends AbstractDoubleEvals<T> {
		@Override
		public T eval(final String sa, final String sb) {
			if (NumberUtils.isNumber(sa)&&NumberUtils.isNumber(sb)) {
				final int a = NumberUtils.toInt(sa);
				final int b = NumberUtils.toInt(sb);
				return eval(a, b);
			}
			return null;
		}

		public abstract T eval(int a, int b);
	}

	public static abstract class AbstractDoubleBooleanEvals<T> extends AbstractDoubleEvals<T> {
		@Override
		public T eval(final String sa, final String sb) {
			final boolean a = BooleanUtils.toBoolean(sa);
			final boolean b = BooleanUtils.toBoolean(sb);
			return eval(a, b);
		}

		public abstract T eval(boolean a, boolean b);
	}

	String eval(List<String> args);
}
