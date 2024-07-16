package top.doe.flinkcore;

import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.checkpoint.MasterTriggerRestoreHook;
import org.apache.flink.runtime.checkpoint.hooks.MasterHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MyMasterHook implements MasterTriggerRestoreHook<String> {

    String someData;
    public MyMasterHook(){
        someData = "triggerCheckpoint";
    }

    @Override
    public String getIdentifier() {

        return "myHook001";
    }

    @Override
    public void reset() throws Exception {
        MasterTriggerRestoreHook.super.reset();
    }

    @Override
    public void close() throws Exception {
        MasterTriggerRestoreHook.super.close();
    }

    @Nullable
    @Override
    public CompletableFuture<String> triggerCheckpoint(long l, long l1, Executor executor) throws Exception {
        return CompletableFuture.supplyAsync(()-> {System.out.println("triggerCheckpoint");return "triggerCheckpoint";});
    }

    @Override
    public void restoreCheckpoint(long l, @Nullable String o) throws Exception {
        this.someData = o;
    }

    @Nullable
    @Override
    public SimpleVersionedSerializer<String> createCheckpointDataSerializer() {


        return new SimpleVersionedSerializer<String>() {
            @Override
            public int getVersion() {
                return 0;
            }

            @Override
            public byte[] serialize(String s) throws IOException {
                return s.getBytes();
            }

            @Override
            public String deserialize(int i, byte[] bytes) throws IOException {
                return new String(bytes);
            }
        };
    }


}
