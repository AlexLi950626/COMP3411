
public class BountyPoint {
	private int x;
	private int y;
	
	public BountyPoint(int _x,int  _y){
		x = _x;
		y = _y;
	}
	
	public void setX(int _x){
		x = _x;
	}
	public int getX(){
		return x;
	}
	
	public void setY(int _y){
		y = _y;
	}
	
	public void setPoint(int _x, int _y){
		x = _x;
		y = _y;
	}
	
	public int getY(){
		return y;
	}
	
	public boolean equals(BountyPoint other) {
		if((this.getX() == other.getX()) && (this.getY() == other.getY())){
			return true;
		}
		return false;
	}
	
	@Override
	public BountyPoint clone(){
		return new BountyPoint(x, y);
	}
	
	@Override
	public String toString(){
		return "(" + x + "," + y + ")";
	}
}
