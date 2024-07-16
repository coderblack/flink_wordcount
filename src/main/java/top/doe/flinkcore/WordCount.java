package top.doe.flinkcore;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

public class WordCount {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend(false));
        //env.disableOperatorChaining();

        DataStreamSource<String> stream = env.socketTextStream("localhost", 9090);
        stream.keyBy(s->s).map(new RichMapFunction<String, String>() {
            ListState<byte[]> hh;

            @Override
            public void open(Configuration parameters) throws Exception {
                hh = getRuntimeContext().getListState(new ListStateDescriptor<byte[]>("hh", TypeInformation.of(new TypeHint<byte[]>() {
                })));
            }

            @Override
            public String map(String s) throws Exception {

                for (int i = 0; i < 10000; i++) {
                    byte[] data = new byte[1024*1024*20]; // 20m
                    Arrays.fill(data,(byte)1);

                    hh.add(data);
                    Thread.sleep(1000);
                }

                return s;
            }
        }).disableChaining().filter(s->s.startsWith("a")).print();

        env.execute();

    }
}