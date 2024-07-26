package top.doe.calcite.demo1;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.hep.HepMatchOrder;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.*;

public class Demo2 {
    public static void main(String[] args) throws SqlParseException, RelConversionException, ValidationException {


        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        SchemaPlus hr = rootSchema.add("hr", new ReflectiveSchema(new HrScheamBean()));

        SqlParser.Config parserConfig = SqlParser.configBuilder().setCaseSensitive(false).build();

        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                //.defaultSchema(rootSchema)  // from hr.emps
                .defaultSchema(hr)  // from emps
                .parserConfig(parserConfig)
                .build();


        Planner planner = Frameworks.getPlanner(frameworkConfig);

        // 解析得到抽象语法树  AST
        SqlNode sqlNode = planner.parse("select empid,upper(name) as name,salary+100 from (select * from emps where deptno>2 ) tmp where empid>5");

        // 做sql校验
        SqlNode validatedSqlNode = planner.validate(sqlNode);

        // 把sqlnode转成逻辑执行计划（在calcite中所谓逻辑执行计划就是一个“关系代数”树 RelNode
        RelRoot relRoot = planner.rel(validatedSqlNode);

        RelNode relNode = relRoot.project();

        System.out.println("----优化前-------");
        System.out.println(relNode.explain());


        // calcite中提供了两种优化器
        // HepPlanner（基于规则的优化器） VolcanoPlanner（基于代价的优化器）

        HepProgram hepProgram = new HepProgramBuilder()
                .addGroupBegin()
                .addRuleInstance(CoreRules.PROJECT_MERGE)
                .addRuleInstance(CoreRules.FILTER_MERGE)
                //.addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE)
                // 添加自定义优化规则
                .addRuleInstance(MyFilterProjectTranspose.Config.DEFAULT.toRule())
                .addGroupEnd()
                .addMatchOrder(HepMatchOrder.BOTTOM_UP)
                .build();

        HepPlanner hepPlanner = new HepPlanner(hepProgram);

        // 把原始的逻辑计划树，注册到优化器
        hepPlanner.setRoot(relNode);  // 内部就是对relNode按深度遍历，把每一个节点转成relVetex，并在父子之间构建边，来形成图Graph

        RelNode bestExp = hepPlanner.findBestExp();


        System.out.println("----优化后-------");
        System.out.println(bestExp.explain());



        System.out.println();
    }
}
