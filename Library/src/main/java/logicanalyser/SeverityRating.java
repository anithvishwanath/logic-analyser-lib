package logicanalyser;

public enum SeverityRating {
	/**
	 * Informational items are not problems, just additional information that
	 * may be useful
	 */
	Informational,
	/**
	 * Warnings are not problems yet, but might cause some
	 */
	Warning,
	/**
	 * An error is a problem, but your application will still probably work
	 * without fixing it
	 */
	Error,
	/**
	 * Critical errors are such that they absolutely must be fixed for your
	 * program to work
	 */
	CriticalError
}
