package smallwheel.mybro.common.exception;

public class GeneralException extends RuntimeException {

	private static final long serialVersionUID = -5433592147542806769L;

	public GeneralException() {
		super();
	}

	public GeneralException(Throwable e) {
		super(e);
	}

	public GeneralException(String errorMsg) {
		super(errorMsg);
	}

	public GeneralException(String errorMsg, Throwable e) {
		super(errorMsg, e);
	}

	public GeneralException(int errorCode, String errorMsg) {
		super();
	}
}
