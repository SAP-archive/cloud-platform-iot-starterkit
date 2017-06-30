package commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Console {

	private Scanner scanner;

	private static class ConsoleHolder {
		public static final Console INSTANCE = new Console();
	}

	public static Console getInstance() {
		return ConsoleHolder.INSTANCE;
	}

	private Console() {
		scanner = new Scanner(System.in);
	}

	public void close() {
		if (scanner != null) {
			scanner.close();
		}
	}

	public String awaitNextLine(String value, String format, Object... args) {
		if (value == null || value.trim().isEmpty()) {
			do {
				value = nextLine(format, args);
			}
			while (value.trim().isEmpty());
		}
		return value;
	}

	public String nextLine(String format, Object... args) {
		System.out.print(String.format(format, args));
		return scanner.nextLine();
	}

	public String nextPassword(String format, Object... args) {
		java.io.Console console = System.console();
		if (console != null) {
			return new String(console.readPassword(format, args));
		}
		System.out.print(String.format(format, args));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			return br.readLine();
		}
		catch (IOException e) {
			return "";
		}
		finally {
			FileUtil.closeStream(br);
		}
	}

}
