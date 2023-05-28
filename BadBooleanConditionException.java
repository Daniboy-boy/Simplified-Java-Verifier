package oop.ex5.main;

/**
 * This class represents a syntax or logic error
 * conditions in the code
 */
public class BadBooleanConditionException extends BadJavasFileException{

    private static final long serialVersionUID = 1L;

    /* -----= Constructors =----- */

    /**
     * BadBooleanConditionException constructor,
     * it gets an informative message.
     */
    public BadBooleanConditionException(){
        super("Error: error regarding if or while condition.");
    }
}
