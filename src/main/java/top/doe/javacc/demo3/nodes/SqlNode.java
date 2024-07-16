package top.doe.javacc.demo3.nodes;

import java.util.List;

public interface SqlNode {

    void addChild(SqlNode childNode);
    SqlKind getKind();
    List<SqlNode> getChildren();

}
