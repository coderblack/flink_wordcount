package top.doe.calcite.demo1;

import java.util.Arrays;

public class HrScheamBean {

    public final HrEmployee[] emps = {
            new HrEmployee(1,10,"jack",10000,1000),
            new HrEmployee(2,20,"eric",20000,2000),
            new HrEmployee(3,10,"sally",8000,1200),
            new HrEmployee(4,10,"tom",12000,1100),
            new HrEmployee(5,10,"brown",15000,900),
            new HrEmployee(6,20,"black",11000,200),
            new HrEmployee(7,10,"white",10000,120)
    };


    public final HrDepartment[] depts = {
            new HrDepartment(10,"sales", Arrays.asList(emps[1],emps[3],emps[5]),"A-102"),
            new HrDepartment(20,"sales", Arrays.asList(emps[2],emps[4],emps[6]),"B-206")
    };


}
