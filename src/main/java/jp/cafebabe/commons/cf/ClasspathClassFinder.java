package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;

/**
 * This class is serach classpath.
 *
 * @author Haruaki TAMADA
 */
public class ClasspathClassFinder extends AbstractClassFinder{
    @Override
    public ClassObject[] findClass(File file) throws IOException{
        String className = file.getName();
        try{
            Class<?> c = Class.forName(className);
            JavaClass jc = Repository.lookupClass(c);

            String path = className.replace('.', '/');
            ClassObject wrapper = new ClassObject(jc, c.getResource("/" + path + ".class"));
            Repository.addClass(jc);

            return new ClassObject[] { wrapper, };
        } catch(ClassNotFoundException e){
            throw new IOException(e.getMessage());
        }
    }
}
