//******************************************************************************
//
// File:    Constants.java
// Package: benchmarks.determinism.pj.edu.ritpj.cluster
// Unit:    Class benchmarks.determinism.pj.edu.ritpj.cluster.Constants
//
// This Java source file is copyright (C) 2006 by Alan Kaminsky. All rights
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

package benchmarks.determinism.pj.edu.ritpj.cluster;

/**
 * Class Constants contains various constants used in the PJ cluster middleware.
 *
 * @author  Alan Kaminsky
 * @version 19-Oct-2006
 */
public class Constants
	{

// Prevent construction.

	private Constants()
		{
		}

// Exported constants.

	/**
	 * Host name referring to all network interfaces (<TT>"0.0.0.0"</TT>).
	 */
	public static final String ALL_NETWORK_INTERFACES = "0.0.0.0";

	/**
	 * The default port number to which the Job Scheduler listens for
	 * connections from job frontend processes (20617).
	 */
	public static final int PJ_PORT = 20617;

	/**
	 * The default port number for the Job Scheduler's web interface (8080).
	 */
	public static final int WEB_PORT = 8080;

	/**
	 * The lease renewal interval (60 seconds).
	 */
	public static final long LEASE_RENEW_INTERVAL = 60000L;

	/**
	 * The lease expiration interval (150 seconds).
	 */
	public static final long LEASE_EXPIRE_INTERVAL = 150000L;

	}
