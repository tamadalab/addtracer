package jp.cafebabe.commons.bcul.updater;

import java.util.Iterator;

/**
 *
 * @author Haruaki TAMADA
 */
public interface InstructionUpdateHandlerManager{
    /**
     * Register given {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>} as given type.
     */
    public void addHandler(String type, InstructionUpdateHandler handler);

    /**
     * Returns {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>}
     * mapped by given type.
     */
    public InstructionUpdateHandler getHandler(String type);

    /**
     * Remove registered {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>}
     * mapped by given type.
     */
    public void removeHandler(String type);

    /**
     * Returns the set of registered handler names.
     */
    public Iterator<String> handlerNames();

    /**
     * Returns the set of registered handlers.
     */
    public Iterator<InstructionUpdateHandler> handlers();

    /**
     * Returns the set of registered handlers as an array.
     */
    public InstructionUpdateHandler[] getHandlers();
}
