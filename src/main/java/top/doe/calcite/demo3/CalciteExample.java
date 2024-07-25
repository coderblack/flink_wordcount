package top.doe.calcite.demo3;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class CalciteExample {
    public static void main(String[] args) throws Exception {
        // 创建 Calcite 连接
        Properties info = new Properties();
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);

        // 创建框架配置，并设置自定义的 SqlOperatorTable
        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(calciteConnection.getRootSchema())
                .operatorTable(new MyCustomOperatorTable())
                .build();

        // 创建 Planner
        Planner planner = Frameworks.getPlanner(frameworkConfig);

        // 解析和验证 SQL 查询
        String sql = "SELECT MY_CUSTOM_FUNC(1)";
        SqlNode sqlNode = planner.parse(sql);
        SqlNode validatedSqlNode = planner.validate(sqlNode);

        // 打印验证后的 SQL 查询
        System.out.println(validatedSqlNode.toString());
    }
}
