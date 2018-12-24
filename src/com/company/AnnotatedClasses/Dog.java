package com.company.AnnotatedClasses;

import com.company.Annotations.XmlObject;
import com.company.Annotations.XmlTag;

@XmlObject
public class Dog {
  @XmlTag
  private String color;
  @XmlTag
  private String breed;

  public Dog(String color, String breed){
    this.color = color;
    this.breed = breed;
  }
}
