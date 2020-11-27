package pl.koziolekweb.ragecomicsmaker;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEventListener;


/**
 * Hello world!
 */
public class App extends Application implements ErrorEventListener {

	@SuppressWarnings("UnstableApiUsage")
	public static final EventBus EVENT_BUS = new EventBus();

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/ui.fxml")));

		primaryStage.setTitle("RCM");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	@SuppressWarnings("UnstableApiUsage")
	@Override
	@Subscribe
	public void handleErrorEvent(ErrorEvent event) {
//		JOptionPane.showConfirmDialog(main, event.message, "UWAGA!", JOptionPane.DEFAULT_OPTION);
	}
}
