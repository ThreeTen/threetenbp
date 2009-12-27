/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */

package javax.time.i18n;

/**
 * Package scoped utility class for international calendars.
 *
 * @author Ryoji Suzuki
 */
class I18NUtil {
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
       if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }
}
