package top.doe.calcite.demo3;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;

public class MyCustomOperator extends SqlFunction {
    public MyCustomOperator() {
        super("MY_CUSTOM_FUNC",
                SqlKind.OTHER_FUNCTION,
                ReturnTypes.INTEGER,
                null,
                OperandTypes.NUMERIC,
                SqlFunctionCategory.USER_DEFINED_FUNCTION);
    }
}
