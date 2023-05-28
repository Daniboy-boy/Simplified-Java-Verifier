package oop.ex5.main;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GlobalScope extends Scope{

    /* -----= Instance Data Fields =----- */

    public static final String METHOD_NAME = "\\s*[a-zA-Z]\\w*\\s*";
    public static final String METHOD_REGEX = "\\s*void" + METHOD_NAME + "[(].*[)]\\s*[{]\\s*";
    public static final String ARGUMENT_DECLARATION = "(" + FINAL_PATTERN + ")?\\s*(" + TYPES_PATTERN +
            ")(" + VARIABLE_NAME_PATTERN + ")";

    /**
     * HashMap that contains all methods,
     * the keys represents the method's name.
     */
    public HashMap<String, Method> methodHashMap = new HashMap<>();

    /* -----= Constructors =----- */

    /**
     * Global Scope constructor.
     */
    public GlobalScope(){
        super();
    }

    /* -----= Instance Methods =----- */

    @Override
    public int parseLine(String line, int numOfLine, Stack<Scope> allScope, GlobalScope globalScope, int openScopesCounter)
            throws BadJavasFileException {
        if (isCommentLine(line) || isWhiteSpaceLine(line))
            return 0;
        else if (openScopesCounter == 0 && checkForVariable(line,allScope, globalScope))
            return 0;
        else if (openScopesCounter == 0 && checkForMethod(line)) {
            createMethod(line, numOfLine);
            return 1;
        }
        else if (openScopesCounter > 0 && line.matches(".*[{]\\s*$")){
            return 1;
        }
        else if (line.matches(".*[}]\\s*$")){
            return -1;
        }
        else if (openScopesCounter == 0)
            throw new ParenthesisException();
        return 0;
    }


    /**
     * This function returns true if the function deceleration is written
     * correctly, by java s standards. returns false otherwise.
     * @param line the line to check.
     * @return true or false.
     */
    private boolean checkForMethod(String line) {
        return line.matches(METHOD_REGEX);
    }

    /**
     * This function creates a method according to the method declaration line.
     * @param line method declaration line
     * @param numOfLine number of line
     * @throws BadJavasFileException throws a MethodException.
     */
    private void createMethod(String line, int numOfLine) throws BadJavasFileException {
        String methodName = line.substring(line.indexOf("d")+1, line.indexOf(OPENING_PARENTHESES)).strip();
        if (methodHashMap.get(methodName) != null) // if method's name exist
            throw new MethodException();

        //get the arguments as an array.
        String args = line.substring(line.indexOf(OPENING_PARENTHESES)+1, line.indexOf(CLOSING_PARENTHESES));
        String[] argumentsInString = args.split(",");

        HashMap<String,Variable> arguments = new HashMap<>();

        if (!args.equals("")){
            for (String strArgument: argumentsInString) {
                Pattern pattern = Pattern.compile(ARGUMENT_DECLARATION);
                Matcher matcher = pattern.matcher(strArgument);
                if (matcher.matches()){
                    boolean isFinal = (matcher.group(1) != null);
                    String type = matcher.group(2).strip(), name = matcher.group(3).strip();
                    if (arguments.get(name) != null || VARIABLES_VALUE_REGEXES.get(type) == null)
                        throw new MethodException();
                    arguments.put(name, new Variable(name, type, isFinal, true));
                }
                else
                    throw new MethodException();

            }
        }
        methodHashMap.put(methodName, new Method(methodName, arguments, numOfLine));
    }

}
