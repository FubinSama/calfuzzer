/*
 * @(#)Enumeration.java	1.13 01/12/12
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package benchmarks.dstest;

/**
 * An object that implements the Enumeration interface generates a
 * series of elements, one at a time. Successive calls to the
 * <code>nextElement</code> method return successive elements of the
 * series.
 * <p/>
 * For example, to print all elements of a vector <i>v</i>:
 * <blockquote><pre>
 *     for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
 *         System.out.println(e.nextElement());<br>
 *     }
 * </pre></blockquote>
 * <p/>
 * Methods are provided to enumerate through the elements of a
 * vector, the keys of a hashtable, and the values in a hashtable.
 * Enumerations are also used to specify the input streams to a
 * <code>SequenceInputStream</code>.
 *
 * @author Lee Boynton
 * @version 1.13, 12/12/01
 * @see java.io.SequenceInputStream
 * @see java.util.Enumeration#nextElement()
 * @see java.util.Hashtable
 * @see java.util.Hashtable#elements()
 * @see java.util.Hashtable#keys()
 * @see java.util.Vector
 * @see java.util.Vector#elements()
 * @since JDK1.0
 */
public interface Enumeration {
    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements;
     *         <code>false</code> otherwise.
     * @since JDK1.0
     */
    boolean hasMoreElements();

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     * @throws NoSuchElementException if no more elements exist.
     * @since JDK1.0
     */
    Object nextElement();
}
