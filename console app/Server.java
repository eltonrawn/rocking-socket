import java.net.*;
import java.io.*;
import java.util.*;



public class Server	{
	private ServerSocket serverSocket;
	private ArrayList<ClientHandler> clientAra;
	Server()	{
		clientAra = new ArrayList<ClientHandler>();
	}
	public void start(int port)	{
		try	{
			serverSocket = new ServerSocket(port);
			while(true)	{
				System.out.println("Trying to accept");
				ClientHandler ch = new ClientHandler(serverSocket.accept());
				ch.start();
				clientAra.add(ch);
				System.out.println("Accepted");
				//serverSocket.accept() blocks till client make a connection
				//serverSocket.accept() returns the reference for socket connected with client
			}
		}
		catch(Exception E)	{
			
		}
	}
	private void broadcast(String str)	{
		for(int i = 0; i < clientAra.size(); i++)	{
			ClientHandler client = clientAra.get(i);
			client.writeMsg(str);
		}
	}
	
	public static void main(String[] args)	{
		
		Server server = new Server();
		server.start(6666);
	}
	
	/***************************************************************************************************************/
	class ClientHandler extends Thread	{
	
		private Socket clientSocket;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		
		public ClientHandler(Socket socket)	{
			this.clientSocket = socket;
		}
		
		public void clearAll()	{
			try	{
				in.close();
				out.close();
				clientSocket.close();
			}
			catch(Exception e)	{
			}
			
		}
		
		public void run()	{
			///This will be separate for each client
			try	{
				/**
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				String inputLine;
				while((inputLine = in.readLine()) != null)	{
					if(".".equals(inputLine))	{
						out.println("good bye");
						break;
					}
					out.println(inputLine);
				}
				*/
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in  = new ObjectInputStream(clientSocket.getInputStream());
				
				ChatMessage input;
				while((input = (ChatMessage)in.readObject()) != null)	{
					if(".".equals(input.message))	{
						
						out.writeObject("good bye");
						break;
					}
					//System.out.println(input.message);
					//out.writeObject(input.message);
					broadcast(input.message);
				}
				this.clearAll();
			}
			catch(Exception e)	{
			}
		}
		private void writeMsg(String str)	{
			try	{
				out.writeObject(str);
			}
			catch(Exception e)	{
				
			}
			
		}
	}
}



