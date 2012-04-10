package javax.time.builder;

import javax.time.CalendricalException;

/**
 * A field of date/time.
 * <p>
 * All fields of date and/or time inherit from this interface.
 */
public interface CalendricalField {

    /**
     * Gets a descriptive name for the field.
     * <p>
     * The should be of the format 'BaseOfRange', such as 'MonthOfYear',
     * unless the field is unbounded, such as 'Year' or 'Era', when only
     * the base unit is mentioned.
     * 
     * @return the name, not null
     */
    String getName();

    /**
     * Gets the value of the field from the specified calendrical.
     * 
     * @param calendrical  the calendrical object, not null
     * @return the value of the field
     * @throws CalendricalException if unable to get the field
     */
    long getValueFrom(CalendricalObject calendrical);

}
