package com.company.AnnotatedClasses;

import com.company.Annotations.XmlObject;
import com.company.Annotations.XmlTag;

@XmlObject
public class DogFancier extends Person {
  @XmlTag
  private Dog dog;

  public DogFancier(String name, String lang, int age, String color, String breed){
    super(name, lang, age);
    dog = new Dog(color, breed);
  }
  public Dog getDog(){
    return dog;
  }
}
