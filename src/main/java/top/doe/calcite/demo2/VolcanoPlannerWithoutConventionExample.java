package top.doe.calcite.demo2;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeFactoryImpl;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelBuilderFactory;
import top.doe.calcite.demo1.HrScheamBean;

public class VolcanoPlannerWithoutConventionExample {
    public static void main(String[] args) throws Exception {
        // Define schema
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        ReflectiveSchema schema = new ReflectiveSchema(new HrScheamBean());
        SchemaPlus hr = rootSchema.add("HR", schema);

        // Framework configuration
        SqlParser.Config insensitiveParser = SqlParser.configBuilder()
                .setCaseSensitive(false)
                .build();

        // Build a global configuration object
        FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(insensitiveParser)
                .defaultSchema(hr)
                .build();

        // Create planner
        Planner planner = Frameworks.getPlanner(config);

        // Parse SQL
        String sql = "SELECT * FROM emps";
        SqlNode parsed = planner.parse(sql);
        SqlNode validated = planner.validate(parsed);
        RelRoot root = planner.rel(validated);
        RelNode logicalPlan = root.rel;

        // Create and configure VolcanoPlanner
        VolcanoPlanner volcanoPlanner = (VolcanoPlanner) logicalPlan.getCluster().getPlanner();
        volcanoPlanner.addRelTraitDef(ConventionTraitDef.INSTANCE); // This line is commented out
        volcanoPlanner.addRelTraitDef(RelCollationTraitDef.INSTANCE);

        // Add rules
        volcanoPlanner.addRule(CoreRules.FILTER_INTO_JOIN);
        volcanoPlanner.addRule(CoreRules.PROJECT_MERGE);
        volcanoPlanner.addRule(CoreRules.SORT_PROJECT_TRANSPOSE);

        // Register the logical plan
        volcanoPlanner.setRoot(logicalPlan);

        // Set desired traits and convert the logical plan to physical plan
        RelTraitSet desiredTraits = logicalPlan.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);
        RelNode newRelNode = volcanoPlanner.changeTraits(logicalPlan, desiredTraits);
        volcanoPlanner.setRoot(newRelNode);

        // Find the best expression
        RelNode bestExp = volcanoPlanner.findBestExp();

        // Print the best plan
        System.out.println(RelOptUtil.toString(bestExp));
    }
}
