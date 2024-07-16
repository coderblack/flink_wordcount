package top.doe.flinkcore;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class ChkTest {
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        //conf.setString("execution.savepoint.path","hdfs://doitedu:8020/ckpt/faa092c17f475424e86eb4af9d207d11/chk-2");


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.enableCheckpointing(10000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("hdfs://doitedu:8020/ckpt");
        //env.getCheckpointConfig().enableUnalignedCheckpoints();
        env.setParallelism(2);
        env.disableOperatorChaining();

        env.setStateBackend(new HashMapStateBackend());

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("doitedu:9092")
                .setGroupId("grp001")
                .setClientIdPrefix("cli-")
                .setTopics("ss-1", "ss-2")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();


        DataStreamSource<String> dataStreamSource = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "s");


        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers("doitedu:9092")
                //.setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("ccc")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setTransactionalIdPrefix("ttt")
                .build();

        dataStreamSource.keyBy(s -> s).map(new MapF()).setParallelism(2).sinkTo(sink);


        env.execute();

    }


    public static class MapF extends RichMapFunction<String, String> implements CheckpointedFunction {
        ValueState<Integer> cntState;

        MapState<String, Integer> mapState;

        ListState<String> operatorListState;

        ListState<String> listState;

        List<String> someWordsImport = new ArrayList<>();


        @Override
        public void open(Configuration parameters) throws Exception {
            cntState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>(
                    "element_count_state",
                    Integer.class));


            mapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Integer>(
                    "wc_state",
                    String.class,
                    Integer.class));


            listState = getRuntimeContext().getListState(new ListStateDescriptor<String>(
                    "xx",
                    String.class));

        }

        @Override
        public String map(String order) throws Exception {


            cntState.update(cntState.value() == null ? 1 : cntState.value() + 1);

            String[] split = order.split(":");
            String cid = split[0];
            String pid = split[1];

            Integer oldValue = mapState.get(pid);
            int newValue = oldValue == null ? 1 : oldValue + 1;

            mapState.put(pid, newValue);

            if (order.startsWith("X")) {
                someWordsImport.add(order);
            }

            return order;
        }


        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {

            operatorListState.addAll(someWordsImport);

        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {

            OperatorStateStore operatorStateStore = context.getOperatorStateStore();
            operatorListState = operatorStateStore.getListState(new ListStateDescriptor<String>(
                    "op_state",
                    String.class));


            for (String s : operatorListState.get()) {
                someWordsImport.add(s);
            }


        }
    }

}
