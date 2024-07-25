package top.doe.calcite.demo2;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.rules.TransformationRule;
import org.apache.calcite.tools.RelBuilder;
import org.immutables.value.Value;


@Value.Enclosing
public class MyOptRule extends RelRule<MyOptRule.Config> implements TransformationRule {

    MyOptRule(){
        super(Config.DEFAULT);
    }

    protected MyOptRule(MyOptRule.Config config) {
        super(config);
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


    @Value.Immutable
    public interface Config extends RelRule.Config{
//
//        MyOptRule.Config DEFAULT = ImmutableMyOptRuleModifyed.Config.of()
//                .withOperandFor(Filter.class, Project.class);

        Config DEFAULT = ImmutableMyOptRule.Config.builder().build().withOperandFor(Filter.class, Project.class);

        @Override default MyOptRule toRule() {
            return new MyOptRule(this);
        }

        default Config withOperandFor(Class<? extends Filter> filterClass,Class<? extends Project> projectClass) {
            return withOperandSupplier(b0 ->
                    b0.operand(filterClass).oneInput(b1 ->
                            b1.operand(projectClass).anyInputs()))
                    .as(MyOptRule.Config.class);
        }

    }

}
