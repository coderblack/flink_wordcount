package top.doe.calcite.demo1;

import java.util.List;

public class HrDepartment {

    public int deptno;
    public String name;
    public List<HrEmployee> employees;
    public String location;

    public HrDepartment(int deptno, String name, List<HrEmployee> employees, String location) {
        this.deptno = deptno;
        this.name = name;
        this.employees = employees;
        this.location = location;
    }
}
