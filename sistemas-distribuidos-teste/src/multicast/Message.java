package multicast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private String text;
	private String clientName;
	private Date timestamp;
	
	public Message(String text, String clientName) {
		this.text = text;
		this.clientName = clientName;
		this.timestamp = new Date();
	}
	
	public String toString() {
	    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
		return "[" + dateFormat.format(this.timestamp) + "] " +  this.clientName + ": " + this.text;
	}

}
