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
 * $Id: BytecodeUpdaterFactory.java 110 2006-09-19 12:10:06Z harua-t $
 */

import junit.framework.TestCase;

/**
 *
 * @author Haruaki TAMADA
 * @version $Revision: 110 $ $Date: 2006-09-19 21:10:06 +0900 (Tue, 19 Sep 2006) $
 */
public class BytecodeUpdaterFactoryTest extends TestCase{
    private BytecodeUpdaterFactory factory;

    @Override
    public void setUp(){
        factory = BytecodeUpdaterFactory.getInstance();
    }

    public void testAvailableFormats() throws Exception{
        assertTrue(factory.isAvailableFormat("simple"));
        assertTrue(factory.isAvailableFormat("hex"));
        assertTrue(factory.isAvailableFormat("std"));
    }
}
