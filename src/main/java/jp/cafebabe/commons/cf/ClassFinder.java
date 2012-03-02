package jp.cafebabe.commons.cf;

import java.io.File;
import java.io.IOException;

/**
 * The interface for finding classes.
 *
 * @author Haruaki TAMADA
 */
public interface ClassFinder{
    public ClassObject[] findClass(String path) throws IOException;

    public ClassObject[] findClass(String[] pathList) throws IOException;

    public ClassObject[] findClass(File file) throws IOException;

    public ClassObject[] findClass(File[] fileList) throws IOException;
}
