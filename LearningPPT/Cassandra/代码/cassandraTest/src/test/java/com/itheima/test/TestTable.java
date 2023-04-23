package com.itheima.test;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.Drop;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.schemabuilder.SchemaStatement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.itheima.pojo.Student;
import io.netty.util.Mapping;
import org.junit.Before;
import org.junit.Test;


import java.util.*;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

public class TestTable {

    Session session = null;
    /**
     * 初始化
     */
    @Before
    public void init(){
        String address = "192.168.5.142";
        int port = 9042;
        Cluster cluster = Cluster.builder()
                .addContactPoint(address)
                .withPort(port)
                .build();

        session = cluster.connect();
    }

    /**
     * 创建表
     */
    @Test
    public void testCreateTable(){
        Create create = SchemaBuilder.
                createTable("geomesa", "student")
                .addPartitionKey("id", DataType.bigint())
                .addColumn("address", DataType.text())
                .addColumn("age", DataType.cint())
                .addColumn("name", DataType.text())
                .addColumn("gender", DataType.cint())
                .addColumn("interest", DataType.set(DataType.text()))
                .addColumn("phone", DataType.list(DataType.text()))
                .addColumn("education", DataType.map(DataType.text(), DataType.text()))
                .ifNotExists();
        session.execute(create);
    }

    @Test
    public void alterTable(){
//        新增一个字段
//        SchemaStatement statement = SchemaBuilder.
//                alterTable("geomesa", "student")
//                .addColumn("email")
//                .type(DataType.text());
//        session.execute(statement);
//        修改字段,无法把text类型修改为其他类型，只能改为varchar
//        SchemaStatement statement1 = SchemaBuilder.alterTable("geomesa", "student")
//                .alterColumn("email")
//                .type(DataType.varchar());
//        session.execute(statement1);
        SchemaStatement dropColumn = SchemaBuilder.
                alterTable("geomesa", "student")
                .dropColumn("email");
        session.execute(dropColumn);

    }

    /**
     * 删除表
     */
    @Test
    public void removeTable(){
        Drop drop = SchemaBuilder.dropTable("geomesa", "student").ifExists();
        session.execute(drop);
    }

    /**
     * 使用cql 新增数据
     */
    @Test
    public void insertTableByCQL(){
        String cql = "INSERT INTO geomesa.student (id,address,age,gender,name,interest, phone,education) VALUES (1011,'中山路21号',16,1,'Tom',{'游泳', '跑步'},['010-88888888','13888888888'],{'小学' : '城市第一小学', '中学' : '城市第一中学'})";
        ResultSet resultSet = session.execute(cql);
        System.out.println(resultSet);
    }

    @Test
    public void insertTableByMapper(){
//        (1012,'朝阳路19号',17,2,'Jerry',{'看书', '电影'},['020-66666666','13666666666'],
//        {'小学' :'城市第五小学','中学':'城市第五中学'});
        Set<String> interest = new HashSet<>();
        interest.add("看书");
        interest.add("电影");
        List<String> phone = new ArrayList<>();
        phone.add("020-66666666");
        phone.add("13666666666");
        Map<String,String> education = new HashMap();
        education.put("小学" ,"城市第五小学");
        education.put("中学","城市第五中学");
        Student student = new Student(1012L,
                "朝阳路19号",
                "Jerry",
                17,
                1,
                interest,
                phone,
                education);
        Mapper<Student> mapper = new MappingManager(session).mapper(Student.class);
        mapper.save(student);

    }

    /**
     * 查询所有数据
     */
    @Test
    public void findAll(){
        ResultSet resultSet = session.execute(select().from("geomesa", "student"));
        Mapper<Student> mapper = new MappingManager(session).mapper(Student.class);
        List<Student> studentList = mapper.map(resultSet).all();
        for (Student student : studentList) {
            System.out.println(student);
            System.out.println("========================");
        }
    }

    /**
     * 根据主键id 查询
     */
    @Test
    public void findById(){
        ResultSet resultSet = session.execute(select().
                from("geomesa", "student").
                where(eq("id", 1012L)));
        Mapper<Student> mapper = new MappingManager(session).mapper(Student.class);
        Student student = mapper.map(resultSet).one();
        System.out.println(student);

    }

    /**
     * 删除
     */
    @Test
    public void delete(){
        Mapper<Student> mapper = new MappingManager(session).mapper(Student.class);
        Long id = 1011L;
        mapper.delete(id);
    }

    /**
     * 创建索引
     */
    @Test
    public void createIndex(){
//      普通数据类型创建索引
        SchemaStatement statement = SchemaBuilder.
                createIndex("nameindex").
                onTable("geomesa", "student").
                andColumn("name");
        session.execute(statement);
//       给map类型创建索引
            SchemaStatement statement1 = SchemaBuilder.createIndex("educationindex")
                .onTable("geomesa", "student")
                .andKeysOfColumn("education");

        session.execute(statement1);
    }

    /**
     * 删除索引
     */
    @Test
    public void dropIndex(){
        Drop drop = SchemaBuilder.dropIndex("geomesa","nameindex");
        session.execute(drop);
    }

}
