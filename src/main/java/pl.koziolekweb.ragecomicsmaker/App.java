package pl.koziolekweb.ragecomicsmaker;


import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Hello world!
 */
public class App extends Application {
	@SuppressWarnings("UnstableApiUsage")
	public static final EventBus EVENT_BUS = new EventBus();

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/ui.fxml")));
		primaryStage.setTitle("RCM");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
