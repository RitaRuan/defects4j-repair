/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath;

import java.util.Iterator;

/**
 * Represents a compiled XPath. The interpretation of compiled XPaths
 * may be faster, because it bypasses the compilation step. The reference
 * implementation of JXPathContext also globally caches some of the
 * results of compilation, so the direct use of JXPathContext is not
 * always less efficient than the use of CompiledExpression.
 * <p>
 * Use CompiledExpression only when there is a need to evaluate the
 * same expression multiple times and the CompiledExpression can be
 * conveniently cached.
 * <p>
 * To acqure a CompiledExpression, call {@link JXPathContext#compile
 * JXPathContext.compile}
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public interface CompiledExpression {

    /**
     * Evaluates the xpath and returns the resulting object. Primitive
     * types are wrapped into objects.
     */
    Object getValue(JXPathContext context);

    /**
     * Evaluates the xpath, converts the result to the specified class and
     * returns the resulting object.
     */
    Object getValue(JXPathContext context, Class requiredType);

    /**
     * Modifies the value of the property described by the supplied xpath.
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>The xpath does not in fact describe an existing property
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    void setValue(JXPathContext context, Object value);

    /**
     * Creates intermediate elements of
     * the path by invoking an AbstractFactory, which should first be
     * installed on the context by calling "setFactory".
     */
    Pointer createPath(JXPathContext context);

    /**
     * The same as setValue, except it creates intermediate elements of
     * the path by invoking an AbstractFactory, which should first be
     * installed on the context by calling "setFactory".
     * <p>
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>Elements of the xpath aleady exist, by the path does not in
     *  fact describe an existing property
     * <li>The AbstractFactory fails to create an instance for an intermediate
     * element.
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    Pointer createPathAndSetValue(JXPathContext context, Object value);

    /**
     * Traverses the xpath and returns a Iterator of all results found
     * for the path. If the xpath matches no properties
     * in the graph, the Iterator will not be null.
     */
    Iterator iterate(JXPathContext context);

    /**
     * Traverses the xpath and returns a Pointer.
     * A Pointer provides easy access to a property.
     * If the xpath matches no properties
     * in the graph, the pointer will be null.
     */
    Pointer getPointer(JXPathContext context, String xpath);

    /**
     * Traverses the xpath and returns an Iterator of Pointers.
     * A Pointer provides easy access to a property.
     * If the xpath matches no properties
     * in the graph, the Iterator be empty, but not null.
     */
    Iterator iteratePointers(JXPathContext context);

    /**
     * Remove the graph element described by this expression
     */
    void removePath(JXPathContext context);

    /**
     * Remove all graph elements described by this expression
     */
    void removeAll(JXPathContext context);
}
