package top.doe.flinkcore;

import sun.misc.Unsafe;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;

public class MemoryTest {

    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long used = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        System.out.println("一开始:" + used/1024/1024);



        Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe =  (sun.misc.Unsafe) unsafeField.get(null);

        Thread.sleep(20000);
        System.out.println("准备申请....");




        long address = unsafe.allocateMemory(1024 * 1024 * 64 );
        System.out.println(address);
        used = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        System.out.println("申请后:" + used/1024/1024);


        Thread.sleep(10000);

        System.out.println("准备释放....");
        unsafe.freeMemory(address);
        System.out.println("释放完毕....");
        used = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        System.out.println("释放后:" + used/1024/1024);


        Thread.sleep(20000);

    }

}
