package com.example.studentapi.service;
import com.example.studentapi.dto.StudentRequest;
import com.example.studentapi.dto.StudentResponse;
import com.example.studentapi.entity.Student;
import com.example.studentapi.exception.DuplicateStudentException;
import com.example.studentapi.exception.StudentNotFoundException;
import com.example.studentapi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    public StudentResponse registerStudent(StudentRequest request) {
        if (request.getName() == null || request.getName().isEmpty() || 
            request.getCourse() == null || request.getCourse().isEmpty()) {
            throw new IllegalArgumentException("Name and course must not be null or empty");
        }
        if (studentRepository.existsById(request.getId())) {
            throw new DuplicateStudentException("Student ID already exists: " + request.getId());
        }
        Student student = new Student();
        student.setId(request.getId());
        student.setName(request.getName());
        student.setCourse(request.getCourse());
        Student savedStudent = studentRepository.save(student);
        return new StudentResponse(savedStudent.getId(), savedStudent.getName(), savedStudent.getCourse());
    }
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getCourse()))
                .collect(Collectors.toList());
    }
    public StudentResponse getStudentById(int id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return new StudentResponse(student.getId(), student.getName(), student.getCourse());
    }
    public void deleteStudent(int id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student does not exist with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}