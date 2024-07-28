package top.doe.calcite.demo;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.rules.TransformationRule;
import org.apache.calcite.tools.RelBuilder;
import org.immutables.value.Value;


@Value.Enclosing
public class MyFilterProjectTranspose extends RelRule<MyFilterProjectTranspose.Config>
        implements TransformationRule {

    protected MyFilterProjectTranspose(Config config) {
        super(config);
    }


    @Override
    public void onMatch(RelOptRuleCall call) {

        Filter filterNode = (Filter) call.rels[0];
        Project projectNode = (Project) call.rels[1];

        RelBuilder builder = call.builder();

        // 把命中子树的子节点压栈
        builder.push(projectNode.getInput());

        // 构造一个新的filter，保留之前filter的条件
        builder.filter(filterNode.getCondition());

        // 构造一个新的project，保留之前的字段
        builder.project(projectNode.getProjects(),projectNode.getRowType().getFieldNames());

        RelNode newNode = builder.build();

        call.transformTo(newNode);

        System.out.println("自定义规则成功被调用................");

    }

    @Value.Immutable
    public interface Config extends RelRule.Config {

        MyFilterProjectTranspose.Config DEFAULT = ImmutableMyFilterProjectTranspose.Config.builder()
                .build()
                .withOperandFor(Filter.class, Project.class);

        @Override default MyFilterProjectTranspose toRule() {
            return new MyFilterProjectTranspose(this);
        }

        default MyFilterProjectTranspose.Config withOperandFor(Class<? extends Filter> filterClass,
                                                          Class<? extends Project> projectClass) {
            return withOperandSupplier(b0 ->
                    b0.operand(filterClass).oneInput(b1 ->
                            b1.operand(projectClass).anyInputs()))
                    .as(MyFilterProjectTranspose.Config.class);
        }
    }
}
