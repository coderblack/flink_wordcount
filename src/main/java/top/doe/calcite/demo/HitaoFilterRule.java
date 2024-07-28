package top.doe.calcite.demo;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.logical.LogicalFilter;

public class HitaoFilterRule extends ConverterRule{

    public static final ConverterRule.Config DEFAULT_CONFIG = ConverterRule.Config.INSTANCE
            .withConversion(LogicalFilter.class,
                    Convention.NONE, EnumerableConvention.INSTANCE,
                    "HitaoFilterRule")
            .withRuleFactory(HitaoFilterRule::new);

    protected HitaoFilterRule(ConverterRule.Config config) {
        super(config);
    }

    @Override public RelNode convert(RelNode rel) {
        final Filter filter = (Filter) rel;
        return new HitaoFilter(rel.getCluster(),
                rel.getTraitSet().replace(EnumerableConvention.INSTANCE),
                convert(filter.getInput(),
                        filter.getInput().getTraitSet()
                                .replace(EnumerableConvention.INSTANCE)),
                filter.getCondition());
    }

}
