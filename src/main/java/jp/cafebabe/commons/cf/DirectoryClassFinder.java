package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Haruaki TAMADA
 */
public class DirectoryClassFinder extends AbstractClassFinder{
    private DefaultClassFinder finder;

    public DirectoryClassFinder(DefaultClassFinder finder){
        this.finder = finder;
    }

    @Override
    public ClassObject[] findClass(File file) throws IOException{
        List<File> list = new ArrayList<File>();

        if(file.isDirectory()){
            scanDirectory(list, file);
        }

        List<ClassObject> resultList = new ArrayList<ClassObject>();
        for(Iterator<File> i = list.iterator(); i.hasNext(); ){
            File targetFile = i.next();
            ClassObject[] wrappers = finder.findClass(targetFile);

            for(int j = 0; j < wrappers.length; j++){
                resultList.add(wrappers[j]);
            }
        }
        return resultList.toArray(new ClassObject[resultList.size()]);
    }

    private void scanDirectory(List<File> list, File file){
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i++){
            if(files[i].isDirectory()){
                scanDirectory(list, files[i]);
            }
            else{
                list.add(files[i]);
            }
        }
    }
}
