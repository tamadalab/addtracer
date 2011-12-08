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

import java.util.HashSet;
import java.util.Set;

import jp.cafebabe.commons.bcul.updater.BytecodeUpdater;

/**
 * Factory class for AddTracer output format.
 *
 * @author Haruaki TAMADA
 */
public class BytecodeUpdaterFactory{
    /**
     * For singleton pattern.
     */
    private static BytecodeUpdaterFactory factory = new BytecodeUpdaterFactory();

    /**
     * A set of format.
     */
    private Set<String> formatSet = new HashSet<String>();

    /**
     * Default constructor.
     */
    private BytecodeUpdaterFactory(){
        formatSet.add("simple");
        formatSet.add("hex");
        formatSet.add("std");
    }

    /**
     * Returns the only instance of this class.
     */
    public static BytecodeUpdaterFactory getInstance(){
        return factory;
    }

    /**
     * Returns given format is available.  Format name is not case sensitive.
     */
    public boolean isAvailableFormat(String format){
        return format != null && formatSet.contains(format.toLowerCase());
    }

    /**
     * Returns default format BytecodeUpdater.
     */
    public BytecodeUpdater getDefaultUpdater(){
        return getUpdater(null);
    }

    /**
     * Returns given format BytecodeUpdater.
     */
    public BytecodeUpdater getUpdater(String type){
        if(type == null || !formatSet.contains(type.toLowerCase())){
            type = "std";
        }

        return new TracerBytecodeUpdater(type);
    }
}
