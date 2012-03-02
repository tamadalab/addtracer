package jp.cafebabe.commons.cf;

import java.net.URL;

import org.apache.bcel.classfile.JavaClass;

/**
 * @author Haruaki TAMADA
 */
public class ClassObject{
    private URL location;
    private JavaClass classObject;

    public ClassObject(JavaClass classObject){
        this.classObject = classObject;
    }

    public ClassObject(JavaClass classObject, URL location){
        this.classObject = classObject;
        this.location = location;
    }

    public JavaClass getObject(){
        return classObject;
    }

    public URL getLocation(){
        return location;
    }

    public void setLocation(URL location){
        this.location = location;
    }
}
