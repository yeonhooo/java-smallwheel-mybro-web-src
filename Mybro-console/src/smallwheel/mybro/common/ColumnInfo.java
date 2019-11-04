package smallwheel.mybro.common;

public class ColumnInfo {
    private String name;
    private String type;

    public ColumnInfo() {
        super();
    }

    public ColumnInfo(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
