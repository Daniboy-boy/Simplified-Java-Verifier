package oop.ex5.main;
import java.util.Stack;

/**
 * Claas represents a local scope. for example, if and while has local scopes.
 * Local scope has HashMap of variables from Scope, and can parse a line according to how a local variable
 * will process it.
 */
public class LocalScope extends Scope {

    /**
     * Constants of common regular expressions, used by LocalScope
     */
    public static final String RETURN_PATTERN = "\\s*return\\s*;\\s*";
    public static final String CLOSING_PARENTHESES_PATTERN = "\\s*[}]\\s*";
    private static final String METHOD_NAME_PATTERN = "^\\s*[A-Za-z]\\w+\\s*";
    private static final String SUFFIX_PATTERN ="\\s*[;]\\s*";
    private static final String GENERAL_CONDITION_PATTERN = "^(\\s*(if|while))\\s*[(].*[)]\\s*[{]\\s*";
    private static final String METHOD_CALL_PATTERN = METHOD_NAME_PATTERN +"[(]" +".*"+ "[)]" +
            SUFFIX_PATTERN;

    /**
     * ctr
     */
    public LocalScope() {
        super();
    }

    @Override
    public int parseLine(String line, int numOfLine, Stack<Scope> innerScopes, GlobalScope globalScope,
                         int openScopesCounter) throws BadJavasFileException {
        if (isCommentLine(line) || isWhiteSpaceLine(line))
            return 0;

        else if (checkForVariable(line, innerScopes, globalScope))
            return 0;

        else if (line.matches(METHOD_CALL_PATTERN)) {
            validateMethodCall(line, innerScopes, globalScope);
            return 0;
        }

        else if (line.matches(GENERAL_CONDITION_PATTERN)) {
            validateCondition(line, innerScopes, globalScope);
            innerScopes.add(new LocalScope());
            return 1;
        }
        else if (line.matches(RETURN_PATTERN))
            return 0;

        else if (line.matches(CLOSING_PARENTHESES_PATTERN)) {
            innerScopes.pop();
            return -1;
        }
        else
            throw new SyntaxException();
    }

    /**
     * Checks if the method exist in the hashMap of all the methods in global scope.
     * return true if global scope has another method with the same name as method, false otherwise
     * @param method the method to search
     * @return true if global scope has another method with the same name as method, false otherwise
     */
    boolean isMethodExist(String method, GlobalScope globalScope){
        return globalScope.methodHashMap.get(method) != null;
    }

    /**
     * Checks if method call matches a method that exist, and has matches its signature
     * @param line the line of the method call to process
     * @param innerScopes  the stack of all the inner scopes (call stack)
     * @param globalScope the global scope
     * @throws BadJavasFileException if fails
     */
    void validateMethodCall(String line, Stack<Scope> innerScopes, GlobalScope globalScope)
            throws BadJavasFileException {

        String methodName;
        String[] methodArguments;

        try {
            methodName = line.substring(0,line.indexOf(OPENING_PARENTHESES)).strip(); //name of method.
            methodArguments = line.substring(line.indexOf(OPENING_PARENTHESES)+1, //array of all arguments.
                    line.indexOf(CLOSING_PARENTHESES)).replaceAll(" ", "").split(",");
        }
        // if there's no '()' in line
        catch (IndexOutOfBoundsException e){
            throw new SyntaxException();
        }

        //check method name.
        if(!isMethodExist(methodName, globalScope)){
            throw new MethodException();
        }
        // compare its signature to the method registered in the globalScope
        if(!globalScope.methodHashMap.get(methodName).compareMethodArguments
                (methodArguments, innerScopes, globalScope)){
            throw new MethodException();
        }
    }

    /**
     * validates a line with a condition block
     * @param line the line with the condition line
     * @param innerScope the call stack of all the local scopes
     * @param globalScope the global scope
     * @throws BadJavasFileException if fails
     */
    public void validateCondition(String line, Stack<Scope> innerScope, GlobalScope globalScope)
            throws BadJavasFileException {
        String condition = line.substring(line.indexOf(OPENING_PARENTHESES)+1,
                line.lastIndexOf(CLOSING_PARENTHESES)).strip();

        if(condition.endsWith("&&") || condition.endsWith("||") ) {
            throw new BadBooleanConditionException();
        }
        String[] allConditions = condition.split("&&|[|]{2}");

        for (String value: allConditions){
            value = value.strip();
            if(value.matches(BOOLEAN_PATTERN)){ // we're good to go!
                continue;
            }
            // make sure parameter fits a boolean expression
            else if(value.matches(VARIABLE_NAME_PATTERN)){
                Variable variable = searchVariable(innerScope, globalScope, value);
                if(variable == null || (variable.getType().equals("String") ||
                        variable.getType().equals("char")) || !variable.isInitialized()){
                    throw new VariableException();
                }
            }

            else // not a boolean expression
                throw new BadBooleanConditionException();
        }
    }
}
