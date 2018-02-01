
public class Doc_PR_Pair implements Comparable<Doc_PR_Pair>{

	public Doc_PR_Pair(String s, double d){
		setFirst(s);
		setSecond(d);
	}
	
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public double getSecond() {
		return second;
	}
	public void setSecond(double s) {
		this.second = s;
	}

	private String first;   //doc ID
	private double second;  // PageRank for doc
	@Override
	public int compareTo(Doc_PR_Pair otherPair) {
			 
		if(this.second - otherPair.second >= 0)
			return 1;
		else return -1;
	 
	}
}
