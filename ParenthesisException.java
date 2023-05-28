package oop.ex5.main;
/**
 * This class represents an error regarding the parenthesis in the code.
 */
class ParenthesisException extends BadJavasFileException {

    private static final long serialVersionUID = 1L;

    /* -----= Constructors =----- */

    /**
     * ParenthesisException constructor,
     * it gets an informative message.
     */
    public ParenthesisException(){
        super("Error: missing parenthesis in java-s file.");
    }
}
