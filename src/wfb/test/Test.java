package wfb.test;

import java.io.File;

import javato.activetesting.instrumentor.InstrumentorForActiveTesting;

public class Test {
	/**
	 *	*javato.work.dir: src/benchmarks
	 *	*javato.app.main.class: benchmarks.testcases.TestDeadlock1
	 *	javato.ignore.methods: true
	 *	javato.ignore.allocs: true
	 *	javato.ignore.fields: true
	 *	javato.activetesting.errorlist.file: src/benchmarks/error.list
	 *	javato.activetesting.trialnum.list: 1,2,3
	 *	exec instr
	 *		- mkdir /home/wfb/毕设/calfuzzer/tmp
	 *		- javato.app.instr.dir: ""
	 *		- javato.app.class.path: ""
	 *		- javato.app.boot.list: ""
	 *		- javato.instops: "-p jb use-original-names -validate"
	 *		- javato_instops: "-p jb use-original-names -validate"
	 *		- javato_app_class_path: ""
	 *		- java javato.activetesting.instrumentor.InstrumentorForActiveTesting 
	 *		(-cp /home/wfb/毕设/calfuzzer/classes:/home/wfb/毕设/calfuzzer/lib/sootall-2.3.0.jar)
	 *			-keep-line-number 
	 *			-p jb use-original-names -validate 
	 *			-no-output-inner-classes-attribute 
	 *			-d /home/wfb/毕设/calfuzzer/src/benchmarks/tmpclasses 
	 *			-x javato
	 *			-x edu.berkeley.cs.detcheck
	 *			--app benchmarks.testcases.TestDeadlock1
	 *			
	 *	exec analysis-once javato.activetesting.analysis.class = javato.activetesting.IGoodlockAnalysis
	 *		- javato.app.args ""
	 *		- javato.app.class.path ""
	 *		- jvm_args -ea -Xbootclasspath/p:tmpbootclasses:${javato.home.dir}/classes
	 *		- start a stopwatch named timer
	 *		- java benchmarks.testcases.TestDeadlock1 
	 *		(-ea -Xbootclasspath/p:tmpbootclasses:/home/wfb/毕设/calfuzzer/classes)
//	 *		(-cp /home/wfb/毕设/calfuzzer/src/benchmarks/tmpclasses:/home/wfb/毕设/calfuzzer/classes)
	 * 		(将全部变量设置为系统变量)
	 *		- stop a stopwatch named timer and echo
	 *	exec active-loop javato.activetesting.analysis.class = javato.activetesting.DeadlockFuzzerAnalysis
	 *		- loadFile property="javato.activetesting.errorlist" srcFile="${javato.activetesting.errorlist.file}"
	 *		- echo the message in errorlist
	 */
	
	public static void main(String[] args) {
		File file = new File("/home/wfb/毕设/calfuzzer/tmp");
		file.mkdir();
		String[] args1 = {
			"-keep-line-number",
			"-p",
			"jb",
			"use-original-names",
			"-validate",
			"-no-output-inner-classes-attribute",
			"-d",
			"/home/wfb/毕设/calfuzzer/src/benchmarks/tmpclasses",
			"-x",
			"javato",
			"-x",
			"edu.berkeley.cs.detcheck",
			"--app",
			"benchmarks.testcases.TestDeadlock1"
		};
		InstrumentorForActiveTesting.main(args1);
		System.setProperty("javato.activetesting.analysis.class", "javato.activetesting.HybridAnalysis");
		
	}
}
