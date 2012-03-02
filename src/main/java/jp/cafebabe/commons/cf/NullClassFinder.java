package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;

/**
 * @author Haruaki TAMADA
 */
public class NullClassFinder extends AbstractClassFinder{
    @Override
    public ClassObject[] findClass(File file) throws IOException{
        return new ClassObject[0];
    }
}
