package smallwheel.mybro.domain;

public class ColumnInfo {
	private String name;
	private String dataType;
	private boolean isPrivateKey;
	
	public ColumnInfo() {
		super();
	}

	public ColumnInfo(String name, String dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
	}
	
	public ColumnInfo(String name, String dataType, boolean isPrivateKey) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.isPrivateKey = isPrivateKey;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isPrivateKey() {
		return isPrivateKey;
	}

	public void setPrivateKey(boolean isPrivateKey) {
		this.isPrivateKey = isPrivateKey;
	}
}
