package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * @author Haruaki TAMADA
 */
public class ClassFileClassFinder extends AbstractClassFinder{
    public ClassFileClassFinder(){
    }

    @Override
    public ClassObject[] findClass(File file) throws IOException{
        String name = file.getName();
        if(!name.endsWith(".class")){
            throw new IOException("illegal file type");
        }

        ClassParser parser = new ClassParser(file.getPath());
        JavaClass jc = parser.parse();

        ClassObject wrapper = new ClassObject(jc, file.toURI().toURL());
        Repository.addClass(jc);

        if(jc != null){
            return new ClassObject[] { wrapper };
        }
        return new ClassObject[0];
    }
}
