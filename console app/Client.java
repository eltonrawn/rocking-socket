import java.net.*;
import java.io.*;
import java.util.*;

public class Client	{
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public void startConnection(String ip, int port)	{
		try	{
			clientSocket = new Socket(ip, port);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in  = new ObjectInputStream(clientSocket.getInputStream());
			new ServerListener().start();
		}
		catch(Exception e)	{
			
		}
	}
	
	public void sendMessage(ChatMessage msg)	{
		
		try	{
			out.writeObject(msg);//sending message to server
		}
		catch(Exception e)	{
			
		}
	}
	
	public void stopConnection()	{
		try	{
			in.close();
			out.close();
			clientSocket.close();
		}
		catch(Exception e)	{
			
		}		
	}
	public static void main(String[] args)	{
		Client client = new Client();
		client.startConnection("localhost", 6666);
		
		
		Scanner sc = new Scanner(System.in);
		//String msg;
		while(sc.hasNext())	{
			client.sendMessage(new ChatMessage(sc.next()));
		}
		
		//client.sendMessage(new ChatMessage("hi man, what's up"));
		client.stopConnection();
	}
	
	/***************************************************************************************************************/
	class ServerListener extends Thread	{
		public void run()	{
			while(true)	{
				try {
					String msg = (String) in.readObject();
					System.out.println(msg);
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	
}