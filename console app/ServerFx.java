import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.stage.Stage;
import javafx.event.*;

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

public class ServerFx extends Application	{
	Server server;
	Stage window;
	
	///homelayout
	Button createUserButton;
	Button manageRoomButton;
	Scene homeScene;
	
	///createUserlayout
	ListView<String> userListView;
	Scene userScene;
	Button userBackButton;
	Button userCreateButton;
	TextField userField, passField;
	Label userLabel, passLabel;
	
	
	///createRoomLayout
	ListView<String> roomListView;
	Scene roomScene;
	Button roomBackButton;
	TextField roomCreateField;
	Button roomAssignUserButton;
	Button roomCreateButton;
	
	//AssignLayout
	ArrayList< ListView<String> > assignListView;
	Scene assignScene;
	Button assignBackButton;
	Button assignButton;
	TextField assignField;
	
	
	String curRoomName;
	int curRoomPos;
	
	public ServerFx()	{
		///createRoomLayout
		roomListView = new ListView<String>();
		
		//AssignLayout
		assignListView = new ArrayList< ListView<String> >();
		
		//createUserLayout
		userListView = new ListView<String>();
		
		server = new Server();
		server.start();
		
	}
	
	public static void main(String[] args)	{
		launch(args);
		ServerFx serverFx = new ServerFx();
	}
	
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
        window.setTitle("Server");
		
		window.setOnCloseRequest(e -> System.exit(0));
		
		///homelayout
		createUserButton = new Button("Create User");
		manageRoomButton = new Button("Manage Chat Room");
		
		VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(20, 20, 20, 20));
        homeLayout.getChildren().addAll(createUserButton, manageRoomButton);
		homeScene = new Scene(homeLayout, 200, 200);
		
		manageRoomButton.setOnAction(e -> {
			window.setScene(roomScene);
		});
		
		createUserButton.setOnAction(e -> {
			window.setScene(userScene);
		});
		
		
		///createUserlayout
		
		userField = new TextField();
		passField = new TextField();
		
		userLabel = new Label("username");
		passLabel = new Label("password");
		
		userCreateButton = new Button("Create User");
		userBackButton = new Button("Back");
		VBox userLayout = new VBox(10);
        userLayout.setPadding(new Insets(20, 20, 20, 20));
        userLayout.getChildren().addAll(userListView, userLabel, userField, passLabel, passField, userCreateButton, userBackButton);
		userScene = new Scene(userLayout, 500, 500);
		
		userBackButton.setOnAction(e -> {
			window.setScene(homeScene);
		});
		
		userCreateButton.setOnAction(e -> {
			//window.setScene(homeScene);
			String user = userField.getText().replaceAll("\\s+","");
			String pass = passField.getText().replaceAll("\\s+","");
			if(user.equals(""))	{
				//.replaceAll("\\s+","") removes all whitespace
			}
			else	{
				server.addUser(user, pass);
			}
		});
		
		///manageRoomLayout
		roomCreateButton = new Button("Create Room");
		roomAssignUserButton = new Button("Assign User");
		roomCreateField = new TextField();
		
		roomBackButton = new Button("Back");
		
		VBox roomLayout = new VBox(10);
        roomLayout.setPadding(new Insets(20, 20, 20, 20));
        roomLayout.getChildren().addAll(roomListView, roomAssignUserButton, roomCreateField, roomCreateButton, roomBackButton);
		roomScene = new Scene(roomLayout, 500, 500);
		
		roomBackButton.setOnAction(e -> {
			window.setScene(homeScene);
		});
		roomCreateButton.setOnAction(e -> {
			//window.setScene(homeScene);
			String yo = roomCreateField.getText().replaceAll("\\s+","");
			if(yo.equals(""))	{
				//.replaceAll("\\s+","") removes all whitespace
			}
			else	{
				server.addChatRoom(yo);
			}
		});
		roomAssignUserButton.setOnAction(e -> {
			switchToAssign();
		});
		
		
		//assignUserLayout
		assignButton = new Button("Assign User");
		assignBackButton = new Button("Back");
		assignField = new TextField();
		
		
		
		assignButton.setOnAction(e -> {
			String userN = assignField.getText().replaceAll("\\s+","");
			if(!server.roomUserExists(curRoomName, userN))	{
				
			}
			server.addChatRoomUser(curRoomName, userN);
			
		});
		assignBackButton.setOnAction(e -> {
			window.setScene(roomScene);
		});
		
		window.setScene(homeScene);
        window.show();
		
	}
	
	void switchToAssign()	{
		//System.out.println("hi");
		if(roomListView.getSelectionModel().getSelectedItem() == null)	{
			return;
		}
		
		curRoomName = roomListView.getSelectionModel().getSelectedItem();
		curRoomPos = server.getRoomPos(curRoomName);
		
		
		VBox assignLayout = new VBox(10);
        assignLayout.setPadding(new Insets(20, 20, 20, 20));
        assignLayout.getChildren().addAll(assignListView.get(curRoomPos), assignField, assignButton, assignBackButton);
		assignScene = new Scene(assignLayout, 500, 500);
		
		window.setScene(assignScene);
	}
	
	public void fxAddUser(String userName)	{
		userListView.getItems().add(userName);
	}
	
	public void fxAddRoom(String chatRoom)	{
		roomListView.getItems().add(chatRoom);
		assignListView.add( new ListView<String>() );
	}
	public void fxAddChatRoomUser(int roomPos, String userName)	{
		assignListView.get(roomPos).getItems().add(userName);
	}
	
	class Server extends Thread	{
		private ServerSocket serverSocket;///server listening to one port
		private ArrayList<ClientHandler> clientAra;///saving each clientthread to broadcast later
		private TreeMap<String, String> userMap;///stores username and password
		
		private ArrayList<ChatRoom> chatRoomAra;///stores information for each chatroom
		private TreeMap<String, Integer> posOfChatRoom;///stores position of chatroom in chatroomara
		
		Server()	{
			clientAra = new ArrayList<ClientHandler>();
			///inserting users and passwords
			userMap = new TreeMap<String, String>();
			/**
			userMap.put("rawn", "rawn");
			userMap.put("rawn0", "rawn0");
			userMap.put("rawn1", "rawn1");
			*/
			addUser("rawn", "rawn");
			addUser("rawn0", "rawn0");
			addUser("rawn1", "rawn1");
			
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
		
		public boolean roomUserExists(String roomName, String userName)	{
			int roomPos = getRoomPos(roomName);
			if(chatRoomAra.get(roomPos).exists(userName))	{
				return true;
			}
			return false;
		}
		
		public boolean roomExists(String roomName)	{
			if(posOfChatRoom.get(roomName) == null)	{
				return false;
			}
			return true;
		}
		
		
		public boolean userExists(String userName)	{
			if(userMap.get(userName) == null)	{
				return false;
			}
			return true;
		}
		
		public void addUser(String userName, String password)	{
			if(!userExists(userName))	{
				userMap.put(userName, password);
				fxAddUser(userName);
			}
		}
		
		public void addChatRoom(String roomName)	{
			if(!roomExists(roomName))	{
				posOfChatRoom.put(roomName, chatRoomAra.size());
				chatRoomAra.add(new ChatRoom(roomName));
				fxAddRoom(roomName);
			}
		}
		
		public void addChatRoomUser(String roomName, String userName)	{
			if(userExists(userName) && !roomUserExists(roomName, userName))	{
				Integer pos = posOfChatRoom.get(roomName);
				chatRoomAra.get(pos).addUser(userName);
				fxAddChatRoomUser(pos, userName);
			}			
		}
		
		public int getRoomPos(String roomName)	{
			return posOfChatRoom.get(roomName);
		}
		
		public void run()	{
			try	{
				serverSocket = new ServerSocket(6666);
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
	
}





