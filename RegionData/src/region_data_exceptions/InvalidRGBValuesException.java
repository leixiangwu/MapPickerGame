package region_data_exceptions;

/**
 * The InvalidRGBValuesException is a checked exception that represents the
 * occasion where the RGB values are greater than 255 and less than 0.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class InvalidRGBValuesException extends Exception {

    // NAME OF THE REGION THAT DIDN'T HAVE A VALID RGB VALUES
    private String regionWithError;

    /**
     * Constructor for this exception, these are simple objects, we'll just
     * store some info about the error.
     *
     * @param initRegionWithError the region name that didn't have valid RGB
     * values
     *
     */
    public InvalidRGBValuesException(String initRegionWithError) {
        // KEEP IT FOR LATER
        regionWithError = initRegionWithError;
    }

    /**
     * This method builds and returns a textual description of this object,
     * which basically summarizes what went wrong.
     *
     * @return This message will be useful for describing what region didn't
     * have good RGB values.
     */
    @Override
    public String toString() {
        return regionWithError + " has invalid RGB values.";
    }
}
