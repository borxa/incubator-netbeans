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

package org.netbeans.modules.java.source.compat8;

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.source.ModificationResult;
import org.openide.modules.PatchFor;
import org.openide.text.PositionRef;

/**
 * Provides PositionRefs instead of Positions.
 * @author sdedic
 */
@PatchFor(ModificationResult.Difference.class)
public class ModificationResult8 {
    private ModificationResult.Difference inst;
    
    public ModificationResult8() {
        this.inst = (ModificationResult.Difference)(Object)this;
    }
    
    public @NonNull PositionRef getStartPosition() {
        return (PositionRef)this.inst.getStartPosition();
    }

    public @NonNull PositionRef getEndPosition() {
        return (PositionRef)this.inst.getEndPosition();
    }
        
}
