package top.doe.calcite.demo2;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableFilter;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.logical.LogicalFilter;

public class MyEnumerableFilterRule extends ConverterRule {
    /** Default configuration. */
    public static final Config DEFAULT_CONFIG = Config.INSTANCE
            .withConversion(LogicalFilter.class, f -> !f.containsOver(),
                    Convention.NONE, EnumerableConvention.INSTANCE,
                    "EnumerableFilterRule")
            .withRuleFactory(MyEnumerableFilterRule::new);

    protected MyEnumerableFilterRule(Config config) {
        super(config);
    }

    @Override public RelNode convert(RelNode rel) {
        final Filter filter = (Filter) rel;
        return new MyEnumerableFilter(rel.getCluster(),
                rel.getTraitSet().replace(EnumerableConvention.INSTANCE),
                convert(filter.getInput(),
                        filter.getInput().getTraitSet()
                                .replace(EnumerableConvention.INSTANCE)),
                filter.getCondition());
    }
}