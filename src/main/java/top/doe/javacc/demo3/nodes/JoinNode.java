package top.doe.javacc.demo3.nodes;

import java.util.ArrayList;
import java.util.List;

public class JoinNode implements SqlNode{


    SqlKind kind = SqlKind.JOIN;
    Condition condition;
    List<SqlNode> children = new ArrayList<>();


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
        return this.children;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
