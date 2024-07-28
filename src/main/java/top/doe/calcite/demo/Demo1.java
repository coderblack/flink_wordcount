package top.doe.calcite.demo;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.externalize.RelWriterImpl;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.RelRunners;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Demo1 {

    public static void main(String[] args) throws Exception {

        // Build the schema
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        ReflectiveSchema schema = new ReflectiveSchema(new HrScheamBean());
        SchemaPlus hr = rootSchema.add("HR", schema);

        // Get a non-sensitive parser
        SqlParser.Config insensitiveParser = SqlParser.configBuilder()
                .setCaseSensitive(false)
                .build();

        // Build a global configuration object
        FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(insensitiveParser)
                .defaultSchema(hr)
                .build();

        // Get the planner tool
        Planner planner = Frameworks.getPlanner(config);

        // Parse the sql to a tree
        SqlNode sqlNode = planner.parse(
                "select depts.name, count(emps.empid) " +
                        "from emps inner join depts on emps.deptno = depts.deptno " +
                        "where emps.deptno >3  " +
                        "group by depts.deptno, depts.name "
                        //+"order by depts.name"
        );



        // Print it
        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
        System.out.println("------------------------");

        // Validate the tree
        SqlNode sqlNodeValidated = planner.validate(sqlNode);

        // Convert the sql tree to a relation expression
        RelRoot relRoot = planner.rel(sqlNodeValidated);

        // Explain, print the relational expression
        RelNode relNode = relRoot.project();
        final RelWriter relWriter = new RelWriterImpl(new PrintWriter(System.out), SqlExplainLevel.EXPPLAN_ATTRIBUTES, false);
        relNode.explain(relWriter);

        System.out.println("------优化分割线---------");

        //add HepProgram
        HepProgramBuilder builder = new HepProgramBuilder();
        builder.addRuleInstance(CoreRules.FILTER_INTO_JOIN); //note: 添加 rule
        HepPlanner hepPlanner = new HepPlanner(builder.build());


        hepPlanner.setRoot(relNode);
        relNode = hepPlanner.findBestExp();

        relNode.explain(relWriter);

        // Run it
        PreparedStatement run = RelRunners.run(relNode);
        ResultSet resultSet = run.executeQuery();

        System.out.println("------------------");

        // Print it
        System.out.println("Result:");
        while (resultSet.next()) {
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                System.out.print(resultSet.getObject(i)+",");
            }
            System.out.println();
        }

    }
}
