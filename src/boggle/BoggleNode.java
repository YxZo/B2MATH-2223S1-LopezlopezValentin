package boggle;

public class BoggleNode {
	 private char value;
	 private boolean isVisited;
     
     public BoggleNode(char value) {
         this.value = value;
     }

     @Override
     public String toString() {
         return String.valueOf(value);
     }

	public char getValue() {
		return value;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
}