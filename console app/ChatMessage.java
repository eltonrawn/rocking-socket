import java.net.*;
import java.io.*;
import java.util.*;

class ChatMessage implements Serializable	{
	String message;
	public ChatMessage(String message)	{
		this.message = message;
	}
}