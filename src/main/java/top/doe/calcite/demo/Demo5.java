package top.doe.calcite.demo;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql2rel.ReflectiveConvertletTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * @Author: deep as the sea
 * @Site: <a href="www.51doit.com">多易教育</a>
 * @QQ: 657270652
 * @Date: 2024/7/28
 * @Tips: 学大数据，到多易教育
 * @Desc:
 *  自定义元数据管理系统
 **/
public class Demo5 {
    public static void main(String[] args) throws Exception {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);

        SchemaPlus hitao = rootSchema.add("hitao_catalog",new HitaoCatalog());

        SqlParser.Config parserConfig = SqlParser.configBuilder().setCaseSensitive(false).build();

        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema)
                .parserConfig(parserConfig)
                .build();


        Planner planner = Frameworks.getPlanner(frameworkConfig);

        // 解析得到抽象语法树  AST
        SqlNode sqlNode = planner.parse("select a.id,gender,address,nickname from hitao_catalog.deep_sea_db.extra_info  a join hitao_catalog.deep_sea_db.base_info b on a.id = b.id");

        // 做sql校验
        SqlNode validatedSqlNode = planner.validate(sqlNode);

        // 把sqlnode转成逻辑执行计划（在calcite中所谓逻辑执行计划就是一个“关系代数”树 RelNode
        RelRoot relRoot = planner.rel(validatedSqlNode);

        RelNode relNode = relRoot.project();

        System.out.println(relNode.explain());

    }

    /**
     * @Author: deep as the sea
     * @Site: <a href="www.51doit.com">多易教育</a>
     * @QQ: 657270652
     * @Date: 2024/7/28
     * @Tips: 学大数据，到多易教育
     * @Desc:
     *    Catalog级别的schema
     **/
    public static class HitaoCatalog implements Schema{

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
            return null;
        }

        @Override
        public Set<String> getTypeNames() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Function> getFunctions(String name) {
            return Collections.emptySet();
        }

        @Override
        public Set<String> getFunctionNames() {
            return Collections.emptySet();
        }

        @Override
        public @Nullable Schema getSubSchema(String name) {

            if("deep_sea_db".equals(name)){
               return new DeepSeaDatabase();
            }

            return null;
        }

        @Override
        public Set<String> getSubSchemaNames() {
            return new HashSet<>(Collections.singletonList("deep_sea_db"));
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

    public static class DeepSeaDatabase implements Schema{

        @Override
        public @Nullable Table getTable(String name) {

            AbstractTable table1 = new AbstractTable() {
                @Override
                public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                    return typeFactory.builder()
                            .add("id", SqlTypeName.BIGINT)
                            .add("name",SqlTypeName.VARCHAR)
                            .add("age",SqlTypeName.INTEGER)
                            .add("gender",SqlTypeName.VARCHAR)
                            .build();
                }
            };

            AbstractTable table2 = new AbstractTable() {
                @Override
                public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                    return typeFactory.builder()
                            .add("id", SqlTypeName.BIGINT)
                            .add("address",SqlTypeName.VARCHAR)
                            .add("salary",SqlTypeName.INTEGER)
                            .add("nickname",SqlTypeName.VARCHAR)
                            .build();
                }
            };

            if("base_info".equals(name)){
                return table1;
            }else if("extra_info".equals(name)){
                return table2;
            }
            return null;
        }

        @Override
        public Set<String> getTableNames() {
            return new HashSet<>(Arrays.asList("base_info","extra_info"));
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
            return Collections.emptySet();
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


}
