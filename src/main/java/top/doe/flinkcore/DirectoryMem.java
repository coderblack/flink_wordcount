package top.doe.flinkcore;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMem {
    static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        //printHeapMem();
        //printNonHeapMem();
        Thread.sleep(5000);

        ArrayList<byte[]> lst = new ArrayList<>();

        ArrayList<ByteBuffer> byteBuffers = new ArrayList<>();


        for (int i = 0; i < 64; i++) {
            //byte[] bs = new byte[1024 * 1024];
            //lst.add(bs);
            Thread.sleep(1000);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * 1024);
            byteBuffers.add(byteBuffer);


            //printHeapMem();
            //printNonHeapMem();
            System.out.println(i + " --------------");

        }


        Thread.sleep(5000);
        System.out.println(lst.size());
        System.out.println(byteBuffers.size());
    }


    public static void printHeapMem(){
        System.out.println("heap-committed: " + memoryMXBean.getHeapMemoryUsage().getCommitted()/1024/1024);
        System.out.println("heap-used: " + memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024);
        System.out.println("heap-init: " + memoryMXBean.getHeapMemoryUsage().getInit()/1024/1024);
        System.out.println("heap-max: " + memoryMXBean.getHeapMemoryUsage().getMax()/1024/1024);
    }


    public static void printNonHeapMem() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> vmClass = Class.forName("sun.misc.VM");
        java.lang.reflect.Method m = vmClass.getDeclaredMethod("maxDirectMemory");
        long maxDirectMemory = (Long) m.invoke(null);
        System.out.println("Max direct memory: " + maxDirectMemory / 1024 / 1024 + " MB");

        Class<?> bitsClass = Class.forName("java.nio.Bits");
        java.lang.reflect.Method usedMemory = bitsClass.getDeclaredMethod("usedDirectMemory");
        long usedDirectMemory = (Long) usedMemory.invoke(null);
        System.out.println("Used direct memory: " + usedDirectMemory / 1024 / 1024 + " MB");

    }

}
