import java.net.*;
import java.io.*;
import java.util.*;

class UserSideChatRoom implements Serializable	{
	///this contains what user side is allowed to see
	String roomName;
	boolean hasAccess;
	public UserSideChatRoom()	{
		this.roomName = "";
		this.hasAccess = false;
	}
	public UserSideChatRoom(String roomName, boolean hasAccess)	{
		this.roomName = roomName;
		this.hasAccess = hasAccess;
	}
}