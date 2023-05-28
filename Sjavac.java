package oop.ex5.main;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.*;


/**
 * The oop.ex5.main class, containing the program that runs it all
 */
public class Sjavac {


    /**
     * The oop.ex5.main func
     * @param args the arguments of the program
     */
    public static void main(String[] args){
        GlobalScope globalScope = new GlobalScope(); // the global scope
        Stack<Scope> innerScopes = new Stack<>(); // the stack call, not including the global scope
        List<String> lines = new ArrayList<>();

        try {
            File javasFile = new File(args[0]);
            BufferedReader buffer = new BufferedReader(new FileReader(javasFile));
            String readLine;
            // get file's lines into array
            while ((readLine = buffer.readLine()) != null) {
                lines.add(readLine);
            }

            int currentLine = 0, openScopesCounter = 0;
            // first run on the lines - only collects global vars and methods
            for (String line: lines) {
                openScopesCounter += globalScope.parseLine
                        (line, currentLine, innerScopes, globalScope, openScopesCounter);
                ++currentLine;
            }
            // second run on the lines, only on the inner lines of all the methods
            for (Method method: globalScope.methodHashMap.values()){
                innerScopes.add(new LocalScope());
                innerScopes.peek().putAll(method.arguments); // get method args in scope
                int i = method.lineOfBeginning + 1;
                // end of method only if cur line is 'return' and next line is '}'
                while(i < lines.size() -1 && !(lines.get(i+1).matches(LocalScope.CLOSING_PARENTHESES_PATTERN)
                        && lines.get(i).matches(LocalScope.RETURN_PATTERN) && innerScopes.size() == 1)) {

                    innerScopes.peek().parseLine
                            (lines.get(i), i, innerScopes, globalScope, openScopesCounter);
                    i++;
                }

                if (i < lines.size() -1 && lines.get(i+1).matches(LocalScope.CLOSING_PARENTHESES_PATTERN) &&
                        lines.get(i).matches(LocalScope.RETURN_PATTERN))
                    innerScopes.pop();
                if (!innerScopes.empty()) { // not enough '}' to close stack call
                    throw new ParenthesisException();
                }
            }
        }
        catch (BadJavasFileException e){ // not a valid java-s file
            System.err.println(e.getMessage());
            System.out.println(1);
            return;
        }
        catch (IOException ioe){ // error handling as described in the guidelines
            System.err.println(ioe.getMessage());
            System.out.println(2);
            return;
        }
        System.out.println(0);
    }
}
