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

package org.apache.uima.textmarker.ide.core;

import org.eclipse.dltk.core.search.AbstractSearchFactory;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.matching.MatchLocator;


/**
 * TextMarker search factory
 */
public class TextMarkerSearchFactory extends AbstractSearchFactory {

  public IMatchLocatorParser createMatchParser(MatchLocator locator) {
    return new TextMarkerMatchLocatorParser(locator);
  }

//  public SourceIndexerRequestor createSourceRequestor() {
//    return new TextMarkerSourceIndexerRequestor();
//  }

//  public ISearchPatternProcessor createSearchPatternProcessor() {
//    return new TextMarkerSearchPatternProcessor();
//  }
}