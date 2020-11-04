package main;

import javafx.application.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Main {
	public static int bandLength, index = 0, times = 1, timesSet = 1, pipeIndex = 0, quick = 0, x = 0;
	public static int[] band;
	public static Scanner cli = new Scanner(System.in);
	public static List<String> pipeline = new ArrayList<>();
	public static Stack<Integer> stack = new Stack<>();

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.print("Parameters unspecified! Enter parameters: ");
			args = cli.nextLine().split(" ");
		}
		if (args.length < 1)
			return;
		try {
			Scanner sc = new Scanner(new File(args[0]));
			bandLength = Integer.parseInt(sc.nextLine());
			if (bandLength < 1) {
				System.out.println("Band length needs to be atleast 1!");
				return;
			}
			band = new int[bandLength];
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.trim().equals(""))
					pipeline.add(line);
			}
			for (pipeIndex = 0; pipeIndex < pipeline.size(); ++pipeIndex) {
				execute(pipeline.get(pipeIndex));
				if (times != timesSet) {
					times = timesSet;
					timesSet = 1;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void execute(String s) {
		String[] words = s.split(" ", 2);
		String[] param = new String[0];
		if (words.length > 1)
			param = words[1].split(" ");
		for (int j = 0; j < times; ++j)
			switch (words[0]) {
				case "debug":
					if (index == 0)
						System.out.print("[");
					else
						System.out.print("|");
					for (int k = 0; k < bandLength; ++k) {
						System.out.print((int) band[k]);
						if (k == index)
							System.out.print("]");
						else if (k == index - 1)
							System.out.print("[");
						else
							System.out.print(" ");
					}
					System.out.println(" Q:" + quick + " X:" + x);
					break;
				case "print":
					System.out.print(words[1]);
					break;
				case "println":
					System.out.println(words[1]);
					break;
				case "int.i":
					band[index] = Integer.parseInt(cli.nextLine());
					break;
				case "int.c":
					band[index] = Integer.parseInt(param[0]);
					break;
				case "int.r":
					band[index] = new Random().nextInt(band[index]);
					break;
				case "int.p":
					System.out.print(band[index]);
					break;
				case "int.g":
					band[index] = index;
					break;
				case "char.p":
					System.out.print((char) band[index]);
					break;
				case "string.i": {
					char[] chars = cli.nextLine().toCharArray();
					int k = 0;
					clear();
					for (; k < chars.length; ++k)
						band[increment(index, k)] = chars[k];
					break;
				}
				case "string.c": {
					char[] chars = words[1].toCharArray();
					int k = 0;
					clear();
					for (; k < words[1].length(); ++k)
						band[increment(index, k)] = chars[k];
					break;
				}
				case "string.p": {
					char[] chars = new char[bandLength];
					for (int i = 0; i < bandLength; i++)
						chars[i] = (char)band[increment(index, i)];
					System.out.print(String.valueOf(chars));
					break;
				}
				case "ln":
					System.out.println();
					break;
				case "if":
					if (param[0].equals(">") && band[index] <= band[x]) {
						jumpAfter("else");
						return;
					}
					if (param[0].equals("<") && band[index] >= band[x]) {
						jumpAfter("else");
						return;
					}
					if (param[0].equals("=") && band[index] != band[x]) {
						jumpAfter("else");
						return;
					}
					if (param[0].equals("!") && band[index] == band[x]) {
						jumpAfter("else");
						return;
					}
					if (param[0].equals("?") && index != x) {
						jumpAfter("else");
						return;
					}
					break;
				case "else":
					jumpAfter("end");
					return;
				case "set":
					band[index] = band[Integer.parseInt(param[0])];
					break;
				case ">":
					index = increment(index, 1);
					break;
				case "<":
					index = increment(index, -1);
					break;
				case "?":
					if (words.length > 1) {
						index = Integer.parseInt(words[1]) % bandLength;
					} else
						index = quick;
					break;
				case "@":
					quick = index;
					break;
				case "q":
					int aux = index;
					index = quick;
					quick = aux;
					break;
				case "x":
					x = index;
					break;
				case "+": {
					if (words.length > 1) {
						band[index] += band[Integer.parseInt(words[1])];
					} else
						++band[index];
					break;
				}
				case "-": {
					if (words.length > 1) {
						band[index] -= band[Integer.parseInt(words[1])];
					} else
						--band[index];
					break;
				}
				case "*": {
					if (words.length > 1) {
						band[index] *= band[Integer.parseInt(words[1])];
					} else
						band[index] *= band[index];
					break;
				}
				case "/": {
					band[index] /= band[Integer.parseInt(words[1])];
					break;
				}
				case "%": {
					band[index] %= band[Integer.parseInt(words[1])];
					break;
				}
				case "&": {
					if (band[index] != 0 && band[x] != 0)
						band[index] = 1;
					else
						band[index] = 0;
					break;
				}
				case "|": {
					if (band[index] != 0 || band[x] != 0)
						band[index] = 1;
					else
						band[index] = 0;
					break;
				}
				case "^": {
					if ((band[index] != 0) != (band[x] != 0))
						band[index] = 1;
					else
						band[index] = 0;
					break;
				}
				case "!": {
					if (band[index] != 0)
						band[index] = 0;
					else
						band[index] = 1;
					break;
				}
				case "call":
					stack.push(pipeIndex);
				case "goto":
					jumpTo(words[1]);
					return;
				case "return":
					pipeIndex = stack.pop();
					break;
				case "exit":
					if (words.length > 1)
						System.exit(Integer.parseInt(words[1]));
					else
						System.exit(0);
					break;
				case "clear":
					clear();
					break;
				case "t":
					times = 1;
					if (words.length > 1)
						timesSet = band[Integer.parseInt(words[1])];
					else
						timesSet = band[index];
					break;
				case "exe":
					System.out.print("Execute: ");
					execute(cli.nextLine());
					break;
			}
	}

	public static void jumpAfter(String destination) {
		for (; pipeIndex < pipeline.size(); ++pipeIndex) {
			if (pipeline.get(pipeIndex).equals(destination)) {
				return;
			}
		}
		System.out.println("Couldn't find instruction \"" + destination + "\"!");
		System.exit(-1);
	}

	public static void jumpTo(String destination) {
		pipeIndex = 0;
		jumpAfter(destination);
		--pipeIndex;
	}

	public static void clear() {
		band = new int[bandLength];
	}

	public static int increment(int start, int inc) {
		start += inc;
		if (start < 0)
			start += bandLength;
		else if (start >= bandLength)
			start -= bandLength;
		return start;
	}
}