package jp.cafebabe.commons.lang;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

/**
 * @author Haruaki TAMADA
 */
public class Utility{
    /**
     * jar ファイルからロードされたクラスの URL から jar ファイルのパスを取り出す．
     * <pre>jar:file://path/to/jar/file.jar!/class/path/ClassName.class</pre>
     * のような表現から
     * <pre>file://path/to/jar/file.jar</pre>
     * のような表現を取り出す．
     */
    public static String getJarFilePath(String path){
        if(path.startsWith("jar:")){
            if(path.indexOf("!") > 0){
                return path.substring("jar:".length(), path.indexOf("!"));
            }
            else{
                return path.substring("jar:".length());
            }
        }
        return null;
    }

    /**
     * jar ファイルからロードされたクラスの URL から jar ファイルの絶対パスを取り出す．
     * ただし，jar ファイルはローカルファイルである必要がある．
     * <pre>jar:file://path/to/jar/file.jar!/class/path/ClassName.class</pre>
     * のような表現から
     * <pre>/path/to/jar/file.jar</pre>
     * のような表現を取り出す．
     */
    public static String getAbsoluteJarPath(URL url){
        String path = url.toString();
        if(path.startsWith("jar:file:/")){
            if(path.indexOf("!") > 0){
                path = path.substring("jar:file:/".length(), path.indexOf("!"));
            }
            else{
                path = path.substring("jar:file:/".length());
            }
        }
        else if(path.startsWith("file:")){
            path = path.substring("file:".length());
        }
        else{
            path = null;
        }
        if(path != null && !path.startsWith("/")){
            path = "/" + path;
        }
        return path;
    }

    public static String findMainClass(URL[] urls){
        for(URL u: urls){
            String name = findMainClass(u);
            if(name != null){
                return name;
            }
        }
        return null;
    }

    public static String findMainClass(URL url){
        String path = url.toString();
        String mainClass = null;

        if(path.startsWith("file:/") && path.endsWith(".jar")){
            try{
                File file = new File(path.substring("file:/".length()));
                JarFile jarfile = new JarFile(file);
                Manifest manifest = jarfile.getManifest();
                Attributes attributes = manifest.getMainAttributes();

                if(attributes.containsKey(Attributes.Name.MAIN_CLASS)){
                    mainClass = attributes.getValue(Attributes.Name.MAIN_CLASS);
                }
            } catch(IOException e){
            }
        }
        return mainClass;
    }

    /**
     * 与えられた jar ファイルを表す URL から jar ファイルを読み，
     * MANIFEST ファイルに記述されている Class-Path をも含む URL のリストを含む．
     */
    public static URL[] findDependencies(URL url) throws IOException{
        List<URL> urllist = new ArrayList<URL>();
        urllist.add(url);

        String path = url.toString();
        if(path.startsWith("file:/") && path.endsWith(".jar")){
            File file = new File(path.substring("file:/".length()));
            JarFile jarfile = new JarFile(file);
            URL[] urls = findDependencies(file.getParentFile(), jarfile);
            for(URL u: urls){
                urllist.add(u);
            }
        }

        return urllist.toArray(new URL[urllist.size()]);
    }

    public static URL[] findDependencies(File base, JarFile jarfile) throws IOException{
        List<URL> urllist = new ArrayList<URL>();

        Manifest manifest = jarfile.getManifest();
        Attributes attributes = manifest.getMainAttributes();

        if(attributes.containsKey(Attributes.Name.CLASS_PATH)){
            if(base == null) base = new File(".");

            String classpathlist = attributes.getValue(Attributes.Name.CLASS_PATH);
            String[] classpath = classpathlist.split(" ");

            for(String p: classpath){
                File targetFile = new File(base, p);
                urllist.add(targetFile.toURI().toURL());
            }
        }
        return urllist.toArray(new URL[urllist.size()]);
    }
}
