package backgroundClasses;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;

public class Sprite {
	protected double xPos;
	protected double yPos;
	protected int angle;
	protected double xVel;
	protected double yVel;
	protected double d;
	protected Color color;
	protected boolean isAlive = true;
	
	private Shape spriteShape;
	
	public boolean isAlive(){
		return isAlive;
	}
	public void die(){
		isAlive = false;
	}
	public void revive(){
		isAlive = true;
	}
	public void setAlive(boolean a){
		isAlive = a;
	}
	public void setPos(int x, int y){
		xPos = x;
		yPos = y;
	}
	public void moveNow(int x, int y){
		xPos+=x;
		yPos+=y;
	}
	
	public void moveNow(){
		xPos+=xVel;
		yPos+=yVel;
	}
	public void stop(){
		xVel = 0;
		yVel = 0;
		d=0;
	}
	public void moveBackNow(){
		xPos-=xVel;
		yPos-=yVel;
	}
	public void moveBackNow (int n){
		for(int i=0;i<n;i++){
			moveBackNow();
		}
		stop();
	}
	public double getX(){
		return xPos;
	}
	public double getXVel(){
		return xVel;
	}
	public double getY(){
		return yPos;
	}
	public double getYVel(){
		return yVel;
	}
	public double getD(){
		return d;
	}
	public void setAngle(int a){
		angle = a;
	}
	public void incAngle(int a){
		angle+=a;
	}
	public int getAng(){
		return angle;
	}
	public void setShape(Shape a){
		spriteShape = a;
	}
	public Shape getShape(){
		return spriteShape;
	}
	public void changeColor(Color newColor){
		color = newColor;
	}
	public boolean intersects(Shape other){			
		Area thisArea = new Area(other);
		thisArea.intersect(new Area(spriteShape));
		return !thisArea.isEmpty();
	}
	public boolean isInside(Point p){
		return this.spriteShape.contains(p);
	}
	
	protected void draw(Graphics gvar){
		Graphics2D g2 = (Graphics2D) gvar;
		
		g2.drawString("Null Sprite!", 100, 100);
	}
}
