package benchmarks.harness;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

/**
 * Copyright (c) 2004
 * <p/>
 * Koushik Sen <ksen@cs.uiuc.edu>
 * Pallavi Joshi <pallavi@cs.berkeley.edu>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class JigsawHarnessPretex {
    static final int iterations = 1;
    static final String[] URLs = {
            "http://localhost:8001/Doc/FAQ.html",
            "http://localhost:8001/Doc/frameset1.html",
            "http://localhost:8001/Doc/Main.html",
            "http://localhost:8001/Doc/Overview.html",
            "http://localhost:8001/Doc/page2.html",
            "http://localhost:8001/Doc/Programmer/design.html",
            "http://localhost:8001/Doc/TOC.html",
            "http://localhost:8001/Overview.html",
            "http://localhost:8001/RelNotes.html",
            "http://localhost:8001/User/Administration/AdminTools.html",
            "http://localhost:8001/User/Administration/cmdline.html",
            "http://localhost:8001/User/Administration/fr-toc.html",
            "http://localhost:8001/User/Administration/JigAdm.html",
            "http://localhost:8001/User/Administration/Overview.html",
            "http://localhost:8001/User/Administration/pics.html",
            "http://localhost:8001/User/Administration/propeditor.html",
            "http://localhost:8001/User/Administration/resedit.html",
            "http://localhost:8001/User/Administration/tools/jhttpd.html",
            "http://localhost:8001/User/Administration/tools/jindex.html",
            "http://localhost:8001/User/Administration/tools/Overview.html",
            "http://localhost:8001/User/FAQ.html",
            "http://localhost:8001/User/fr-Overview.html",
            "http://localhost:8001/User/fr-toc.html",
            "http://localhost:8001/User/Introduction/architecture-new.html",
            "http://localhost:8001/User/Introduction/architecture.html",
            "http://localhost:8001/User/Introduction/fr-toc.html",
            "http://localhost:8001/User/Introduction/indexer.html",
            "http://localhost:8001/User/Introduction/installation.html",
            "http://localhost:8001/User/Introduction/mac.html",
            "http://localhost:8001/User/Introduction/Overview.html",
            "http://localhost:8001/User/Introduction/performance.html",
            "http://localhost:8001/User/Introduction/wp.html",
            "http://localhost:8001/User/Overview.html",
            "http://localhost:8001/User/Paper/Position.html",
            "http://localhost:8001/User/Reference/filters.html",
            "http://localhost:8001/User/Reference/fr-filters.html",
            "http://localhost:8001/User/Reference/fr-properties.html",
            "http://localhost:8001/User/Reference/fr-resources.html",
            "http://localhost:8001/User/Reference/fr-toc.html",
            "http://localhost:8001/User/Reference/Overview.html",
            "http://localhost:8001/User/Reference/resources.html",
            "http://localhost:8001/User/Reference/template.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.auth.AuthFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.auth.GenericAuthFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.contrib.CheckpointResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.contrib.LogFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.contrib.PasswordEditor.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.contrib.SalvagerResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.cvs.AutoLookupDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.cvs.CvsDirectoryResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.filters.AccessLimitFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.filters.CounterFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.filters.DebugFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.filters.ErrorFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.filters.ProcessFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.filters.PutSizeFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.forms.CgiResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.forms.PostableResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.http.GeneralProp.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.http.LoggingProp.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.http.socket.SocketConnectionProp.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.http.UnixProp.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.indexer.ExtensionsEditor.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.map.MapResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.pics.DataBaseBureauResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.pics.LabelBureauResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.pics.PICSFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.proxy.CacheProp.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.proxy.ForwardDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.proxy.MirrorDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.proxy.ProxyDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.proxy.ProxyProp.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.DirectoryResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.FileResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.FilteredResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.HTTPResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.NegotiatedResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.PassDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.ProcessFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.PutableDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.Resource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.ResourceFilter.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.SSIResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.StoreContainer.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.resources.VirtualHostResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.servlet.RemoteServletWrapper.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.servlet.ServletDirectory.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.servlet.ServletWrapper.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.ssi.DefaultCommandRegistry.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.ssi.SSIResource.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.status.Statistics.html",
            "http://localhost:8001/User/Reference/w3c.jigsaw.status.ThreadStat.html",
            "http://localhost:8001/User/Reference/w3c.www.protocol.http.cache.CacheFilter.html",
            "http://localhost:8001/User/Reference/w3c.www.protocol.http.cookies.CookieFilter.html",
            "http://localhost:8001/User/Reference/w3c.www.protocol.http.HttpManager.html",
            "http://localhost:8001/User/Reference/w3c.www.protocol.http.icp.ICPFilter.html",
            "http://localhost:8001/User/Reference/w3c.www.protocol.http.proxy.ProxyDispatcher.html",
            "http://localhost:8001/User/Tutorials/configuration.html",
            "http://localhost:8001/User/Tutorials/filter.html",
            "http://localhost:8001/User/Tutorials/fr-toc.html",
            "http://localhost:8001/User/Tutorials/Overview.html",
            "http://localhost:8001/User/Tutorials/resource.html",
            "http://localhost:8001/User/Tutorials/SSIResource.html",
            "http://localhost:8001/User/Tutorials/usecvs.html"
    };

    private static String readFully(Reader r) throws IOException {
        char[] buf = new char[10000];
        StringBuffer str = new StringBuffer();
        while (true) {
            int len = r.read(buf);
            if (len < 0) {
                return str.toString();
            } else {
                str.append(buf, 0, len);
            }
        }
    }

    public static String readURL(String url) {
        try {
            //System.out.println("Opening url " + url + " at time " + System.currentTimeMillis());
            URL u = new URL(url);
            URLConnection c = u.openConnection();
            c.connect();
            String encoding = c.getContentEncoding();
            InputStream s = c.getInputStream();
            Reader r = new BufferedReader(encoding == null ? new InputStreamReader(s)
                    : new InputStreamReader(s, encoding));
            String result = readFully(r);
            r.close();
            System.out.println("done with url");
            return result;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            //System.exit(99);
            return null;
        }
    }

    static void waitForPort(int port) {
        while (true) {
            try {
                Socket s = new Socket("localhost", port);
                return;
            } catch (IOException ex) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    static void runLoader() {
        waitForPort(8001);
        Thread[] threads = new Thread[URLs.length];

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            System.out.println("Iteration: " + i);
            for (int j = 0; j < URLs.length; j++) {
                threads[j] = (new MyThread(URLs[j]));
                threads[j].start();
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                    System.out.println("Exception while sleeping between spawning threads");
                }
                //readURL(URLs[j]);
            }
        }
        for (int i = 0; i < URLs.length; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException e) {
                System.out.println("Exception while waiting for thread " + i);
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Took " + (t2 - t1) + "ms");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        runLoader();
    }

}

class MyThread extends Thread {
    private String url;

    public MyThread(String name) {
        super();
        this.url = name;
    }

    public void run() {
        JigsawHarnessPretex.readURL(url);
    }
}