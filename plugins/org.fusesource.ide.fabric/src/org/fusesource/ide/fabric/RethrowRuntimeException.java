package org.fusesource.ide.fabric;


/**
 * A {@link RuntimeException} to wrap a real exception we wish to rethrow later
 */
public class RethrowRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -824753052842450329L;

	public RethrowRuntimeException(Throwable cause) {
		super(cause);
	}

	public void rethrowCause() throws Exception {
		Throwable cause = getCause();
		if (cause instanceof Error) {
			throw (Error) cause;
		} else {
			throw (Exception) cause;
		}
	}
}
