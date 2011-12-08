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

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

import jp.cafebabe.commons.bcul.RepositoryWrapper;

import org.apache.bcel.Repository;

/**
 * @author Haruaki TAMADA
 * @version $Revision$ $Date$
 */
public class AddTracerAgent{
    /**
     * register transformer for AddTracer into Instrumentation class.
     */
    public void perform(String agentArgs, Instrumentation inst){
        Repository.setRepository(new RepositoryWrapper(Repository.getRepository()));

        Map<String, String> options = parseOption(agentArgs);

        AddTracer tracer = initializeAddTracer(options);

        String pattern = options.get("pattern");

        inst.addTransformer(new AddTracerTransformer(tracer, pattern));
    }

    private AddTracer initializeAddTracer(Map<String, String> options){
        AddTracer addTracer = new AddTracer(options.get("format"), options.containsKey("verbose"));

        if(options.containsKey("disable-arithmetic"))           addTracer.setArithmeticInstruction(false);
        if(options.containsKey("disable-constant"))             addTracer.setConstant(false);
        if(options.containsKey("disable-method"))               addTracer.setMethod(false);
        if(options.containsKey("disable-throws"))               addTracer.setThrow(false);
        if(options.containsKey("disable-local-variable"))       addTracer.setLocalVariables(false);
        if(options.containsKey("disable-field-instructuction")) addTracer.setFieldInstruction(false);
        if(options.containsKey("disable-array-instructuction")) addTracer.setArray(false);

        return addTracer;
    }

    /**
     * parse given options which format is ``name1=value1,name2=value2,...''
     */
    private Map<String, String> parseOption(String args){
        Map<String, String> options = new HashMap<String, String>();

        if(args != null){
            String[] optionArray = args.split(",");
            for(String option: optionArray){
                if(option.indexOf("=") > 0){
                    int index = option.indexOf("@");
                    String key = option.substring(0, index);
                    String value = option.substring(index + 1);

                    options.put(key, value);
                }
                else{
                    options.put(option, null);
                }
            }
        }
        return options;
    }

    /**
     * premain
     */
    public static void premain(String agentArgs, Instrumentation inst){
        AddTracerAgent agent = new AddTracerAgent();

        agent.perform(agentArgs, inst);
    }
}
