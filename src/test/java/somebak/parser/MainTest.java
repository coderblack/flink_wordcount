//package vip.hitao.parser;
//
//import java.io.ByteArrayInputStream;
//
//public class MainTest {
//    public static void main(String[] args) {
//        String sql = "select a.id, upper(a.name), b.address from a join b on a.id=b.id";
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(sql.getBytes());
//        SqlParser parser = new SqlParser(inputStream);
//
//        try {
//            SqlNode rootNode = parser.Statement();
//            SqlParser.printTree(rootNode, 0);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
