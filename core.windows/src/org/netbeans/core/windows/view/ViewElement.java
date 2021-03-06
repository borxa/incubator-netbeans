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


package org.netbeans.core.windows.view;


import java.awt.*;


/**
 * Class which represents one element in ViewHierarchy.
 * It could be split, mode, or editor type element.
 *
 * @author  Peter Zavadsky
 */
public abstract class ViewElement {

    private final Controller controller;

    private final double resizeWeight;


    public ViewElement(Controller controller, double resizeWeight) {
        this.controller = controller;
        this.resizeWeight = resizeWeight;
    }


    public final Controller getController() {
        return controller;
    }

    public abstract Component getComponent();
    
    public final double getResizeWeight() {
        return resizeWeight;
    }
    
    /**
     * lets the visual components adjust to the current state.
     * @returns true if a change was performed.
     */
    public abstract boolean updateAWTHierarchy(Dimension availableSpace);
    
    
}

