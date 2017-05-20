package caculator;

import java.util.*;

/**
 * Calculator Computing Core: No Complex Version
 * @version 1.0
 * @author lanti
 *
 */
public class CalCore {
	
	private Stack<Double> OPND = new Stack<Double>(); 				// Diclare operand stack
	private Stack<Character> OPTR = new Stack<Character>(); 		// Diclare operator stack
	private char[] optr_set = {'+', '-', '*', '/', '(', ')', '='};
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
	
	public double operate(double num1, char optr, double num2){
		double result = 0;
		switch (inOP(optr)){
			case 0: result =  num1 + num2; break;
			case 1: result =  num1 - num2; break;
			case 2: result =  num1 * num2; break;
			case 3: result =  num1 / num2; break;
		}
		return result;
	}
	
	
	
	/* 
	 * Handle the input expression
	 */
	
	public double evaluate(String expression){
		OPTR.push('=');
		char c;
		char optr;
		double lnum;
		double rnum;
		int i = 0;
		c = expression.charAt(i);
		
		
		
		
		
		while (c!='=' || OPTR.peek()!='='){
			
			/* DEBUG:
			System.out.println("OPTR");
			printStack(OPTR);
			System.out.println("OPND");
			printStack(OPND);
			*/
			
			if (inOP(c)==-1){
				String str = "" + c;
				// Process multi-char number.
				while (true){
					c = expression.charAt(++i);
					if (Character.isDigit(c) || c == '.')
						str += c;
					else
						break;
				}
				i--;	// Undo a read for OPTR
				OPND.push(Double.parseDouble(str));
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
	
	private void printStack(Stack s){
		for (int i =0; i< s.size(); i++)
			System.out.println(i+"\t"+s.elementAt(i));
	}
	
	public static void main(String[] args) {
		CalCore c = new CalCore();
		System.out.println(c.evaluate("0-1="));
	}

}
