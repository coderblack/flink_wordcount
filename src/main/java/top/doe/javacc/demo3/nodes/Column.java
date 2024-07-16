package top.doe.javacc.demo3.nodes;

public class Column {

    String filedName;
    String tableName;

    public Column() {
    }

    public Column(String filedName, String tableName) {
        this.filedName = filedName;
        this.tableName = tableName;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "Column{" +
                "filedName='" + filedName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
