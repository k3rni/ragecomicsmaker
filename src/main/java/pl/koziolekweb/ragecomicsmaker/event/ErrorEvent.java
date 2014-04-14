package pl.koziolekweb.ragecomicsmaker.event;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ErrorEvent {

	public final String message;
	public final Throwable t;

	public ErrorEvent(String message, Throwable t) {
		this.message = message;
		this.t = t;
	}
}
