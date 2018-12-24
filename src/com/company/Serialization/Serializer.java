package com.company.Serialization;

import com.company.Annotations.XmlAttribute;
import com.company.Annotations.XmlObject;
import com.company.Annotations.XmlTag;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Serializer {
  private Element root;
  private Document document;
  private Pattern getPattern;
  private Matcher matcher;

  public Serializer() {
    document = DocumentHelper.createDocument();
    root = document.addElement("objects");
    getPattern = Pattern.compile( "^get.*" );
  }

  public void serialize(Object obj) {
    Class<?> cl = obj.getClass();
    if (!cl.isAnnotationPresent(XmlObject.class)) {
      return;
    }

    Element objElement;
    XmlObject classAnnotation = cl.getAnnotation(XmlObject.class);
    if (classAnnotation.name().equals("")) {
      objElement = root.addElement(cl.getSimpleName());
    } else {
      objElement = root.addElement(classAnnotation.name());
    }

    while (cl.isAnnotationPresent(XmlObject.class)) {
      serializeTags(obj, objElement, cl);
      serializeAttributes(obj, objElement, cl);
      cl = cl.getSuperclass();
    }
  }

  private void serializeAttributes(Object obj, Element objElement, Class<?> cl) {
    Method[] methods = cl.getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(XmlAttribute.class) && (method.getParameterCount() == 0)
              && !method.getReturnType().equals(Void.TYPE)) {
        try {
          method.setAccessible(true);
          String text = method.invoke(obj).toString();
          XmlAttribute methodAttributeAnnotation = method.getAnnotation(XmlAttribute.class);
          if (methodAttributeAnnotation.tag().equals("")) {
            if (methodAttributeAnnotation.name().equals("")) {
              matcher = getPattern.matcher(method.getName());
              if (matcher.matches()){
                objElement.addAttribute(method.getName().substring(3), text);
              } else {
                objElement.addAttribute(method.getName(), text);
              }
            } else {
              objElement.addAttribute(methodAttributeAnnotation.name(), text);
            }
          } else {
            String nameOfTagToAddAttribute = methodAttributeAnnotation.tag();
            Iterator<Element> elements = objElement.elementIterator();
            while (elements.hasNext()) {
              Element currentElement = elements.next();
              if (currentElement.getName().equals(nameOfTagToAddAttribute)) {
                if (methodAttributeAnnotation.name().equals("")) {
                  currentElement.addAttribute(method.getName(), text);
                } else {
                  currentElement.addAttribute(methodAttributeAnnotation.name(), text);
                }
              }
            }
          }
        } catch (java.lang.IllegalAccessException e) {
          e.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    Field[] fields = cl.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(XmlAttribute.class)) {
        try {
          field.setAccessible(true);
          String text = field.get(obj).toString();
          XmlAttribute fieldAttributeAnnotation = field.getAnnotation(XmlAttribute.class);
          if (fieldAttributeAnnotation.tag().equals("")) {
            if (fieldAttributeAnnotation.name().equals("")) {
              objElement.addAttribute(field.getName(), text);
            } else {
              objElement.addAttribute(fieldAttributeAnnotation.name(), text);
            }
          } else {
            String nameOfTagToAddAttribute = fieldAttributeAnnotation.tag();
            Iterator<Element> elements = objElement.elementIterator();
            while (elements.hasNext()) {
              Element currentElement = elements.next();
              if (currentElement.getName().equals(nameOfTagToAddAttribute)) {
                if (fieldAttributeAnnotation.name().equals("")) {
                  currentElement.addAttribute(field.getName(), text);
                } else {
                  currentElement.addAttribute(fieldAttributeAnnotation.name(), text);
                }
              }
            }
          }
        } catch (java.lang.IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void serializeTags(Object obj, Element objElement, Class<?> cl) {
    Method[] methods = cl.getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(XmlTag.class) && (method.getParameterCount() == 0)
              && !method.getReturnType().equals(Void.TYPE)) {
        try {
          method.setAccessible(true);
          if (method.getReturnType().isAnnotationPresent(XmlObject.class)) {
            Class<?> returnedClass = method.getReturnType();
            Element tagElement;
            XmlTag methodTagAnnotation = method.getAnnotation(XmlTag.class);
            if (methodTagAnnotation.name().equals("")) {
              matcher = getPattern.matcher(method.getName());
              if (matcher.matches()) {
                tagElement = objElement.addElement(method.getName().substring(3));
              } else {
                tagElement = objElement.addElement(method.getName());
              }
            } else {
              tagElement = objElement.addElement(methodTagAnnotation.name());
            }
            while (returnedClass.isAnnotationPresent(XmlObject.class)) {
              serializeTags(method.invoke(obj), tagElement, method.getReturnType());
              serializeAttributes(method.invoke(obj), tagElement, method.getReturnType());
              returnedClass = returnedClass.getSuperclass();
            }
          } else {
            String text = method.invoke(obj).toString();
            XmlTag methodTagAnnotation = method.getAnnotation(XmlTag.class);
            if (methodTagAnnotation.name().equals("")) {
              matcher = getPattern.matcher(method.getName());
              if (matcher.matches()) {
                objElement.addElement(method.getName().substring(3)).addText(text);
              } else {
                objElement.addElement(method.getName()).addText(text);
              }
            } else {
              objElement.addElement(methodTagAnnotation.name()).addText(text);
            }
          }
        } catch (java.lang.IllegalAccessException e) {
          e.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    Field[] fields = cl.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(XmlTag.class)) {
        try {
          field.setAccessible(true);
          if (field.getType().isAnnotationPresent(XmlObject.class)) {
            Class<?> returnedClass = field.getType();
            Element tagElement;
            XmlTag fieldTagAnnotation = field.getAnnotation(XmlTag.class);
            if (fieldTagAnnotation.name().equals("")) {
              tagElement = objElement.addElement(field.getName());
            } else {
              tagElement = objElement.addElement(fieldTagAnnotation.name());
            }
            while (returnedClass.isAnnotationPresent(XmlObject.class)) {
              serializeTags(field.get(obj), tagElement, field.getType());
              serializeAttributes(field.get(obj), tagElement, field.getType());
              returnedClass = returnedClass.getSuperclass();
            }
          } else {
            String text = field.get(obj).toString();
            XmlTag fieldAnnotation = field.getAnnotation(XmlTag.class);
            if (fieldAnnotation.name().equals("")) {
              objElement.addElement(field.getName()).addText(text);
            } else {
              objElement.addElement(fieldAnnotation.name()).addText(text);
            }
          }
        } catch (java.lang.IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void writeXml() {
    try {
      OutputFormat format = OutputFormat.createPrettyPrint();
      XMLWriter writer;
      writer = new XMLWriter(System.out, format);
      writer.write(document);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}