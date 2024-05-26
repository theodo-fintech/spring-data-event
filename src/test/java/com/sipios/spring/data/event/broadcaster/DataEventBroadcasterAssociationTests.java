package com.sipios.spring.data.event.broadcaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

public class DataEventBroadcasterAssociationTests {

    private DataEventBroadcaster broadcaster;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        broadcaster = new DataEventBroadcaster(kafkaTemplate, new ObjectMapper());
    }

    @Nested
    class OneToOneAssociationTests {
        @Test
        void testBroadcastEntityWithOneToOneAssociation() throws Exception {
            Course course = new Course(1, "Test Course");
            Student student = new Student(2, "Test Name", course);
            course.setStudent(student);

            broadcaster.broadcastEntityCreated(course, "");
            String expectedJson = "{\"id\":1,\"title\":\"Test Course\",\"student\":{\"id\":2,\"name\":\"Test Name\",\"course\":1}}";
            JSONObject expectedJsonObject = new JSONObject(expectedJson);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq("course.created"), jsonCaptor.capture());

            String actualJson = jsonCaptor.getValue();
            JSONObject actualJsonObject = new JSONObject(actualJson);

            assertEquals(expectedJsonObject.toString(), actualJsonObject.toString());
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        class Course {
            @Id
            private int id;
            private String title;

            @OneToOne(cascade = CascadeType.ALL, mappedBy = "course")
            private Student student;

            public Course(int id, String title) {
                this.id = id;
                this.title = title;
            }

            public void setStudent(Student student) {
                this.student = student;
                student.setCourse(this);
            }
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        class Student {
            @Id
            private int id;
            private String name;

            @OneToOne
            private Course course;
        }
    }

    @Nested
    class ManyToManyAssociationTests {
        @Test
        void testBroadcastEntityWithManyToManyAssociation() throws Exception {
            Student student = new Student(1, "Test Name");
            Course course = new Course(101, "Test Course");
            student.getCourses().add(course);
            course.getStudents().add(student);

            broadcaster.broadcastEntityCreated(student, "");
            String expectedJson = "{\"id\":1,\"name\":\"Test Name\",\"courses\":[{\"id\":101,\"name\":\"Test Course\",\"students\":[1]}]}";
            JSONObject expectedJsonObject = new JSONObject(expectedJson);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq("student.created"), jsonCaptor.capture());

            String actualJson = jsonCaptor.getValue();
            JSONObject actualJsonObject = new JSONObject(actualJson);

            assertEquals(expectedJsonObject.toString(), actualJsonObject.toString());
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        class Student {
            @Id
            private int id;
            private String name;

            @ManyToMany(cascade = CascadeType.ALL)
            @JoinTable(
                    name = "enrollment",
                    joinColumns = @JoinColumn(name = "student_id"),
                    inverseJoinColumns = @JoinColumn(name = "course_id")
            )
            private Set<Course> courses = new HashSet<>();

            public Student(int id, String name) {
                this.id = id;
                this.name = name;
            }
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        class Course {
            @Id
            private int id;
            private String name;

            @ManyToMany(mappedBy = "courses")
            private Set<Student> students = new HashSet<>();

            public Course(int id, String name) {
                this.id = id;
                this.name = name;
            }
        }
    }

    @Nested
    class OneToManyAssociationTests {
        @Test
        void testBroadcastEntityWithManyToOneAssociation() throws Exception {
            Course course = new Course(1, "Test Course");
            Student student = new Student(2, "Test Name", course);
            course.getStudents().add(student);

            broadcaster.broadcastEntityCreated(student, "");
            String expectedStudentJson = "{\"id\":2,\"name\":\"Test Name\",\"course\":{\"id\":1,\"title\":\"Test Course\", \"students\":[2]}}";
            JSONObject expectedStudentJsonObject = new JSONObject(expectedStudentJson);

            ArgumentCaptor<String> studentJsonCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq("student.created"), studentJsonCaptor.capture());

            String actualStudentJson = studentJsonCaptor.getValue();
            JSONObject actualStudentJsonObject = new JSONObject(actualStudentJson);

            assertEquals(expectedStudentJsonObject.toString(), actualStudentJsonObject.toString());
        }

        @Test
        void testBroadcastEntityWithOneToManyAssociation() throws Exception {
            Course course = new Course(1, "Test Course");
            Student student = new Student(2, "Test Name", course);
            course.getStudents().add(student);

            broadcaster.broadcastEntityCreated(course, "");
            String expectedCourseJson = "{\"id\":1,\"title\":\"Test Course\",\"students\":[{\"id\":2,\"name\":\"Test Name\", \"course\":1}]}";
            JSONObject expectedCourseJsonObject = new JSONObject(expectedCourseJson);

            ArgumentCaptor<String> courseJsonCaptor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq("course.created"), courseJsonCaptor.capture());

            String actualCourseJson = courseJsonCaptor.getValue();
            JSONObject actualCourseJsonObject = new JSONObject(actualCourseJson);

            assertEquals(expectedCourseJsonObject.toString(), actualCourseJsonObject.toString());
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        class Course {
            @Id
            private int id;
            private String title;

            @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
            private Set<Student> students = new HashSet<>();

            public Course(int id, String title) {
                this.id = id;
                this.title = title;
            }
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        class Student {
            @Id
            private int id;
            private String name;

            @ManyToOne
            @JoinColumn(name = "course_id")
            private Course course;
        }
    }
}
