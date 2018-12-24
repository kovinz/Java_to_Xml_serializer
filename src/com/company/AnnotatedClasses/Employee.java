package com.company.AnnotatedClasses;

import com.company.Annotations.XmlAttribute;
import com.company.Annotations.XmlObject;
import com.company.Annotations.XmlTag;

@XmlObject
public class Employee extends Person {
  @XmlTag
  private double salary;
  @XmlAttribute
  private String position;
  @XmlAttribute(tag = "salary")
  private String currency;

  public Employee(String name, String lang, int age, double salary, String currency, String position){
    super(name, lang, age);
    this.currency = currency;
    this.position = position;
    this.salary = salary;
  }
}
