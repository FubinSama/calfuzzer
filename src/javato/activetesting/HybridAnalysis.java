package javato.activetesting;

import java.util.HashSet;
import java.util.Set;

import com.wfb.net.PetriNet;
import com.wfb.utils.Util;

import javato.activetesting.activechecker.ActiveChecker;
import javato.activetesting.analysis.AnalysisImpl;
import javato.activetesting.common.Parameters;
import javato.activetesting.hybridracedetection.HybridRaceTracker;
import javato.activetesting.lockset.LockSet;
import javato.activetesting.lockset.LockSetTracker;
import javato.activetesting.reentrant.IgnoreRentrantLock;
import javato.activetesting.vc.VectorClockTracker;

public class HybridAnalysis extends AnalysisImpl {
    //private ContextIndexingTracker ciTracker;
    private VectorClockTracker vcTracker;
    private LockSetTracker lsTracker;
    private IgnoreRentrantLock ignoreRentrantLock;
    private HybridRaceTracker eb;
    
    private Set<Integer> systemLock;
    private PetriNet net;
    private volatile boolean flag = false;

    public void initialize() {
        //ciTracker = new ContextIndexingTracker();
        synchronized (ActiveChecker.lock) {
            vcTracker = new VectorClockTracker();
            lsTracker = new LockSetTracker();
            ignoreRentrantLock = new IgnoreRentrantLock();
            eb = new HybridRaceTracker();
            
            Util.setIidToLineMap(Parameters.iidToLineMapFile);
            net = new PetriNet();
            systemLock = new HashSet<>();
        }
    }
    
    private void createRootPlace(int threadNumber) {
    	if(flag) return;
    	synchronized (net) {
			if(flag) return;
			net.createRootNode(threadNumber);
			flag = true;
		}
    }

    public void lockBefore(Integer iid, Integer thread, Integer lock, Object actualLock) {
    	createRootPlace(thread);
        synchronized (ActiveChecker.lock) {
            if (ignoreRentrantLock.lockBefore(thread, lock)) {
//                if (Parameters.trackLockRaces) {
//                    LockSet ls = lsTracker.getLockSet(thread);
//                    Long mem = (long) lock;
//                    eb.checkRace(iid, thread, mem , false, vcTracker.getVectorClock(thread), ls,true,false);
//                    eb.addEvent(iid, thread, mem, false, vcTracker.getVectorClock(thread), ls);
//                }
                boolean isDeadlock = lsTracker.lockBefore(iid, thread, lock);
                
                if(actualLock.toString().charAt(0) != 'T') {
                	// 将不是在方法调用前添加的锁加入petri网,特征为不是Thread开头
                	net.addAcqTransitionNode(iid, thread, lock);
                } else {
                	systemLock.add(lock);
                }
            }
        }
    }
    
    public void unlockAfter(Integer iid, Integer thread, Integer lock) {
    	createRootPlace(thread);
        synchronized (ActiveChecker.lock) {
            if (ignoreRentrantLock.unlockAfter(thread, lock)) {
                lsTracker.unlockAfter(thread);
                
                if(!systemLock.contains(lock)) net.addRelTransitionNode(iid, thread, lock);
            }
        }
    }
    
    public void newExprAfter(Integer iid, Integer object, Integer objOnWhichMethodIsInvoked) {
        //ciTracker.newExprAfter(iid, object, 3); //@todo 3 must be parameterized
    }

    public void methodEnterBefore(Integer iid, Integer thread) {
    	createRootPlace(thread);
        //ciTracker.methodEnterBefore(iid);
    }

    public void methodExitAfter(Integer iid, Integer thread) {
        //ciTracker.methodExitAfter(iid);
    }
    
    public void startBefore(Integer iid, Integer parent, Integer child) {
    	createRootPlace(parent);
        synchronized (ActiveChecker.lock) {
            vcTracker.startBefore(parent, child);
            net.addStartTransitionNode(iid, parent, child);
        }
    }

    public void waitBefore(Integer iid, Integer thread, Integer lock) {
    	createRootPlace(thread);
        synchronized (ActiveChecker.lock) {
//            if (Parameters.trackLockRaces) {
//                LockSet ls = lsTracker.getLockSet(thread);
//                Long mem = (long) lock;
//                eb.checkRace(iid, thread, mem , false, vcTracker.getVectorClock(thread), ls,true,false);
//                eb.addEvent(iid, thread, mem, false, vcTracker.getVectorClock(thread), ls);
//            } else {
                Integer acquireIid = lsTracker.getLockAcquireIID(thread,lock);
                Long mem = (long) lock;
                eb.checkRace(acquireIid, thread, mem , false, vcTracker.getVectorClock(thread), LockSet.emptySet,true,false);
                eb.addEvent(acquireIid, thread, mem, false, vcTracker.getVectorClock(thread), LockSet.emptySet);
                net.addWaitTransitionNode(iid, thread, lock);
//            }
        }
    }

    public void waitAfter(Integer iid, Integer thread, Integer lock) {
    	net.connectNotifyWait(thread, lock);
//        if (!Parameters.trackLockRaces) {
//            synchronized (ActiveChecker.lock) {
//                vcTracker.waitAfter(thread, lock);
//            }
//        }
    }

    public void notifyBefore(Integer iid, Integer thread, Integer lock) {
    	createRootPlace(thread);
//        if (!Parameters.trackLockRaces) {
//            synchronized (ActiveChecker.lock) {
//                vcTracker.notifyBefore(thread, lock);
//            }
//        }
        synchronized (ActiveChecker.lock) {
//            if (!Parameters.trackLockRaces) {
                Integer acquireIid = lsTracker.getLockAcquireIID(thread,lock);
                Long mem = (long) lock;
                eb.checkRace(acquireIid, thread, mem , true, vcTracker.getVectorClock(thread), LockSet.emptySet,true,false);
                eb.addEvent(acquireIid, thread, mem, true, vcTracker.getVectorClock(thread), LockSet.emptySet);
                
                net.addNotifyTransitionNode(iid, thread, lock);
            }
//        }
    }

    public void notifyAllBefore(Integer iid, Integer thread, Integer lock) {
    	createRootPlace(thread);
//        if (!Parameters.trackLockRaces) {
//            synchronized (ActiveChecker.lock) {
//                vcTracker.notifyBefore(thread, lock);
//            }
//        }
        synchronized (ActiveChecker.lock) {
//            if (!Parameters.trackLockRaces) {
                Integer acquireIid = lsTracker.getLockAcquireIID(thread,lock);
                Long mem = (long) lock;
                eb.checkRace(acquireIid, thread, mem , true, vcTracker.getVectorClock(thread), LockSet.emptySet,true,false);
                eb.addEvent(acquireIid, thread, mem, true, vcTracker.getVectorClock(thread), LockSet.emptySet);
                
                net.addNotifyAllTransitionNode(iid, thread, lock);
//            }
        }
    }

    public void joinAfter(Integer iid, Integer parent, Integer child) {
    	createRootPlace(parent);
        synchronized (ActiveChecker.lock) {
            vcTracker.joinAfter(parent, child);
            
            net.addJoinTransitionNode(iid, parent, child);
        }
    }

    public void readBefore(Integer iid, Integer thread, Long memory, boolean isVolatile) {
    	createRootPlace(thread);
        synchronized (ActiveChecker.lock) {
            LockSet ls = lsTracker.getLockSet(thread);
            eb.checkRace(iid, thread, memory, true, vcTracker.getVectorClock(thread), ls, false,isVolatile);
            eb.addEvent(iid, thread, memory, true, vcTracker.getVectorClock(thread), ls);
            
            net.addReadTransitionNode(iid, thread, memory);
        }
    }

    public void writeBefore(Integer iid, Integer thread, Long memory, boolean isVolatile) {
    	createRootPlace(thread);
        synchronized (ActiveChecker.lock) {
            LockSet ls = lsTracker.getLockSet(thread);
            eb.checkRace(iid, thread, memory, false, vcTracker.getVectorClock(thread), ls, false,isVolatile);
            eb.addEvent(iid, thread, memory, false, vcTracker.getVectorClock(thread), ls);
            
            net.addWriteTransitionNode(iid, thread, memory);
        }
    }

    public void finish() {
        synchronized (ActiveChecker.lock) {
            eb.dumpRaces();
//            System.out.println("程序分析结束");
            String serverPath = System.getProperty("javato.tomcat.dir");
            if (serverPath == null) serverPath = System.getProperty("javato.home.dir") + "/";
            String className = System.getProperty("className");
            if (className == null) className = "petri5";
            else className = className.replace(".", "_");
            net.htmlShowNet(serverPath + "source/" + className + ".html");
            net.generateMap();
            net.generatePXML(serverPath + "source/" + className + ".xml");
            net.copyToFile(serverPath + "source/" + className + ".obj");
        }
    }
}
