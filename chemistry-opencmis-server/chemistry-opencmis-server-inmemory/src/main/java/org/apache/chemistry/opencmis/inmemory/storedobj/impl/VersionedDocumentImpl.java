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
package org.apache.chemistry.opencmis.inmemory.storedobj.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.provider.BindingsObjectFactory;
import org.apache.chemistry.opencmis.commons.provider.ContentStreamData;
import org.apache.chemistry.opencmis.commons.provider.PropertyData;
import org.apache.chemistry.opencmis.inmemory.FilterParser;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.DocumentVersion;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.VersionedDocument;

public class VersionedDocumentImpl extends AbstractMultiFilingImpl implements VersionedDocument {
  
  private boolean fIsCheckedOut;
  private String fCheckedOutUser;
  private List<DocumentVersion> fVersions;
  
  
  public VersionedDocumentImpl(ObjectStoreImpl objStore) {
    super(objStore);
    fVersions = new ArrayList<DocumentVersion>();
    fIsCheckedOut = false;
  }
  
  public DocumentVersion addVersion(ContentStreamData content,
      VersioningState verState, String user) {
    
    if (isCheckedOut())
        throw new CmisConstraintException("Cannot add a version to document, document is checked out.");
    
    DocumentVersionImpl ver = new DocumentVersionImpl(fRepositoryId, this, content, verState, fObjStore);
    ver.setSystemBasePropertiesWhenCreatedDirect(getName(), getTypeId(), user); // copy name and type id from version series.
    ver.persist();
    fVersions.add(ver);
    if (verState == VersioningState.CHECKEDOUT) {
      fCheckedOutUser = user;
      fIsCheckedOut = true;
    }
    
    return ver;
  }

  public boolean deleteVersion(DocumentVersion version) {
    if (fIsCheckedOut)
      throw new RuntimeException("version cannot be deleted if document is checked-out: " + version.getId());
    boolean found = fVersions.remove(version);
    if (!found)
      throw new RuntimeException("Version is not contained in the document:" + version.getId());
    
    return !fVersions.isEmpty();
  }
  
  
  public void cancelCheckOut(String user) {
    DocumentVersion pwc = getPwc();
    fVersions.remove(pwc);
    fObjStore.removeVersion(pwc);
    fIsCheckedOut = false;
    fCheckedOutUser = null;
  }

  public void checkIn(boolean isMajor, String checkinComment, String user) {
    if (fIsCheckedOut) {
      if (fCheckedOutUser.equals(user)) {        
        fIsCheckedOut = false;
        fCheckedOutUser = null;
      } else {
        throw new CmisConstraintException("Error: Can't checkin. Document " + getId()
            + " user " + user + " has not checked out the document");
      }
    }
    else
      throw new CmisConstraintException("Error: Can't cancel checkout, Document " + getId() + " is not checked out.");
    
    DocumentVersion pwc = getPwc();
    pwc.setCheckinComment(checkinComment);
    pwc.commit(isMajor);
  }

  public DocumentVersion checkOut(ContentStreamData content, String user) {
    if (fIsCheckedOut) {
      throw new CmisConstraintException("Error: Can't checkout, Document " + getId() + " is already checked out.");
    }
    
    // create PWC
    DocumentVersion pwc = addVersion(content, VersioningState.CHECKEDOUT, user); // will set check-out flag
    return pwc;
  }

  public List<DocumentVersion> getAllVersions() {
    return fVersions;
  }

  public DocumentVersion getLatestVersion(boolean major) {
    
    DocumentVersion latest = null;
    if (major) {
      for (DocumentVersion ver : fVersions) {
        if (ver.isMajor())
          latest = ver;
      }
    } else {
      latest = fVersions.get(fVersions.size() - 1);
    }
    return latest;
  }

  public boolean isCheckedOut() {
    return fIsCheckedOut;
  }

  public String getCheckedOutBy() {
    return fCheckedOutUser;
  }
  
  public DocumentVersion getPwc() {
    for ( DocumentVersion ver : fVersions) {
      if (ver.isPwc())
        return ver;
    }
    return null;
  }

  public void fillProperties(Map<String, PropertyData<?>> properties, BindingsObjectFactory objFactory,
      List<String> requestedIds) {
    
    DocumentVersion pwc = getPwc();
    
    super.fillProperties(properties, objFactory, requestedIds);
    
    // overwrite the version related properties 
    if (FilterParser.isContainedInFilter(PropertyIds.CMIS_VERSION_SERIES_ID, requestedIds)) { 
      properties.put(PropertyIds.CMIS_VERSION_SERIES_ID, objFactory.createPropertyIdData(PropertyIds.CMIS_VERSION_SERIES_ID, getId()));
    }
    if (FilterParser.isContainedInFilter(PropertyIds.CMIS_IS_VERSION_SERIES_CHECKED_OUT, requestedIds)) {
      properties.put(PropertyIds.CMIS_IS_VERSION_SERIES_CHECKED_OUT, objFactory.createPropertyBooleanData(PropertyIds.CMIS_IS_VERSION_SERIES_CHECKED_OUT, isCheckedOut()));
    }
    if (FilterParser.isContainedInFilter(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_BY, requestedIds)) {
      properties.put(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_BY, objFactory.createPropertyStringData(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_BY, getCheckedOutBy()));
    }
    if (FilterParser.isContainedInFilter(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_ID, requestedIds)) {
      properties.put(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_ID, objFactory.createPropertyIdData(PropertyIds.CMIS_VERSION_SERIES_CHECKED_OUT_ID, pwc == null ? null : pwc.getId()));
    }
    
  }

}
