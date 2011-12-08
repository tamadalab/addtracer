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

/*
 * $Id: AddTracer.java 110 2006-09-19 12:10:06Z harua-t $
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * @author Haruaki TAMADA
 * @version $Revision$ $Date$
 */
public class AddTracerTransformer implements ClassFileTransformer{
    private AddTracer tracer;
    private Pattern pattern;

    public AddTracerTransformer(AddTracer tracer, String patternString){
        this.tracer = tracer;
        if(patternString == null){
            patternString = "^[^((jp\\.naist\\.se\\.)|(jp\\.cafebabe\\.))].*";
        }
        pattern = Pattern.compile(patternString);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer){

        if(loader != null && pattern.matcher(className).matches()){
            JavaClass javaClass = buildJavaClass(className, classfileBuffer);
            JavaClass transformed = tracer.addTrace(javaClass);

            return transformed.getBytes();
        }

        return null;
    }

    private JavaClass buildJavaClass(String className, byte[] data){
        ByteArrayInputStream in = null;

        try{
            in = new ByteArrayInputStream(data);
            String name = className.replace('.', '/');

            ClassParser parser = new ClassParser(in, name + ".class");
            return parser.parse();
        } catch(IOException e){
        } finally{
            try{
                in.close();
            } catch(IOException e){
            }
        }
        return null;
    }
}
