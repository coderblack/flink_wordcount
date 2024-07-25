package top.doe.calcite.demo2;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.interpreter.Bindables;
import org.apache.calcite.linq4j.*;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.*;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.TransformationRule;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class Demo2 {
    public static void main(String[] args) throws SqlParseException, ValidationException, RelConversionException {

        SchemaPlus rootSchema = Frameworks.createRootSchema(true);

        rootSchema.add("my_c",new MyCatalogSchema());

        SqlParser.Config parserConfig = SqlParser.config()
                .withCaseSensitive(false);

        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema)
                .parserConfig(parserConfig)
                .build();

        Planner planner = Frameworks.getPlanner(frameworkConfig);


        SqlNode sqlNode = planner.parse(
                "select order_sn,id " +
                        "from (" +
                        "select order_sn,id,address " +
                        "from my_c.my_d.od " +
                        "where id>5) as tmp  " +
                        "where order_sn>'od008' ");
        SqlNode validate = planner.validate(sqlNode);
        RelRoot relRoot = planner.rel(validate);
        RelNode relNode = relRoot.project();


        System.out.println("优化前-------------");
        System.out.println(relNode.explain());


/*        HepProgram hepProgram = new HepProgramBuilder()
                .addConverters(true)
                .addRuleInstance(CoreRules.FILTER_MERGE)
                //.addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE)
                .addRuleInstance(CoreRules.PROJECT_MERGE)
                .addRuleInstance(new MyOptRule())
                .build();
        HepPlanner hepPlanner = new HepPlanner(hepProgram);
        hepPlanner.setRoot(relNode);
        RelNode bestExp = hepPlanner.findBestExp();
        System.out.println("hep优化后-------------");
        System.out.println(bestExp.explain());*/

        VolcanoPlanner volcanoPlanner = (VolcanoPlanner) relNode.getCluster().getPlanner();
        volcanoPlanner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        volcanoPlanner.addRelTraitDef(RelCollationTraitDef.INSTANCE);

        RelOptCluster relOptCluster = RelOptCluster.create(volcanoPlanner, relNode.getCluster().getRexBuilder());
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_JOIN_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_CORRELATE_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_CALC_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_AGGREGATE_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_LIMIT_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_COLLECT_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_UNCOLLECT_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_MERGE_UNION_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_UNION_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_REPEAT_UNION_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_TABLE_SPOOL_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_INTERSECT_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_MINUS_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_TABLE_MODIFICATION_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_WINDOW_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_TABLE_FUNCTION_SCAN_RULE);
        volcanoPlanner.addRule(EnumerableRules.ENUMERABLE_MATCH_RULE);
        volcanoPlanner.addRule(EnumerableRules.TO_INTERPRETER);
        volcanoPlanner.addRule(CoreRules.FILTER_MERGE);
        volcanoPlanner.addRule(CoreRules.PROJECT_MERGE);
        volcanoPlanner.addRule(CoreRules.PROJECT_TABLE_SCAN);
        volcanoPlanner.addRule(CoreRules.PROJECT_INTERPRETER_TABLE_SCAN);
        volcanoPlanner.addRule(Bindables.BindableTableScanRule.Config.DEFAULT.toRule());


        for (RelOptRule rule : volcanoPlanner.getRules()) {
            System.out.println(rule);
        }




        RelTraitSet desired = relNode.getTraitSet().replace(EnumerableConvention.INSTANCE);
        RelNode newRelNode = volcanoPlanner.changeTraits(relNode, desired);

        volcanoPlanner.setRoot(newRelNode);
        RelNode bestExp1 = volcanoPlanner.findBestExp();

        System.out.println("volcano优化后-------------");
        System.out.println(bestExp1.explain());


    }


    public static class Order{
        public String order_sn;
        public int id;
        public String address;

        public Order(String order_sn, int id, String address) {
            this.order_sn = order_sn;
            this.id = id;
            this.address = address;
        }
    }

    public static class Orders{
        public Order[] orders = { new Order("o1",1,"beijing"),new Order("o2",2,"beijing")  };
    }

    public static class MyCostFactory implements RelOptCostFactory{

        @Override
        public RelOptCost makeCost(double rowCount, double cpu, double io) {

            return new RelOptCostImpl(rowCount*cpu*io);
        }

        @Override
        public RelOptCost makeHugeCost() {
            return new RelOptCostImpl(10000);
        }

        @Override
        public RelOptCost makeInfiniteCost() {
            return new RelOptCostImpl(Double.MAX_VALUE);
        }

        @Override
        public RelOptCost makeTinyCost() {
            return new RelOptCostImpl(2);
        }

        @Override
        public RelOptCost makeZeroCost() {
            return new RelOptCostImpl(0);
        }
    }


    public static class MyCatalogSchema implements  Schema {

        @Override
        public @Nullable Table getTable(String name) {

            return null;
        }

        @Override
        public Set<String> getTableNames() {

            return Collections.emptySet();
        }

        @Override
        public @Nullable RelProtoDataType getType(String name) {
            return (factory)-> factory.createSqlType(SqlTypeName.VARCHAR);
        }

        @Override
        public Set<String> getTypeNames() {

            return Collections.emptySet();
        }

        @Override
        public Collection<Function> getFunctions(String name) {
            return Collections.emptyList();
        }

        @Override
        public Set<String> getFunctionNames() {
            return Collections.emptySet();
        }

        @Override
        public @Nullable Schema getSubSchema(String name) {
            MyDatabaseSchema myDatabaseSchema;
            if(name.equals("my_d")) {
                myDatabaseSchema = new MyDatabaseSchema();
            }else{
                System.out.println("居然调用了别的database");
                myDatabaseSchema=null;
            }
            return myDatabaseSchema;
        }

        @Override
        public Set<String> getSubSchemaNames() {
            HashSet<String> objects = new HashSet<>();
            objects.add("my_d");
            return  objects;
        }

        @Override
        public Expression getExpression(@Nullable SchemaPlus parentSchema, String name) {
            return null;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Schema snapshot(SchemaVersion version) {
            return null;
        }
    }


    public static class MyDatabaseSchema implements Schema{

        @Override
        public @Nullable Table getTable(String name) {

            Table table;
            if(name.equals("od") || name.equals("OD")) {
                /*table = new AbstractTable() {
                    @Override
                    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                        RelDataType build = typeFactory.builder()
                                .add("order_sn", SqlTypeName.VARCHAR)
                                .add("id", SqlTypeName.INTEGER)
                                .add("address", SqlTypeName.VARCHAR)
                                .build();

                        return build;
                    }
                };*/

                Object[][] data = {};
                table = new SimpleScannableTable(data);
            }else if(name.equals("member")){
                table = new AbstractTable() {
                    @Override
                    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                        RelDataType build = typeFactory.builder()
                                .add("order_sn", SqlTypeName.VARCHAR)
                                .add("id", SqlTypeName.VARCHAR)
                                .add("address", SqlTypeName.VARCHAR)
                                .build();

                        return build;
                    }
                };
            }else{
                System.out.println("居然在找别的表: " + name);
                table = null;
            }

            return table;
        }

        @Override
        public Set<String> getTableNames() {
            HashSet<String> tableNames = new HashSet<>();
            tableNames.add("od");
            tableNames.add("OD");
            tableNames.add("member");
            tableNames.add("MEMBER");
            return  tableNames;
        }

        @Override
        public @Nullable RelProtoDataType getType(String name) {
            return null;
        }

        @Override
        public Set<String> getTypeNames() {

            return Collections.emptySet();
        }

        @Override
        public Collection<Function> getFunctions(String name) {
            return Collections.emptyList();
        }

        @Override
        public Set<String> getFunctionNames() {
            return Collections.emptySet();
        }


        @Override
        public @Nullable Schema getSubSchema(String name) {
            return null;
        }

        @Override
        public Set<String> getSubSchemaNames() {
            return Collections.emptySet();
        }

        @Override
        public Expression getExpression(@Nullable SchemaPlus parentSchema, String name) {
            return null;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Schema snapshot(SchemaVersion version) {
            return null;
        }
    }

    public static class MyOptimizeRule extends RelOptRule implements TransformationRule{

        protected MyOptimizeRule() {
            super(operand(Filter.class,operand(Project.class,any())),"myOptRule");
        }

        @Override
        public void onMatch(RelOptRuleCall call) {

            Filter filter = call.rel(0);
            Project project = call.rel(1);

            RelBuilder builder = call.builder();
            builder.push(project.getInput());

            builder.filter(filter.getCondition());
            builder.project(project.getProjects(),project.getRowType().getFieldNames());

            RelNode newNode = builder.build();

            call.transformTo(newNode);


        }
    }

    public static class SimpleScannableTable extends AbstractTable implements ScannableTable {
        private final Object[][] data;

        public SimpleScannableTable(Object[][] data) {
            this.data = data;
        }

        @Override
        public Enumerable<Object[]> scan(DataContext root) {
            return Linq4j.asEnumerable(data);
        }

        @Override
        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder()
                    .add("order_sn", SqlTypeName.VARCHAR)
                    .add("id", SqlTypeName.VARCHAR)
                    .add("address", SqlTypeName.VARCHAR)
                    .build();
        }
    }

}
