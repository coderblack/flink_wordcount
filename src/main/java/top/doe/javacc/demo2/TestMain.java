package top.doe.javacc.demo2;

import top.doe.javacc.demo2.gen.Demo2Parser;

public class TestMain {
    public static void main(String[] args) throws Exception {

        Demo2Parser demo2Parser = new Demo2Parser(System.in);
        double res = demo2Parser.parseAndCalc();
        System.out.println(res);
    }
}
