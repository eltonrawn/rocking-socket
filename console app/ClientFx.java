import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
/**
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
*/
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.application.Platform;

import java.net.*;
import java.io.*;
import java.util.*;

public class ClientFx extends Application {

    Stage window;
    Scene userScene, chatScene;
	///layout1
	Label userLabel, passLabel;
	TextField userField, passField;
	Button loginButton;
	
	///layout2
    Button chatButton;
	TextField chatField;
    ListView<String> listView;
	Client client;
	UserInfo userInfo;
	
	public ClientFx()	{
		client = new Client();
		client.startConnection("localhost", 6666);
		//userInfo = new UserInfo();
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Chat");
		
		//Layout1
		userLabel = new Label("Username : ");
		passLabel = new Label("Password : ");
		
		userField = new TextField();
		passField = new TextField();
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
		
		
		//Layout2
		chatButton = new Button("Submit");
		chatField = new TextField();
        listView = new ListView<>();
        //listView.getItems().addAll("Iron Man", "Titanic", "Contact", "Surrogates");
        //listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //button.setOnAction(e -> buttonClicked());
		chatButton.setOnAction(e -> {
			System.out.println("message sent");
			client.sendMessage(new ChatMessage(chatField.getText()));
		});
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(20, 20, 20, 20));
        chatLayout.getChildren().addAll(listView, chatField, chatButton);
		chatScene = new Scene(chatLayout, 500, 500);
        
		
		window.setScene(userScene);
        window.show();
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
							System.out.println("message porar jonno ready 0");
							//Boolean isValid = (Boolean) in.readObject();
							Boolean isValid = (Boolean)obj; 
							if(isValid == true)	{
								Platform.runLater(new Runnable(){
									public void run() {
										threadStatus = 1;
										System.out.println("user is varified " + threadStatus);
										window.setScene(chatScene);
									}
								});
							}
						}
						else	{
							System.out.println("message porar jonno ready 1");
							//String msg = (String) in.readObject();
							String msg = (String) obj;
							//System.out.println("message recieved from server");
							
							Platform.runLater(new Runnable(){
								public void run() {
									listView.getItems().add(msg);
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