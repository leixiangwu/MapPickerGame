package region_data_exceptions;

/**
 * The ImageNotFoundException is a checked exception that represents the
 * occasion where the image was not found.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class ImageNotFoundException extends Exception {

    // NAME OF THE IMAGE THAT WASN'T FOUND.
    private String imgWithError;

    /**
     * Constructor for this exception, these are simple objects, we'll just
     * store some info about the error.
     *
     * @param initImgWithError the image name that wasn't found.
     */
    public ImageNotFoundException(String initImgWithError) {
        // KEEP IT FOR LATER
        imgWithError = initImgWithError;
    }

    /**
     * This method builds and returns a textual description of this object,
     * which basically summarizes what went wrong.
     *
     * @return This message will be useful for describing what image wasn't
     * found.
     */
    @Override
    public String toString() {
        return imgWithError + " was not found.";
    }
}
