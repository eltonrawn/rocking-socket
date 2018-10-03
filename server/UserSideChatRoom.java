import java.net.*;
import java.io.*;
import java.util.*;

class UserSideChatRoom implements Serializable	{
	///this contains what user side is allowed to see
	String roomName;
	boolean hasAccess;
	ArrayList<String> chatRoomLog;
	public UserSideChatRoom()	{
		this.roomName = "";
		this.hasAccess = false;
	}
	public UserSideChatRoom(String roomName, boolean hasAccess, ArrayList<String> chatRoomLog)	{
		this.roomName = roomName;
		this.hasAccess = hasAccess;
		this.chatRoomLog = chatRoomLog;
	}
}