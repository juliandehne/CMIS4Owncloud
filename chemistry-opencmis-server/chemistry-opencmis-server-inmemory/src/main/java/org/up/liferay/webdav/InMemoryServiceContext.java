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
package org.up.liferay.webdav;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.inmemory.server.InMemoryService;
import org.apache.chemistry.opencmis.inmemory.storedobj.api.StoredObject;
import org.apache.chemistry.opencmis.server.support.CmisServiceWrapper;

import com.github.sardine.DavResource;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * Helper class to associate context information with each incoming call.
 * 
 */
public final class InMemoryServiceContext {
	
	public static Cache<WebdavResourceKey, List<DavResource>> CACHE = CacheBuilder.newBuilder().expireAfterWrite(WebdavConfigurationLoader.getCacheTimeout(), TimeUnit.SECONDS).maximumSize(1000).build();

    private static final class ContextHolder {
        private CmisServiceWrapper<InMemoryService> wrapper;
        private CallContext callContext;

        private ContextHolder(CmisServiceWrapper<InMemoryService> wrapper) {
            this.wrapper = wrapper;
        }

        public CmisServiceWrapper<InMemoryService> getServiceWrapper() {
            return wrapper;
        }

        public void setCallContext(CallContext context) {
            this.callContext = context;
        }

        public CallContext getCallContext() {
            return callContext;
        }
    }

    private static ThreadLocal<ContextHolder> threadLocalService = new ThreadLocal<ContextHolder>();

    private InMemoryServiceContext() {
    }

    public static synchronized void setWrapperService(CmisServiceWrapper<InMemoryService> wrapperService) {
        threadLocalService.remove();
        if (null != wrapperService) {
            ContextHolder holder = new ContextHolder(wrapperService);
            threadLocalService.set(holder);
        }
    }

    public static synchronized InMemoryService getCmisService() {
        ContextHolder holder = threadLocalService.get();
        if (null == holder) {
            return null;
        } else {
            CmisServiceWrapper<InMemoryService> wrapperService = holder.getServiceWrapper();
            return wrapperService == null ? null : wrapperService.getWrappedService();
        }
    }

    public static synchronized void setCallContext(CallContext context) {
        ContextHolder holder = threadLocalService.get();
        if (null == holder) {
            throw new IllegalStateException("Cannot store call context, no service wrapper set.");
        } else {
            holder.setCallContext(context);
        }
    }

    public static CallContext getCallContext() {
        ContextHolder holder = threadLocalService.get();
        return null == holder ? null : holder.getCallContext();
    }

}
