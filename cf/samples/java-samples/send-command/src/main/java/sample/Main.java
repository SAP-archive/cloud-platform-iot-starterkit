package sample;

import commons.SampleException;
import commons.utils.Console;

public class Main {

	public static void main(String[] args) {
		try {
			new SampleApp().run();
		}
		catch (SampleException e) {
			Console.printError(String.format("Unable to run the sample - %1$s", e.getMessage()));
			System.exit(1);
		}
	}

}