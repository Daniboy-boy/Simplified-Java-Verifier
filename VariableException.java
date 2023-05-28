package oop.ex5.main;

/**
 * This class represents an error regarding variables in the code.
 */
public class VariableException extends BadJavasFileException{

    private static final long serialVersionUID = 1L;


    /* -----= Constructors =----- */

    /**
     * VariableException constructor,
     * it gets an informative message.
     */
    public VariableException(){
        super("Error: error regarding Variables.");
    }
}
