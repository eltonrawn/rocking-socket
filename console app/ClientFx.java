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

public class ClientFx extends Application {

    Stage window;
    Scene userScene;
	Scene chatListScene;
	ArrayList<Scene> chatScene;
	ArrayList<VBox> chatLayout;
	
	Button chatListButton;
	///layout1
	Label userLabel, passLabel;
	TextField userField, passField;
	Button loginButton;
	
	///layout2
	ArrayList<Button> backButton;
    ArrayList<Button> chatButton;
	ArrayList<TextField> chatField;
    //ListView<String> listView[];
	ArrayList< ListView<String> > listViewAra;
	ListView<String> chatListView;
	Client client;
	UserInfo userInfo;
	
	TreeMap<String, Integer> posOfChatRoom;
	String curRoomName;int curRoomPos;
	
	
	public ClientFx()	{
		client = new Client();
		client.startConnection("localhost", 6666);
		//userInfo = new UserInfo();
		listViewAra = new ArrayList< ListView<String> >();
		chatScene = new ArrayList<Scene>();//literal chat box
		posOfChatRoom = new TreeMap<String, Integer>();
		chatLayout = new ArrayList<VBox>();
		chatField = new ArrayList<TextField>();
		chatButton = new ArrayList<Button>();
		backButton = new ArrayList<Button>();
	}
	public void stopConnection()	{
		client.stopConnection();
	}
	
    public static void main(String[] args) {
        launch(args);
		
		ClientFx cf = new ClientFx();
		
		
		/**
		//String msg;
		while(sc.hasNext())	{
			client.sendMessage(new ChatMessage(sc.next()));
		}
		*/
		
		//client.sendMessage(new ChatMessage("hi man, what's up"));
		//client.stopConnection();
		cf.stopConnection();
    }
	
	
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Chat");
		
		window.setOnCloseRequest(e -> System.exit(0));
		//chatListScene
		
		chatListView = new ListView<String>();
		chatListButton = new Button("Enter Chat");
		
		VBox chatListLayout = new VBox(10);
        chatListLayout.setPadding(new Insets(20, 20, 20, 20));
        chatListLayout.getChildren().addAll(chatListView, chatListButton);
		chatListScene = new Scene(chatListLayout, 400, 400);
		chatListButton.setOnAction(e -> {
			curRoomName = chatListView.getSelectionModel().getSelectedItem();
			curRoomPos = posOfChatRoom.get(curRoomName);
			System.out.println("curRoomPos : " + curRoomPos);
			window.setScene(chatScene.get(curRoomPos));
		});
		
		//userScene
		userLabel = new Label("Username : ");
		passLabel = new Label("Password : ");
		
		userField = new TextField("rawn");
		passField = new TextField("rawn");
        loginButton = new Button("Login");
		VBox userLayout = new VBox(10);
        userLayout.setPadding(new Insets(20, 20, 20, 20));
        userLayout.getChildren().addAll(userLabel, userField, passLabel, passField, loginButton);
		userScene = new Scene(userLayout, 200, 200);
		loginButton.setOnAction(e -> {
			//userInfo.setUserInfo(userField.getText(), passField.getText());
			
			userInfo = new UserInfo(userField.getText(), passField.getText());
			//client.verifyUser(new UserInfo(userField.getText(), passField.getText()));
			client.verifyUser(userInfo);
		});
		
		
		//chatScene
		
		/**
		chatButton = new Button("Submit");
		chatField = new TextField();
		chatButton.setOnAction(e -> {
			System.out.println("message sending to " + curRoomName);
			client.sendMessage(new ChatMessage(curRoomName, chatField.getText()));
		});
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(20, 20, 20, 20));
		for(int i = 0; i < listViewAra.size(); i++)	{
			chatLayout.getChildren().addAll(listViewAra.get(i), chatField, chatButton);
		}
        */
		//chatScene = new Scene(chatLayout, 500, 500);
        
		
		window.setScene(userScene);
        window.show();
    }
	
	/**
	public String getTextFunc(TextField tf)	{
		return tf.getText();
	}
	*/
	
	public void setUpChatRoom(ArrayList<UserSideChatRoom> userChatAra)	{
		for(int i = 0; i < userChatAra.size(); i++)	{
								
			System.out.println(userChatAra.get(i).roomName + " " + userChatAra.get(i).hasAccess);
			
			
			posOfChatRoom.put(userChatAra.get(i).roomName, i);
			
			
			
			chatButton.add(new Button("Submit"));
			backButton.add(new Button("Back"));
			chatField.add(new TextField());
			
			String roomName = userChatAra.get(i).roomName;
			//String textToSend = chatField.get(i).getText();
			TextField tmpTextField = chatField.get(i);
			
			chatButton.get(i).setOnAction(e -> {
				//Message is sent with this button
				System.out.println("message sending to " + roomName);
				//final String textToSend = chatField.get(i).getText();
				client.sendMessage(new ChatMessage(roomName, tmpTextField.getText() + "@" + userInfo.userName));
			});
			backButton.get(i).setOnAction(e -> {
				window.setScene(chatListScene);
			});
			
			
			chatLayout.add(new VBox(10));
			chatLayout.get(i).setPadding(new Insets(20, 20, 20, 20));
			listViewAra.add(new ListView<String>());
			chatLayout.get(i).getChildren().addAll(listViewAra.get(i), chatField.get(i), chatButton.get(i), backButton.get(i));
			
			chatScene.add(new Scene(chatLayout.get(i), 500, 500));
			if(userChatAra.get(i).hasAccess == false)	{
				continue;
			}
			chatListView.getItems().add(userChatAra.get(i).roomName);
			
			
			for(int j = 0; j < userChatAra.get(i).chatRoomLog.size(); j++)	{
				listViewAra.get(i).getItems().add(userChatAra.get(i).chatRoomLog.get(j));
			}
			//listViewAra.get(i).getItems().add("ashen admin er kaj kori");
		}
		
		/**
		listViewAra.get(0).getItems().add("ashen admin er kaj kori");
		listViewAra.get(2).getItems().add("ki khobor programmer");
		*/
	}
	
    private void buttonClicked(){
		/**
        System.out.println(msgField.getText());
		listView.getItems().add(msgField.getText());
		*/
		//client.sendMessage(new ChatMessage(msgField.getText()));
    }
	
	
	
	
	class Client	{
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
				System.out.println("sending" + msg.message);
				out.writeObject(msg);//sending message to server
			}
			catch(Exception e)	{
				
			}
		}
		
		public void verifyUser(UserInfo user)	{
			try	{
				System.out.println(user.userName + " " + user.password);
				out.writeObject(user);//sending message to server
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
		/**
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
		*/
		/***************************************************************************************************************/
		
		class ServerListener extends Thread	{
			private int threadStatus;
			ServerListener()	{
				threadStatus = 0;
			}
			public void run()	{
				while(true)	{
					try {
						Object obj = in.readObject();
						if(threadStatus == 0)	{///it is in login stage
							//System.out.println("message porar jonno ready 0");
							//Boolean isValid = (Boolean) in.readObject();
							Boolean isValid = (Boolean)obj; 
							if(isValid == true)	{
								Platform.runLater(new Runnable(){
									public void run() {
										threadStatus = 1;
										System.out.println("user is varified " + threadStatus);
										//window.setScene(chatScene);
										window.setScene(chatListScene);
									}
								});
							}
						}
						else if(threadStatus == 1)	{
							///setting up chat room list and each chat room accordin to it
							ArrayList<UserSideChatRoom> userChatAra = (ArrayList<UserSideChatRoom>) obj;
							setUpChatRoom(userChatAra);
							threadStatus = 2;
						}
						else	{
							//System.out.println("message porar jonno ready 1");
							//String msg = (String) in.readObject();
							ChatMessage msg = (ChatMessage) obj;
							System.out.println("message recieved from server");
							
							Platform.runLater(new Runnable(){
								public void run() {
									int pos = posOfChatRoom.get(msg.roomName);
									System.out.println(pos + msg.message);
									listViewAra.get( pos ).getItems().add(msg.message);
								}
								
							});
						}
					}
					catch(Exception e) {
					}
				}
			}
		}
		
		/**
		class userVerifyListener extends Thread	{
			boolean running = true;
			public void run()	{
				while(running)	{
					try {
						String is_verify = (String) in.readObject();
						//System.out.println(msg);
						
						Platform.runLater(new Runnable(){
							public void run() {
								if(is_verify == 'Yes')	{
									window.setScene(chatScene);
									new ServerListener().start();
									running = false;
								}
								else	{
									
								}
							}
						});
					}
					catch(Exception e) {
					}
				}
			}
		}
		*/
		
		
	}

}