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
 * $Id: TracerInjectionProcessor.java 117 2006-10-02 06:21:58Z harua-t $
 */

import java.io.PrintWriter;
import java.net.URL;

import jp.cafebabe.commons.io.PrintWriterStream;
import jp.cafebabe.donquixote.rocinante.Arguments;
import jp.cafebabe.donquixote.rocinante.BytecodeException;
import jp.cafebabe.donquixote.rocinante.BytecodeProcessor;
import jp.cafebabe.donquixote.rocinante.Summary;
import jp.cafebabe.donquixote.rocinante.processors.FilterBytecodeProcessor;
import jp.cafebabe.donquixote.rocinante.summary.FormattedSummary;

import org.apache.bcel.classfile.JavaClass;

/**
 * DonQuixote plugin for AddTracer.
 *
 * <table>
 *   <thead>
 *     <tr>
 *       <th>Property Name</th>
 *       <th>Description</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <th>format</th>
 *       <th>output format of AddTracer. Available value is `simple,' `hex,' and `standard.'</th>
 *     </tr>
 *     <tr>
 *       <th>quiet</th>
 *       <th>If this value is `true', quiet mode.  (Also accepts `yes' and `on')</th>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Haruaki TAMADA
 * @version $Revision: 117 $ $Date: 2006-10-02 15:21:58 +0900 (Mon, 02 Oct 2006) $
 */
public class TracerInjectionProcessor extends FilterBytecodeProcessor{
    /**
     * summary class.
     */
    private FormattedSummary summary;

    /**
     * PrintWriter for summary of processor.
     */
    private PrintWriter out;

    /**
     * The main processor of AddTracer.
     */
    private AddTracer tracer;

    /**
     * Constructor.
     */
    public TracerInjectionProcessor(BytecodeProcessor processor){
        super(processor);
    }

    /**
     * initialize this processor.
     */
    @Override
    public void initialize(){
        summary = new FormattedSummary(getName());
        out = summary.getWriter();

        System.setOut(new PrintWriterStream(out));
        Arguments args = getArguments();
        tracer = new AddTracer(args.getProperty("format"),
                               isTrue(args.getProperty("quiet")));
    }

    /**
     * perform addtracer process.
     */
    @Override
    public void perform(JavaClass target, URL location) throws BytecodeException{
        JavaClass jc = tracer.addTrace(target);

        processor.perform(jc, location);
    }

    /**
     * summarize addtracer process.
     */
    @Override
    public Summary summarize(){
        out.close();

        return summary;
    }

    /**
     * String to boolean value.
     */
    private boolean isTrue(String value){
        return value != null && (value.equalsIgnoreCase("true") ||
                                 value.equalsIgnoreCase("yes")  ||
                                 value.equalsIgnoreCase("on"));
    }
}
