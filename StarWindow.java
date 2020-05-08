package be.vilevar.gravitation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StarWindow {

	private static final MathContext PRECISION = new MathContext(3, RoundingMode.HALF_UP);
	
	private Star star;
	private Stage window;
	
	private String strTime = "Time = ";
	private String strX = "x = ";
	private String strY = "y = ";
	private String strDistance = "Distance = ";
	private String strSpeed = "Speed = ";
	private String strSpeedx = "Speed x = ";
	private String strSpeedy = "Speed y = ";
	private String strAcc = "Acc = ";
	private String strAccx = "Acc x = ";
	private String strAccy = "Acc y = ";
	private String strAcct = "Acc tan = ";
	private String strAcca = "Acc norm = ";
	
	private Label time = new Label();
	private Label x = new Label();
	private Label y = new Label();
	private Label distance = new Label();
	private Label speed = new Label();
	private Label speedx = new Label();
	private Label speedy = new Label();
	private Label acc = new Label();
	private Label accx = new Label();
	private Label accy = new Label();
	private Label acct = new Label();
	private Label acca = new Label();
	
	private Canvas canvas;
	
	private int i;
	
	public StarWindow(Star owner) {
		this.star = owner;
		
		this.window = new Stage();
		this.window.setTitle(owner.getName());
		this.window.setOnCloseRequest(e -> {
			e.consume();
			owner.closeWindow();
		});
		
		Color text = owner.getColor().grayscale().getBrightness() > .5 ? Color.BLACK : Color.WHITE;
		
		this.time.setPrefWidth(200);
		this.time.setTextFill(text);
		this.x.setPrefWidth(200);
		this.x.setTextFill(text);
		this.y.setPrefWidth(200);
		this.y.setTextFill(text);
		this.distance.setPrefWidth(200);
		this.distance.setTextFill(text);
		this.speed.setPrefWidth(200);
		this.speed.setTextFill(text);
		this.speedx.setPrefWidth(200);
		this.speedx.setTextFill(text);
		this.speedy.setPrefWidth(200);
		this.speedy.setTextFill(text);
		this.acc.setPrefWidth(200);
		this.acc.setTextFill(text);
		this.accx.setPrefWidth(200);
		this.accx.setTextFill(text);
		this.accy.setPrefWidth(200);
		this.accy.setTextFill(text);
		this.acct.setPrefWidth(200);
		this.acct.setTextFill(text);
		this.acca.setPrefWidth(200);
		this.acca.setTextFill(text);
		
		BorderPane root = new BorderPane();
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(this.time, 0, 0);
		grid.add(this.x, 1, 0);
		grid.add(this.y, 2, 0);
		grid.add(this.distance, 3, 0);
		grid.add(this.speed, 0, 1);
		grid.add(this.speedx, 1, 1);
		grid.add(this.speedy, 2, 1);
		grid.add(this.acc, 0, 2);
		grid.add(this.accx, 1, 2);
		grid.add(this.accy, 2, 2);
		grid.add(this.acct, 3, 2);
		grid.add(this.acca, 4, 2);
		grid.setPadding(new Insets(20, 10, 10, 20));
		grid.setBackground(new Background(new BackgroundFill(owner.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
		root.setTop(grid);
		
		this.canvas = new Canvas(800, 300);
		Pane body = new Pane(new Group(this.canvas));
		body.setPrefSize(800, 300);
		body.setMinHeight(300);
		body.widthProperty().addListener((obs, old, width) -> this.canvas.setWidth(width.doubleValue()));
		Simulator.DT.addListener((obs, old, speed) -> {
			if(old.doubleValue() != speed.doubleValue()) {
				GraphicsContext ctx = this.canvas.getGraphicsContext2D();
				ctx.setStroke(Color.BLACK);
				ctx.setLineWidth(1);
				int pos = this.i / 10;
				ctx.strokeLine(pos, 0, pos, 300);
			}
		});
		root.setCenter(body);
		
		this.window.setScene(new Scene(root));
	}
	
	public void update(BigDecimal time) {
		// Texts
		this.time.setText(this.strTime+time+"s");
		this.x.setText(this.strX+this.convert(this.star.getPosition().getX())+"m");
		this.y.setText(this.strY+this.convert(this.star.getPosition().getY())+"m");
		this.distance.setText(this.strDistance+this.convert(this.star.getPosition().length())+"m");
		this.speed.setText(this.strSpeed+this.convert(this.star.getSpeed().length())+"m/s");
		this.speedx.setText(this.strSpeedx+this.convert(this.star.getSpeed().getX())+"m/s");
		this.speedy.setText(this.strSpeedy+this.convert(this.star.getSpeed().getY())+"m/s");
		// Acceleration
		double acc = this.star.getAcceleration().length();
		this.acc.setText(this.strAcc+this.convert(acc)+"m/s²");
		this.accx.setText(this.strAccx+this.convert(this.star.getAcceleration().getX())+"m/s²");
		this.accy.setText(this.strAccy+this.convert(this.star.getAcceleration().getY())+"m/s²");
		if(this.star.getLastPosition() != null) {
			double cos = this.star.getAcceleration().cosAngleWith(this.star.getPosition().clone().subtract(this.star.getLastPosition()));
			this.acct.setText(this.strAcct+this.convert(acc * cos)+"m/s²");
			this.acca.setText(this.strAcca+this.convert(acc * Math.sqrt(1.0 - Math.pow(cos, 2)))+"m/s²");
		}
		
		// Draw graphs
		int pos = (++i) / 10;
		GraphicsContext ctx = this.canvas.getGraphicsContext2D();
		PixelWriter pw = ctx.getPixelWriter();
		pw.setColor(pos, 300 - (int) (this.star.getPosition().length() / 800_000_000), Color.BLUE); // Position
		pw.setColor(pos, 300 - (int) (this.star.getSpeed().length() /  1000), Color.GREEN); // Speed
		pw.setColor(pos, 300 - (int) (this.star.getAcceleration().length() * 1000), Color.RED); // Acceleration
	}
	
	public void show() {
		this.initializeLabels();
		this.window.setIconified(true);
		this.window.show();
	}
	
	@Deprecated
	public void close() {
		this.i = 0;
		this.window.close();
		if(this.canvas != null)
			this.canvas.getGraphicsContext2D().clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
	}
	
	
	
	public void initializeLabels() {
		this.time.setText(this.strTime);
		this.x.setText(this.strX);
		this.y.setText(this.strY);
		this.distance.setText(this.strDistance);
		this.speed.setText(this.strSpeed);
		this.speedx.setText(this.strSpeedx);
		this.speedy.setText(this.strSpeedy);
		this.acc.setText(this.strAcc);
		this.accx.setText(this.strAccx);
		this.accy.setText(this.strAccy);
		this.acct.setText(this.strAcct);
		this.acca.setText(this.strAcca);
	}
	
	private String convert(double a) {
		return new BigDecimal(a, PRECISION).toEngineeringString();
	}
	
//	private String convert(double a, BigDecimal multi, BigDecimal divi) {
//		return new BigDecimal(a).multiply(multi).divide(divi, PRECISION).toString();
//	}
}
