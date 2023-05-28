package oop.ex5.main;
import java.util.HashMap;
import java.util.Stack;

/**
 * This class represents a method in class
 * in terms of what is in the method.
 */
public class Method  {

    /* -----= Instance Data Fields =----- */

    /**
     * Method's name
     */
    public final String name;
    /**
     * A hash map with the method's arguments names as keys
     * and the variable object it represents.
     */
    public HashMap<String,Variable> arguments;
    /**
     * Number of line in the given file where the method started.
     */
    public int lineOfBeginning;

    /* -----= Constructors =----- */

    /**
     * Method constructor.
     * @param name Method name
     * @param arguments Arguments that the method gets.
     * @param lineOfBeginning Number of line in the given file where the method started.
     */
    public Method(String name, HashMap<String, Variable> arguments, int lineOfBeginning){
        this.name = name;
        this.lineOfBeginning = lineOfBeginning;
        this.arguments = arguments;
    }


    /* -----= Instance Methods =----- */

    /**
     * This function gets given arguments from a call to tha methods
     * and checks if it fits.
     * @param inputArguments given arguments from a call to the method.
     * @param InnerScopes all Inner scopes.
     * @param globalScope global scope
     * @return ture if the given arguments fit and false otherwise
     * @throws MethodException the call to the methods was incorrect.
     */
    public boolean compareMethodArguments(String[] inputArguments, Stack<Scope> InnerScopes,
                                           GlobalScope globalScope) throws MethodException {

        if (arguments.size() == 0 && inputArguments.length == 1 && inputArguments[0].equals(""))
            return true;
        if (arguments.size() != inputArguments.length) {
            return false;
        }

        int index = 0;
        for (Variable variable : arguments.values()) {
            if (!inputArguments[index].matches("true|false") &&
                    inputArguments[index].matches(Scope.VARIABLE_NAME_PATTERN)) {
                Variable argument = Scope.searchVariable(InnerScopes, globalScope, inputArguments[index]);
                if (argument == null) {
                    throw new MethodException();
                } else if (!argument.getType().equals(variable.getType())) {
                    return false;
                }

            }
            else if (!inputArguments[index].matches(Scope.VARIABLES_VALUE_REGEXES.get(variable.getType())))
                return false;

            index++;
        }
        return true;

    }

}
