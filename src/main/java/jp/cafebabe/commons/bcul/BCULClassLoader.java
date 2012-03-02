package jp.cafebabe.commons.bcul;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import jp.cafebabe.commons.lang.DataHandler;

import org.apache.bcel.classfile.JavaClass;

/**
 * The ClassLoader for transformated JavaClass.
 *
 * @author Haruaki TAMADA
 */
public class BCULClassLoader extends ClassLoader implements DataHandler<JavaClass>{
    private Map<String, JavaClass> targetClasses = new HashMap<String, JavaClass>();

    public BCULClassLoader(JavaClass[] targetClasses, ClassLoader parent) throws IOException{
        super(parent);
        putAll(targetClasses);
    }

    public BCULClassLoader(JavaClass[] targetClasses) throws IOException{
        putAll(targetClasses);
    }

    @Override
    public boolean isContain(String name){
        return targetClasses.containsKey(name);
    }

    @Override
    public JavaClass get(String name){
        return targetClasses.get(name);
    }

    @Override
    public String getName(URL url){
        String path = url.getFile();
        String name = path.substring(path.indexOf("!") + 1);
        if(name.startsWith("/")) name = name.substring(1);
        if(name.endsWith(".class")) name = name.substring(0, name.length() - 6);
        name = name.replace('/', '.');

        return name;
    }

    @Override
    protected URL findResource(String name){
        if(name.endsWith(".class")){
            String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');

            if(targetClasses.get(className) != null){
                try{
                    final DataHandler<JavaClass> handler = this;
                    return new URL(
                        "memory", null, -1,
                        getClass().getName() + "@" + System.identityHashCode(this) + "!/" + name,
                        new URLStreamHandler(){
                            @Override
                            public URLConnection openConnection(URL url){
                                return new BCULMemoryURLConnection(url, handler);
                            }
                        }
                    );
                } catch(MalformedURLException e){
                    return null;
                }
            }
        }
        return super.getResource(name);
    }

    @Override
    public Class<?> findClass(String name){
        if(targetClasses.get(name) != null){
            JavaClass jc = (JavaClass)targetClasses.get(name);
            byte[] data = jc.getBytes();

            return defineClass(name, data, 0, data.length);
        }
        return null;
    }

    private void putAll(JavaClass[] jclist){
        for(JavaClass jc: jclist){
            targetClasses.put(jc.getClassName(), jc);
        }
    }
}
