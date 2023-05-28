package oop.ex5.main;

/**
 * This class represents an error in the s-java code file.
 */
public class BadJavasFileException extends Exception{
    private static final long serialVersionUID = 1L;

    /* -----= Constructors =----- */

    /**
     * BadJavasFileException constructor.
     * @param message An informative message that fits the error.
     */
    public BadJavasFileException(String message){
        super(message);
    }
}
