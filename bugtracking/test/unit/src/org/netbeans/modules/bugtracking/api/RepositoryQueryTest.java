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

package org.netbeans.modules.bugtracking.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.bugtracking.spi.RepositoryQueryImplementation;
import org.netbeans.modules.bugtracking.BugtrackingOwnerSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.MockLookup;

/**
 *
 * @author tomas
 */
public class RepositoryQueryTest extends NbTestCase {

    public RepositoryQueryTest(String arg0) {
        super(arg0);
    }

    @Override
    protected Level logLevel() {
        return Level.ALL;
    }   
    
    @Override
    protected void setUp() throws Exception {    
        MockLookup.setLayersAndInstances();
        APITestConnector.init();
    }

    @Override
    protected void tearDown() throws Exception {   
    }

    public void testDefaultImpl() throws IOException {
        Repository repo = getRepo();
        
        File file = new File(getWorkDir(), "testfile");
        file.createNewFile();
        
        FileObject fo = FileUtil.toFileObject(file);
        BugtrackingOwnerSupport.getInstance().setFirmAssociation(file, repo.getImpl());
        
        assertNotNull(fo);
        
        Repository r = RepositoryQuery.getRepository(fo, false);
        assertNotNull(r);
        assertEquals(repo, r);
    }

    public void testCustomImpl() throws IOException {
        File noRepoFile = new File(getWorkDir(), "norepo");
        noRepoFile.createNewFile();
        File assocFile = new File(getWorkDir(), "someassocfile");
        assocFile.createNewFile();
        
        FileObject noRepoFO = FileUtil.toFileObject(noRepoFile);
        assertNotNull(noRepoFO);
        Repository r = RepositoryQuery.getRepository(noRepoFO, false);
        assertNull(r);
        
        FileObject assocFO = FileUtil.toFileObject(assocFile);
        assertNotNull(assocFO);
        r = RepositoryQuery.getRepository(assocFO, false);
        assertNotNull(r);
        assertEquals(getRepo(), r);
    }
    
    private Repository getRepo() {
        return APITestKit.getRepo(APITestRepository.ID);
    }
    
    @org.openide.util.lookup.ServiceProvider(service = org.netbeans.modules.bugtracking.spi.RepositoryQueryImplementation.class)
    public static class RepositoryQImpl implements RepositoryQueryImplementation {
        @Override
        public String getRepositoryUrl(FileObject fileObject) {
            if(fileObject.getName().endsWith("norepo")) {
                return null;
            }
            return APITestKit.getRepo(APITestRepository.ID).getUrl();
        }
    }
    
}
