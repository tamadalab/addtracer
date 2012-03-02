package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Haruaki TAMADA
 */
public class DefaultClassFinder extends AbstractClassFinder{
    private static DefaultClassFinder instance = new DefaultClassFinder();

    private Map<String, ClassFinder> map = new HashMap<String, ClassFinder>();

    private DefaultClassFinder(){
        map.put("zip",         new JarFileClassFinder());
        map.put("class",       new ClassFileClassFinder());
        map.put("jar",         map.get("zip"));
        map.put("<directory>", new DirectoryClassFinder(this));
        map.put("<classpath>", new ClasspathClassFinder());
    }

    public static ClassFinder getInstance(){
        return instance;
    }

    @Override
    public ClassObject[] findClass(File file) throws IOException{
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);

        ClassFinder extracter = map.get(extension);
        if(file.isDirectory()) {
            extracter = map.get("<directory>");
        }
        if(extracter == null) {
            extracter = map.get("<classpath>");
        }
        if(extracter == null){
            extracter = new NullClassFinder();
        }

        return extracter.findClass(file);
    }
}
