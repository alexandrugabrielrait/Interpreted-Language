package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

public class FileExecutor {

	public int pipeIndex = 0;
	public Map<String, Integer> map = new TreeMap<>();
	public List<String> pipeline = new ArrayList<>();
	public Stack<Integer> stack = new Stack<>();

	public int executeFile(String filename) {
		try {
			Scanner sc = new Scanner(new File(filename));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.trim().equals(""))
					pipeline.add(line);
			}
			map.put("res", 0);
			for (pipeIndex = 0; pipeIndex < pipeline.size(); ++pipeIndex) {
				map.put("res", execute(pipeline.get(pipeIndex)));
			}
		} catch (Throwable e) {
			System.out.println("Exception while executing line " + pipeIndex + ": " + pipeline.get(pipeIndex));
			e.printStackTrace();
		}
		return map.get("res");
	}

	public int execute(String s) throws Exception {
		String[] words = s.split(" ", 2);
		String[] param = new String[0];
		char[] chars = words[0].toCharArray();
		if (chars[0] == '>' || chars[0] == '#' || words[0].equals("end"))
			return map.get("res");
		switch (words[0]) {
			case "println":
			case "print":
				if (words[1].startsWith("\""))
					System.out.print(words[1].substring(1, words[1].length() - 1));
				else
					System.out.print(execute(words[1]));
				if (words[0].equals("println"))
					System.out.println();
				break;
			case "if":
				if (execute(words[1]) == 0)
					jumpAfter("else");
				break;
			case "else":
				jumpAfter("end");
				break;
			case "set":
				param = words[1].split(" ", 2);
				param[0] = reformat(param[0]);
				map.put(param[0], execute(param[1]));
				return map.get(param[0]);
			case "abs":
				return Math.abs(execute(words[1]));
			case "random":
				return new Random().nextInt();
			case "not":
				if (execute(words[1]) == 0)
					return 1;
				return 0;
			case "++":
				map.put(words[1], map.get(words[1]) + 1);
				return map.get(words[1]);
			case "--":
				map.put(words[1], map.get(words[1]) - 1);
				return map.get(words[1]);
			case "==":
				param = words[1].split(" ", 2);
				return btoi(execute(param[0]) == execute(param[1]));
			case ">":
				param = words[1].split(" ", 2);
				return btoi(execute(param[0]) > execute(param[1]));
			case ">=":
				param = words[1].split(" ", 2);
				return btoi(execute(param[0]) >= execute(param[1]));
			case "<":
				param = words[1].split(" ", 2);
				return btoi(execute(param[0]) < execute(param[1]));
			case "<=":
				param = words[1].split(" ", 2);
				return btoi(execute(param[0]) <= execute(param[1]));
			case "+":
				param = words[1].split(" ", 2);
				return execute(param[0]) + execute(param[1]);
			case "-":
				param = words[1].split(" ", 2);
				return execute(param[0]) - execute(param[1]);
			case "*":
				param = words[1].split(" ", 2);
				return execute(param[0]) * execute(param[1]);
			case "/":
				param = words[1].split(" ", 2);
				return execute(param[0]) / execute(param[1]);
			case "%":
				param = words[1].split(" ", 2);
				return execute(param[0]) % execute(param[1]);
			case "call":
				stack.push(pipeIndex);
			case "goto":
				jumpTo(">" + words[1]);
				break;
			case "from":
				return new FileExecutor().executeFile(words[1]);
			case "return":
				if (words.length == 2)
					map.put("res", execute(words[1]));
				if (stack.empty())
					pipeline = new ArrayList<>();
				else
					pipeIndex = stack.pop();
				return map.get("res");
			case "exit":
				if (words.length > 1)
					System.exit(execute(words[1]));
				else
					System.exit(0);
				break;
			case "get":
				if (words[1].equals("int"))
					return Main.cli.nextInt();
				else if (words[1].equals("line"))
					return Integer.parseInt(Main.cli.nextLine());
			case "cli":
				System.out.print("Execute: ");
				return execute(Main.cli.nextLine());
			default:
				try {
					return Integer.parseInt(words[0]);
				}
				catch (NumberFormatException e) {
					words[0] = reformat(words[0]);
					if (map.containsKey(words[0]))
						return map.get(words[0]);
					else throw new Exception("Variable " + words[0] + " uninitialized.");
				}
		}
		return 0;
	}

	public void jumpAfter(String destination) throws Exception {
		for (; pipeIndex < pipeline.size(); ++pipeIndex) {
			if (pipeline.get(pipeIndex).equals(destination)) {
				return;
			}
		}
		throw new Exception("Couldn't find instruction \"" + destination + "\".");
	}

	public void jumpTo(String destination) throws Exception {
		pipeIndex = 0;
		jumpAfter(destination);
		--pipeIndex;
	}

	public String reformat(String s) throws Exception {
		int sb_open = s.indexOf('[');
		if (sb_open != -1) {
			int sb_closed = s.indexOf(']');
			if (sb_closed != -1)
				s = s.substring(0, sb_open + 1) + execute(s.substring(sb_open + 1, sb_closed)) + s.substring(sb_closed);
		}
		return s;
	}

	public int btoi(boolean b)
	{
		if (b)
			return 1;
		return 0;
	}
}
