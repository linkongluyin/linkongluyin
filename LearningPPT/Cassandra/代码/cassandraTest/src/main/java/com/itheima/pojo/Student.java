package com.itheima.pojo;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(keyspace = "geomesa",name = "student")
public class Student {
    @PartitionKey  //标识当前的属性对应的字段是分区键
    private Long id;
    private String address;
    private String name;
    private Integer age;
    private Integer gender;
    private Set<String> interest;
    private List<String> phone;
    private Map<String,String> education;
}
