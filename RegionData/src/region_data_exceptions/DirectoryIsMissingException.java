package region_data_exceptions;

/**
 * The DirectoryIsMissingException is a checked exception that represents the
 * occasion where the directory is not existed.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class DirectoryIsMissingException extends Exception {

    // NAME OF THE DIRECTORY FILE THAT WASN'T FOUND.
    private String directoryWithError;

    /**
     * Constructor for this exception, these are simple objects, we'll just
     * store some info about the error.
     *
     * @param initDirectoryWithError the name of the directory that wasn't
     * found.
     *
     */
    public DirectoryIsMissingException(String initDirectoryWithError) {
        // KEEP IT FOR LATER
        directoryWithError = initDirectoryWithError;
    }

    /**
     * This method builds and returns a textual description of this object,
     * which basically summarizes what went wrong.
     *
     * @return This message will be useful for describing what directory wasn't
     * found.
     */
    @Override
    public String toString() {
        return "Directory for region (" + directoryWithError + ") wasn't found.";
    }
}
