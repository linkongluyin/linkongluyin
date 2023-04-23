package com.itheima.springcass.repository;

import com.itheima.springcass.pojo.Student;
import org.springframework.data.cassandra.repository.CassandraRepository;

//CassandraRepository 提供简单的CRUD的方法
public interface StudentRepository extends CassandraRepository<Student,Long> {
}
