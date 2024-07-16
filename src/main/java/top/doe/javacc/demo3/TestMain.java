package top.doe.javacc.demo3;

import top.doe.javacc.demo3.gen.HitaoSqlParser;
import top.doe.javacc.demo3.gen.ParseException;
import top.doe.javacc.demo3.nodes.SqlNode;

public class TestMain {
    public static void main(String[] args) throws ParseException {

        HitaoSqlParser hitaoSqlParser = new HitaoSqlParser(System.in);
        SqlNode sqlNode = hitaoSqlParser.parseSelect();

        System.out.println(sqlNode);

    }
}
