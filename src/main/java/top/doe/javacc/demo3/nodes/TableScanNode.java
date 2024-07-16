package top.doe.javacc.demo3.nodes;

import java.util.ArrayList;
import java.util.List;

public class TableScanNode implements SqlNode{


    SqlKind kind = SqlKind.TABLESCAN;
    List<SqlNode> children = new ArrayList<>();

    String tableName;

    public TableScanNode(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void addChild(SqlNode childNode) {
        throw new RuntimeException("不支持给表扫描节点添加子节点");
    }

    @Override
    public SqlKind getKind() {
        return kind;
    }

    @Override
    public List<SqlNode> getChildren() {
        return children;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
