
public class Pair {
	public Pair(int i, double s){
		setFirst(i);
		setSecond(s);
	}
	
	public int getFirst() {
		return first;
	}
	public void setFirst(int first) {
		this.first = first;
	}
	public double getSecond() {
		return second;
	}
	public void setSecond(double s) {
		this.second = s;
	}

	private int first;   //node number
	private double second;  // edge weight

}
