/*                          Apache License
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package jp.naist.se.ant.taskdefs;

import java.io.File;
import java.io.IOException;

import jp.naist.se.addtracer.AddTracer;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * <p>
 * {@link AddTracer <code>AddTracer</code>} Task for Apache Ant.
 * </p>
 * <h3>How to use</h3>
 * <pre class="code">
 * &lt;taskdef name=&quot;addtracer&quot;
 *          classname=&quot;jp.naist.se.addtracer.AddTracer&quot;
 *          classpathref=&quot;project.class.path&quot;
 * /&gt;
 * &lt;mkdir dir=&quot;${output.dir}&quot; /&gt;
 * &lt;addtracer destdir=&quot;${output.dir}&quot;
 *            basedir=&quot;${compiled.dir}&quot;&gt;
 *   &lt;include name=&quot;**&#047;*.class&quot; /&gt;
 *   &lt;exclude name=&quot;**&#047;*Test.class&quot; /&gt;
 * &lt;/addtracer&gt;</pre>
 * <h4>Parameters</h4>
 * <table>
 *   <thead>
 *     <tr>
 *       <th>Attribute</th>
 *       <th>Description</th>
 *       <th>Required</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>baseDir</code></td>
 *       <td>base directory for searching class files.</td>
 *       <td>No, Default is project directory.</td>
 *     </tr>
 *     <tr>
 *       <td><code>destDir</code></td>
 *       <td>Output directory</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>lastModifiedCheck</code></td>
 *       <td>Check the last modified time of file for running AddTracer or not.</td>
 *       <td>No, Default is true</td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 136 $ $Date: 2006-10-12 12:02:44 +0900 (Thu, 12 Oct 2006) $
 */
public class AddTracerTask extends MatchingTask{
    /**
     * destination.
     */
    private String destdir;
    /**
     * Class files searching directory.
     */
    private String basedir;
    /**
     * check last modified time of file for running AddTracer or not.
     */
    private boolean lastModifiedCheck;

    public void setDestdir(String destdir){
        this.destdir = destdir;
    }

    public String getDestdir(){
        return destdir;
    }

    public void setBasedir(String basedir){
        this.basedir = basedir;
    }

    public String getBasedir(){
        return basedir;
    }

    public void setLastmodifiedcheck(String value){
        lastModifiedCheck = (value.equalsIgnoreCase("true") ||
                             value.equalsIgnoreCase("on")   ||
                             value.equalsIgnoreCase("yes"));
    }

    public boolean getLastmodifiedcheck(){
        return lastModifiedCheck;
    }

    /**
     * execute AddTracer
     */
    @Override
    public void execute() throws BuildException{
        DirectoryScanner scanner;
        String[]         list;

        if(getBasedir() == null){
            setBasedir(getProject().resolveFile(".").getPath());
        }
        if(getDestdir() == null){
            throw new BuildException("destdir attribute must be set!");
        }
        log("Transforming into: " + new File(getDestdir()).getAbsolutePath(),
            Project.MSG_INFO);

        AddTracer tracer = new AddTracer();
        // find the files/directories
        scanner = getDirectoryScanner(new File(getBasedir()));

        // get a list of files to work on
        list = scanner.getIncludedFiles();
        for(int i = 0; i < list.length; i++){
            if(!list[i].endsWith(".class")){
                log(list[i] + " is not class file", Project.MSG_WARN);
            }
            else{
                addTrace(tracer, new File(getBasedir()), list[i], new File(getDestdir()));
            }
        }
    }

    /**
     *
     */
    private void addTrace(AddTracer tracer, File baseDir, String targetFile,
                          File destDir) throws BuildException{

        File target = new File(baseDir, targetFile);
        File output = new File(destDir, targetFile);

        if(getLastmodifiedcheck() && output.exists() &&
           output.lastModified() > target.lastModified()){
            return;
        }
        try{
            ClassParser parser = new ClassParser(target.getPath());

            JavaClass jc = parser.parse();
            String className = jc.getClassName();
            className = className.replace('.', '/');
            log("add trace in " + className.replace('/', '.'), Project.MSG_INFO);

            jc = tracer.addTrace(jc);


            File outFile = new File(destDir, className + ".class");

            ensureDirectoryFor(outFile);

            log("output to " + outFile);

            jc.dump(outFile);
        } catch(IOException e){
            throw new BuildException(e.getMessage(), e);
        }
    }


    /**
     * create directories as needed
     */
    private void ensureDirectoryFor(File targetFile) throws BuildException{
        File directory = new File(targetFile.getParent());
        if(!directory.exists()){
            if(!directory.mkdirs()){
                throw new BuildException("Unable to create directory: "
                                         + directory.getAbsolutePath() );
            }
        }
    }
}
