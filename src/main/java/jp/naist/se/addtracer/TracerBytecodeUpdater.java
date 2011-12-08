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
 * $Id: TracerBytecodeUpdater.java 139 2006-10-24 04:38:55Z harua-t $
 */

import jp.cafebabe.commons.bcul.updater.BytecodeUpdater;
import jp.cafebabe.commons.bcul.updater.InstructionUpdateHandler;

/**
 *
 * @author Haruaki TAMADA
 * @version $Revision: 139 $ $Date: 2006-10-24 13:38:55 +0900 (Tue, 24 Oct 2006) $
 */
public class TracerBytecodeUpdater extends BytecodeUpdater{
    public static final String ARITHMETIC_INSTRUCTION_UPDATE_HANDLER       = "arithmetic";
    public static final String AFTER_INCREMENT_INSTRUCTION_UPDATE_HANDLER  = "after_increment";
    public static final String BEFORE_INCREMENT_INSTRUCTION_UPDATE_HANDLER = "before_inrement";
    public static final String ARRAY_COPY_INSTRUCTION_UPDATE_HANDLER       = "array_copy";
    public static final String ARRAY_LENGTH_INSTRUCTION_UPDATE_HANDLER     = "array_length";
    public static final String ARRAY_STORE_INSTRUCTION_UPDATE_HANDLER      = "array_store";
    public static final String ARRAY_STORE_WIDE_INSTRUCTION_UPDATE_HANDLER = "array_store_wide";
    public static final String ARRAY_LOAD_INSTRUCTION_UPDATE_HANDLER       = "array_load";
    public static final String LOCAL_VARIABLE_INSTRUCTION_UPDATE_HANDLER   = "local_variable";
    public static final String PUT_FIELD_INSTRUCTION_UPDATE_HANDLER        = "put_field";
    public static final String GET_FIELD_INSTRUCTION_UPDATE_HANDLER        = "get_field";
    public static final String ATHROW_INSTRUCTION_UPDATE_HANDLER           = "athrow";
    public static final String START_INSTRUCTION_UPDATE_HANDLER            = "start";
    public static final String RETURN_INSTRUCTION_UPDATE_HANDLER           = "return";
    public static final String CONSTANT_PUSH_INSTRUCTION_UPDATE_HANDLER    = "constants";
    public static final String INVOKE_INSTRUCTION_UPDATE_HANDLER           = "invoke";

    public TracerBytecodeUpdater(){
    }

    public TracerBytecodeUpdater(String format){
        registerHandler(format);
    }

    public void registerHandler(String format){
        String packageName = getClass().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        addHandler(ARITHMETIC_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ArithmeticInstructionUpdateHandler"));
        addHandler(AFTER_INCREMENT_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "AppendIncrementInstructionUpdateHandler"));
        addHandler(BEFORE_INCREMENT_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "InsertIncrementInstructionUpdateHandler"));

        addHandler(ARRAY_COPY_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ArrayCopyInstructionUpdateHandler"));
        addHandler(ARRAY_LENGTH_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ArrayLengthInstructionUpdateHandler"));
        addHandler(ARRAY_LOAD_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ArrayLoadInstructionUpdateHandler"));
        addHandler(ARRAY_STORE_WIDE_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ArrayStoreWideInstructionUpdateHandler"));
        addHandler(ARRAY_STORE_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ArrayStoreNonWideInstructionUpdateHandler"));

        addHandler(ATHROW_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "AthrowInstructionUpdateHandler"));

        addHandler(CONSTANT_PUSH_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ConstantPushInstructionUpdateHandler"));

        addHandler(GET_FIELD_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "GetFieldInstructionUpdateHandler"));
        addHandler(PUT_FIELD_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "PutFieldInstructionUpdateHandler"));

        addHandler(LOCAL_VARIABLE_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "LocalVariableInstructionUpdateHandler"));

        addHandler(RETURN_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "ReturnInstructionUpdateHandler"));
        addHandler(START_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "StartOfMethodInstructionUpdateHandler"));
        addHandler(INVOKE_INSTRUCTION_UPDATE_HANDLER,
                   createInstance(packageName, format, "InvokeInstructionUpdateHandler"));
    }

    private InstructionUpdateHandler createInstance(String packageName, String format, String className){
        String cn = packageName + "." + format + "." + className;

        try{
            Class<?> c = Class.forName(cn);
            Object o = c.newInstance();

            return (InstructionUpdateHandler)o;
        } catch(ClassNotFoundException e){
            try{
                Class<?> c = Class.forName(packageName + ".common." + className);
                Object o = c.newInstance();
                return (InstructionUpdateHandler)o;
            } catch(ClassNotFoundException ee){
                ee.printStackTrace();
            } catch(Exception ee){
                ee.printStackTrace();
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
