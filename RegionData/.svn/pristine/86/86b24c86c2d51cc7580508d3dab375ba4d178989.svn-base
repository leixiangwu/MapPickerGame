package region_data_exceptions;

/**
 * The FileIsMissingException is a checked exception that represents the
 * occasion where the file like XML or is missing.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class FileIsMissingException extends Exception {

    // NAME OF THE FILE THAT WASN'T FOUND.
    private String fileWithError;

    /**
     *
     * Constructor for this exception , these are simple objects , we'll just
     * store some info about the error.
     *
     * @param initFileWithError the file name that wasn't found.
     *
     */
    public FileIsMissingException(String initFileWithError) {
        // KEEP IT FOR LATER
        fileWithError = initFileWithError;
    }

    /**
     * This method builds and returns a textual description of this object,
     * which basically summarizes what went wrong.
     *
     * @return This message will be useful for describing what file
     * wasn't found.
     */
    @Override
    public String toString() {
        return "File (" + fileWithError + ") wasn't found.";
    }
}
