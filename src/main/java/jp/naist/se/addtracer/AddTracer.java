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
package jp.naist.se.addtracer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jp.cafebabe.commons.bcul.RepositoryWrapper;
import jp.cafebabe.commons.bcul.updater.BytecodeUpdater;
import jp.sourceforge.talisman.xmlcli.CommandLinePlus;
import jp.sourceforge.talisman.xmlcli.OptionsBuilder;
import jp.sourceforge.talisman.xmlcli.XmlCliConfigurationException;
import jp.sourceforge.talisman.xmlcli.builder.OptionsBuilderFactory;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.xml.sax.SAXException;

/**
 * <p>
 * The class for injecting tracer code (monitoring code) into target classes.
 * </p>
 *
 * @author Haruaki TAMADA
 */
public class AddTracer{
    private BytecodeUpdater updater;
    private boolean verboseMode = true;
    private String format;

    /**
     * Default constructor.
     */
    public AddTracer(){
    }

    /**
     * Constructor with tracer format.
     */
    public AddTracer(String format){
        this(format, true);
    }

    /**
     * Constructor with tracer format and verbose mode.
     */
    public AddTracer(String format, boolean verboseMode){
        this.format = format;
        updater = BytecodeUpdaterFactory.getInstance().getUpdater(format);
        this.verboseMode = verboseMode;
    }

    /**
     * returns the tracer format.
     */
    public String getFormat(){
        return format;
    }

    public void setArithmeticInstruction(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.ARITHMETIC_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.AFTER_INCREMENT_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.BEFORE_INCREMENT_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setInvokeInstruction(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.INVOKE_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setConstant(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.CONSTANT_PUSH_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setMethod(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.START_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.RETURN_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setThrow(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.ATHROW_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setLocalVariables(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.LOCAL_VARIABLE_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setFieldInstruction(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.GET_FIELD_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.PUT_FIELD_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    public void setArray(boolean flag){
        if(!flag){
            updater.removeHandler(TracerBytecodeUpdater.ARRAY_COPY_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.ARRAY_LENGTH_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.ARRAY_STORE_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.ARRAY_STORE_WIDE_INSTRUCTION_UPDATE_HANDLER);
            updater.removeHandler(TracerBytecodeUpdater.ARRAY_LOAD_INSTRUCTION_UPDATE_HANDLER);
        }
    }

    /**
     * inject tracer code into given class file and returned tracer
     * code injected class file.
     */
    public JavaClass addTrace(JavaClass jc){
        ClassGen classGen = updater.update(new ClassGen(jc));

        JavaClass jcc = classGen.getJavaClass();
        updater.clear();

        return jcc;
    }

    private void dump(JavaClass[] ff, String dest, String[] src) throws IOException{
        String destination = ".";
        if(dest != null){
            destination = dest;
        }

        for(int i = 0; i < ff.length; i++){
            dump(ff[i], destination);
        }
    }

    private void dump(JavaClass jc, String dest) throws IOException{
        File f = new File(dest);
        String className = jc.getClassName().replace('.', '/') + ".class";
        File target = new File(f, className);
        if(!target.getParentFile().exists()){
            target.getParentFile().mkdirs();
        }
        jc.dump(new FileOutputStream(target));
    }

    private void initialize(){
        if(verboseMode){
            System.out.println("Format: " + getFormat());

            for(Iterator<String> i = updater.handlerNames(); i.hasNext(); ){
                String name = (String)i.next();
                if(updater.getHandler(name) != null){
                    System.out.println(
                        name + ": " + updater.getHandler(name).getClass().getName()
                    );
                }
            }
        }
    }

    private static void printHelp(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("addtracer [options...] <classes...|jars...>", options);

        System.out.println();
        printVersion();
    }

    private static void printVersion(){
        System.out.println("AddTracer version 2.1");
        System.out.println("Copyright (C) 2003-2011 by Haruaki TAMADA");
    }

    private static void printLicense() throws IOException{
        URL url = AddTracer.class.getResource(
            "/META-INF/license.txt"
        );

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = null;
        while((line = in.readLine()) != null){
            System.out.println(line);
        }
        in.close();
    }

    private static Options buildOptions() throws IOException, SAXException, XmlCliConfigurationException{
        OptionsBuilderFactory factory = OptionsBuilderFactory.getInstance();
        URL url = AddTracer.class.getResource(
            "/META-INF/options.xml"
        );
        OptionsBuilder odp = factory.createBuilder(url);

        return odp.buildOptions();
    }

    private static JavaClass parseClassFile(ClassParser parser) throws IOException{
        JavaClass jc = parser.parse();
        Repository.addClass(jc);

        return jc;
    }

    private static void setDisables(AddTracer tracer, CommandLinePlus line){
        if(line.hasOption("disable-arithmetic"))         tracer.setArithmeticInstruction(false);
        if(line.hasOption("disable-constant"))           tracer.setConstant(false);
        if(line.hasOption("disable-method"))             tracer.setMethod(false);
        if(line.hasOption("disable-throw"))              tracer.setThrow(false);
        if(line.hasOption("disable-local-variable"))     tracer.setLocalVariables(false);
        if(line.hasOption("disable-field-instruction"))  tracer.setFieldInstruction(false);
        if(line.hasOption("disable-array-instruction"))  tracer.setArray(false);
        if(line.hasOption("disable-invoke-instruction")) tracer.setInvokeInstruction(false);
    }

    public static void main(String[] args) throws Exception{
        Repository.setRepository(new RepositoryWrapper(Repository.getRepository()));

        Options options = buildOptions();
        CommandLineParser parser = new PosixParser();
        CommandLinePlus line = new CommandLinePlus(parser.parse(options, args));
        boolean exitFlag = false;
        String format = null;

        if(line.hasOption("help")){
            printHelp(options);
            exitFlag = true;
        }
        else if(line.hasOption("version")){
            printVersion();
            exitFlag = true;
        }
        if(line.hasOption("license")){
            printLicense();
            exitFlag = true;
        }
        if(line.hasOption("format")){
            format = line.getOptionValue("format");
        }

        List<JavaClass> list = new ArrayList<JavaClass>();
        AddTracer tracer = new AddTracer(format, !line.hasOption("quiet"));

        setDisables(tracer, line);

        String[] arguments = line.getArgs();
        for(int i = 0; i < arguments.length; i++){
            if(arguments[i].endsWith(".class")){
                list.add(parseClassFile(new ClassParser(arguments[i])));
            }
            else if(arguments[i].endsWith(".jar") || arguments[i].endsWith(".zip")){
                ZipFile file = new ZipFile(arguments[i]);
                for(Enumeration<?> e = file.entries(); e.hasMoreElements(); ){
                    ZipEntry entry = (ZipEntry)e.nextElement();
                    String n = entry.getName();
                    if(n.endsWith(".class")){
                        list.add(parseClassFile(new ClassParser(arguments[i], n)));
                    }
                }
            }
        }

        if(!exitFlag){
            if(list.size() == 0){
                printHelp(options);
            }
            else{
                JavaClass[] jcs = (JavaClass[])list.toArray(new JavaClass[list.size()]);
                tracer.initialize();
                for(int i = 0; i < jcs.length; i++){
                    jcs[i] = tracer.addTrace(jcs[i]);
                }

                tracer.dump(jcs, line.getOptionValue("dest"), arguments);
            }
        }
    }
}
