package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * @author Haruaki TAMADA
 */
public class JarFileClassFinder extends AbstractClassFinder {
    @Override
    public ClassObject[] findClass(File file) throws IOException{
        String fileName = file.getName();
        if(!fileName.endsWith(".jar") && !fileName.endsWith(".zip")){
            throw new IOException("illegal file type");
        }

        ZipFile zip = new ZipFile(file);
        List<ClassObject> wrappers = new ArrayList<ClassObject>();

        Enumeration<?> e = zip.entries();

        for(int i = 0; i < zip.size(); i++){
            ZipEntry entry = (ZipEntry)e.nextElement();
            String name = entry.getName();
            if(name.endsWith(".class")){
                ClassParser parser = new ClassParser(file.getPath(), name);
                JavaClass javaClass = parser.parse();
                ClassObject wrapper = new ClassObject(
                    javaClass, new URL("jar:" + file.toURI().toURL() + "!/" + name)
                );
                Repository.addClass(javaClass);

                wrappers.add(wrapper);
            }
        }

        return wrappers.toArray(new ClassObject[wrappers.size()]);
    }
}
