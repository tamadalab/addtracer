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

import java.util.Iterator;

import jp.cafebabe.commons.bcul.updater.InstructionUpdateHandler;

/**
 *
 * @author Haruaki TAMADA
 */
public interface UpdateHandlerFactory{
    public void reset();

    public boolean isAvailable(String type);

    public void setAvailable(String type, boolean flag);

    public void setHandler(String type, InstructionUpdateHandler handler);

    public InstructionUpdateHandler getHandler(String type);

    public Iterator<String> handlerNames();

    public Iterator<InstructionUpdateHandler> handlers();

    public InstructionUpdateHandler[] getHandlers();
}
