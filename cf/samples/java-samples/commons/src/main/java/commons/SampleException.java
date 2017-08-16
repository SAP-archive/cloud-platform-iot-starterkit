package commons;

public class SampleException
extends Exception {

	private static final long serialVersionUID = 1L;

	public SampleException() {
		super();
	}

	public SampleException(String message) {
		super(message);
	}

	public SampleException(String message, Throwable cause) {
		super(message, cause);
	}

	public SampleException(Throwable cause) {
		super(cause);
	}

}
