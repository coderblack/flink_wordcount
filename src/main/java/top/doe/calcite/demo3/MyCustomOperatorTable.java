package top.doe.calcite.demo3;

import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.util.ListSqlOperatorTable;
import org.apache.calcite.sql.validate.SqlNameMatcher;

import java.util.List;

public class MyCustomOperatorTable extends ListSqlOperatorTable {
    public MyCustomOperatorTable() {
        super();
        add(new MyCustomOperator());
    }

    @Override
    public void lookupOperatorOverloads(SqlIdentifier opName, SqlFunctionCategory category, SqlSyntax syntax, List<SqlOperator> operatorList, SqlNameMatcher nameMatcher) {
        super.lookupOperatorOverloads(opName, category, syntax, operatorList, nameMatcher);
    }
}

