package main;

import javafx.application.Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

public class Main {

	public static Scanner cli = new Scanner(System.in);

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.print("Parameters unspecified! Enter parameters: ");
			args = cli.nextLine().split(" ");
		}
		if (args.length < 1)
			return;
		System.exit(new FileExecutor().executeFile(args[0]));
	}
}