package top.doe.calcite.demo;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

public class Demo4 {

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


        VolcanoPlanner optPlanner = (VolcanoPlanner) relNode.getCluster().getPlanner();
        //optPlanner.setNoneConventionHasInfiniteCost(false);

        // 移除初始加入的规则
        Demo3.removeAllInitialRulls(optPlanner);

        // 加入本案例所需要的规则
        optPlanner.addRule(CoreRules.FILTER_MERGE);
        optPlanner.addRule(CoreRules.PROJECT_MERGE);
        optPlanner.addRule(CoreRules.FILTER_PROJECT_TRANSPOSE);

        optPlanner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        optPlanner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        optPlanner.addRule(EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE);


        // 添加自己的物理节点转化规则
        optPlanner.addRule(HitaoFilterRule.DEFAULT_CONFIG.toRule());



        System.out.println("----原始计划树中节点的特质----");

        for (RelTrait relTrait : relNode.getTraitSet()) {
            System.out.println(relTrait);
        }

        RelTraitSet desiredTraitSet = relNode.getTraitSet().replace(EnumerableConvention.INSTANCE);
        //RelTraitSet desiredTraitSet = relNode.getCluster().traitSetOf(EnumerableConvention.INSTANCE);

        System.out.println("----做了特质替换后，新的特质集----");
        for (RelTrait relTrait : desiredTraitSet) {
            System.out.println(relTrait);
        }


        RelNode newRelNode = optPlanner.changeTraits(relNode, desiredTraitSet);

        optPlanner.setRoot(newRelNode);
        RelNode bestExp = optPlanner.findBestExp();

        System.out.println("-----优化后的结果树---------");
        System.out.println(bestExp.explain());


    }

}
