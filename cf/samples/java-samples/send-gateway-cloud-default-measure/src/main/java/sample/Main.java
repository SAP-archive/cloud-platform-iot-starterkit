package sample;

import commons.SampleException;

public class Main {

	public static void main(String[] args) {
		try {
			new SampleApp().run();
		}
		catch (SampleException e) {
			String message = String.format("[ERROR] Unable to run the sample - %1$s",
				e.getMessage());
			System.err.println(message);
			System.exit(1);
		}
	}

}