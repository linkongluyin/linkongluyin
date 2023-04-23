package com.itheima.springcass.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Student {
    @PrimaryKey  //标识当前的属性对应的字段是分区键
    private Long id;
    private String address;
    private String name;
    private Integer age;
    private Integer gender;
    private Set<String> interest;
    private List<String> phone;
    private Map<String,String> education;
}
