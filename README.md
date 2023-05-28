File description     
=============================
Sjavac.java - The oop.ex5.main class, containing the program that runs it all
Scope.java - Class representing scope. scope have map of Variables, and can preform actions on it.
              the class implements Facade design pattern, on HashMap.
GlobalScope.java - Claas represents a global scope
LocalScope.java - Claas represents a local scope
Method.java - class represents a method
Variable.java - class represents a variable
BadJavasFileException.java - This class represents an error in the s-java code file.
VariableException.java - This class represents an error regarding variables in the code.
MethodException.java - This class represents an error regarding methods in the code.
BadBooleanConditionException.java - This class represents a syntax or logic error conditions in the code
ParenthesisException.java - This class represents an error regarding the parenthesis in the code.
SyntaxException.java - This class represents a syntax error in the s-java code file.

Design
=============================
We looked at the code from the compiler's point of view. Every scope has its own "rules" - type of commands,
variables, inner scopes, ect.
With that said we chose our design as follows:
There can only two types of scopes in a code file: global scope and local scopes (like if and while blocks).
therefore, we implemented an abstract class, Scope, ancestor of LocalScope and GlobalScope classes.
(By doing so we used the modularity principle).
It implements a Facade design pattern on hashMap, because it has an inner privet HashMap,
and a 'put', 'putAll' and 'get' methods to handle Variables in it
(allowing the user to accesses only specific parts in HashMap).

We implemented classes for methods and variables as an independent classes.



Implementation details
=============================
We saw it as if every line has a scope it belongs to, the last scope in the call stack.
Variables are saved in the scope they're at, and died with the scope as the scope dies
(when we encounter a '}').
In the oop.ex5.main func, we obtain a GlobalScope object (representing the global scope of javas file), and a
stack of LocalScope objects, representing the call stack in the computer (meaning every call to a method
or an if/while block has a scope).
That allowed us to parse the code lines of the file easley (using polymorphism principle).

Method class contains a hashmap of its arguments and useful functions.

Variable class contains all the characteristics of a variable.


Answers to questions
=============================
6.1 Error handling:

If an IO exception has occurred, we print 2 and the error message, as described in the guidelines.
If the java-s file is illegal, we throw a BadJavasFileException type of exception.
BadJavasFileException is an abstract super to a subclasses, each one represents a different kind of illegal
line in the code.
Using polymorphism, in the oop.ex5.main func we catch BadJavasFileException exception (including its sub-classes),
and print '1' and an informative error message, as required.


6.2 OOP design:

* The description of our design and thinking process can be found in the DESIGN section above.
Alternatives we ruled out: in the process of finding the best design for our code, we considered giving every
scope object an array of scopes, with all the scopes nested in it. This way parsing the code will accrue
recursively.
We ruled out that option, because it's not space efficient, and makes the search for a variable more
complicated, and has more potential for bugs.

- if we'd like to add another type of variable (float for example), all we need to do is t add the desired
    type in the TYPES regex in Scope class, and its valueRegex to the regexMap.
- to add the feature of different method return type, we simply add a regex of return types - just
    like variable types but with void in in. then we check if a line starts with a [type name ()] for method,
    and [type name = .*] for variable.
    we'll also need to check every return statement, and match its value.type to the function's return type.
- to add the feature of methods of standard java, we'll add a map of all the reserved words (names of those
    methods, ect.), and their regexes, (like we did with the types and their regexes) in LocalScope.
    Check if the current line match a specific reserved word.


6.3 Regex:

VARIABLES_VALUE_REGEXES: a hashmap, mapping the name of the type to its value's regular expression.
    The types in the map, are boolean, String, int, char and double, as required.

ARGUMENT_DECLARATION: this regex checks if an argument of a method is valid.
    Has 3 groups - search for optional final, and search for type and name of the argument.
