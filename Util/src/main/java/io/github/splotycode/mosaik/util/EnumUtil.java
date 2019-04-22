package io.github.splotycode.mosaik.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * General Utils for enums
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumUtil {

    /**
     * Gets a human readable display name from a enum name
     */
    public static String toDisplayName(Enum enumm) {
        return toDisplayName(enumm, "-");
    }

    /**
     * Gets a human readable display name from a enum name
     * @param separator the separator that should be used (My-Name-Is-David or My Name Is David)
     */
    public static String toDisplayName(Enum enumm, String separator) {
        return StringUtil.camelCase(enumm.name().replace('_', '-'), separator);
    }

}
