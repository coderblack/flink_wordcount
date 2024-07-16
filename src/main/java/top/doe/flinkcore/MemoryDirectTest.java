package top.doe.flinkcore;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MemoryDirectTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        DataStreamSource<String> stream = env.socketTextStream("localhost", 9090);
        stream.map(new RichMapFunction<String, String>() {

            List<ByteBuffer> lst = new ArrayList<>();

            @Override
            public String map(String s) throws Exception {

                for(int i=1;i<=1000;i++){
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048000);
                    lst.add(byteBuffer);
                    System.out.println(i+" * 2m ==> " + (i*2) + " m" );
                    Thread.sleep(500);
                }

                return s;
            }
        }).print();

        env.execute();
    }
}
