package com.itheima.springcass.test;

import com.itheima.springcass.pojo.Student;
import com.itheima.springcass.service.StudentService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 测试类
 */
public class TestCass {


    private StudentService studentService;

    /**
     * 初始化
     */
    @Before
    public void init(){
//        读取配置文件，初始化Spring,获取上下文
        ConfigurableApplicationContext ctx
                = new ClassPathXmlApplicationContext("springContext.xml");
//      从上下文件中获取 StudentService
        studentService = (StudentService)ctx.getBean("studentService");

//        cassandraTemplate = (CassandraTemplate)ctx.getBean("cassandraTemplate");

    }

    /**
     * 查询所有
     */
    @Test
    public void findAll(){
        List<Student> studentList = studentService.queryAll();
        for (Student student : studentList) {
            System.out.println(student);
            System.out.println("==========================");
        }
    }

    /**
     * 根据主键id 查询对象
     */
    @Test
    public void findById(){
        Student student = studentService.queryOne(1023L);
        System.out.println(student);
    }

    /**
     * 保存信息
     */
    @Test
    public void save(){
        HashMap<String, String> education = new HashMap<>();
        education.put("小学", "中心第23小学");
        education.put("中学", "中心实验23中学");
        HashSet<String> interest = new HashSet<>();
        interest.add("看书23");
        interest.add("电影23");
        List<String> phones = new ArrayList<>();
        phones.add("130-6666666623");
        phones.add("1576666666623");
//        构造student
        Student student = new Student(
                1023L,
                "北京市朝阳区80023号",
                "小小咸23",
                30,
                1,
                interest,
                phones,
                education
                );
        studentService.saveStudent(student);
    }

    /**
     * 根据主键id删除
     */
    @Test
    public void delteById(){
        studentService.deleteById(1023L);
    }
}
