package jp.cafebabe.commons.bcul.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.cafebabe.commons.bcul.BytecodeUtility;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;

/**
 * This is Manager class for {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>}.
 *
 * @author Haruaki TAMADA
 */
public class BytecodeUpdater implements InstructionUpdateHandlerManager{
    private Map<String, InstructionUpdateHandler> map = new HashMap<String, InstructionUpdateHandler>();
    private List<Method> methodList = new ArrayList<Method>();
    private UpdateData data;

    public BytecodeUpdater(){
    }

    public void clear(){
        data.clear();
    }

    public UpdateData getUpdateData(){
        return data;
    }

    /**
     * Update given Java class file by a set of registered
     * {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>}.
     */
    public synchronized ClassGen update(ClassGen classGen){
        init();
        resetHandler();
        initialize();

        data = new UpdateData(classGen);

        Method[] methods = data.getClassGen().getMethods();

        for(int i = 0; i < methods.length; i++){
            if(!methods[i].isAbstract() && !methods[i].isNative()){
                MethodGen methodGen = new MethodGen(methods[i], data.getClassName(), data.getConstantPoolGen());

                updateMethod(methodGen);
            }
        }
        return commit();
    }

    @Override
    public InstructionUpdateHandler getHandler(String type){
        return map.get(type);
    }

    @Override
    public void addHandler(String type, InstructionUpdateHandler handler){
        map.put(type, handler);
    }

    @Override
    public void removeHandler(String type){
        map.remove(type);
    }

    @Override
    public InstructionUpdateHandler[] getHandlers(){
        InstructionUpdateHandler[] handlers = new InstructionUpdateHandler[map.size()];
        int index = 0;
        for(Iterator<String> i = handlerNames(); i.hasNext(); ){
            handlers[index] = getHandler(i.next());
            index++;
        }

        return handlers;
    }

    @Override
    public Iterator<String> handlerNames(){
        return map.keySet().iterator();
    }

    @Override
    public Iterator<InstructionUpdateHandler> handlers(){
        final Iterator<String> i = handlerNames();

        return new Iterator<InstructionUpdateHandler>(){
            @Override
            public InstructionUpdateHandler next(){
                return getHandler(i.next());
            }

            @Override
            public boolean hasNext(){
                return i.hasNext();
            }

            @Override
            public void remove(){

            }
        };
    }

    public void initialize(){
    }

    private void init(){
        if(data != null){
            data.clear();
        }
        methodList.clear();
    }

    public void resetHandler(){
        for(Iterator<InstructionUpdateHandler> i = handlers(); i.hasNext(); ){
            InstructionUpdateHandler handler = i.next();
            handler.reset();
        }
    }

    private MethodGen updateMethod(MethodGen method){
        InstructionList list = method.getInstructionList();
        data.setMethod(method);

        preprocess(method, list);

        process(method, list);

        postprocess(method, list);

        list.setPositions(true);

        method.setMaxLocals();
        method.setMaxStack();
        BytecodeUtility.updateLineNumberTables(method);

        methodList.add(method.getMethod());

        return method;
    }

    private void preprocess(MethodGen method, InstructionList list){
        InstructionUpdateHandler[] handlers = getHandlers();
        for(int i = 0; i < handlers.length; i++){
            if(handlers[i] instanceof PreProcessRequired){
                PreProcessRequired rpp = (PreProcessRequired)handlers[i];
                rpp.preprocess(list, data);
            }
        }
    }

    private void postprocess(MethodGen method, InstructionList list){
        InstructionUpdateHandler[] handlers = getHandlers();
        for(int i = 0; i < handlers.length; i++){
            if(handlers[i] instanceof PostProcessRequired){
                PostProcessRequired rpp = (PostProcessRequired)handlers[i];
                rpp.postprocess(list, data);
            }
        }
    }

    private ClassGen commit(){
        for(InstructionUpdateHandler handler: getHandlers()){
            if(handler instanceof MethodCreatable){
                MethodCreatable mc = (MethodCreatable)handler;
                if(mc.isContain()){
                    Method[] methods = mc.getMethods();
                    for(Method m: methods){
                        methodList.add(m);
                    }
                }
            }
        }
        UpdateData data = getUpdateData();

        ClassGen classGen = new ClassGen(data.getClassGen().getJavaClass());

        classGen.setMethods(methodList.toArray(new Method[methodList.size()]));
        classGen.setConstantPool(data.getConstantPoolGen());

        return classGen;
    }

    private void process(MethodGen method, InstructionList list){
        InstructionHandle[] handles = list.getInstructionHandles();
        int byteOffset = 0;
        int beginIndex = getBeginIndex(handles);
        data.setTargetIndex(0);

        for(int i = beginIndex; i < handles.length; i++){
            Instruction instruction = handles[i].getInstruction();
            data.setByteOffset(byteOffset);
            boolean replaceFlag = false;
            InstructionList resultList = null;

            for(Iterator<String> iter = handlerNames(); iter.hasNext(); ){
                String updaterName = iter.next();
                InstructionUpdateHandler iuh = getHandler(updaterName);

                if(iuh.isTarget(handles[i], data)){
                    InstructionList result = iuh.updateInstruction(handles[i], data);
                    UpdateType updateType = iuh.getUpdateType(handles[i]);

                    if(result != null){
                        if(updateType.equals(UpdateType.REPLACE)){
                            if(resultList == null){
                                resultList = result;
                            }
                            else{
                                resultList.append(result);
                            }
                            replaceFlag = true;
                        }
                        else if(updateType.equals(UpdateType.APPEND)){
                            list.append(handles[i], result);
                        }
                        else if(updateType.equals(UpdateType.INSERT)){
                            list.insert(handles[i], result);
                        }
                        else if(updateType.equals(UpdateType.BOTH)){
                            list.insert(handles[i], result);
                            data.setBefore(false);
                            result = iuh.updateInstruction(handles[i], data);
                            list.append(handles[i], result);
                        }
                    }
                }
            }
            if(replaceFlag){
                list.append(handles[i], resultList);
                try{
                    BytecodeUtility.updateTargeters(handles[i], handles[i].getNext());
                    BytecodeUtility.updateExceptionHandlers(method.getExceptionHandlers(), handles[i], handles[i].getNext());

                    list.delete(handles[i]);
                } catch(TargetLostException e){
                    e.printStackTrace();
                }
            }
            byteOffset += instruction.getLength();
            data.incrementTargetIndex();
        }
    }

    private int getBeginIndex(InstructionHandle[] handles){
        UpdateData data = getUpdateData();

        MethodGen mg = data.getMethod();
        ConstantPoolGen poolGen = data.getConstantPoolGen();
        int index = 0;

        if(mg.getName().equals("<init>")){
            for(int i = 0; i < handles.length; i++){
                index = i;
                Instruction instruction = handles[i].getInstruction();
                if(instruction instanceof INVOKESPECIAL){
                    try{
                        // TODO
                        String className = ((INVOKESPECIAL)instruction).getReferenceType(poolGen).toString();
                        String methodName = ((INVOKESPECIAL)instruction).getMethodName(poolGen);

                        if(methodName.equals("<init>")
                                && Repository.instanceOf(data.getClassName(), className)){
                            break;
                        }
                    } catch(ClassNotFoundException e){
                    }
                }
            }
        }

        return index;
    }
}
