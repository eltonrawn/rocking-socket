import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import java.net.*;
import java.io.*;
import java.util.*;

public class ClientFx extends Application {

    Stage window;
    Scene scene;
    Button button;
	TextField msgField;
    ListView<String> listView;
	Client client;
	
	public ClientFx()	{
		client = new Client();
		client.startConnection("localhost", 6666);
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
        window.setTitle("ListView Demo");
        button = new Button("Submit");
		
		msgField = new TextField();

        listView = new ListView<>();
        //listView.getItems().addAll("Iron Man", "Titanic", "Contact", "Surrogates");
        //listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		
        button.setOnAction(e -> buttonClicked());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(listView, msgField, button);

        scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.show();
    }

    private void buttonClicked(){
		/**
        System.out.println(msgField.getText());
		listView.getItems().add(msgField.getText());
		*/
		client.sendMessage(new ChatMessage(msgField.getText()));
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
			public void run()	{
				while(true)	{
					try {
						String msg = (String) in.readObject();
						//System.out.println(msg);
						
						Platform.runLater(new Runnable(){
							public void run() {
								 listView.getItems().add(msg);
							}
							
						});
					}
					catch(Exception e) {
					}
				}
			}
		}
		
		
	}

}