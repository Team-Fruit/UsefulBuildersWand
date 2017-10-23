package net.teamfruit.ubw;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.apache.commons.lang.ArrayUtils;

public class ReflectionUtil {
	public static Constructor<?> $new(final Class<?> $class, final Class<?>... $paramtypes) throws Exception {
		return $class.getConstructor($paramtypes);
	}

	public static Constructor<?> $pnew(final Class<?> $class, final Class<?>... $paramtypes) throws Exception {
		final Constructor<?> $new = $class.getDeclaredConstructor($paramtypes);
		$new.setAccessible(true);
		return $new;
	}

	public static @Nullable Constructor<?> $$new(final @Nullable Class<?> $class, final Class<?>... $paramtypes) {
		if ($class==null||ArrayUtils.contains($paramtypes, null))
			return null;
		try {
			return $new($class, $paramtypes);
		} catch (final Exception e) {
		}
		return null;
	}

	public static @Nullable Constructor<?> $$pnew(final @Nullable Class<?> $class, final Class<?>... $paramtypes) {
		if ($class==null||ArrayUtils.contains($paramtypes, null))
			return null;
		try {
			return $pnew($class, $paramtypes);
		} catch (final Exception e) {
		}
		return null;
	}

	public static Method $method(final Class<?> $class, final String _method, final Class<?>... $paramtypes) throws Exception {
		return $class.getMethod(_method, $paramtypes);
	}

	public static Method $pmethod(Class<?> $class, final String _method, final Class<?>... $paramtypes) throws Exception {
		Method $method = null;
		do
			try {
				$method = $class.getDeclaredMethod(_method, $paramtypes);
				break;
			} catch (final Exception e) {
			}
		while (($class = $class.getSuperclass())!=null);
		if ($method==null)
			throw new NoSuchMethodException();
		$method.setAccessible(true);
		return $method;
	}

	public static @Nullable Method $$method(final @Nullable Class<?> $class, final String _method, final Class<?>... $paramtypes) {
		if ($class==null||ArrayUtils.contains($paramtypes, null))
			return null;
		try {
			return $method($class, _method, $paramtypes);
		} catch (final Exception e) {
		}
		return null;
	}

	public static @Nullable Method $$pmethod(final @Nullable Class<?> $class, final String _method, final Class<?>... $paramtypes) {
		if ($class==null||ArrayUtils.contains($paramtypes, null))
			return null;
		try {
			return $pmethod($class, _method, $paramtypes);
		} catch (final Exception e) {
		}
		return null;
	}

	public static Field $field(final Class<?> $class, final String _field) throws Exception {
		return $class.getField(_field);
	}

	public static Field $pfield(Class<?> $class, final String _field) throws Exception {
		Field $field = null;
		do
			try {
				$field = $class.getDeclaredField(_field);
				break;
			} catch (final Exception e) {
			}
		while (($class = $class.getSuperclass())!=null);
		if ($field==null)
			throw new NoSuchFieldException();
		$field.setAccessible(true);
		return $field;
	}

	public static @Nullable Field $$field(final @Nullable Class<?> $class, final String _field) {
		if ($class==null)
			return null;
		try {
			return $field($class, _field);
		} catch (final Exception e) {
		}
		return null;
	}

	public static @Nullable Field $$pfield(final @Nullable Class<?> $class, final String _field) {
		if ($class==null)
			return null;
		try {
			return $pfield($class, _field);
		} catch (final Exception e) {
		}
		return null;
	}
}
