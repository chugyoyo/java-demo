package java.lang;

import java.io.Serializable;

public class Object implements Serializable {

    private static final long serialVersionUID = 1L;

    public Object () {
        System.out.println("user define java.lang.Object");
    }

    public void sayHello() {
        System.out.println("hello");
    }
}