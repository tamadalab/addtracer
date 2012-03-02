package jp.cafebabe.commons.bcul;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;

/**
 * @author Haruaki TAMADA
 */
public class RepositoryWrapper implements Repository{
    private static final long serialVersionUID = 9237972340952345L;
    private Repository repository;

    public RepositoryWrapper(Repository repository){
        this.repository = repository;
    }

    @Override
    public void storeClass(JavaClass javaClass){
        repository.storeClass(javaClass);
        javaClass.setRepository(this);
    }

    @Override
    public void removeClass(JavaClass javaClass){
        repository.removeClass(javaClass);
    }

    @Override
    public JavaClass findClass(String className){
        return repository.findClass(className);
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException{
        JavaClass javaClass = findClass(className);
        if(javaClass == null){
            try{
                javaClass = repository.loadClass(className);
            } catch(ClassNotFoundException e){
                try{
                    Class<?> c = Class.forName(className);
                    String name = className;
                    name = name.substring(name.lastIndexOf(".") + 1).replace('.', '/') + ".class";
                    URL resource = c.getResource(name);
                    if(resource != null){
                        loadResource(resource);
                    }
                } catch(ClassNotFoundException ee){
                    javaClass = repository.loadClass(className);
                }
            }
        }
        return repository.findClass(className);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public JavaClass loadClass(Class clazz) throws ClassNotFoundException{
        JavaClass javaClass = findClass(clazz.getName());
        if(javaClass == null){
            javaClass = repository.loadClass(clazz);
        }

        return javaClass;
    }

    private void loadResource(URL resource){
        try{
            String url = resource.toString();
            if((url.indexOf(".jar") > 0 || url.indexOf(".zip") > 0) && url.indexOf("!") > 0){
                String fileName = url.substring(9, url.indexOf("!"));
                ZipFile file = new ZipFile(fileName);
                for(Enumeration<?> e = file.entries(); e.hasMoreElements(); ){
                    ZipEntry entry = (ZipEntry)e.nextElement();
                    if(entry.getName().endsWith(".class")){
                        ClassParser parser = new ClassParser(fileName, entry.getName());
                        JavaClass jc = parser.parse();
                        storeClass(jc);
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void clear(){
        repository.clear();
    }

    @Override
    public ClassPath getClassPath(){
        return repository.getClassPath();
    }
}
