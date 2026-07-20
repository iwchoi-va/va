package xwing.export;

public class Dataset {
	private static String[] column;
	private static String[] row;
	private static String[][] parseRow;
	
	public Dataset(String column, String row) {
		// TODO Auto-generated constructor stub
		this.column = column.split("`",-1);//column.split("]");
		this.row = row.split("`",-1);//row.split("]");
		if( this.row.length == 0 ){
			parseRow = null;
		}else {
			parseRow = new String[(this.row.length /this.column.length)][this.column.length];
			parseRows();
		}
		
	}
	
	private void parseRows(){
		int curRow = 0, curCol = 0,
			colSize = this.column.length;
		for(int i=0; i < row.length ; i++){
			if( curCol == colSize){
				curRow ++;
				curCol = 0;
			}
			parseRow[curRow][curCol] = row[i];
			curCol ++;
		}
	}
	
	public int size(){
		if( parseRow.length == 0 ) return 0;
		else return parseRow.length;
	}
	
	public String getValue(int rowIdx, String column){
		int colIdx = -1;
		for(int i=0; i < this.column.length; i++){
			String colValue = this.column[i];
			if( colValue.equals(column)){
				colIdx = i;
				break;
			}
		}
		return getValue(rowIdx, colIdx);
	}
	public String getValue(int rowIdx, int colIdx){
		if( colIdx == -1 || this.parseRow.length <= rowIdx) return "";
		return  this.parseRow[rowIdx][colIdx];
	}
}
