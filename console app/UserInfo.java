import java.net.*;
import java.io.*;
import java.util.*;

class UserInfo implements Serializable	{
	String userName, password;
	public UserInfo()	{
		this.userName = "";
		this.password = "";
	}
	public UserInfo(String userName, String password)	{
		this.userName = userName;
		this.password = password;
	}
	public void setUserInfo(String userName, String password)	{
		this.userName = userName;
		this.password = password;
	}
}