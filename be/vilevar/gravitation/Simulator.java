package be.vilevar.gravitation;

import static be.vilevar.gravitation.Vec2d.CTX;
import static be.vilevar.gravitation.Gravitation.G;

import java.math.BigDecimal;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class Simulator {

	public static final DoubleProperty PRECISION = new SimpleDoubleProperty(100);
	public static final DoubleProperty DT = new SimpleDoubleProperty(5000);
	public static final DoubleProperty SPEED = new SimpleDoubleProperty(50);
	
	private final SimulationBoard board;
	private ObservableList<Star> stars;
	private Timeline timeline;
	
	private boolean running;
	
	public Simulator(SimulationBoard board) {
		this.board = board;
		this.stars = board.getStars();
		
		this.timeline = new Timeline(new KeyFrame(Duration.millis(SPEED.get()), this::step));
		this.timeline.setCycleCount(-1);
		
		SPEED.addListener((obs, old, speed) -> {
			boolean isRunning = this.isRunning();
			ChangeListener<? super Status> statusListener = board.getStatusListener();
			if(statusListener != null)
				this.statusProperty().removeListener(statusListener);
			if(isRunning)
				this.stop();
			
			this.timeline = new Timeline(new KeyFrame(Duration.millis(speed.doubleValue()), this::step));
			this.timeline.setCycleCount(-1);
			if(isRunning)
				this.timeline.play();
			if(statusListener != null)
				this.statusProperty().addListener(statusListener);
		});
		
		this.stars.forEach(star -> star.initialize());
	}
	
	public void step(ActionEvent e) {
		if(this.running) {
			this.pause();
			return;
		}
		this.running = true;
		double Dt = DT.get();
		double precision = PRECISION.get();
		double dt = Math.min(precision, Dt);
		double i;
		for(i = 0; i < Dt; i += dt) {
		/*	if((i/dt) % 1000 == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException exception) {
					System.out.println("Simulator couldn't sleep the thread ! Warning with stack over flow");
				}
			}*/
			this.computeStep(dt);
		}
		this.computeStep(Dt - i);
		this.board.draw();
	/*	try {
			Thread.sleep(20);
		} catch (InterruptedException exception) {
			System.out.println("Simulator couldn't sleep the thread ! Warning with stack over flow");
		}*/
		this.running = false;
	}
	
	private void computeStep(double dt) {
		// Compute accelerations
		this.stars.forEach(star -> star.setAcceleration(Vec2d.NULL.clone()));
		for(int i = 0; i < this.stars.size(); i++) {
			Star s1 = this.stars.get(i);
			for(int j = i + 1; j < this.stars.size(); j++) {
				Star s2 = this.stars.get(j);
				
				Vec2d acc = s2.getPosition().clone().subtract(s1.getPosition());
				BigDecimal u = G.divide(acc.bigSquaredLength(), CTX);
				
				BigDecimal a1 = u.multiply(new BigDecimal(s2.getMass()), CTX);
				BigDecimal a2 = u.multiply(new BigDecimal(s1.getMass()), CTX);
				
				acc.normalize();
				Vec2d acc1 = acc.clone().multiply(a1);
				Vec2d acc2 = acc.negate().multiply(a2);
				
				s1.getAcceleration().add(acc1);
				s2.getAcceleration().add(acc2);
			}
		}
		
		// Compute position and speed
		this.stars.forEach(star -> {
			Vec2d dv = star.getAcceleration().clone().multiply(dt); // dv = a * dt
			star.getPosition().add(star.getSpeed().clone().multiply(dt)).add(dv.clone().multiply(dt).divide(2));  // x = x0 + v0*dt + .5*a*(dt)²
			star.getSpeed().add(dv); // v = v0 + dv = v0 + a*dt
		});
	}
	
	public void start() {
		this.timeline.play();
	}
	
	public void pause() {
		this.timeline.pause();
	}
	
	public void stop() {
		this.timeline.stop();
	}
	
	public boolean isRunning() {
		return this.timeline.getStatus() == Status.RUNNING;
	}
	
	public ReadOnlyObjectProperty<Status> statusProperty() {
		return this.timeline.statusProperty();
	}
	
	public void setStars(ObservableList<Star> stars) {
		this.stars = stars;
		this.stars.forEach(s -> s.initialize());
	}
}
