package com.itheima.springcass.service;

import com.itheima.springcass.pojo.Student;
import com.itheima.springcass.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    /**
     * 查询所有
     */
    public List<Student> queryAll() {

        return repository.findAll();
    }
    /**
     * 根据主键id 查询对象
     */
    public Student queryOne(Long id) {
        Optional<Student> optional = repository.findById(id);
        return optional.orElse(null);
    }

    /**
     * 保存
     * @param student
     */
    public void saveStudent(Student student) {
        repository.save(student);
    }

    /**
     * 根据id 删除
     * @param id
     */
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
