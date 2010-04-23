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
package org.apache.chemistry.opencmis.fit.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.PagingIterable;
import org.junit.Test;

public class WriteObjectVersionIT extends AbstractSessionTest {
    @Test
    public void checkOutDocs() {

        /* check out one versionable document */
        String path = "/" + Fixture.TEST_ROOT_FOLDER_NAME + "/" + FixtureData.DOCUMENT1_NAME;
        Document document = (Document) this.session.getObjectByPath(path);
        assertNotNull("Document not found: " + path, document);
        DocumentType dt = (DocumentType) document.getType();
        assertNotNull(dt);
        ObjectId id = null;
        if (dt.isVersionable()) {
            id = document.checkOut();
        }

        /* get all verchecked out docs which should be exactly one or zero */
        Folder f = this.session.getRootFolder();
        assertNotNull(f);
        PagingIterable<Document> pi = f.getCheckedOutDocs(10);
        assertNotNull(pi);

        for (Document d : pi) {
            assertNotNull(d);
            assertEquals(id, d.getId());
            break; // check only first and only loop entry
        }
    }

}