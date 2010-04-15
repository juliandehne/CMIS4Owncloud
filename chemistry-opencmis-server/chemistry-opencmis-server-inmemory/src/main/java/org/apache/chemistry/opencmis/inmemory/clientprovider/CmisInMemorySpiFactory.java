/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.chemistry.opencmis.inmemory.clientprovider;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.bindings.spi.CmisSpi;
import org.apache.chemistry.opencmis.client.bindings.spi.CmisSpiFactory;
import org.apache.chemistry.opencmis.client.bindings.spi.Session;

/**
 * Factory class for an in-memory SMIS SPI. For the in-memory implementation the 
 * CMIS SPI creates one instance per session
 * 
 * @author Jens
 *
 */
public class CmisInMemorySpiFactory implements CmisSpiFactory {

  private static Map<Integer, CmisInMemorySpi> IN_MEM_SPIS = new HashMap<Integer, CmisInMemorySpi>();
  public CmisSpi getSpiInstance(Session session) {
    // we maintain one InMemory SPI for each session
    
    int sessionId = System.identityHashCode(session);
    CmisInMemorySpi spi = IN_MEM_SPIS.get(sessionId);
    if (null == spi) {
      // does not yet exist, create one:
      spi = new CmisInMemorySpi(session);
      IN_MEM_SPIS.put(sessionId, spi);
    }
    return spi;
  }

}
