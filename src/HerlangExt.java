import java.io.*;
import java.util.ArrayList;

public class HerlangExt {

	public enum Action {
		SET,           
		CHANGE_ADDR,  
		ADD,           
		SUB,         
		MUL,           
		DIV,          
		COPY,          
		MOVE,        
		PRINT,       
		PRINT_LN,      
		PRINT_TYPE,   
		
		// HerlangExt
		JUMP,         
		JEZ,          
		JNZ,          
		JLZ,         
		JGZ,         
		CMP,         
		LABEL       
	}
	
	private static boolean didPrint = false;
	private static boolean logActions = false;
	private static Action lastAction = null;
	private static PrintWriter actionWriter = null;

	public static void main(String[] args) {
		try {
			if(args.length <= 0) {
				exitErr("USAGE: java -jar HerlangPlus.jar <file>.her");
			} else {
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					if(args[i].startsWith("--")) {
						if(args[i].equalsIgnoreCase("--log-actions")) {
							logActions = true;
						}
						continue;
					}
					sb.append(args[i]).append(" ");
				}
				String fileName = sb.toString().trim();
				
				File f = new File(fileName);
				if(!f.exists()) {
					exitErr("\"" + fileName + "\" does not exist!");
				} else if(f.isDirectory()) {
					exitErr("\"" + fileName + "\" is a directory, only files are permitted!");
				} else if(!fileName.toLowerCase().endsWith(".her")) {
					exitErr("\"" + fileName + "\" is not a valid Herlang file!");
				}
				
				if(logActions) {
					actionWriter = new PrintWriter(new FileWriter(f.getAbsolutePath() + ".actions.log"));
				}
				
				// 执行程序
				executeProgram(f);
			}
		} catch(Exception e) {
			System.err.println("ERROR: " + e.getMessage());
			System.err.print("Caused by ");
			e.printStackTrace(System.err);			
			System.exit(-1);
		} finally {
			try {
				if(logActions && actionWriter != null) {
					actionWriter.flush();
					actionWriter.close();
				}
			} catch(Exception e) {
				; // shuddup ;P
			}
		}
 	}
 	
 	private static void executeProgram(File f) throws Exception {
 		int[] memory = new int[4096];
		int[] addressHolder = {0};
		boolean[] isAsciiHolder = {false}; 
		int programCounter = 0; 
		
		Action lastAction = null;
		String[] lines = readLines(f);
		
		while(programCounter < lines.length) {
			String ln = lines[programCounter];
			String charactersToRetain = " 啊";
			StringBuilder filter = new StringBuilder();
			for (int j = 0; j < ln.length(); j++) {
				char c = ln.charAt(j);
				if (charactersToRetain.contains(String.valueOf(c))) {
					filter.append(c);
				}
			}
			ln = filter.toString();
			
			if(ln.contains(" ") && ln.contains("啊")) {
				exitErr("SYNTAX", programCounter+1, 0, "Line contains both spaces and tabs.");
				return;
			}
			
			if(ln.startsWith(" ") || ln.isEmpty()) { // value (spaces)
				if(lastAction == null) {
					exitErr("SYNTAX", programCounter+1, 0, "Value given with no action specified.");
					return;
				}
				int spaceCount = 0;
				for(int j = 0; j < ln.length(); j++) {
					if(ln.charAt(j) != ' ') {
						exitErr("SYNTAX", programCounter+1, j+1, "Unexpected character: " + ln.charAt(j));
						return;
					} else {
						spaceCount++;
					}
				}
				
				int nextPC = executeActionWithParam(lastAction, spaceCount, memory, addressHolder, isAsciiHolder, programCounter);
				
				lastAction = null;
				programCounter = nextPC;
			} else if(ln.startsWith("啊")) { // action (tabs)
				int tabCount = 0;
				for(int j = 0; j < ln.length(); j++) {
					if(ln.charAt(j) != '啊') {
						exitErr("SYNTAX", programCounter+1, j+1, "Unexpected character: " + ln.charAt(j));
						return;
					} else {
						tabCount++;
					}
				}
				int actionIndex = tabCount - 1;
				if(actionIndex < 0 || actionIndex >= Action.values().length) {
					exitErr("SYNTAX", programCounter+1, 0, "Invalid tab count: " + tabCount);
					return;
				} else {
					Action action = Action.values()[actionIndex];
					if(action == Action.PRINT) {
						if(isAsciiHolder[0] && memory[addressHolder[0]] == 32) { // special case for spaces
							System.out.print(" ");
						} else {
							System.out.print(isAsciiHolder[0] ? Character.toString((char)memory[addressHolder[0]]) : memory[addressHolder[0]]);
						}
						logAction(programCounter+1, action, addressHolder[0], memory[addressHolder[0]], tabCount);
						didPrint = true;
						programCounter++;
					} else if(action == Action.PRINT_LN) {
						System.out.println();
						logAction(programCounter+1, action, addressHolder[0], memory[addressHolder[0]], tabCount);
						didPrint = true;
						programCounter++;
					} else {
						lastAction = action;
						programCounter++;
					}
				}
			} else {
				programCounter++; 
			}
		}
		
		if(didPrint) {
			System.out.println();
		}
 	}
 	
 	private static int executeActionWithParam(Action action, int param, int[] memory, int[] addressHolder, boolean[] isAsciiHolder, int currentPC) throws Exception {
 		int address = addressHolder[0];
 		switch(action) {
			case SET:
				memory[address] = param;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case CHANGE_ADDR:
				if(param < 0 || param >= memory.length) {
					exitErr("RUNTIME", currentPC+1, 0, "Invalid memory address: " + param);
					return currentPC + 1;
				}
				addressHolder[0] = param; 
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			default:
		}
 		switch(action) {
			case SET:
				memory[address] = param;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case CHANGE_ADDR:
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case ADD:
				memory[address] += param;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case SUB:
				memory[address] -= param;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case MUL:
				memory[address] *= param;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case DIV:
				if(param == 0) {
					exitErr("RUNTIME", currentPC+1, 0, "Division by zero!");
					return currentPC + 1;
				}
				memory[address] /= param;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case COPY:
				if(param < 0 || param >= memory.length) {
					exitErr("RUNTIME", currentPC+1, 0, "Invalid memory address: " + param);
					return currentPC + 1;
				}
				memory[param] = memory[address];
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case MOVE:
				if(param < 0 || param >= memory.length) {
					exitErr("RUNTIME", currentPC+1, 0, "Invalid memory address: " + param);
					return currentPC + 1;
				}
				memory[param] = memory[address];
				memory[address] = 0;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case PRINT_TYPE:
				isAsciiHolder[0] = param == 1; 
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
				
			// HerlangExt Addition!
			case JUMP:
				logAction(currentPC+1, action, address, memory[address], param);
				return Math.max(0, param - 1);
			case JEZ:
				logAction(currentPC+1, action, address, memory[address], param);
				if(memory[address] == 0) {
					return Math.max(0, param - 1);
				}
				return currentPC + 1;
			case JNZ:
				logAction(currentPC+1, action, address, memory[address], param);
				if(memory[address] != 0) {
					return Math.max(0, param - 1);
				}
				return currentPC + 1;
			case JLZ:
				logAction(currentPC+1, action, address, memory[address], param);
				if(memory[address] < 0) {
					return Math.max(0, param - 1);
				}
				return currentPC + 1;
			case JGZ:
				logAction(currentPC+1, action, address, memory[address], param);
				if(memory[address] > 0) {
					return Math.max(0, param - 1);
				}
				return currentPC + 1;
			case CMP:
				if(param < 0 || param >= memory.length) {
					exitErr("RUNTIME", currentPC+1, 0, "Invalid memory address: " + param);
					return currentPC + 1;
				}
				int result = Integer.compare(memory[address], memory[param]);
				memory[address] = result;
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			case LABEL:
				logAction(currentPC+1, action, address, memory[address], param);
				return currentPC + 1;
			default:
				exitErr("RUNTIME", currentPC+1, 0, "Unidentified action: " + action);
				return currentPC + 1;
		}
 	}
 	
 	private static void logAction(int lineNum, Action action, int address, int addressVal, int value) {
 		if(!logActions || action == null) {
 			return;
 		}
 		actionWriter.print("Line " + lineNum + ": ");
 		switch(action) {
 			case SET:
 				actionWriter.println("SET " + address + " TO " + value);
 				break;
 			case CHANGE_ADDR:
 				actionWriter.println("SET ADDRESS TO " + value);
 				break;
 			case ADD:
 				actionWriter.println("ADDED " + value + " TO " + address + " (" + addressVal + ")");
 				break;
 			case SUB:
 				actionWriter.println("SUBTRACTED " + value + " FROM " + address + " (" + addressVal + ")");
 				break;
 			case MUL:
 				actionWriter.println("MULTIPLIED ADDRESS " + address + " BY " + value + " (" + addressVal + ")");
 				break;
 			case DIV:
 				actionWriter.println("DIVIDED ADDRESS " + address + " BY " + value + " (" + addressVal + ")");
 				break;
 			case COPY:
 				actionWriter.println("COPIED ADDRESS " + address + " TO ADDRESS " + value);
 				break;
 			case MOVE:
 				actionWriter.println("MOVED ADDRESS " + address + " TO ADDRESS " + value);
 				break;
 			case PRINT:
 				actionWriter.println("PRINTED VALUE OF " + address + " (" + addressVal + ")");
 				break;
 			case PRINT_LN:
 				actionWriter.println("PRINTED LINE");
 				break;
 			case PRINT_TYPE:
 				actionWriter.println("CHANGED PRINT TYPE TO " + (value != 1 ? "decimal" : "ascii"));
 				break;
 			case JUMP:
 				actionWriter.println("JUMPED TO LINE " + value);
 				break;
 			case JEZ:
 				actionWriter.println("JUMP IF ZERO: " + address + "(" + addressVal + ") TO LINE " + value);
 				break;
 			case JNZ:
 				actionWriter.println("JUMP IF NOT ZERO: " + address + "(" + addressVal + ") TO LINE " + value);
 				break;
 			case JLZ:
 				actionWriter.println("JUMP IF LESS THAN ZERO: " + address + "(" + addressVal + ") TO LINE " + value);
 				break;
 			case JGZ:
 				actionWriter.println("JUMP IF GREATER THAN ZERO: " + address + "(" + addressVal + ") TO LINE " + value);
 				break;
 			case CMP:
 				actionWriter.println("COMPARED ADDRESS " + address + " WITH ADDRESS " + value + " RESULT: " + addressVal);
 				break;
 			case LABEL:
 				actionWriter.println("LABEL " + value);
 				break;
 			default:
 				actionWriter.println("SET ACTION TO " + action);
 				break;
 		}
 	}

	private static String[] readLines(File file) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		ArrayList<String> lines = new ArrayList<String>();
		for(String ln = reader.readLine(); ln != null; ln = reader.readLine()) {
			lines.add(ln);
		}
		reader.close();
		return (String[])lines.toArray(new String[0]);
	}

	private static void exitErr(String type, int lineNum, int colNum, String msg) {
		if(didPrint) {
			System.out.println();
		}
		System.err.println(type + " ERROR at line " + lineNum + " col " + colNum + ": " + msg);
		System.exit(-1);
	}

	private static void exitErr(String msg) {
		System.err.println("ERROR: " + msg);
		System.exit(-1);
	}
}