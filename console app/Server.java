import java.net.*;
import java.io.*;
import java.util.*;



public class Server	{
	private ServerSocket serverSocket;
	private ArrayList<ClientHandler> clientAra;
	private TreeMap<String, String> userMap;///stores username and password
	Server()	{
		clientAra = new ArrayList<ClientHandler>();
		userMap = new TreeMap<String, String>();
		userMap.put("rawn", "rawn");
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
		private int status;
		
		
		public ClientHandler(Socket socket)	{
			this.clientSocket = socket;
			status = 0;
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
				
				/**
				if(status == 0)	{
					UserInfo input;
					while((input = (UserInfo)in.readObject()) != null)	{
						System.out.println("lala" + input.userName + " " + input.password);
					}
				}
				*/
				
				while(true)	{
					if(status == 0)	{
						///user verification stage
						UserInfo input = (UserInfo)in.readObject();
						System.out.println("lala " + input.userName + " " + input.password);
						
						if(userMap.get(input.userName) == null)	{
							System.out.println("user not valid");
							out.writeObject(false);
							continue;
						}
						
						if(userMap.get(input.userName).equals(input.password))	{
							System.out.println("user verified");
							out.writeObject(true);
							status = 1;
							//out.writeObject("welcome user");
							continue;
						}
						else	{
							System.out.println("password don't match");
							out.writeObject(false);
						}
						
					}
					else	{
						ChatMessage input;
						input = (ChatMessage)in.readObject();
						System.out.println("message received");
						broadcast(input.message);
					}
				}
				
				/**
				while((input = (ChatMessage)in.readObject()) != null)	{
					if(".".equals(input.message))	{
						
						out.writeObject("good bye");
						break;
					}
					//System.out.println(input.message);
					//out.writeObject(input.message);
					broadcast(input.message);
				}
				*/
				
				///this is important
				//this.clearAll();
			}
			catch(Exception e)	{
			}
		}
		private void writeMsg(String str)	{
			try	{
				out.writeObject(str);
				System.out.println("message broadcasted");
			}
			catch(Exception e)	{
				
			}
			
		}
	}
}



