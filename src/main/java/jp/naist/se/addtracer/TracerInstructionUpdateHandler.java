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
     * System#out をスタックに積む．
     * </p><p>
     * 実行前: ...
     * </p><p>
     * 実行後: ..., out
     * </p>
     */
    public InstructionList pushSystemOut(UpdateData data){
        InstructionList list = new InstructionList();
        list.append(data.getFactory().createGetStatic(SYSTEM, "out", PRINTSTREAM_TYPE));

        return list;
    }

    /**
     * <p>
     * System#out をスタックに積み，StringBuffer を初期化する．
     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ...
     * </p><p>
     * 実行後: ..., out, stringbuffer
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
     * System#out をスタックに積み，StringBuffer を初期化して，引数に与
     * えられた文字列を作成したStringBufferオブジェクトのappendメソッド
     * を用いて連結する．

     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ...
     * </p><p>
     * 実行後: ..., out, stringbuffer
     * </p>
     */
    public InstructionList pushSystemOutAndStringBuffer(UpdateData data, String value){
        InstructionList list = pushSystemOutAndStringBuffer(data);
        list.append(getAppendInstructions(data, value));

        return list;
    }

    /**
     * <p>
     * 引数に与えられた文字列を既にスタックに積まれているStringBufferに
     * 連結し(appendメソッドを呼び出し)このメソッド実行前に積まれた
     * System#outとStringBufferをもとに，その内容を出力する．
     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ..., out, stringbuffer
     * </p><p>
     * 実行後: ...
     * </p>
     */
    public InstructionList getToStringAndPrintln(UpdateData data, String value){
        InstructionList list = getAppendInstructions(data, value);
        list.append(getToStringAndPrintln(data));

        return list;
    }

    /**
     * <p>
     * このメソッド実行前に積まれたSystem#outとStringBufferをもとに，そ
     * の内容を出力する．
     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ..., out, stringbuffer
     * </p><p>
     * 実行後: ...
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
     * 引数に与えられた文字列を，既にスタックに積まれている
     * StringBufferオブジェクトに連結する(appendメソッドを呼び出す)
     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ..., stringbuffer
     * </p><p>
     * 実行後: ..., stringbuffer
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
     * 引数に与えられた数値を，既にスタックに積まれている
     * StringBufferオブジェクトに連結する(appendメソッドを呼び出す)
     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ..., stringbuffer
     * </p><p>
     * 実行後: ..., stringbuffer
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
     * スタックに積まれた値を同じくスタックに積まれているStringBufferに連結する．
     * このメソッド実行後，スタックの状態は以下の通りとなる(右がスタックの上部を表す)．
     * </p><p>
     * 実行前: ..., stringbuffer, value
     * </p><p>
     * 実行後: ..., stringbuffer
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
     * StringBuffer のappendメソッドの引数の型を判定して返す．
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
