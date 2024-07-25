package top.doe.calcite.demo2;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.*;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.tools.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class VolcanoPlannerExample {
    public static void main(String[] args) throws SQLException, SqlParseException, ValidationException, RelConversionException {
        // 创建 Calcite 连接和 schema
        Properties properties = new Properties();
        properties.setProperty("lex", "JAVA");
        CalciteConnection connection = DriverManager.getConnection("jdbc:calcite:", properties).unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = connection.getRootSchema();
        rootSchema.add("hr", new ReflectiveSchema(new HrSchema()));


        // 创建解析器配置
        SqlParser.Config parserConfig = SqlParser.configBuilder()
                .setConformance(SqlConformanceEnum.DEFAULT)
                .setCaseSensitive(false)
                .build();


        // 创建框架配置
        FrameworkConfig config = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema)
                .parserConfig(parserConfig)
                .build();

        // 创建 Planner
        Planner planner1 = Frameworks.getPlanner(config);

        // 解析 SQL
        SqlNode sqlNode = planner1.parse("SELECT empid, name FROM hr.emps WHERE deptno = 10");
        SqlNode validatedSqlNode = planner1.validate(sqlNode);

        // 将 SqlNode 转换为 RelNode
        RelRoot relRoot = planner1.rel(validatedSqlNode);
        RelNode relNode = relRoot.project();

        // 打印原始 RelNode
        System.out.println("原始 RelNode:");
        System.out.println(RelOptUtil.toString(relNode));

        // 创建和设置 VolcanoPlanner
        //1. 初始化 VolcanoPlanner 对象，并添加相应的 Rule
        //VolcanoPlanner planner = new VolcanoPlanner();
        VolcanoPlanner planner = (VolcanoPlanner) relNode.getCluster().getPlanner();
        //planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        //planner.addRelTraitDef(RelDistributionTraitDef.INSTANCE);
        planner.addRelTraitDef(RelCollationTraitDef.INSTANCE);
        // 添加相应的 rule
        planner.addRule(CoreRules.FILTER_INTO_JOIN);
        planner.addRule(CoreRules.PROJECT_MERGE);
        planner.addRule(PruneEmptyRules.PROJECT_INSTANCE);
        // 添加相应的 ConverterRule
        //planner.addRule(EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE);
        //planner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
        //planner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
        //planner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        //planner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        //2. Changes a relational expression to an equivalent one with a different set of traits.
        RelTraitSet desiredTraits =
                relNode.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);

        relNode = planner.changeTraits(relNode, desiredTraits);
        //3. 通过 VolcanoPlanner 的 setRoot 方法注册相应的 RelNode，并进行相应的初始化操作
        planner.setRoot(relNode);
        //4. 通过动态规划算法找到 cost 最小的 plan
        //relNode = planner.findBestExp();

        // 查找最佳表达式
        RelNode bestRelNode = planner.findBestExp();

        // 打印优化后的 RelNode
        System.out.println("优化后的 RelNode:");
        System.out.println(RelOptUtil.toString(bestRelNode));
    }

    // 定义 schema
    public static class HrSchema {
        public final Employee[] emps = {
                new Employee(100, "Bill", 10),
                new Employee(200, "Eric", 20),
                new Employee(150, "Sebastian", 10)
        };
    }

    // 定义 table
    public static class Employee {
        public final int empid;
        public final String name;
        public final int deptno;

        public Employee(int empid, String name, int deptno) {
            this.empid = empid;
            this.name = name;
            this.deptno = deptno;
        }
    }
}
