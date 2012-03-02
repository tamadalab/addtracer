package jp.cafebabe.commons.bcul.updater;

/**
 * <p>
 * This class represents types of updating.
 * </p><p>
 * {@link InstructionUpdateHandler <code>InstructionUpdateHandler</code>} 
 * is targetted a instruction and generate new instruction sequence.
 * Then, new instruction sequence is added to original instruction.
 * </p><p>
 * This enum type represents generated sequence is added to before
 * original instruction, after original instruction, before and after
 * original instruction or replace original instruction.
 * </p>
 * <dl>
 *   <dt><code>INSERT</code></dt>
 *   <dd>Insert generated instructions before original instruction</dd>
 *   <dt><code>APPEND</code></dt>
 *   <dd>Append generated instructions after original instruction</dd>
 *   <dt><code>BOTH</code></dt>
 *   <dd>Add generated instructions before and after original instruction</dd>
 *   <dt><code>REPLACE</code></dt>
 *   <dd>Replace original instruction to generated instructions</dd>
 * </dl>
 *
 * @author Haruaki TAMADA
 */
public enum UpdateType{
    INSERT,
    APPEND,
    REPLACE,
    BOTH,
}
