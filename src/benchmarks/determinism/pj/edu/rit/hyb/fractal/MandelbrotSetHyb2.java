//******************************************************************************
//
// File:    MandelbrotSetHyb2.java
// Package: benchmarks.determinism.pj.edu.rithyb.fractal
// Unit:    Class benchmarks.determinism.pj.edu.rithyb.fractal.MandelbrotSetHyb2
//
// This Java source file is copyright (C) 2008 by Alan Kaminsky. All rights
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

package benchmarks.determinism.pj.edu.rithyb.fractal;

import benchmarks.determinism.pj.edu.ritcolor.HSB;

import benchmarks.determinism.pj.edu.ritimage.PJGColorImage;
import benchmarks.determinism.pj.edu.ritimage.PJGImage;

import benchmarks.determinism.pj.edu.ritio.Files;

import benchmarks.determinism.pj.edu.ritmp.IntegerBuf;
import benchmarks.determinism.pj.edu.ritmp.ObjectBuf;

import benchmarks.determinism.pj.edu.ritmp.buf.ObjectItemBuf;

import benchmarks.determinism.pj.edu.ritpj.Comm;
import benchmarks.determinism.pj.edu.ritpj.CommStatus;
import benchmarks.determinism.pj.edu.ritpj.IntegerForLoop;
import benchmarks.determinism.pj.edu.ritpj.IntegerSchedule;
import benchmarks.determinism.pj.edu.ritpj.ParallelRegion;
import benchmarks.determinism.pj.edu.ritpj.ParallelSection;
import benchmarks.determinism.pj.edu.ritpj.ParallelTeam;

import benchmarks.determinism.pj.edu.ritutil.Range;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class MandelbrotSetHyb2 is a hybrid parallel program that calculates the
 * Mandelbrot Set. The program runs with one process per node and multiple
 * threads per process. The program uses the master-worker pattern for load
 * balancing. Each process in the program is an independent worker process. Each
 * worker process calculates a series of row slices of the Mandelbrot Set image,
 * as assigned by the master thread. Within each row slice, the threads of the
 * worker process calculate the rows of the slice in parallel. Each worker
 * writes its own slices to its own (partial) PJG image file. The per-worker
 * image files can be combined offline into a single image file if desired.
 * <P>
 * The row slices are determined by the <TT>pj.schedule</TT> property specified
 * on the command line; the default is to divide the rows evenly among the
 * worker processes (i.e. no load balancing). For further information about the
 * <TT>pj.schedule</TT> property, see class {@linkplain benchmarks.determinism.pj.edu.ritpj.PJProperties
 * PJProperties}.
 * <P>
 * Within each row slice, the rows are partitioned among the threads of the
 * worker process using the parallel loop schedule specified by the last command
 * line argument. If this argument is missing, the default is to divide the rows
 * evenly among the threads (i.e. no load balancing). For further information,
 * see the <TT>parse()</TT> method in class {@linkplain
 * benchmarks.determinism.pj.edu.ritpj.IntegerSchedule IntegerSchedule}.
 * <P>
 * Usage: java -Dpj.np=<I>Kp</I> -Dpj.nt=<I>Kt</I> [
 * -Dpj.schedule=<I>procschedule</I> ] benchmarks.determinism.pj.edu.rithyb.fractal.MandelbrotSetHyb2
 * <I>width</I> <I>height</I> <I>xcenter</I> <I>ycenter</I> <I>resolution</I>
 * <I>maxiter</I> <I>gamma</I> <I>filename</I> [ <I>thrschedule</I> ]
 * <BR><I>Kp</I> = Number of parallel processes
 * <BR><I>Kt</I> = Number of parallel threads per process
 * <BR><I>procschedule</I> = Load balancing schedule for processes
 * <BR><I>width</I> = Image width (pixels)
 * <BR><I>height</I> = Image height (pixels)
 * <BR><I>xcenter</I> = X coordinate of center point
 * <BR><I>ycenter</I> = Y coordinate of center point
 * <BR><I>resolution</I> = Pixels per unit
 * <BR><I>maxiter</I> = Maximum number of iterations
 * <BR><I>gamma</I> = Used to calculate pixel hues
 * <BR><I>filename</I> = PJG image file name
 * <BR><I>thrschedule</I> = Load balancing schedule for threads
 * <P>
 * If <I>filename</I> is specified as, for example, <TT>"image.pjg"</TT>, then
 * the per-worker image files are named <TT>"image_0.pjg"</TT>,
 * <TT>"image_1.pjg"</TT>, and so on through <I>Kp</I>-1.
 * <P>
 * The program considers a rectangular region of the complex plane centered at
 * (<I>xcenter,ycenter</I>) of <I>width</I> pixels by <I>height</I> pixels,
 * where the distance between adjacent pixels is 1/<I>resolution</I>. The
 * program takes each pixel's location as a complex number <I>c</I> and performs
 * the following iteration:
 * <P>
 * <I>z</I><SUB>0</SUB> = 0
 * <BR><I>z</I><SUB><I>i</I>+1</SUB> = <I>z</I><SUB><I>i</I></SUB><SUP>2</SUP> + <I>c</I>
 * <P>
 * until <I>z</I><SUB><I>i</I></SUB>'s magnitude becomes greater than or equal
 * to 2, or <I>i</I> reaches a limit of <I>maxiter</I>. The complex numbers
 * <I>c</I> where <I>i</I> reaches a limit of <I>maxiter</I> are considered to
 * be in the Mandelbrot Set. (Actually, a number is in the Mandelbrot Set only
 * if the iteration would continue forever without <I>z</I><SUB><I>i</I></SUB>
 * becoming infinite; the foregoing is just an approximation.) The program
 * creates an image with the pixels corresponding to the complex numbers
 * <I>c</I> and the pixels' colors corresponding to the value of <I>i</I>
 * achieved by the iteration. Following the traditional practice, points in the
 * Mandelbrot set are black, and the other points are brightly colored in a
 * range of colors depending on <I>i</I>. The exact hue of each pixel is
 * (<I>i</I>/<I>maxiter</I>)<SUP><I>gamma</I></SUP>. The image is stored in a
 * Parallel Java Graphics (PJG) file specified on the command line.
 * <P>
 * The computation is performed in parallel in multiple processors. The program
 * measures the computation's running time, including the time to write the
 * image file.
 *
 * @author  Alan Kaminsky
 * @version 26-May-2008
 */
public class MandelbrotSetHyb2
	{

// Prevent construction.

	private MandelbrotSetHyb2()
		{
		}

// Program shared variables.

	// Communicator.
	static Comm world;
	static int size;
	static int rank;

	// Command line arguments.
	static int width;
	static int height;
	static double xcenter;
	static double ycenter;
	static double resolution;
	static int maxiter;
	static double gamma;
	static File filename;
	static IntegerSchedule thrschedule;

	// Initial pixel offsets from center.
	static int xoffset;
	static int yoffset;

	// Image matrix.
	static int[][] matrix;
	static PJGColorImage image;
	static PJGImage.Writer writer;

	// Storage for matrix row slice.
	static int[][] slice;

	// Table of hues.
	static int[] huetable;

// Main program.

	/**
	 * Mandelbrot Set main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		// Start timing.
		long t1 = System.currentTimeMillis();

		// Initialize middleware.
		Comm.init (args);
		world = Comm.world();
		size = world.size();
		rank = world.rank();

		// Validate command line arguments.
		if (args.length < 8 || args.length > 9) usage();
		width = Integer.parseInt (args[0]);
		height = Integer.parseInt (args[1]);
		xcenter = Double.parseDouble (args[2]);
		ycenter = Double.parseDouble (args[3]);
		resolution = Double.parseDouble (args[4]);
		maxiter = Integer.parseInt (args[5]);
		gamma = Double.parseDouble (args[6]);
		filename = new File (args[7]);
		thrschedule =
			args.length == 9 ?
				IntegerSchedule.parse (args[8]) :
				IntegerSchedule.fixed();

		// Initial pixel offsets from center.
		xoffset = -(width - 1) / 2;
		yoffset = (height - 1) / 2;

		// Allocate storage for pixel matrix row references only.
		matrix = new int [height] [];

		// Prepare to write image row slices to per-worker PJG image file.
		image = new PJGColorImage (height, width, matrix);
		writer =
			image.prepareToWrite
				(new BufferedOutputStream
					(new FileOutputStream
						(Files.fileForRank (filename, rank))));

		// Create table of hues for different iteration counts.
		huetable = new int [maxiter+1];
		for (int i = 0; i < maxiter; ++ i)
			{
			huetable[i] = HSB.pack
				(/*hue*/ (float) Math.pow (((double)i)/((double)maxiter),gamma),
				 /*sat*/ 1.0f,
				 /*bri*/ 1.0f);
			}
		huetable[maxiter] = HSB.pack (1.0f, 1.0f, 0.0f);

		long t2 = System.currentTimeMillis();

		// In master process, run master section and worker section in parallel.
		if (rank == 0)
			{
			new ParallelTeam(2).execute (new ParallelRegion()
				{
				public void run() throws Exception
					{
					execute (new ParallelSection()
						{
						public void run() throws Exception
							{
							masterSection();
							}
						},
					new ParallelSection()
						{
						public void run() throws Exception
							{
							workerSection();
							}
						});
					}
				});
			}

		// In worker process, run only worker section.
		else
			{
			workerSection();
			}

		long t3 = System.currentTimeMillis();

		// Close image file.
		writer.close();

		// Stop timing.
		long t4 = System.currentTimeMillis();
		System.out.println ((t2-t1) + " msec pre " + rank);
		System.out.println ((t3-t2) + " msec calc " + rank);
		System.out.println ((t4-t3) + " msec post " + rank);
		System.out.println ((t4-t1) + " msec total " + rank);
		}

// Hidden operations.

	/**
	 * Perform the master section.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private static void masterSection()
		throws IOException
		{
		int worker;
		Range range;

		// Set up a schedule object to divide the row range into chunks.
		IntegerSchedule schedule = IntegerSchedule.runtime();
		schedule.start (size, new Range (0, height-1));

		// Send initial chunk range to each worker. If range is null, no more
		// work for that worker. Keep count of active workers.
		int activeWorkers = size;
		for (worker = 0; worker < size; ++ worker)
			{
			range = schedule.next (worker);
			world.send (worker, ObjectBuf.buffer (range));
			if (range == null) -- activeWorkers;
			}

		// Repeat until all workers have finished.
		while (activeWorkers > 0)
			{
			// Receive an empty message from any worker.
			CommStatus status = world.receive (null, IntegerBuf.emptyBuffer());
			worker = status.fromRank;

			// Send next chunk range to that specific worker. If null, no more
			// work.
			range = schedule.next (worker);
			world.send (worker, ObjectBuf.buffer (range));
			if (range == null) -- activeWorkers;
			}
		}

	/**
	 * Perform the worker section.
	 *
	 * @exception  Exception
	 *     Thrown if an I/O error occurred.
	 */
	private static void workerSection()
		throws Exception
		{
		// Parallel team to calculate each slice in multiple threads.
		ParallelTeam team = new ParallelTeam();

		// Process chunks from master.
		for (;;)
			{
			// Receive chunk range from master. If null, no more work.
			ObjectItemBuf<Range> rangeBuf = ObjectBuf.buffer();
			world.receive (0, rangeBuf);
			Range range = rangeBuf.item;
			if (range == null) break;
			final int lb = range.lb();
			final int ub = range.ub();
			final int len = range.length();

			// Allocate storage for matrix row slice if necessary.
			if (slice == null || slice.length < len)
				{
				slice = new int [len] [width];
				}

			// Compute rows of slice in parallel threads.
			team.execute (new ParallelRegion()
				{
				public void run() throws Exception
					{
					execute (lb, ub, new IntegerForLoop()
						{
						// Use the thread-level loop schedule.
						public IntegerSchedule schedule()
							{
							return thrschedule;
							}

						// Compute all rows and columns in slice.
						public void run (int first, int last)
							{
							for (int r = first; r <= last; ++ r)
								{
								int[] slice_r = slice[r-lb];
								double y = ycenter + (yoffset - r) / resolution;

								for (int c = 0; c < width; ++ c)
									{
									double x =
										xcenter + (xoffset + c) / resolution;

									// Iterate until convergence.
									int i = 0;
									double aold = 0.0;
									double bold = 0.0;
									double a = 0.0;
									double b = 0.0;
									double zmagsqr = 0.0;
									while (i < maxiter && zmagsqr <= 4.0)
										{
										++ i;
										a = aold*aold - bold*bold + x;
										b = 2.0*aold*bold + y;
										zmagsqr = a*a + b*b;
										aold = a;
										bold = b;
										}

									// Record number of iterations for pixel.
									slice_r[c] = huetable[i];
									}
								}
							}
						});
					}
				});

			// Report completion of slice to master.
			world.send (0, IntegerBuf.emptyBuffer());

			// Set full pixel matrix rows to refer to slice rows.
			System.arraycopy (slice, 0, matrix, lb, len);

			// Write row slice of full pixel matrix to image file.
			writer.writeRowSlice (range);
			}
		};

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java -Dpj.np=<Kp> -Dpj.nt=<Kt> [-Dpj.schedule=<procschedule>] benchmarks.determinism.pj.edu.rithyb.fractal.MandelbrotSetHyb2 <width> <height> <xcenter> <ycenter> <resolution> <maxiter> <gamma> <filename> [<thrschedule>]");
		System.err.println ("<Kp> = Number of parallel processes");
		System.err.println ("<Kt> = Number of parallel threads per process");
		System.err.println ("<procschedule> = Load balancing schedule for processes");
		System.err.println ("<width> = Image width (pixels)");
		System.err.println ("<height> = Image height (pixels)");
		System.err.println ("<xcenter> = X coordinate of center point");
		System.err.println ("<ycenter> = Y coordinate of center point");
		System.err.println ("<resolution> = Pixels per unit");
		System.err.println ("<maxiter> = Maximum number of iterations");
		System.err.println ("<gamma> = Used to calculate pixel hues");
		System.err.println ("<filename> = PJG image file name");
		System.err.println ("<thrschedule> = Load balancing schedule for threads");
		System.exit (1);
		}

	}
