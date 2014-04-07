
package region_data_exceptions;

/**
 * The AnthemIsMissingException is a checked exception that represents the
 * occasion where the anthem is not existed.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class AnthemIsMissingException extends Exception {

    // NAME OF THE ANTHEM FILE THAT WASN'T FOUND.
    private String anthemWithError;

    /**
     * Constructor for this exception, these are simple objects, we'll just
     * store some info about the error.
     *
     * @param initAnthemWithError the name of the anthem that wasn't found.
     *
     */
    public AnthemIsMissingException(String initAnthemWithError) {
        // KEEP IT FOR LATER
        anthemWithError = initAnthemWithError;
    }

    /**
     * This method builds and returns a textual description of this object,
     * which basically summarizes what went wrong.
     *
     * @return This message will be useful for describing what anthem wasn't
     * found.
     */
    @Override
    public String toString() {
        return "Anthem (" + anthemWithError + ") wasn't found.";
    }
}
