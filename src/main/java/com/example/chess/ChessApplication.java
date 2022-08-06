package com.example.chess;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class ChessApplication extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception{

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("Hello Chess");
		primaryStage.setScene(new Scene(root, 1000, 600));
		primaryStage.setResizable(false);
		primaryStage.show();
		//System.out.println("showtime");

	}

	public static void main(String[] args) {
		launch(args);
	}

}
