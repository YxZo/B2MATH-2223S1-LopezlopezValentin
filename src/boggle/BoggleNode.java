package boggle;

public class BoggleNode {
	 private int row;
	 private int col;
	 private char value;
	 private boolean isVisited;

	 @Deprecated
     public BoggleNode(int row, int col, char value) {
         this.row = row;
         this.col = col;
         this.value = value;
     }
     
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
