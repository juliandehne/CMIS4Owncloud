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
package org.apache.chemistry.opencmis.client.bindings.atompub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AtomEntryWriter;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AtomPubParser;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.objects.AtomBase;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.objects.AtomElement;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.objects.AtomEntry;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDecimalImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringImpl;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisObjectType;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisProperty;

/**
 * Minimal test for AtomEntryWriter and AtomPubParser.
 */
public class AtomParserTest extends TestCase {

    private static final byte[] CONTENT = "This is my test content!".getBytes();
    private static final String CONTENT_TYPE = "text/plain";

    public void testParser() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        // set up an object
        PropertiesImpl properties = new PropertiesImpl();

        PropertyStringImpl propName = new PropertyStringImpl(PropertyIds.NAME, "TestName");
        properties.addProperty(propName);

        PropertyIntegerImpl propInt = new PropertyIntegerImpl("IntProp", Arrays.asList(new BigInteger[] {
                BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3) }));
        properties.addProperty(propInt);

        PropertyDecimalImpl propDec = new PropertyDecimalImpl("DecProp", new BigDecimal(
                "3.14159253589793238462643383279502884197"
                        + "169399375105820974944592307816406286208998628034825342117067982148086513"));
        properties.addProperty(propDec);

        ObjectDataImpl object1 = new ObjectDataImpl();
        object1.setProperties(properties);

        // write the entry
        ContentStream contentStream = new ContentStreamImpl(null, BigInteger.valueOf(CONTENT.length), CONTENT_TYPE,
                new ByteArrayInputStream(CONTENT));
        AtomEntryWriter aew = new AtomEntryWriter(object1, CmisVersion.CMIS_1_1, contentStream);
        aew.write(bao);

        byte[] entryContent = bao.toByteArray();
        assertTrue(entryContent.length > 0);

        // parse it
        AtomPubParser parser = new AtomPubParser(new ByteArrayInputStream(entryContent));
        parser.parse();
        AtomBase parseResult = parser.getResults();

        assertTrue(parseResult instanceof AtomEntry);
        AtomEntry entry = (AtomEntry) parseResult;

        assertNotNull(entry);
        assertTrue(entry.getElements().size() > 0);

        // find the object
        CmisObjectType object2 = null;
        for (AtomElement element : entry.getElements()) {
            if (element.getObject() instanceof CmisObjectType) {
                assertNull(object2);
                object2 = (CmisObjectType) element.getObject();
            }
        }

        assertNotNull(object2);
        assertNotNull(object2.getProperties());

        // compare properties
        for (PropertyData<?> property1 : object1.getProperties().getPropertyList()) {
            boolean found = false;

            for (CmisProperty property2 : object2.getProperties().getProperty()) {
                if (property1.getId().equals(property2.getPropertyDefinitionId())) {
                    found = true;

                    // TODO: fix test
                    //assertEquals(property1.getValues(), property2.);
                    break;
                }
            }

            assertTrue(found);
        }
    }

    protected void assertEquals(CmisProperty expected, CmisProperty actual) throws Exception {
        if (expected == null && actual == null) {
            return;
        }

        if (expected == null || actual == null) {
            fail("Property is null!");
        }

        assertEquals(expected.getPropertyDefinitionId(), actual.getPropertyDefinitionId());
        assertSame(expected.getClass(), actual.getClass());

        Method m1 = expected.getClass().getMethod("getValue");
        List<?> values1 = (List<?>) m1.invoke(expected);
        assertNotNull(values1);
        assertFalse(values1.isEmpty());

        Method m2 = actual.getClass().getMethod("getValue");
        List<?> values2 = (List<?>) m2.invoke(actual);
        assertNotNull(values2);
        assertFalse(values2.isEmpty());

        assertEquals(values1.size(), values2.size());

        for (int i = 0; i < values1.size(); i++) {
            assertEquals(values1.get(i), values2.get(i));
        }
    }
}
