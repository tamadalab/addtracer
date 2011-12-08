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

import jp.cafebabe.commons.bcul.updater.AbstractInstructionUpdateHandler;
import jp.cafebabe.commons.bcul.updater.InstructionUpdateHandler;
import jp.cafebabe.commons.bcul.updater.UpdateData;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

/**
 * <p>
 * {@link jp.naist.se.addtracer.AddTracer <code>AddTracer</code>} $B~A%9%NFq%=%9~A%9~A%9~A%9~A%9~A%9~A%9(B
 * {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>}$B~A%9(BD
 * </p>
 *
 * @author Haruaki TAMADA
 */
public abstract class TracerInstructionUpdateHandler extends AbstractInstructionUpdateHandler implements InstructionUpdateHandler{
    public static final String STRINGBUFFER = "java.lang.StringBuffer";
    public static final String SYSTEM       = "java.lang.System";
    public static final String PRINTSTREAM  = "java.io.PrintStream";
    public static final String CLASS        = "java.lang.Class";

    public static final Type PRINTSTREAM_TYPE  = new ObjectType(PRINTSTREAM);

    /**
     * Default constructor.
     */
    public TracerInstructionUpdateHandler(){
    }

    @Override
    public abstract InstructionList updateInstruction(InstructionHandle handle, UpdateData data);

    /**
     * <p>
     * System#out $B~A%9~A%9~A%9(BI$B~A%9(By$B~A%9~A%9~A%9~A%9~A%9(Bh$B~A%9(BX$B~A%:~A%9(Bb$B~A%9(BN$B~A%9%N@Q$`Lq%=%9~A%9!,$q%=%9%d$q%=%9~A%9(BD
     * </p><p>
     * $B~A%9~A%9~A%9(Bs$B~A%9(BO: ...
     * </p><p>
     * $B~A%9~A%9~A%9(Bs$B~A%9~A%9(B: out
     * </p>
     */
    public InstructionList pushSystemOut(UpdateData data){
        InstructionList list = new InstructionList();
        list.append(data.getFactory().createGetStatic(SYSTEM, "out", PRINTSTREAM_TYPE));

        return list;
    }

    /**
     * <p>
     * System#out $B~A%9~A%9~A%9(BI$B~A%9(By$B~A%9~A%9~A%9~A%9~A%9(Bh$B~A%9(BX$B~A%:~A%9(Bb$B~A%9(BN$B~A%9%N@Q$_!$(BStringBuffer $B~A%9~A%9(B
     * $B~A%9~A%9~A%9~A%9~A%9%H%*~A%9(By$B~A%9~A%9~A%9~A%9~A%9(Bh$B~A%9(BX$B~A%:~A%9(Bb$B~A%9(BN$B~A%9%N@Q$`Lq%=%9~A%9!,$q%=%9%d$q%=%9~A%9(BD
     * </p><p>
     * $B~A%9~A%9~A%9(Bs$B~A%9(BO: ...
     * </p><p>
     * $B~A%9~A%9~A%9(Bs$B~A%9~A%9(B: stringbuffer, out
     * </p>
     */
    public InstructionList pushSystemOutAndStringBuffer(UpdateData data){
        InstructionList list = new InstructionList();

        list.append(data.getFactory().createGetStatic(SYSTEM, "out", PRINTSTREAM_TYPE));
        list.append(data.getFactory().createNew(STRINGBUFFER));
        list.append(new DUP());
        list.append(data.getFactory().createInvoke(STRINGBUFFER, "<init>", Type.VOID,
                                                   Type.NO_ARGS, Constants.INVOKESPECIAL));
        return list;
    }

    /**
     * <p>
     * System#out $B~A%9~A%9~A%9(BI$B~A%9(By$B~A%9~A%9~A%9~A%9~A%9(Bh$B~A%9(BX$B~A%:~A%9(Bb$B~A%9(BN$B~A%9%N@Q$_!$(BStringBuffer $B~A%9~A%9(B
     * $B~A%9~A%9~A%9~A%9~A%9%H%*~A%9(By$B~A%9~A%9~A%9~A%9~A%9(Bh$B~A%9(BX$B~A%:~A%9(Bb$B~A%9(BN$B~A%9%N@Q$`Lq%=%9~A%9!,$q%=%9%d$q%=%9~A%9(BD$B~A%9~A%9~A%9%U$H$q%=%9~A%9(BC
     * $B~A%9~A%9~A%9(B StringBuffer $B~A%9~A%9(B append $B~A%9~A%9~A%9~A%9%H$q%=%9~A%9~A%9(BD
     * </p><p>
     * $B~A%9~A%9~A%9(Bs$B~A%9(BO: ...
     * </p><p>
     * $B~A%9~A%9~A%9(Bs$B~A%9~A%9(B: stringbuffer, out
     * </p>
     */
    public InstructionList pushSystemOutAndStringBuffer(UpdateData data, String value){
        InstructionList list = pushSystemOutAndStringBuffer(data);
        list.append(getAppendInstructions(data, value));

        return list;
    }

    /**
     * <p>
     * $B~A%9~A%9~A%9~A%9~A%9%N%*~A%9(By$B~A%9~A%9~A%9~A%9~A%9(Bh$B~A%9(BX$B~A%:~A%9(Bb$B~A%9(BN$B~A%9%N@Q$^$q%=%9%H$q%=%9~A%9~A%9%X$q%=%9~A%9~A%9(B StringBuffer $B~A%9~A%9(B append $B~A%9~A%9~A%9%H!$(B
     * toString, println $B~A%9$(D+-(BsD
     * </p><p>
     * sO: stringbuffer, out
     * </p><p>
     * s: ...
     * </p>
     */
    public InstructionList getToStringAndPrintln(UpdateData data, String value){
        InstructionList list = getAppendInstructions(data, value);
        list.append(getToStringAndPrintln(data));

        return list;
    }

    /**
     * <p>
     * toString, println $(D+-(BsD
     * </p><p>
     * sO: stringbuffer, out
     * </p><p>
     * s: ...
     * </p>
     */
    public InstructionList getToStringAndPrintln(UpdateData data){
        InstructionList list = new InstructionList();

        list.append(data.getFactory().createInvoke(
            STRINGBUFFER, "toString", Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL)
        );
        list.append(data.getFactory().createInvoke(
            PRINTSTREAM, "println", Type.VOID, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL)
        );
        return list;
    }

    /**
     * <p>
     * IyhX^bN$(D&x*%(B StringBuffer  append D
     * </p><p>
     * sO: stringbuffer
     * </p><p>
     * s: ...
     * </p>
     */
    public InstructionList getAppendInstructions(UpdateData data, String value){
        InstructionList list = new InstructionList();
        list.append(new PUSH(data.getConstantPoolGen(), value));
        list.append(getAppendInstructions(data, Type.STRING));

        return list;
    }

    /**
     * <p>
     * IyhX^bN$(D&x*%(B StringBuffer  append D
     * </p><p>
     * sO: stringbuffer
     * </p><p>
     * s: ...
     * </p>
     */
    public InstructionList getAppendInstructions(UpdateData data, Number number){
        InstructionList list = new InstructionList();
        list.append(new PUSH(data.getConstantPoolGen(), number));
        list.append(getAppendInstructions(data, Type.getType(number.getClass())));

        return list;
    }

    /**
     * <p>
     * StringBuffer  append lIyhX^bN$(D&x*%(BC
     * \bh append Dtype  append l^D
     * </p><p>
     * sO: value, stringbuffer
     * </p><p>
     * s: ...
     * </p>
     */
    public InstructionList getAppendInstructions(UpdateData data, Type type){
        InstructionList list = new InstructionList();

        list.append(data.getFactory().createInvoke(
            STRINGBUFFER, "append", Type.STRINGBUFFER, new Type[] { type, }, Constants.INVOKEVIRTUAL)
        );
        return list;
    }

    /**
     * <p>
     * StringBuffer ^D
     * </p><p>
     * CStringBuffer  String OIuWFNg^C
     * Object ^ append $(D*%$B'q(BoKvD
     * </p><p>
     * ^^L$(D=y&u(BsCK^D
     * </p>
     */
    public Type getStringBufferArgumentType(Type type){
        Type t = type;
        if(type != Type.STRINGBUFFER && type != Type.STRING &&
           (type instanceof ObjectType || type instanceof ArrayType)){
            t = Type.OBJECT;
        }
        if(type == Type.BYTE || type == Type.BOOLEAN || type == Type.SHORT ||
           type == Type.CHAR){
            t = Type.INT;
        }

        return t;
    }

    public String getTargetClassName(FieldOrMethod form, ConstantPoolGen poolGen){
        ReferenceType type = form.getReferenceType(poolGen);
        if(type instanceof ArrayType){
            throw new IllegalArgumentException("array type");
        }

        return ((ObjectType)type).getClassName();
    }
}
