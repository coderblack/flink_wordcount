package top.doe.javacc.demo1;

import top.doe.javacc.demo1.gen.Demo1Parser;

public class TestMain {
    public static void main(String[] args) throws Exception {

        Demo1Parser demo1Parser = new Demo1Parser(System.in, "UTF-8");
        Double result = demo1Parser.parse();
        System.out.println(result);

    }

}
