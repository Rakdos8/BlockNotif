package me.tabinol.blocknotif.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.blocknotif.BlockNotif;

/**
 * Utility class all about the {@link Enum}.
 */
public final class EnumUtils {

	final static private Logger logger = BlockNotif.getThisPlugin().getLogger();

	private EnumUtils() {
		// Nothing to do
	}

	/**
	 * Retrieves the Enum from the {@link Enum#name()}.
	 *
	 * @param clazz the {@link Class} of the {@link Enum}
	 * @param name the name/value
	 * @return the {@link Enum} according to its name
	 */
	public static <E extends Enum<E>> E getEnumFromName(
			final Class<E> clazz,
			final String name
	) {
		return getEnumFromName(clazz, name, null);
	}

	/**
	 * Retrieves the Enum from the {@link Enum#name()}.
	 *
	 * @param clazz the {@link Class} of the {@link Enum}
	 * @param name the name/value
	 * @param defaultValue the default value
	 * @return the {@link Enum} according to its name
	 */
	public static <E extends Enum<E>> E getEnumFromName(
			final Class<E> clazz,
			final String name,
			final E defaultValue
	) {
		if (clazz == null) {
			logger.log(Level.WARNING, "You did not provided a Enum's class.");
			return defaultValue;
		} else if (org.apache.commons.lang.StringUtils.isBlank(name)) {
			logger.log(Level.WARNING, "You did not provided a Enum's name.");
			return defaultValue;
		}

		try {
			return Enum.valueOf(clazz, org.apache.commons.lang.StringUtils.upperCase(name));
		} catch (final IllegalArgumentException ex) {
			logger.log(Level.FINE, ex.getMessage(), ex) ;
			return defaultValue ;
		}
	}
}