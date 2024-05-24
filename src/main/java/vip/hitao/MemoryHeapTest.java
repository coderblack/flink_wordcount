package vip.hitao;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

public class MemoryHeapTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        DataStreamSource<String> stream = env.socketTextStream("localhost", 9090);
        stream.map(new RichMapFunction<String, String>() {
            ArrayList<byte[]> lst;
            @Override
            public void open(Configuration parameters) throws Exception {
                lst = new ArrayList<>();
            }

            @Override
            public String map(String s) throws Exception {

                for(int i=1;i<=4000;i++){
                    byte[] arr = new byte[2048000];
                    lst.add(arr);
                    System.out.println(i+" * 2m ==> " + (i*2) + " m" );
                    Thread.sleep(200);
                }

                return s;
            }
        }).print();

        env.execute();
    }
}
