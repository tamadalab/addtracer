package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * The abstract class for finding specify classes.
 *
 * @author Haruaki TAMADA
 */
public abstract class AbstractClassFinder implements ClassFinder{
    @Override
    public ClassObject[] findClass(String path) throws IOException{
        return findClass(new File(path));
    }

    @Override
    public ClassObject[] findClass(String[] pathList) throws IOException{
        File[] fileList = new File[pathList.length];
        for(int i = 0; i < fileList.length; i++){
            fileList[i] = new File(pathList[i]);
        }

        return findClass(fileList);
    }

    @Override
    public ClassObject[] findClass(File[] fileList) throws IOException{
        List<ClassObject> list = new ArrayList<ClassObject>();
        for(int i = 0; i < fileList.length; i++){
            ClassObject[] wrappers = findClass(fileList[i]);
            for(int j = 0; j < wrappers.length; j++){
                list.add(wrappers[j]);
            }
        }

        return list.toArray(new ClassObject[list.size()]);
    }

    @Override
    public abstract ClassObject[] findClass(File file) throws IOException;
}

