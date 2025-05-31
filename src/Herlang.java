import java.io.*;
import java.util.ArrayList;

public class Herlang {

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
		PRINT_TYPE
	}
	
	private static boolean didPrint = false;
	private static boolean logActions = false;
	private static Action lastAction = null;
	private static PrintWriter actionWriter = null;

	public static void main(String[] args) {
		try {
			if(args.length <= 0) {
				exitErr("USAGE: java -jar Herlang.jar <file>.ws");
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
				} else if(!fileName.toLowerCase().endsWith(".ws")) {
					exitErr("\"" + fileName + "\" is not a valid Herlang file!");
				}
				
				if(logActions) {
					actionWriter = new PrintWriter(new File(f.getAbsolutePath() + ".actions.log"));
				}
				
				int[] memory = new int[4096];
				int address  = 0;
				
				Action lastAction = null;
				boolean isAscii = false;
				
				String[] lines = readLines(f);
				for(int i = 0; i < lines.length; i++) {
					String ln = lines[i];
					if(ln.contains(" ") && ln.contains("	")) {
						exitErr("SYNTAX", i+1, 0, "Line contains both spaces and tabs.");
						return;
					}
					if(ln.startsWith(" ") || ln.isEmpty()) { // value (spaces)
						if(lastAction == null) {
							exitErr("SYNTAX", i+1, 0, "Value given with no action specified.");
						}
						int spaceCount = 0;
						for(int j = 0; j < ln.length(); j++) {
							if(ln.charAt(j) != ' ') {
								exitErr("SYNTAX", i+1, j+1, "Unexpected character: " + ln.charAt(j));
								return;
							} else {
								spaceCount++;
							}
						}
						switch(lastAction) {
						case SET:
							memory[address] = spaceCount;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case CHANGE_ADDR:
							if(spaceCount < 0 || spaceCount >= memory.length) {
								exitErr("RUNTIME", i+1, 0, "Invalid memory address: " + spaceCount);
								return;
							}
							address = spaceCount;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case ADD:
							memory[address] += spaceCount;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case SUB:
							memory[address] -= spaceCount;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case MUL:
							memory[address] *= spaceCount;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case DIV:
							memory[address] /= spaceCount;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case COPY:
							if(spaceCount < 0 || spaceCount >= memory.length) {
								exitErr("RUNTIME", i+1, 0, "Invalid memory address: " + spaceCount);
								return;
							}
							memory[spaceCount] = memory[address];
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case MOVE:
							if(spaceCount < 0 || spaceCount >= memory.length) {
								exitErr("RUNTIME", i+1, 0, "Invalid memory address: " + spaceCount);
								return;
							}
							memory[spaceCount] = memory[address];
							memory[address] = 0;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						case PRINT_TYPE:
							isAscii = spaceCount == 1;
							logAction(i+1, lastAction, address, memory[address], spaceCount);
							break;
						default:
							exitErr("RUNTIME", i+1, 0, "Unidentified action: " + lastAction);
							break;
						}
						lastAction = null;
					} else if(ln.startsWith("	")) { // action (tabs)
						int tabCount = 0;
						for(int j = 0; j < ln.length(); j++) {
							if(ln.charAt(j) != '	') {
								exitErr("SYNTAX", i+1, j+1, "Unexpected character: " + ln.charAt(j));
								return;
							} else {
								tabCount++;
							}
						}
						int actionIndex = tabCount - 1;
						if(actionIndex < 0 || actionIndex >= Action.values().length) {
							exitErr("SYNTAX", i+1, 0, "Invalid tab count: " + tabCount);
							return;
						} else {
							lastAction = Action.values()[actionIndex];
							if(lastAction == Action.PRINT) {
								if(isAscii && memory[address] == 32) { // special case for spaces
									System.out.print(" ");
								} else {
									System.out.print(isAscii ? Character.toString((char)memory[address]) : memory[address]);
								}
								logAction(i+1, lastAction, address, memory[address], tabCount);
								lastAction = null; didPrint = true;
							} else if(lastAction == Action.PRINT_LN) {
								System.out.println();
								logAction(i+1, lastAction, address, memory[address], tabCount);
								lastAction = null; didPrint = true;
							} 
						}
					}
				}
				
				if(didPrint) {
					System.out.println(); // clean the console output
				}
			}
		} catch(Exception e) {
			System.err.println("ERROR: " + e.getMessage());
			System.err.print("Caused by ");
			e.printStackTrace(System.err);			
			System.exit(-1);
		} finally {
			try {
				if(logActions) {
					actionWriter.close();
				}
			} catch(Exception e) {
				; // shuddup ;P
			}
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