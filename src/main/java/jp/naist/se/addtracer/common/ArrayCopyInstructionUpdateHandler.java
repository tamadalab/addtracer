/*                         Apache License
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
package jp.naist.se.addtracer.common;

/*
 * $Id: ArrayCopyInstructionUpdateHandler.java 111 2006-09-19 12:10:31Z harua-t $
 */

import jp.cafebabe.commons.bcul.updater.MethodCreatable;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.Type;

/**
 * 
 *
 * @author Haruaki TAMADA
 * @version $Revision: 111 $ $Date: 2006-09-19 21:10:31 +0900 (Tue, 19 Sep 2006) $
 */
public class ArrayCopyInstructionUpdateHandler extends TracerInstructionUpdateHandler implements MethodCreatable{
    private Method arraycopy = null;

    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        if(i instanceof INVOKESTATIC){
            String className = getTargetClassName((INVOKESTATIC)i, data.getConstantPoolGen());
            String invokeMethod = ((INVOKESTATIC)i).getMethodName(data.getConstantPoolGen());
            return className.equals("java.lang.System") && invokeMethod.equals("arraycopy");
        }
        return false;
    }

    @Override
    public void reset(){
        super.reset();
        arraycopy = null;
    }

    @Override
    public Method[] getMethods(){
        return new Method[] { arraycopy, };
    }

    @Override
    public boolean isContain(){
        return arraycopy != null;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.REPLACE;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        ClassGen cg = d.getClassGen();

        if(arraycopy == null){
            arraycopy = createArrayCopyMethod(cg, d);
        }

        InstructionList il = new InstructionList();

        il.append(new PUSH(d.getConstantPoolGen(), d.getLineNumber()));
        il.append(d.getFactory().createInvoke(
            cg.getClassName(), arraycopy.getName(), Type.VOID,
            new Type[]{ Type.OBJECT, Type.INT, Type.OBJECT, Type.INT, Type.INT, Type.INT },
            Constants.INVOKESTATIC
        ));
        return il;
    }

    /**
     * System#arraycopy \bhuD
     * <blockquote><pre>
     *     System.out.println(src + "[" + srcPos + "-" + (srcPos + length) +
     *                        "]\t*\treference\t//line " + lineNumber);
     *     System.out.println(dest + "[" + destPos + "-" + (destPos + length) +
     *                        "]\t*\tassignment\t//line " + lineNumber);
     *     System.arraycopy(src, srcPos, dest, destPos, length);
     * </pre></blockquote>
     */
    public Method createArrayCopyMethod(ClassGen classGen, UpdateData d){
        if(arraycopy != null) return arraycopy;
        InstructionList il = createArrayCopyInstruction(0, "reference", d);
        il.append(createArrayCopyInstruction(2, "assignment", d));
        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(InstructionFactory.createLoad(Type.INT, 1));
        il.append(InstructionFactory.createLoad(Type.OBJECT, 2));
        il.append(InstructionFactory.createLoad(Type.INT, 3));
        il.append(InstructionFactory.createLoad(Type.INT, 4));
        il.append(d.getFactory().createInvoke(
            SYSTEM, "arraycopy", Type.VOID,
            new Type[] { Type.OBJECT, Type.INT, Type.OBJECT, Type.INT, Type.INT },
            Constants.INVOKESTATIC
        ));
        il.append(new RETURN());
        MethodGen m = new MethodGen(
            Constants.ACC_FINAL | Constants.ACC_PRIVATE | Constants.ACC_STATIC, Type.VOID,
            new Type[]{ Type.OBJECT, Type.INT, Type.OBJECT, Type.INT, Type.INT, Type.INT},
            new String[] { "src", "srcPos", "dest", "destPos", "length", "lineNumber", },
            "_arraycopy_for_addtracer", classGen.getClassName(), il, d.getConstantPoolGen()
        );
        m.setMaxLocals();
        m.setMaxStack();

        arraycopy = m.getMethod();

        return arraycopy;
    }

    /**
     * 悤g[T}D
     * <blockquote><pre>
     *     System.out.println(src + "[" + srcPos + "-" + (srcPos + length) +
     *                        "]\t*\treference\t//line " + lineNumber);
     * </pre></blockquote>
     */
    private InstructionList createArrayCopyInstruction(int offset, String type, UpdateData d){
        InstructionList il = new InstructionList();

        il.append(pushSystemOutAndStringBuffer(d));
        il.append(InstructionFactory.createLoad(Type.OBJECT, 0 + offset));

        il.append(getAppendInstructions(d, Type.OBJECT));

        il.append(getAppendInstructions(d, "["));
        il.append(InstructionFactory.createLoad(Type.INT, 1 + offset));
        il.append(getAppendInstructions(d, Type.INT));
        il.append(getAppendInstructions(d, "-"));
        il.append(InstructionFactory.createLoad(Type.INT, 1 + offset));
        il.append(InstructionFactory.createLoad(Type.INT, 4));
        il.append(new IADD());
        il.append(getAppendInstructions(d, Type.INT));

        il.append(getAppendInstructions(d, "]\t*\t" + type + "\t// line "));
        il.append(InstructionFactory.createLoad(Type.INT, 5));

        il.append(getAppendInstructions(d, Type.INT));
        il.append(getToStringAndPrintln(d));

        return il;
   }
}
