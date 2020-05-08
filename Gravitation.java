package be.vilevar.gravitation;

import java.math.BigDecimal;

import javafx.application.Application;
import javafx.stage.Stage;

public class Gravitation extends Application {

	public static final BigDecimal G = new BigDecimal(6.67408E-11);
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private Stage window;
	
	@Override
	public void start(Stage stage) throws Exception {
		System.out.println("Start");
		this.window = stage;
		
		this.window.setTitle("Gravitation");
		this.window.setOnCloseRequest((e) -> {
			e.consume();
			this.window.close();
			System.exit(0);
		});
		this.window.setScene(HomeGui.getScene(this));
		this.window.show();
	}
	
	public Stage getWindow() {
		return window;
	}
}
