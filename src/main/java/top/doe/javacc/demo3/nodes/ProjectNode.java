package top.doe.javacc.demo3.nodes;

import java.util.ArrayList;
import java.util.List;

public class ProjectNode implements SqlNode{

    SqlKind kind = SqlKind.SELECT;
    List<SqlNode> children = new ArrayList<>();
    List<Column> columnList = new ArrayList<>();


    public List<Column> getColumnList() {
        return columnList;
    }

    public void addColumn(Column column){
        columnList.add(column);
    }


    @Override
    public void addChild(SqlNode childNode) {
        children.add(childNode);
    }

    @Override
    public SqlKind getKind() {

        return kind;
    }

    @Override
    public List<SqlNode> getChildren() {
        return children;
    }


    @Override
    public String toString() {
        return "ProjectNode{" +
                "kind=" + kind +
                ", children_size =" + children.size() +
                ", columnList=" + columnList +
                '}';
    }
}
