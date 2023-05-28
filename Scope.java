package oop.ex5.main;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing scope. scope have map of Variables, and can preform actions on it.
 * the class implements Facade design pattern, on HashMap.
 */
public abstract class Scope {
    /**
     * the map of the variables of the scope, mapped by name of the variable(key) and the variable itself
     * (value).
     */
    private HashMap<String,Variable> variables;
    /**
     * constant of all regular expressions of the primitives and String, in a map to help get them
     */
    public static final HashMap<String, String> VARIABLES_VALUE_REGEXES = new HashMap<>() {{
        put("boolean", BOOLEAN_PATTERN);
        put("int", INT_PATTERN);
        put("char", CHAR_PATTERN);
        put("String", STRING_PATTERN);
        put("double", DOUBLE_PATTERN);
    }};

    /**
     * constants
     */
    public static final String OPENING_PARENTHESES = "(";
    public static final String CLOSING_PARENTHESES = ")";
    /**
     * Constants of the regular Expressions in use in variables
     */
    public static final String TYPES_PATTERN = "int|double|String|boolean|char";
    public static final String FINAL_PATTERN = "^\\s*final\\s+";
    public static final String EQUALS_PATTERN = "\\s*[=]\\s*";
    public static final String DECLARATION_PATTERN = "("+ FINAL_PATTERN +")?(" +
            TYPES_PATTERN + ")"+".*[;]\\s*";
    public static final String VARIABLE_NAME_PATTERN = "((\\s*[A-Za-z]\\w*\\s*)|(\\s*[_]+\\w+\\s*))";
    public static final String INITIALIZATION_PATTERN = VARIABLE_NAME_PATTERN + EQUALS_PATTERN + ".+";
    /**
     * constant of all regular expressions of the primitives and String
     */
    public static final String INT_PATTERN = "\\s*^-?[0-9]\\d*\\s*";
    public static final String DOUBLE_PATTERN = "\\s*-?[0-9]\\d*(\\.\\d+)?$\\s*" + "|" + INT_PATTERN;
    public static final String STRING_PATTERN = "\\s*\".*\"\\s*";
    public static final String BOOLEAN_PATTERN = "\\s*true\\s*$|^\\s*false\\s*" + "|" + DOUBLE_PATTERN +
            "|" + INT_PATTERN;
    public static final String CHAR_PATTERN = "\\s*'.'\\s*";


    /**
     * ctr
     */
    public Scope(){
        variables = new HashMap<>();
    }

    /**
     * part of the Facade design pattern of HashMap, putting var in the inner HashMap
     * @param var the Variable to insert in the inner HashMap
     */
    public void put(Variable var){
        variables.put(var.name,var);
    }

    /**
     * part of the Facade design pattern of HashMap, putting an HashMap of Variables inside
     * the inner HashMap of the scope
     * @param variableCollection the HashMap of the variables to add in the inner HashMap
     */
    public void putAll(HashMap<String,Variable> variableCollection){
        variables.putAll(variableCollection);
    }

    /**
     * part of the Facade design pattern of HashMap, getting the value associated with the key 'name'
     * in the inner hashmap, returning it or null if not found.
     * @param name the name of the variable to search in the hashmap
     * @return the variable associated with the key name, null if not found
     */
    public Variable get(String name){
        return variables.get(name);
    }

    /**
     * Abstract method, gets line and process it according to the current scope we're at
     * @param line the string of the current line
     * @param numOfLine the number of the current line
     * @param innerScopes the call stack of all the scopes
     * @param globalScope the global scope of the file
     * @param openScopesCounter counts how many scopes are currently open (outside the global)
     * @return 1 if added open scope, -1 if closed one, and 0 id did nothing
     * @throws BadJavasFileException any kind of its sub-exceptions
     */
    public abstract int parseLine(String line, int numOfLine, Stack<Scope> innerScopes,
                                  GlobalScope globalScope, int openScopesCounter)
            throws BadJavasFileException;

    /**
     * checks if a line is a line of comments
     * @param line the line to check
     * @return true if line of comment. false otherwise
     */
    public boolean isCommentLine(String line) {
        return line.startsWith("//");
    }

    /**
     * checks if line is a line of whiteSpaces (or empty line)
     * @param line the line to check
     * @return true if empty or line of whiteSpaces, false otherwise
     */
    public boolean isWhiteSpaceLine(String line){
        return line.strip().length() == 0;
    }

    /**
     * checks if line is a line of init/declare of variables, according to the guidelines.
     * if it is, process it and insert it (or them, if there's more than one) in the right scope.
     * @param line the line to process
     * @param innerScope the stack of the inner scopes
     * @param globalScope the global scope
     * @return true if is a Variable line, false if not
     * @throws BadJavasFileException if is line of variables but failed in the process
     */
    protected boolean checkForVariable(String line, Stack<Scope> innerScope, GlobalScope globalScope)
            throws BadJavasFileException {
        Pattern pattern = Pattern.compile(DECLARATION_PATTERN); // declaration of var(s)
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            boolean isFinal = (matcher.group(1) != null);
            String type = matcher.group(2);
            addVariable(line, isFinal, type, innerScope, globalScope);
        }
        else if(line.matches(INITIALIZATION_PATTERN)) // line in the format 'name=value'
            initializeVariableLine(line, innerScope, globalScope);
        else
            return false; // not a var line
        return true; // success
    }

    /**
     * adding a variable(s) from lne to the right scope
     * @param line the line to check
     * @param isFinal if it(they) final
     * @param type the type of it (them)
     * @param innerScopes the call stack (of inner scopes)
     * @param globalScope the global scope
     * @throws BadJavasFileException if fails
     */
    private void addVariable(String line, boolean isFinal, String type, Stack<Scope> innerScopes,
                             GlobalScope globalScope) throws BadJavasFileException {
        if (innerScopes.empty())
            globalScope.putAll(createVariables(line, isFinal, type, innerScopes, globalScope));
        else
            innerScopes.peek().putAll(createVariables(line, isFinal, type, innerScopes, globalScope));
    }

    /**
     *
     * @param line the line to see if the var in it
     * @param innerScopes the calls stack (inner scopes)
     * @param globalScope the global scope
     * @throws VariableException if init the vars failed
     */
    private static void initializeVariableLine(String line, Stack<Scope> innerScopes,
                                               GlobalScope globalScope) throws VariableException {
        String[] initLine = line.split("=");
        String name = initLine[0].strip(), value = initLine[1].strip().replaceAll(";", "");
        Variable variable = searchVariable(innerScopes, globalScope, name);

        if (variable != null && value.matches(VARIABLES_VALUE_REGEXES.get(variable.getType()))
                && !variable.getIsFinal())
            variable.initialize();
        else
            throw new VariableException();
    }

    /**
     * checks if var is already in the cur scope
     * @param name the var to search in the cur scope
     * @param innerScopes the call stack of the inner calls (scopes)
     * @param globalScope the global scope
     * @return true if var is in the current scope, false otherwise
     */
    private boolean isVariableInScope(String name, Stack<Scope> innerScopes, GlobalScope globalScope){
        if (innerScopes.empty())
            return globalScope.get(name) != null;
        else
            return innerScopes.peek().get(name) != null;
    }

    /**
     * creates a hashmap of all the vars declared or initialized in a specific line
     * @param line the line to process
     * @param isFinal indicates if the variables are final
     * @param type the type of the vars
     * @param innerScopes the stack of the calls (inner scopes)
     * @param globalScope the global scope
     * @return hashmap of all the variables (in Variable type) in the line
     * @throws BadJavasFileException if any operation failed
     */
    private HashMap<String,Variable> createVariables(String line, boolean isFinal, String type,
                                                     Stack<Scope> innerScopes, GlobalScope globalScope)
            throws BadJavasFileException {

        HashMap<String, Variable> variableMap = new HashMap<>();

        // removes the final and type from the beginning of the line
        String[] allVariablesDeclarations = line.substring(line.indexOf(type) + type.length(),
                line.lastIndexOf(";")).split(",");
        // every variable declaration in line
        for (String VariableDeclared : allVariablesDeclarations) {
            String name = VariableDeclared.strip();
            if (isVariableInScope(name, innerScopes, globalScope))
                throw new VariableException();

            if (VariableDeclared.matches(VARIABLE_NAME_PATTERN)) {
                if (isFinal) // don't change final vars!
                    throw new VariableException();
                variableMap.put(name, new Variable(name, type, true, false));
            }
            else if (VariableDeclared.matches(INITIALIZATION_PATTERN)) { // if init line
                name = name.replaceAll(" ", "").split("[=]")[0]; // name of the var
                String value = VariableDeclared.substring(VariableDeclared.indexOf("=")+1).strip();
                Variable variable = searchVariable(innerScopes, globalScope, value);
                // see if it's a var, and put reference to it if already exists
                if (variable != null && variable.getType().equals(type) && variable.isInitialized())
                    variableMap.put(name, variable);
//                checks if the type and value match
                else if (value.matches(VARIABLES_VALUE_REGEXES.get(type)))
                    variableMap.put(name, new Variable(name, type, isFinal, true));
                else
                    throw new VariableException();
            }
            else
                throw new VariableException();
        }
        return variableMap;
    }

    /**
     * searching variable in all the scopes, starting from the last (top in the stack) all the way back
     * to the global scope
     * @param innerScopes the calls stack (inner scopes)
     * @param globalScope the global scope
     * @param name the name of the variable to search in all the scopes
     * @return the variable searched if found, null otherwise
     */
    public static Variable searchVariable(Stack<Scope> innerScopes, GlobalScope globalScope, String name){
        Variable variable = null;
        for (int i = innerScopes.size() - 1; i >= 0; --i) {
            if (variable == null)
                variable = innerScopes.elementAt(i).get(name);
            else
                break;
        }

        // if not found yet
        if (variable == null)
            variable = globalScope.get(name);

        return variable;
    }


}
