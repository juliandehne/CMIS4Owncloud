package org.up.ple.dcim4owncloud;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

public class OwnCloudFileShareRepositoryManager {
	   private final Map<String, OwnCloudFileShareRepository> repositories;

	    public OwnCloudFileShareRepositoryManager() {
	        repositories = new HashMap<String, OwnCloudFileShareRepository>();
	    }

	    /**
	     * Adds a repository object.
	     */
	    public void addRepository(OwnCloudFileShareRepository fsr) {
	        if (fsr == null || fsr.getRepositoryId() == null) {
	            return;
	        }

	        repositories.put(fsr.getRepositoryId(), fsr);
	    }

	    /**
	     * Gets a repository object by id.
	     */
	    public OwnCloudFileShareRepository getRepository(String repositoryId) {
	    	OwnCloudFileShareRepository result = repositories.get(repositoryId);
	        if (result == null) {
	            throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
	        }

	        return result;
	    }

	    /**
	     * Returns all repository objects.
	     */
	    public Collection<OwnCloudFileShareRepository> getRepositories() {
	        return repositories.values();
	    }

	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();

	        for (OwnCloudFileShareRepository repository : repositories.values()) {
	            sb.append('[');
	            sb.append(repository.getRepositoryId());
	            sb.append(" -> ");
	            sb.append(repository.getRootDirectory().getAbsolutePath());
	            sb.append(']');
	        }

	        return sb.toString();
	    }
}
