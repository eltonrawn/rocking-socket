import java.net.*;
import java.io.*;
import java.util.*;


class ChatRoom	{
	String roomName;
	TreeSet<String> user;///stores username which is unique
	ChatRoom(String roomName)	{
		this.roomName = roomName;
		user = new TreeSet<String>();
	}
	public void addUser(String userName)	{
		user.add(userName);
	}
	public boolean exists(String userName)	{
		return user.contains(userName);
	}
	public void seeUsers()	{
		System.out.println(roomName + " : ");
		for(String eachUser : user)	{
			System.out.println(eachUser);
		}
	}
}

public class Server	{
	private ServerSocket serverSocket;///server listening to one port
	private ArrayList<ClientHandler> clientAra;///saving each clientthread to broadcast later
	private TreeMap<String, String> userMap;///stores username and password
	
	private ArrayList<ChatRoom> chatRoomAra;///stores information for each chatroom
	private TreeMap<String, Integer> posOfChatRoom; 
	
	Server()	{
		clientAra = new ArrayList<ClientHandler>();
		///inserting users and passwords
		userMap = new TreeMap<String, String>();
		userMap.put("rawn", "rawn");
		userMap.put("rawn0", "rawn0");
		userMap.put("rawn1", "rawn1");
		
		///creating chatroom
		chatRoomAra = new ArrayList<ChatRoom>();
		posOfChatRoom = new TreeMap<String, Integer>();
		addChatRoom("Admin");
		addChatRoom("Sales");
		addChatRoom("Programmer");
		addChatRoom("arekta");
		
		
		addChatRoomUser("Admin", "rawn");
		addChatRoomUser("Programmer", "rawn");
		addChatRoomUser("arekta", "rawn");
		addChatRoomUser("Admin", "rawn0");
		
		
		for(int i = 0; i < chatRoomAra.size(); i++)	{
			chatRoomAra.get(i).seeUsers();
		}
		
	}
	
	public void addChatRoom(String roomName)	{
		posOfChatRoom.put(roomName, chatRoomAra.size());
		chatRoomAra.add(new ChatRoom(roomName));
		
	}
	
	public void addChatRoomUser(String roomName, String userName)	{
		Integer pos = posOfChatRoom.get(roomName);
		chatRoomAra.get(pos).addUser(userName);
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
	private void broadcast(ChatMessage msg)	{
		for(int i = 0; i < clientAra.size(); i++)	{
			ClientHandler client = clientAra.get(i);
			client.writeMsg(msg);
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
		
		private String userName;
		private String password;
		
		
		
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
					Object obj = in.readObject();
					if(status == 0)	{
						///user verification stage
						UserInfo input = (UserInfo)obj;
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
							
							userName = input.userName;
							password = input.password;
							//out.writeObject("welcome user");
							
							
							//give information about chatrooms
							ArrayList<UserSideChatRoom> userChatAra = new ArrayList<UserSideChatRoom>();
							for(int i = 0; i < chatRoomAra.size(); i++)	{
								userChatAra.add(new UserSideChatRoom(chatRoomAra.get(i).roomName, chatRoomAra.get(i).exists(userName)));
							}
							for(int i = 0; i < userChatAra.size(); i++)	{
								System.out.println(userChatAra.get(i).roomName + " " + userChatAra.get(i).hasAccess);
							}
							out.writeObject(userChatAra);
							
							continue;
						}
						else	{
							System.out.println("password don't match");
							out.writeObject(false);
						}
					}
					else	{
						ChatMessage input = (ChatMessage)obj;
						System.out.println("message received");
						broadcast(input);
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
		private void writeMsg(ChatMessage msg)	{
			try	{
				out.writeObject(msg);
				System.out.println("message broadcasted");
			}
			catch(Exception e)	{
				
			}
			
		}
	}
}



