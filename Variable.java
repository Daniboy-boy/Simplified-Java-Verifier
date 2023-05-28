package oop.ex5.main;

/**
 * This class represents a variable.
 */
public class Variable {
    /* -----= Instance Data Fields =----- */

    /**
     * variable's name
     */
    public final String name;
    /**
     * variable's type
     */
    private final String type;
    /**
     * true if variable is final and false otherwise.
     */
    private final boolean isFinal;
    /**
     * true if variable is initialized and false otherwise.
     */
    private boolean isInitialized;

    /* -----= Constructors =----- */

    /**
     * Variable's constructor
     * @param name variable's name
     * @param type variable's type
     * @param isFinal true if variable is final and false otherwise.
     * @param isInitialized true if variable is initialized and false otherwise.
     */
    public Variable(String name, String type, boolean isFinal, boolean isInitialized){
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isInitialized = isInitialized;
    }


    /* -----= Instance Methods =----- */

    /**
     * This method returns true if
     * variable is final and false otherwise.
     * @return true of false
     */
    public boolean getIsFinal(){
        return isFinal;
    }

    /**
     * This method returns the variable's type.
     * @return the variable's type.
     */
    public String getType() {
        return type;
    }


    /**
     * This method true if variable is initialized and false otherwise.
     * @return true or false
     */
    public boolean isInitialized(){
        return isInitialized;
    }

    /**
     * This function turns variable to be initialized.
     */
    public void initialize(){
        isInitialized = true;
    }
}
