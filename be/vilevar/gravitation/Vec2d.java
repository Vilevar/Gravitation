package be.vilevar.gravitation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Vec2d implements Cloneable {

	public static final MathContext CTX = new MathContext(100, RoundingMode.HALF_UP);
	public static final Vec2d NULL = new UnmodifiableVec2d(0, 0);
	public static final Vec2d UNIT_X = new UnmodifiableVec2d(1, 0);
	public static final Vec2d UNIT_Y = new UnmodifiableVec2d(0, 1);
	
	private BigDecimal x;
	private BigDecimal y;
	
	public Vec2d(double x, double y) {
		this(new BigDecimal(x), new BigDecimal(y));
	}
	
	public Vec2d(BigDecimal x, BigDecimal y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x.doubleValue();
	}
	
	public BigDecimal getBigX() {
		return x;
	}
	
	public double getY() {
		return y.doubleValue();
	}
	
	public BigDecimal getBigY() {
		return y;
	}
	
	public Vec2d setX(BigDecimal x) {
		this.x = x;
		return this;
	}
	
	public Vec2d setX(double x) {
		return this.setX(new BigDecimal(x));
	}
	
	public Vec2d setY(BigDecimal y) {
		this.y = y;
		return this;
	}
	
	public Vec2d setY(double y) {
		return this.setY(new BigDecimal(y));
	}
	
	public double alpha() {
		double x = this.getX();
		double y = this.getY();
		if(x == 0)
			return y > 0 ? Math.PI : -Math.PI;
		double alpha = Math.atan(this.y.divide(this.x, CTX).doubleValue());
		if(alpha < 0 && y > 0)
			alpha += Math.PI;
		else if(alpha > 0 && x < 0)
			alpha -= Math.PI;
		return alpha;
	}
	
	public double length() {
		return Math.sqrt(this.squaredLength());
	}
	
	public BigDecimal bigSquaredLength() {
		return x.pow(2, CTX).add(y.pow(2, CTX), CTX);
	}
	
	public double squaredLength() {
		return this.bigSquaredLength().doubleValue();
	}
	
	public Vec2d add(BigDecimal x, BigDecimal y) {
		this.x = this.x.add(x, CTX);
		this.y = this.y.add(y, CTX);
		return this;
	}
	
	public Vec2d add(double x, double y) {
		return this.add(new BigDecimal(x), new BigDecimal(y));
	}
	
	public Vec2d multiply(BigDecimal a) {
		this.x = this.x.multiply(a, CTX);
		this.y = this.y.multiply(a, CTX);
		return this;
	}
	
	public Vec2d multiply(double a) {
		return this.multiply(new BigDecimal(a));
	}
	
	public Vec2d divide(BigDecimal a) {
		this.x = this.x.divide(a, CTX);
		this.y = this.y.divide(a, CTX);
		return this;
	}
	
	public Vec2d divide(double a) {
		return this.divide(new BigDecimal(a));
	}
	
	public Vec2d normalize() {
		BigDecimal length = new BigDecimal(this.length());
		this.x = this.x.divide(length, CTX);
		this.y = this.y.divide(length, CTX);
		return this;
	}
	
	public Vec2d negate() {
		this.x = this.x.negate();
		this.y = this.y.negate();
		return this;
	}
	
	public double dot(Vec2d other) {
		return this.x.multiply(other.x, CTX).add(this.y.multiply(other.y, CTX)).doubleValue(); 
	}
	
	public double distance(Vec2d other) {
		return this.clone().subtract(other).length();
	}
	
	public double angleWith(Vec2d other) {
		return Math.acos(this.dot(other) / (this.length() * other.length()));
	}
	
	public Vec2d add(Vec2d other) {
		return this.add(other.x, other.y);
	}
	
	public Vec2d subtract(Vec2d other) {
		return this.add(other.x.negate(), other.y.negate());
	}

	public Vec2d clone() {
		return new Vec2d(x, y);
	}
	
	public boolean equals(Vec2d vec2d) {
		return vec2d.x == this.x && vec2d.y == this.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Vec2d && this.equals((Vec2d) obj);
	}
	
	@Override
	public String toString() {
		return "Vec2d["+x+"; "+y+"]";
	}
	
	public String basicString() {
		return "("+x+"; "+y+")";
	}
	
	
	public static class UnmodifiableVec2d extends Vec2d {

		public UnmodifiableVec2d(double x, double y) {
			super(x, y);
		}
		
		@Override
		public Vec2d setX(BigDecimal x) {
			return super.clone().setX(x);
		}
		
		@Override
		public Vec2d setY(BigDecimal y) {
			return super.clone().setY(y);
		}
		
		@Override
		public Vec2d add(BigDecimal x, BigDecimal y) {
			return super.clone().add(x, y);
		}
		
		@Override
		public Vec2d normalize() {
			return super.clone().normalize();
		}
	}
}
