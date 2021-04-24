package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

    public static String delims = " \t*+-/()[]";
            
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        /** COMPLETE THIS METHOD **/
        /** DO NOT create new vars and arrays - they are already created before being sent in
         ** to this method - you just need to fill them in.
         **/
        
//        StringTokenizer separator = new StringTokenizer(expr, delims);
//        
//        while(separator.hasMoreTokens()) {
//            
//            String ptr = separator.nextToken();
//        }
        
        int startIndex = -1;
        for(int i = 0; i < expr.length(); i++)
        {
            if(Character.isDigit(expr.charAt(i)) == false && delims.contains(""+ (expr.charAt(i))) == false)
            {
                if(startIndex == -1)
                {
                    startIndex = i;
                }
            }
            else
            {
                if(startIndex != -1)
                {
                    if(expr.charAt(i) == '[')
                    {
                        Array arrayName = new Array(expr.substring(startIndex, i));
                        if(arrays.contains(arrayName) == false)
                        {
                            arrays.add(arrayName);
                        }                       
                        startIndex = -1;
                    }
                    else
                    {   
                        Variable arrayName = new Variable(expr.substring(startIndex, i));
                        if(vars.contains(arrayName) == false)
                        {
                            vars.add(arrayName);
                        }
                                
                        startIndex = -1;
                    }
                }
            }
        }
        if(startIndex != -1)
        {
                Variable arrayName = new Variable(expr.substring(startIndex, expr.length()));
                if(vars.contains(arrayName) == false)
                {
                    vars.add(arrayName);
                }
                    
        }

//      System.out.println("vars is :" + vars);
//      System.out.println("arrays is :" + arrays);
}
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            arr = arrays.get(arri);
            arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    private static int lookingForInner(String expr, int startValue) // this method looks for bracket/parenthesis
    {
        char startingBracket = expr.charAt(startValue);
        Stack <String> count = new Stack <String> ();
        
                                    // storing the first bracket in the stack, 
                                    // if parenthesis then only parenthesis will be store in the stack, 
                                    // if bracket then only bracket will be store in the stack
        if(startingBracket == '(')
        {
            count.push("(");
        }
        else if (startingBracket == '[') 
        {
            count.push("[");
        }
        
        
        for(int i = startValue + 1; i < expr.length(); i++) // Storing them in a Stack of Strings. keeping track of the brackets/parenthesis
        {
            if(expr.charAt(i) == startingBracket &&  startingBracket == '(') 
            {
                count.push("(");
            }
            else if (expr.charAt(i) == startingBracket && startingBracket == '[') 
            {
                count.push("[");
            }
            if(startingBracket =='[' && expr.charAt(i)==']')
            {
                count.pop();
            }
            else if (startingBracket == '(' && expr.charAt(i) == ')')
            {
                count.pop();
            }
            if(count.isEmpty())
            {
                return i;  // the ending of a parethesis / bracket
            }
        }
        
        return -1;
    }
    
    private static String priorCalculate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays)
    {
        StringTokenizer subExpr = new StringTokenizer(expr, delims); // making sub string by using the delims as the separations
        
        Stack<Float> values = new Stack<Float>();  // storing values
        Stack<String> operations = new Stack<String>();  // storing operations       
        
        Stack<Float> reverseValues = new Stack<Float>();
        Stack<String> reverseOperations = new Stack<String>();
        
        String mathSigns = "";
        int mathSignIndex = 0;
        
        for(int i = 0; i < expr.length(); i++) // storing the operations in a string
        {
            if(expr.charAt(i) == '*' || expr.charAt(i) == '+' || expr.charAt(i) == '-' || expr.charAt(i) == '/')            
            {
                mathSigns = mathSigns + expr.charAt(i);
            }              
        }
        
        boolean finished = false;
        String miniString = "";
        String temp = "";
        float negatveNumber;
        float num;
        int index;
        
        while(subExpr.countTokens()!= 0)
        {
            miniString = subExpr.nextToken();
            
            
            if(miniString.charAt(0) == ('?'))  // < means that it is a negative number in the front
            {
                temp = miniString.substring(1, miniString.length());
                negatveNumber = Float.valueOf(temp);
                negatveNumber = negatveNumber * -1;
                values.push(negatveNumber);
            }
            
            if(Character.isDigit(miniString.charAt(0))) // storing actual numbers in the values stack
            {
                num = Float.valueOf(miniString);
                values.push(num);
            }
            else  // if variable or array name, get the value and store it in the values stack
            {
                if(vars.contains(new Variable(miniString)))
                {
                    index = vars.indexOf(new Variable(miniString));
                    num = (float)(vars.get(index)).value;
                    values.push(num);
                }
                
                if(arrays.contains(new Array(miniString)))
                {
                    index = arrays.indexOf(new Array(miniString));
                    int[] quick = (arrays.get(index)).values;
                    int ind = (int)Double.parseDouble(subExpr.nextToken());
                    values.push((float)quick[ind]);
                }
            }
            if(mathSignIndex < mathSigns.length()) // comparing the current operation done to total amount of operations that need to be completed
            {
                if(operations.isEmpty() == true && finished == false)  // no operation to be used, need to store values/operation in stack
                {
                    char sign = mathSigns.charAt(mathSignIndex);
                    temp = String.valueOf(sign);
                    operations.push(temp);
                    
                    if(mathSignIndex == mathSigns.length()-1)
                    {
                        finished = true;
                    }            
                }
                else // if the stack contains operations, then do the calculation for multiplication and division
                {
                    String useOperation = operations.peek();
                    
                    if(useOperation.equals("*"))
                    {
                        operations.pop();
                        Float b = values.pop();
                        Float a = values.pop();
                        
                       
                        values.push(a*b);
                        
                        if(finished == false)
                        {
                            char sign = mathSigns.charAt(mathSignIndex);
                            temp = String.valueOf(sign);
                            
                            operations.push(temp);
                            
                            if(mathSignIndex == mathSigns.length() - 1)
                            {
                                finished = true;
                            }                        
                        }
                        
                    }
                    else if(useOperation.equals("/"))
                    {
                        operations.pop();
                        Float b = values.pop();
                        Float a = values.pop();
                        
                       
                        values.push(a/b);
                        
                        if(finished == false)
                        {
                            char sign = mathSigns.charAt(mathSignIndex);
                            temp = String.valueOf(sign);
                            
                            operations.push(temp);
                            
                            if(mathSignIndex == mathSigns.length()-1)
                            {
                                finished = true;
                            }
                        }
                        
                    }
                    else if(finished == false)
                    {
                        char sign = mathSigns.charAt(mathSignIndex);
                        temp = String.valueOf(sign);
                        
                        operations.push(temp);
                        if(mathSignIndex == mathSigns.length()-1)
                        {
                            finished = true;
                        }
                    }
                }
            }
            
            mathSignIndex = mathSignIndex + 1;
        }
        
        if(operations.isEmpty() == false)  // doing the calculation for the last number if multiplication or division
        {
            if(operations.peek().equals("*"))
            {
                String popper = operations.pop();
                float b = values.pop();
                float a = values.pop();
                if(popper.equals("*"))
                {
                    values.push(a*b);
                }
                
            }
            else if(operations.peek().equals("/"))
            {
                String popper = operations.pop();
                float b = values.pop();
                float a = values.pop();
                if(popper.equals("/"))
                {
                    values.push(a/b);
                }
            }
        }

        while(values.isEmpty() == false)  // the values needs to be flip
        {
            reverseValues.push(values.pop());
        }
        
        while(operations.isEmpty() == false)  // the operations need to be flip
        {
            reverseOperations.push(operations.pop());
        }
        
        
        while(reverseOperations.isEmpty() == false)
        {
            String signal = reverseOperations.pop();
            if(signal.equals("+"))
            {
                Float a = reverseValues.pop();
                Float b = reverseValues.pop();
//                float addition = a + b;
                
                reverseValues.push(a+b);
            }
            
            if(signal.equals("-"))
            {
                Float a = reverseValues.pop();
                Float b = reverseValues.pop();
                
//                float subtraction = a -b;
                reverseValues.push(a-b);
            }
        }
        
        Float ans = reverseValues.pop();
        
        if(ans < 0)
        {
            return "?" + (0 - ans);
        }
            
        return ans.toString();
    }

    private static String startingBracket(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays)
    {
        String temp = "";
        
        int i = 0;
        int endingBracketIndex;
        String innerString;
        
        while(i < expr.length())
        {
            if(expr.charAt(i) == '(')
            {
                endingBracketIndex = lookingForInner(expr,i);
                innerString = expr.substring(i + 1, endingBracketIndex);
                temp = temp + startingBracket(innerString,vars,arrays);
                i = endingBracketIndex + 1;
            }
            else if(expr.charAt(i) =='[')
            {
                endingBracketIndex = lookingForInner(expr, i);
                innerString = expr.substring(i+1, endingBracketIndex);
                temp = temp + "[" + startingBracket(innerString, vars, arrays) + "]";
                i = endingBracketIndex + 1;
            }
            else
            {
                temp = temp + expr.charAt(i);
                i = i + 1;
            }
        }
        
        String result = priorCalculate(temp, vars, arrays);

        return result;
    }
    
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        /** COMPLETE THIS METHOD **/
        // following line just a placeholder for compilation

        String finalAnswer = startingBracket(expr,vars,arrays);
        if(finalAnswer.charAt(0) == '?')
        {
            return 0 - Float.valueOf(finalAnswer.substring(1, finalAnswer.length()));
        }
        return Float.valueOf(finalAnswer);
    }
}