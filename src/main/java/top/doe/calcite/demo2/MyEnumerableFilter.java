package top.doe.calcite.demo2;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableFilter;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.*;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.metadata.RelMdCollation;
import org.apache.calcite.rel.metadata.RelMdDistribution;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.util.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class MyEnumerableFilter extends Filter
        implements EnumerableRel {
    /** Creates an EnumerableFilter.
     *
     * <p>Use {@link #create} unless you know what you're doing. */
    public MyEnumerableFilter(
            RelOptCluster cluster,
            RelTraitSet traitSet,
            RelNode child,
            RexNode condition) {
        super(cluster, traitSet, child, condition);
        assert getConvention() instanceof EnumerableConvention;
    }

    /** Creates an EnumerableFilter. */
    public static MyEnumerableFilter create(final RelNode input,
                                          RexNode condition) {
        final RelOptCluster cluster = input.getCluster();
        final RelMetadataQuery mq = cluster.getMetadataQuery();
        final RelTraitSet traitSet =
                cluster.traitSetOf(EnumerableConvention.INSTANCE)
                        .replaceIfs(
                                RelCollationTraitDef.INSTANCE,
                                () -> RelMdCollation.filter(mq, input))
                        .replaceIf(RelDistributionTraitDef.INSTANCE,
                                () -> RelMdDistribution.filter(mq, input));
        return new MyEnumerableFilter(cluster, traitSet, input, condition);
    }

    @Override public MyEnumerableFilter copy(RelTraitSet traitSet, RelNode input,
                                           RexNode condition) {
        return new MyEnumerableFilter(getCluster(), traitSet, input, condition);
    }

    @Override public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        // EnumerableCalc is always better
        throw new UnsupportedOperationException();
    }

    @Override public @Nullable Pair<RelTraitSet, List<RelTraitSet>> passThroughTraits(
            RelTraitSet required) {
        RelCollation collation = required.getCollation();
        if (collation == null || collation == RelCollations.EMPTY) {
            return null;
        }
        RelTraitSet traits = traitSet.replace(collation);
        return Pair.of(traits, ImmutableList.of(traits));
    }

    @Override public @Nullable Pair<RelTraitSet, List<RelTraitSet>> deriveTraits(
            final RelTraitSet childTraits, final int childId) {
        RelCollation collation = childTraits.getCollation();
        if (collation == null || collation == RelCollations.EMPTY) {
            return null;
        }
        RelTraitSet traits = traitSet.replace(collation);
        return Pair.of(traits, ImmutableList.of(traits));
    }

    @Override public @Nullable RelOptCost computeSelfCost(RelOptPlanner planner,
                                                          RelMetadataQuery mq) {
        double dRows = 0;
        double dCpu = 0;
        double dIo = 0;
        return planner.getCostFactory().makeCost(dRows, dCpu, dIo);
    }

}
