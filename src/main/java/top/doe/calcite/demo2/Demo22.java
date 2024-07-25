package top.doe.calcite.demo2;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.interpreter.Bindables;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

public class Demo22 {
    public static void main(String[] args) throws Exception {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);

        rootSchema.add("hr", new ReflectiveSchema(new VolcanoPlannerExample.HrSchema()));
//        rootSchema.add("my_c",new Demo2.MyCatalogSchema());

        SqlParser.Config parserConfig = SqlParser.config()
                .withCaseSensitive(false);

        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema)
                .parserConfig(parserConfig)
                .build();

        Planner planner0 = Frameworks.getPlanner(frameworkConfig);

        SqlNode sqlNode = planner0.parse("select empid,name,deptno from ( select * from hr.emps where empid>2 ) tmp where deptno>2");



/*        SqlNode sqlNode = planner0.parse(
                "select order_sn,id " +
                        "from (" +
                        "select order_sn,id,address " +
                        "from my_c.my_d.od " +
                        "where id>5) as tmp  " +
                        "where order_sn>'od008' ");*/

        SqlNode validate = planner0.validate(sqlNode);
        RelRoot relRoot = planner0.rel(validate);
        RelNode relNode = relRoot.project();


        System.out.println("优化前-------------");
        System.out.println(relNode.explain());
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
        planner.addRule(EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        //planner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        planner.removeRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        planner.addRule(MyEnumerableFilterRule.DEFAULT_CONFIG.toRule());


        //------------
        for (RelOptRule rule : planner.getRules()) {
            System.out.println(rule);
        }



        //2. Changes a relational expression to an equivalent one with a different set of traits.
        RelTraitSet desiredTraits =
                relNode.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);

        relNode = planner.changeTraits(relNode, desiredTraits);
        //3. 通过 VolcanoPlanner 的 setRoot 方法注册相应的 RelNode，并进行相应的初始化操作
        planner.setRoot(relNode);
        //4. 通过动态规划算法找到 cost 最小的 plan
        // 查找最佳表达式
        RelNode bestRelNode = planner.findBestExp();

        // 打印优化后的 RelNode
        System.out.println("优化后的 RelNode:");
        System.out.println(RelOptUtil.toString(bestRelNode));
    }
}
