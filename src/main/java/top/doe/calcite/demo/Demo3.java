package top.doe.calcite.demo;


import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.config.CalciteSystemProperty;
import org.apache.calcite.interpreter.Bindables;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.stream.StreamRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

/**
 * @Author: deep as the sea
 * @Site: <a href="www.51doit.com">多易教育</a>
 * @QQ: 657270652
 * @Date: 2024/7/27
 * @Tips: 学大数据，到多易教育
 * @Desc: VolcanoPlanner使用基本示例
 **/
public class Demo3 {

    public static void main(String[] args) throws Exception {

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


        //
        VolcanoPlanner volcanoPlanner = (VolcanoPlanner) relNode.getCluster().getPlanner();
        // 如果我们优化之前的Convention=NONE，优化的目标结果依然是Convention=NONE，那么就要设置如下参数来正确计算成本
        volcanoPlanner.setNoneConventionHasInfiniteCost(false);


        // 由于VolcanoPlanner在创建之后就被添加了一系列核心规则，不便于分析测试，所以在此把自动添加的规则移除
        removeAllInitialRulls(volcanoPlanner);


        System.out.println("-------------------------");
        //volcanoPlanner.addRule(CoreRules.PROJECT_MERGE);
        //volcanoPlanner.addRule(CoreRules.FILTER_MERGE);
        volcanoPlanner.addRule(CoreRules.FILTER_PROJECT_TRANSPOSE);

        for (RelOptRule rule : volcanoPlanner.getRules()) {
            System.out.println(rule);
        }

        /* *
         *  1. 注册输入的relNode树：为树中的每一个节点，生成对应的 RelSet（"等价集"）和RelSubSet（"等价子集"）  结构
         *  2. 进行初始的规则匹配，如果有命中的规则和子树结构，则生成一个match任务（对象）放到一个任务队列里
         */
        volcanoPlanner.setRoot(relNode);
        RelNode bestExp = volcanoPlanner.findBestExp();


        System.out.println("-----优化后---------");
        System.out.println(bestExp.explain());


    }

    public static void removeAllInitialRulls(VolcanoPlanner volcanoPlanner) {
        PubRules.ABSTRACT_RELATIONAL_RULES.forEach(volcanoPlanner::removeRule);
        volcanoPlanner.removeRule(CoreRules.JOIN_ASSOCIATE);
        PubRules.ABSTRACT_RULES.forEach(volcanoPlanner::removeRule);
        PubRules.BASE_RULES.forEach(volcanoPlanner::removeRule);

        PubRules.MATERIALIZATION_RULES.forEach(volcanoPlanner::removeRule);

        for (RelOptRule rule : Bindables.RULES) {
            volcanoPlanner.removeRule(rule);
        }

        volcanoPlanner.removeRule(Bindables.BINDABLE_TABLE_SCAN_RULE);
        volcanoPlanner.removeRule(CoreRules.PROJECT_TABLE_SCAN);
        volcanoPlanner.removeRule(CoreRules.PROJECT_INTERPRETER_TABLE_SCAN);

        EnumerableRules.ENUMERABLE_RULES.forEach(volcanoPlanner::removeRule);
        volcanoPlanner.removeRule(EnumerableRules.TO_INTERPRETER);

        volcanoPlanner.removeRule(EnumerableRules.TO_BINDABLE);

        for (RelOptRule rule : StreamRules.RULES) {
            volcanoPlanner.removeRule(rule);
        }
        volcanoPlanner.removeRule(CoreRules.FILTER_REDUCE_EXPRESSIONS);
        volcanoPlanner.removeRule(CoreRules.FILTER_PROJECT_TRANSPOSE);
    }
}
