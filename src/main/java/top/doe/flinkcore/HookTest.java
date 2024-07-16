package top.doe.flinkcore;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class HookTest {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10000);

        DataStreamSource<String> stream = env.socketTextStream("doitedu", 9090);
        stream.print();


        env.execute();


    }
}
