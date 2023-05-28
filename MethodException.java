package oop.ex5.main;

/**
 * This class represents an error regarding methods in the code.
 */
public class MethodException extends BadJavasFileException{
    private static final long serialVersionUID = 1L;

    /* -----= Constructors =----- */

    /**
     * MethodException constructor,
     * it gets an informative message.
     */
    public MethodException(){
        super("Error: error regarding Methods in java-s file.");
    }
}
