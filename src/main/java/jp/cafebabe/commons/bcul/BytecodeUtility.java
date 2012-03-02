package jp.cafebabe.commons.bcul;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LineNumberGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import jp.cafebabe.commons.lang.Utility;

/**
 * @author Haruaki TAMADA
 */
public final class BytecodeUtility{
    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String STATIC_INITIALIZER_NAME = "<clinit>";

    private BytecodeUtility(){
    }

    /**
     * convert given <code>Class</code> object to
     * <code>JavaClass</code>.
     */
    public static final JavaClass toJavaClass(Class<?> target){
        String className = target.getName();
        String name = className.replace('.', '/') + ".class";
        URL location = target.getResource("/" + name);
        String from = location.toString();

        try{
            ClassParser parser;
            if(from.startsWith("jar:")){
                String path = Utility.getAbsoluteJarPath(location);
                parser = new ClassParser(path, name);
            }
            else if(from.startsWith("file:/")){
                parser = new ClassParser(from.substring("file:/".length()));
            }
            else{
                parser = new ClassParser(location.openStream(), name.substring(name.lastIndexOf('/') + 1));
            }

            JavaClass jc = parser.parse();
            Repository.addClass(jc);

            return jc;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * convert given <code>JavaClass</code> object loaded from
     * location to <code>Class</code> object.
     */
    public static final Class<?> toClass(JavaClass target, URL location){
        return toClass(target, location, BytecodeUtility.class.getClassLoader());
    }

    /**
     * convert given <code>JavaClass</code> object loaded from
     * location to <code>Class</code> object with given <code>ClassLoader</code>.
     */
    public static final Class<?> toClass(JavaClass target, URL location, ClassLoader parent){
        try{
            URL[] urls = Utility.findDependencies(location);
            URLClassLoader urlloader = new URLClassLoader(urls, parent);
            BCULClassLoader loader = new BCULClassLoader(new JavaClass[] { target }, urlloader);

            return loader.loadClass(target.getClassName());
        } catch(Exception e){
        }
        return null;
    }

    public static final Class<?>[] toClasses(JavaClass[] targets, URL location){
        return toClasses(targets, location, BytecodeUtility.class.getClassLoader());
    }

    public static final Class<?>[] toClasses(JavaClass[] targets, URL location, ClassLoader parent){
        try{
            URL[] urls = Utility.findDependencies(location);
            URLClassLoader urlloader = new URLClassLoader(urls, parent);
            BCULClassLoader loader = new BCULClassLoader(targets, urlloader);

            Class<?>[] classes = new Class<?>[targets.length];
            for(int i = 0; i < classes.length; i++){
                classes[i] = loader.loadClass(targets[i].getClassName());
            }
            return classes;

        } catch(Exception e){
        }
        return null;
    }

    /**
     * check given method name is constructor name.
     */
    public static final boolean isConstructor(String methodName){
        return methodName != null && methodName.equals(CONSTRUCTOR_NAME);
    }

    /**
     * check given method name is static initializer name.
     */
    public static final boolean isStaticInitializer(String methodName){
        return methodName != null && methodName.equals(STATIC_INITIALIZER_NAME);
    }

    /**
     * 
     */
    public static final boolean checkSuperClass(String className, String superClassName){
        JavaClass[] jc;
        try{
            jc = Repository.getSuperClasses(superClassName);
            for(int i = 0; i < jc.length; i++){
                if(className.equals(jc[i].getClassName())){
                    return true;
                }
            }
            return className.equals(superClassName);
        } catch(ClassNotFoundException e){
            // ignore exception
        }
        return false;
    }

    public static final ObjectType getWrapperClassType(Type type){
        ObjectType resultType = null;

        if(type instanceof BasicType){
            if(type.equals(Type.BOOLEAN))     resultType = new ObjectType("java.lang.Boolean");
            else if(type.equals(Type.INT))    resultType = new ObjectType("java.lang.Integer");
            else if(type.equals(Type.BYTE))   resultType = new ObjectType("java.lang.Byte");
            else if(type.equals(Type.CHAR))   resultType = new ObjectType("java.lang.Character");
            else if(type.equals(Type.DOUBLE)) resultType = new ObjectType("java.lang.Double");
            else if(type.equals(Type.FLOAT))  resultType = new ObjectType("java.lang.Float");
            else if(type.equals(Type.LONG))   resultType = new ObjectType("java.lang.Long");
            else if(type.equals(Type.SHORT))  resultType = new ObjectType("java.lang.Short");
        }
        return resultType;
    }

    /**
     * update targeters from oldHandle to newHandle.
     * If oldHandle has no targeters, this method does nothing.
     */
    public static final void updateTargeters(InstructionHandle oldHandle, InstructionHandle newHandle){
        if(oldHandle.hasTargeters()){
            InstructionTargeter[] targeters = oldHandle.getTargeters();
            for(int i = 0; i < targeters.length; i++){
                targeters[i].updateTarget(oldHandle, newHandle);
            }
        }
    }

    /**
     * update exception handles from oldHandle to newHandle.
     */
    public static final void updateExceptionHandlers(CodeExceptionGen[] handlers, InstructionHandle oldHandle,
                                               InstructionHandle newHandle){
        for(int i = 0; i < handlers.length; i++){
            InstructionHandle start = handlers[i].getStartPC();
            InstructionHandle end   = handlers[i].getEndPC();

            if(start.equals(oldHandle) && newHandle != null){
                handlers[i].setStartPC(newHandle);
            }
            else if(end.equals(oldHandle) && newHandle != null){
                handlers[i].setEndPC(newHandle);
            }
        }
    }

    public static final void updateLineNumberTables(MethodGen method){
        LineNumberGen[] numbers = method.getLineNumbers();
        for(LineNumberGen number: numbers){
            if(number.getInstruction() == null){
                method.removeLineNumber(number);
            }
        }
    }
}
