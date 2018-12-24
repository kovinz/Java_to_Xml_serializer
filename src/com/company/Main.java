package com.company;

import com.company.AnnotatedClasses.DogFancier;
import com.company.AnnotatedClasses.Employee;
import com.company.AnnotatedClasses.Person;
import com.company.Serialization.Serializer;

public class Main {

    public static void main(String[] args) {
        Serializer serializer = new Serializer();
        Person person = new Person("Sergey", "RUS", 32);
        serializer.serialize(person);
        Employee employee = new Employee("Gleb", "RUS", 25,
                4000, "USD", "Frontend Developer");
        serializer.serialize(employee);
        DogFancier dogFancier = new DogFancier("Nikola", "ENG", 16, "Brown", "Shepherd");
        serializer.serialize(dogFancier);
        serializer.writeXml();
    }
}