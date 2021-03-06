/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.hibernate.loaders.reveng;

import org.netbeans.modules.hibernate.loaders.mapping.*;
import org.netbeans.modules.hibernate.loaders.cfg.*;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.modules.hibernate.reveng.model.HibernateReverseEngineering;
import org.openide.filesystems.FileObject;

/**
 *
 * @author gowri
 */
public class HibernateRevengMetadata {
    private static final HibernateRevengMetadata DEFAULT = new HibernateRevengMetadata();
    private Map<FileObject,HibernateReverseEngineering> ddReveng;
    
    private HibernateRevengMetadata() {
        ddReveng = new WeakHashMap<FileObject,HibernateReverseEngineering>(5);
    }
    
    /**
     * Use this to get singleton instance of provider
     *
     * @return singleton instance
     */
    public static HibernateRevengMetadata getDefault() {
        return DEFAULT;
    }
    
    /**
     * Provides root element as defined in hibernate-reverse-engineering-3.0.dtd
     * 
     * @param fo FileObject represnting Hibernate Reverse Engineering file
     * It can be retrieved from {@link PersistenceProvider} for any file
     * @throws java.io.IOException 
     * @return root element of schema or null if it doesn't exist for provided 
     * persistence.xml deployment descriptor
     * @see PersistenceProvider
     */
    public HibernateReverseEngineering getRoot(FileObject fo) throws java.io.IOException {
        if (fo == null) {
            return null;
        }
        HibernateReverseEngineering revEngineering = null;
        synchronized (ddReveng) {
            revEngineering = (HibernateReverseEngineering) ddReveng.get(fo);
            if (revEngineering == null) {
                revEngineering = HibernateReverseEngineering.createGraph(fo.getInputStream());
                ddReveng.put(fo, revEngineering);
            }
        }
        return revEngineering;
    }

}
