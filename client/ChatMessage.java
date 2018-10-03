import java.net.*;
import java.io.*;
import java.util.*;

class ChatMessage implements Serializable	{
	String roomName, message;
	public ChatMessage(String roomName, String message)	{
		this.roomName = roomName;
		this.message = message;
	}
}