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

package org.netbeans.tax.dom;

import org.w3c.dom.*;
import org.netbeans.tax.*;

/**
 * Utility class holding children methods.
 *
 * @author  Petr Kuzel
 */
class Children {

    public static Node getNextSibling(TreeChild child) {
        TreeChild sibling = child.getNextSibling();

        while (sibling != null) {
            if (sibling instanceof TreeElement) {
                return Wrapper.wrap((TreeElement) sibling);
            } else if (sibling instanceof TreeText) {
                return Wrapper.wrap((TreeText) sibling);
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }
    
    public static Node getPreviousSibling(TreeChild child) {
        TreeChild sibling = child.getPreviousSibling();
        
        while (sibling != null) {
            if (sibling instanceof TreeElement) {
                return Wrapper.wrap((TreeElement) sibling);
            } else if (sibling instanceof TreeText) {
                return Wrapper.wrap((TreeText) sibling);
            }
            sibling = sibling.getPreviousSibling();
        }
        return null;
    }
    
}
