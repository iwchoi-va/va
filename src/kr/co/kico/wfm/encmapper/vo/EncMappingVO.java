package kr.co.kico.wfm.encmapper.vo;

public class EncMappingVO {
	private String queryId;
	private String columns;
	private String initechPolyColumns;

	public String getQueryId() {
		return this.queryId;
	}
	
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
	public String getColumns() {
		return this.columns;
	}
	
	public void setColumns(String columns) {
		this.columns = columns;
	}
	
	public String getInitechPolyColumns() {
		return this.initechPolyColumns;
	}
	
	public void setInitechPolyColumns(String initechPolyColumns) {
		this.initechPolyColumns = initechPolyColumns;
	}
}
