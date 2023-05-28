package oop.ex5.main;

/**
 * This class represents a syntax error in the s-java code file.
 */
public class SyntaxException extends BadJavasFileException{
    private static final long serialVersionUID = 1L;

    /* -----= Constructors =----- */

    /**
     * SyntaxException constructor.
     * it gets an informative message.
     */
    public SyntaxException(){
        super("Error: syntax error in java-s file.");
    }
}
