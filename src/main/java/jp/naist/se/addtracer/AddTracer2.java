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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jp.naist.se.addtracer.asm.AddTracerClassVisitor;
import jp.naist.se.addtracer.asm.ReadingMethodPropertiesVisitor;
import jp.sourceforge.talisman.xmlcli.CommandLinePlus;
import jp.sourceforge.talisman.xmlcli.OptionsBuilder;
import jp.sourceforge.talisman.xmlcli.XmlCliConfigurationException;
import jp.sourceforge.talisman.xmlcli.builder.OptionsBuilderFactory;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.xml.sax.SAXException;

/**
 * <p>
 * The class for injecting tracer code (monitoring code) into target classes.
 * </p>
 *
 * @author Haruaki TAMADA
 */
public class AddTracer2{
    /**
     * Default constructor.
     */
    public AddTracer2(){
    }

    public void perform(List<URI> list, String dest){
        for(Iterator<URI> i = list.iterator(); i.hasNext(); ){
            URI uri = i.next();
            try{
                addTrace(uri.toURL().openStream(), dest);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * inject tracer code into given class file and returned tracer
     * code injected class file.
     * @throws IOException 
     */
    public void addTrace(InputStream in, String dest) throws IOException{
        ClassReader reader = new ClassReader(in);
        ReadingMethodPropertiesVisitor lnrv = new ReadingMethodPropertiesVisitor();
        reader.accept(lnrv, 0);
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        AddTracerClassVisitor visitor = new AddTracerClassVisitor(
            writer, lnrv.getLineNumberTable(), lnrv.getLocalVariableTable()
        );
        reader.accept(visitor, 0);

        String className = visitor.getClassName();
        byte[] data = writer.toByteArray();
        dump(dest, className, data);
    }

    private void dump(String dest, String className, byte[] data) throws IOException{
        File destFile = new File(dest);
        String path = className.replace('.', '/') + ".class";
        File target = new File(destFile, path);

        if(!target.getParentFile().exists()){
            target.getParentFile().mkdirs();
        }
        OutputStream out = null;
        try{
            out = new FileOutputStream(target);
            out.write(data);
        } finally{
            if(out != null){
                out.close();
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
        URL url = AddTracer2.class.getResource(
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
        URL url = AddTracer2.class.getResource(
            "/META-INF/options.xml"
        );
        OptionsBuilder odp = factory.createBuilder(url);

        return odp.buildOptions();
    }

    public static void main(String[] args) throws Exception{
        Options options = buildOptions();
        CommandLineParser parser = new PosixParser();
        CommandLinePlus line = new CommandLinePlus(parser.parse(options, args));
        boolean exitFlag = false;

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

        List<URI> list = new ArrayList<URI>();
        AddTracer2 tracer = new AddTracer2();

        String[] arguments = line.getArgs();
        for(int i = 0; i < arguments.length; i++){
            if(arguments[i].endsWith(".class")){
                list.add(new File(arguments[i]).toURI());
            }
            else if(arguments[i].endsWith(".jar") || arguments[i].endsWith(".zip")){
                ZipFile file = new ZipFile(arguments[i]);
                URI uri = new File(arguments[i]).toURI();
                for(Enumeration<?> e = file.entries(); e.hasMoreElements(); ){
                    ZipEntry entry = (ZipEntry)e.nextElement();
                    String entryName = entry.getName();
                    if(entryName.endsWith(".class")){
                        list.add(new URI("jar:" + uri + "!/" + entryName));
                    }
                }
            }
        }

        if(!exitFlag){
            if(list.size() == 0){
                printHelp(options);
            }
            else{
                tracer.perform(list, line.getOptionValue("dest"));
            }
        }
    }
}
