//******************************************************************************
//
// File:    Viewable.java
// Package: benchmarks.determinism.pj.edu.ritswing
// Unit:    Interface benchmarks.determinism.pj.edu.ritswing.Viewable
//
// This Java source file is copyright (C) 2007 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java Library ("PJ"). PJ is free
// software; you can redistribute it and/or modify it under the terms of the GNU
// General Public License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// PJ is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

package benchmarks.determinism.pj.edu.ritswing;

/**
 * Interface Viewable specifies the interface for an object that can be viewed
 * on the screen using the {@linkplain View} program.
 *
 * @author  Alan Kaminsky
 * @version 11-Dec-2007
 */
public interface Viewable
	extends Displayable
	{

// Exported operations.

	/**
	 * Get a displayable frame in which to view this viewable object. Initially,
	 * the returned frame is displaying this viewable object.
	 *
	 * @return  Displayable frame.
	 */
	public DisplayableFrame getFrame();

	/**
	 * Get the title for the frame used to view this viewable object. If the
	 * title is null, a default title is used.
	 *
	 * @return  Title.
	 */
	public String getTitle();

	}
