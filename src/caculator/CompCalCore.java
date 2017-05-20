package caculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

/**
 * Calculator Computing Core: Complex Version
 * @version 1.1
 * @author lanti
 *
 */

import java.util.Stack;

public class CompCalCore {
		/* DEBUG */
//		String str = "5(3+i)(2-i)";
//		String processed = preProcess(str);
//		System.out.println(str);
//		System.out.println(processed);
//		System.out.println(evaluate(processed));

	private String str;
	private String proc;
	private Stack<Complex> OPND = new Stack<Complex>(); 				// Diclare operand stack
	private Stack<Character> OPTR = new Stack<Character>(); 			// Diclare operator stack
	private char[] optr_set = {'+', '-', '*', '/', '(', ')', '='};
	private char[] legal_set = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '(', ')', 'i', '.', 'p'};
	private char[] arithmetic_set = {'+', '-', '*', '/'};
	private static Complex pre_answer = new Complex(0, 0);
	
	private int[][] prec = {
			{ 1,  1, -1, -1, -1,  1,  1},
			{ 1,  1, -1, -1, -1,  1,  1},
			{ 1,  1,  1,  1, -1,  1,  1},
			{ 1,  1,  1,  1, -1,  1,  1},
			{-1, -1, -1, -1, -1,  0, -2},
			{ 1,  1,  1,  1, -2,  1,  1},
			{-1, -1, -1, -1, -1, -2,  0}
	};
	
	public int inOP(char optr){       						// +	-	*	/	(	)	=
		for (int i = 0; i < optr_set.length; i++)			// 0    1   2   3   4   5   6
			if (optr == optr_set[i])
				return i;
		return -1;
	}
	
	public int precede(char loptr, char roptr){
		return prec[inOP(loptr)][inOP(roptr)];
	}
	
	private boolean isNumChar(char c){
		return Character.isDigit(c) || c == '.' || c == 'i' || c == 'p';
	}
	
	private boolean isAriChar(char c){
		for (int i = 0; i < arithmetic_set.length; i++)
			if (c == arithmetic_set[i]) return true;
		return false;
	}
	
	private boolean isLegalChar(char c){
		for (int i = 0; i < legal_set.length; i++)
			if (c == legal_set[i] )
				return true;
		return false;
	}
	
	public Complex operate(Complex num1, char optr, Complex num2){
		Complex result;
		switch (inOP(optr)){
			case 0: result = num1.add(num2); break;
			case 1: result = num1.sub(num2); break;
			case 2: result = num1.mul(num2); break;
			case 3: result = num1.div(num2); break;
			default:
				result = null;
		}
		return result;
	}
	
	
	
	/* 
	 * Handle the input expression
	 */
	
	public Complex evaluate(String expression){
		OPTR.push('=');
		char c;
		char optr;
		Complex lnum;
		Complex rnum;
		int i = 0;
		c = expression.charAt(i);
		while (c != '=' || OPTR.peek() != '='){
			
			/* DEBUG: 
			System.out.println("OPTR");
			printStack(OPTR);
			System.out.println("OPND");
			printStack(OPND);
			System.out.println("=============================================");
			*/
			
			if (inOP(c) == -1){
				String str = "" + c;
				// Process multi-char number.
				while (true){
					c = expression.charAt(++i);
					if (isNumChar(c))
						str += c;
					else
						break;
				}
				i--;	// Undo a read for OPTR
				OPND.push(readComplex(str));
				c = expression.charAt(++i);	// Read next OPTR
			}
			else{
				switch (precede(OPTR.peek(), c)){
					case -1: OPTR.push(c); c = expression.charAt(++i); break;
					case  0: OPTR.pop(); c = expression.charAt(++i); break;
					case  1: optr = OPTR.pop(); rnum = OPND.pop(); lnum = OPND.pop();
							 OPND.push(operate(lnum, optr, rnum));
							 break;
				}
			}
			
		}
		return OPND.peek();
	}
	
	private void ans(){
		if (!OPND.isEmpty())
			this.pre_answer = OPND.peek();
	}
	
	
	private String preProcess(String src){
		ans();
		if (src.equals(""))
			return "0=";
		System.out.println(pre_answer);
		src = src.replaceAll("p", pre_answer.toString());
		
		// -...	=>	0-...
		if (src.charAt(0) == '-') 
			src = '0' + src;
		if (src.charAt(0) == 'i')
			src = '1' + src;
		// (- 	=> 	(0-
		src = src.replace("(-", "(0-");
		
		String result = "";
		Pattern ptn;
		Matcher mt;
		String[] raw;
		String tmp = "";
		
		// Add "1" before "i"
		ptn = Pattern.compile("([\\+\\-\\*/()])i");
		mt = ptn.matcher(src);
		raw = ptn.split(src);
		for (int i = 0; i < raw.length; i++){
			result += raw[i];
			if (mt.find()) result += mt.group().charAt(0)+"1i";
		}
		src = result;
		result = "";

		// Add "*" between ")" and "[\d i]"
		ptn = Pattern.compile("(\\))[\\di]");
		mt = ptn.matcher(src);
		raw = ptn.split(src);
		for (int i = 0; i < raw.length; i++){
			result += raw[i];
			if (mt.find()){
				tmp = mt.group();
				result += ")*"+tmp.charAt(tmp.length()-1);
			}
		}
		src = result;
		result = "";

		// Add "*" between "[) i \d]" and "("
		ptn = Pattern.compile("([\\di\\)])\\(");
		mt = ptn.matcher(src);
		raw = ptn.split(src);
		for (int i = 0; i < raw.length; i++){
			result += raw[i];
			if (mt.find())
				result += mt.group().charAt(0)+"*(";
		}
		src = result + "=";
		result = "";
		
		return src;
	}
	
	
	/**
	 * To transfer a legal string to Complex
	 * @param src
	 * @return 
	 */
	
	private boolean checkExpression(String str){
		int bracket_flag = 0;
		boolean dot_flag = false;
		for (int i = 0; i < str.length(); i++){
			if (!isLegalChar(str.charAt(i))){
				System.out.println("There are some invaid characters in your expression!");
				return false;
			}
			if (isNumChar(str.charAt(i))){
				// Operand
				if (str.charAt(i) == '.'){
					if (dot_flag){
						System.out.println("Dot error!");
						return false;
					}
					else
						dot_flag = true;
					
					if (i > str.length()-2 || !Character.isDigit(str.charAt(i + 1))){
						System.out.println("Dot error");
						return false;
					}
				}
				
				if (str.charAt(i) == 'i'){
					if(i < str.length() - 1 && str.charAt(i + 1) == 'i'){
						System.out.println("Missing operator between double 'i'.");
						return false;
					}
				}
				
			}
			else{
				// Operator
				
				// Bracket
				if (str.charAt(i) == '('){
					if (i > str.length()-2 || str.charAt(i+1)==')'){
						System.out.println("Missing operand between parentheses.");
						return false;
					}
					bracket_flag++;
				}
				else if (str.charAt(i) == ')')bracket_flag--;
				if (bracket_flag < 0){
					System.out.println("Parentheses not match!");
					return false;
				}
				dot_flag = false;
				
				// Arithmetic
				if (isAriChar(str.charAt(i)))
					if (i > str.length()-2 || isAriChar(str.charAt(i + 1))){
						System.out.println("There are some unsuitable inputs in your expression!");
					return false;
				}
			}
		}
		return true;
	}
	
	private Complex readComplex(String src){
		if (src.charAt(src.length() - 1) == 'i')
			return new Complex(0, Double.parseDouble(src.substring(0, src.length() - 1)));
		else
			return new Complex(Double.parseDouble(src), 0);
	}
	
	/**
	 * DEBUG
	 * @param s
	 */
	private void printStack(Stack s){
		for (int i =0; i< s.size(); i++)
			System.out.println(i+"\t"+s.elementAt(i));
	}
	
	public static void main(String[] args) {
		CompCalCore ccc = new CompCalCore();
		String sample1 = "3*(7-2)";
		sample1 = ccc.preProcess(sample1);
		System.out.println("Sample1: "+sample1);
		System.out.println(ccc.evaluate(sample1));
		
		String sample2 = "p*2";
		sample2 = ccc.preProcess(sample2);
		System.out.println("Sample2: "+sample2);
		System.out.println(ccc.evaluate(sample2));
		
	}

}