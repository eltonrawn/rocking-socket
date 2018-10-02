import java.net.*;
import java.io.*;
import java.util.*;


class ChatRoom	{
	String roomName;
	TreeSet<String> user;///stores username which is unique
	ArrayList<String> chatRoomLog;///stores previous chat
	ChatRoom(String roomName)	{
		this.roomName = roomName;
		user = new TreeSet<String>();
		chatRoomLog = new ArrayList<String>();
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
	private TreeMap<String, Integer> posOfChatRoom;///stores position of chatroom in chatroomara
	
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
	private synchronized void broadcast(ChatMessage msg)	{
		//ArrayList<Integer> remIdx = new ArrayList<Integer>();
		int pos = posOfChatRoom.get(msg.roomName);
		chatRoomAra.get(pos).chatRoomLog.add(msg.message);
		
		for(int i = 0; i < clientAra.size(); i++)	{
			ClientHandler client = clientAra.get(i);
			System.out.println("message broadcasting to " + client.user.userName);
			if(chatRoomAra.get(pos).exists(client.user.userName))	{
				client.writeMsg(msg);
				/**
				if(!client.writeMsg(msg))	{
					//clientAra.remove(i);
					remIdx.add(i);
					client.threadRunning = false;
				}
				*/
			}
		}
		/**
		for(int i = remIdx.size() - 1; i >= 0; i--)	{
			int idx = remIdx.get(i);
			clientAra.remove(idx);
		}
		*/
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
		
		boolean threadRunning;
		
		UserInfo user;
		
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
			///This thread will be separate for each client
			try	{
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in  = new ObjectInputStream(clientSocket.getInputStream());
				threadRunning = true;
				
				while(threadRunning)	{
					Object obj = in.readObject();
					if(status == 0)	{
						///user verification stage
						user = (UserInfo)obj;
						
						System.out.println("lala " + user.userName + " " + user.password);
						
						if(userMap.get(user.userName) == null)	{
							System.out.println("user not valid");
							out.writeObject(false);
							continue;
						}
						
						if(userMap.get(user.userName).equals(user.password))	{
							System.out.println("user verified");
							out.writeObject(true);
							status = 1;
							
							
							//give information about chatrooms
							ArrayList<UserSideChatRoom> userChatAra = new ArrayList<UserSideChatRoom>();
							for(int i = 0; i < chatRoomAra.size(); i++)	{
								userChatAra.add(new UserSideChatRoom(chatRoomAra.get(i).roomName, chatRoomAra.get(i).exists(user.userName), chatRoomAra.get(i).chatRoomLog));
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
				
				///this is important
				this.clearAll();
			}
			catch(Exception e)	{
			}
		}
		private boolean writeMsg(ChatMessage msg)	{
			/**
			if(!clientSocket.isConnected()) {
				///this does not work
				this.clearAll();
				return false;
			}
			*/
			try	{
				out.writeObject(msg);
				//System.out.println("message broadcasted");
			}
			catch(Exception e)	{
			}
			return true;
		}
	}
}



