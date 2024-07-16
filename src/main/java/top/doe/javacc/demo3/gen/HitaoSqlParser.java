/* HitaoSqlParser.java */
/* Generated By:JavaCC: Do not edit this line. HitaoSqlParser.java */
package top.doe.javacc.demo3.gen;

import java.util.List;

import top.doe.javacc.demo3.nodes.*;

public class HitaoSqlParser implements HitaoSqlParserConstants {

    final public SqlNode parseSelect() throws ParseException {
        ProjectNode projectNode = new ProjectNode();
        jj_consume_token(SELECT);
        parse_column(projectNode.getColumnList());
        jj_consume_token(FROM);
        parse_join(projectNode);
        jj_consume_token(EOL);
        {
            if ("" != null) return projectNode;
        }
        throw new Error("Missing return statement in function");
    }

    final public void parse_column(List<Column> columnList) throws ParseException {
        Token t;
        Token c;
        label_1:
        while (true) {
            t = jj_consume_token(ID);
            jj_consume_token(DOT);
            c = jj_consume_token(ID);
            switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                case COMMA: {
                    jj_consume_token(COMMA);
                    break;
                }
                default:
                    jj_la1[0] = jj_gen;
                    ;
            }
            String tableName = t.image;
            String fieldName = c.image;
            Column column = new Column(fieldName, tableName);
            columnList.add(column);
            switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                case ID: {
                    ;
                    break;
                }
                default:
                    jj_la1[1] = jj_gen;
                    break label_1;
            }
        }
    }

    // a join b on a.id = b.id
    final public void parse_join(SqlNode parent) throws ParseException {
        Token t1;
        Token c1;
        Token t2;
        Token c2;
        Token opToken;
        SqlNode leftTableNode;
        SqlNode rightTableNode;
        leftTableNode = table();
        jj_consume_token(JOIN);
        rightTableNode = table();
        jj_consume_token(ON);
        t1 = jj_consume_token(ID);
        jj_consume_token(DOT);
        c1 = jj_consume_token(ID);
        opToken = jj_consume_token(OP);
        t2 = jj_consume_token(ID);
        jj_consume_token(DOT);
        c2 = jj_consume_token(ID);
        JoinNode joinNode = new JoinNode();
        parent.addChild(joinNode);

        joinNode.addChild(leftTableNode);
        joinNode.addChild(rightTableNode);

        Column lc = new Column(c1.image, t1.image);
        Column rc = new Column(c2.image, t2.image);
        Condition condition = new Condition(lc, rc, opToken.image);
        joinNode.setCondition(condition);
    }

    final public SqlNode table() throws ParseException {
        Token t;
        t = jj_consume_token(ID);
        {
            if ("" != null) return new TableScanNode(t.image);
        }
        throw new Error("Missing return statement in function");
    }

    /**
     * Generated Token Manager.
     */
    public HitaoSqlParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    /**
     * Current token.
     */
    public Token token;
    /**
     * Next token.
     */
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    final private int[] jj_la1 = new int[2];
    static private int[] jj_la1_0;

    static {
        jj_la1_init_0();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0x800, 0x400,};
    }

    /**
     * Constructor with InputStream.
     */
    public HitaoSqlParser(java.io.InputStream stream) {
        this(stream, null);
    }

    /**
     * Constructor with InputStream and supplied encoding
     */
    public HitaoSqlParser(java.io.InputStream stream, String encoding) {
        try {
            jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        token_source = new HitaoSqlParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++) jj_la1[i] = -1;
    }

    /**
     * Reinitialise.
     */
    public void ReInit(java.io.InputStream stream) {
        ReInit(stream, null);
    }

    /**
     * Reinitialise.
     */
    public void ReInit(java.io.InputStream stream, String encoding) {
        try {
            jj_input_stream.ReInit(stream, encoding, 1, 1);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++) jj_la1[i] = -1;
    }

    /**
     * Constructor.
     */
    public HitaoSqlParser(java.io.Reader stream) {
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new HitaoSqlParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++) jj_la1[i] = -1;
    }

    /**
     * Reinitialise.
     */
    public void ReInit(java.io.Reader stream) {
        if (jj_input_stream == null) {
            jj_input_stream = new SimpleCharStream(stream, 1, 1);
        } else {
            jj_input_stream.ReInit(stream, 1, 1);
        }
        if (token_source == null) {
            token_source = new HitaoSqlParserTokenManager(jj_input_stream);
        }

        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++) jj_la1[i] = -1;
    }

    /**
     * Constructor with generated Token Manager.
     */
    public HitaoSqlParser(HitaoSqlParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++) jj_la1[i] = -1;
    }

    /**
     * Reinitialise.
     */
    public void ReInit(HitaoSqlParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 2; i++) jj_la1[i] = -1;
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;
        if ((oldToken = token).next != null) token = token.next;
        else token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        if (token.kind == kind) {
            jj_gen++;
            return token;
        }
        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }


    /**
     * Get the next Token.
     */
    final public Token getNextToken() {
        if (token.next != null) token = token.next;
        else token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        jj_gen++;
        return token;
    }

    /**
     * Get the specific Token.
     */
    final public Token getToken(int index) {
        Token t = token;
        for (int i = 0; i < index; i++) {
            if (t.next != null) t = t.next;
            else t = t.next = token_source.getNextToken();
        }
        return t;
    }

    private int jj_ntk_f() {
        if ((jj_nt = token.next) == null)
            return (jj_ntk = (token.next = token_source.getNextToken()).kind);
        else
            return (jj_ntk = jj_nt.kind);
    }

    private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;

    /**
     * Generate ParseException.
     */
    public ParseException generateParseException() {
        jj_expentries.clear();
        boolean[] la1tokens = new boolean[14];
        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }
        for (int i = 0; i < 2; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 14; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.add(jj_expentry);
            }
        }
        int[][] exptokseq = new int[jj_expentries.size()][];
        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = jj_expentries.get(i);
        }
        return new ParseException(token, exptokseq, tokenImage);
    }

    private boolean trace_enabled;

    /**
     * Trace enabled.
     */
    final public boolean trace_enabled() {
        return trace_enabled;
    }

    /**
     * Enable tracing.
     */
    final public void enable_tracing() {
    }

    /**
     * Disable tracing.
     */
    final public void disable_tracing() {
    }

}
