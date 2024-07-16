package somebak.parser;

import java.util.ArrayList;
import java.util.List;

public class SqlNode {
    private String type;
    private String value;
    private List<SqlNode> children;

    public SqlNode(String type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(SqlNode child) {
        children.add(child);
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public List<SqlNode> getChildren() {
        return children;
    }
}
