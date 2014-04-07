package region_data_exceptions;

/**
 * The ImageInvalidDimensionException is a checked exception that represents the
 * occasion where the image has an invalid dimension, like -1 length.
 *
 * @author Leixiang Wu Stony Brook Student
 * @version 1.0
 */
public class ImageInvalidDimensionException extends Exception {

    // NAME OF THE IMAGE THAT DIDN'T HAVE A VALID DIMENSION
    private String imageWithError;

    /**
     * Constructor for this exception, these are simple objects, we'll just
     * store some info about the error.
     *
     * @param initImageWithError the name of the image that didn't have a valid
     * dimension.
     */
    public ImageInvalidDimensionException(String initImageWithError) {
        // KEEP IT FOR LATER
        imageWithError = initImageWithError;
    }

    /**
     * This method builds and returns a textual description of this object,
     * which basically summarizes what went wrong.
     *
     * @return This message will be useful for describing which image has an
     * invalid dimension.
     */
    @Override
    public String toString() {
        return imageWithError + " image has invalid dimension.";
    }
}