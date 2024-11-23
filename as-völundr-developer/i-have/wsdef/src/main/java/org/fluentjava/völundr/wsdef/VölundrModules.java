package org.fluentjava.völundr.wsdef;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fluentjava.iwant.api.javamodules.CodeFormatterPolicy;
import org.fluentjava.iwant.api.javamodules.CodeFormatterPolicy.TabulationCharValue;
import org.fluentjava.iwant.api.javamodules.DefaultTestClassNameFilter;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import org.fluentjava.iwant.api.wsdef.WorkspaceContext;
import org.fluentjava.iwant.core.javamodules.JavaModules;

public class VölundrModules extends JavaModules {

	private static final String HAMCREST_VER = "1.3";
	private static final String SLF4J_VER = "1.7.25";

	private static final JavaModule commonsCollections = binModule(
			"commons-collections", "commons-collections", "3.2.1");
	private static final JavaModule commonsIo = binModule("commons-io",
			"commons-io", "2.11.0");
	private static final JavaModule hamcrestCore = binModule("org.hamcrest",
			"hamcrest-core", HAMCREST_VER);
	private static final JavaModule jfreechart = binModule("org.jfree",
			"jfreechart", "1.5.0");
	private static final JavaModule junit = binModule("junit", "junit", "4.12",
			hamcrestCore);
	private static final JavaModule log4j = binModule("log4j", "log4j",
			"1.2.17");
	private static final JavaModule osmotester = binModule("net.kanstren.osmo",
			"osmotester", "3.8.1");
	private static final JavaModule slf4jApi = binModule("org.slf4j",
			"slf4j-api", SLF4J_VER);
	private static final JavaModule slf4jLog4j12 = binModule("org.slf4j",
			"slf4j-log4j12", SLF4J_VER, log4j, slf4jApi);
	private static final JavaModule spotbugsAnnotations = binModule(
			"com.github.spotbugs", "spotbugs-annotations", "4.5.2");

	private final Set<JavaModule> junit5runnerMods;

	public VölundrModules(WorkspaceContext ctx) {
		this.junit5runnerMods = ctx.iwantPlugin().junit5runner()
				.withDependencies();
		dependencyRoots();
	}

	private IwantSrcModuleSpex völundrModule(String name) {
		// prefix name so you can safely import modules from another project to
		// the same IDE:
		return srcModule("völundr-" + name).locationUnderWsRoot(name)
				.testedBy(new VölundrTestNameFilter())
				.codeFormatter(codeFormatterPolicy())
				.testDeps(junit5runnerMods);
	}

	private static class VölundrTestNameFilter
			extends DefaultTestClassNameFilter {

		private static final List<String> BROKEN_TESTS = List.of(
				"org.fluentjava.volundr.graph.frequency.FrequencyGraphBuilderTest",
				"org.fluentjava.volundr.graph.scatterplot.ScatterPlotBuilderTest",
				"org.fluentjava.volundr.graph.jfreechart.api.ChartGraphApiTest",
				"org.fluentjava.volundr.graph.jfreechart.api.FrequencyGraphApiTest",
				"org.fluentjava.volundr.junit.JUnitUtilsTest");

		@Override
		public boolean matches(String candidate) {
			// TODO fix the build or the tests so this custom test name filter
			// is not needed
			if (BROKEN_TESTS.contains(candidate)) {
				System.err.println("WARN: skipping broken test " + candidate);
				return false;
			}
			if (candidate.startsWith("org.fluentjava.volundr.smithy")) {
				System.err
						.println("WARN: skipping whole broken test package of "
								+ candidate);
				return false;
			}
			return super.matches(candidate);
		}

	}

	private static CodeFormatterPolicy codeFormatterPolicy() {
		CodeFormatterPolicy codeFormatterPolicy = new CodeFormatterPolicy();
		codeFormatterPolicy.lineSplit = 120;
		codeFormatterPolicy.tabulationChar = TabulationCharValue.SPACE;
		return codeFormatterPolicy;
	}

	SortedSet<JavaSrcModule> dependencyRoots() {
		return new TreeSet<>(Arrays.asList(classhelper(), junitUtils(),
				stringSplitter(), threadengineApiExample(), volundrOsmoTester(),
				volundrSmithy()));
	}

	private JavaSrcModule asExpected() {
		return lazy(() -> völundrModule("as-expected").noTestResources()
				.mainDeps(junit).testDeps().end());
	}

	private JavaSrcModule bytesToString() {
		return lazy(() -> völundrModule("bytes-to-string").noTestResources()
				.mainDeps().testDeps(junit).end());
	}

	private JavaSrcModule classhelper() {
		return lazy(() -> völundrModule("classhelper").mainDeps()
				.testDeps(junit, slf4jApi).testRuntimeDeps(slf4jLog4j12).end());
	}

	private JavaSrcModule concurrent() {
		return lazy(() -> völundrModule("concurrent").noTestResources()
				.mainDeps(slf4jApi).testDeps(junit)
				.testRuntimeDeps(slf4jLog4j12).end());
	}

	private JavaSrcModule linereader() {
		return lazy(() -> völundrModule("linereader").noTestResources()
				.mainDeps().testDeps(junit).end());
	}

	private JavaSrcModule stringToBytes() {
		return lazy(() -> völundrModule("string-to-bytes").noTestResources()
				.mainDeps().testDeps(junit).end());
	}

	private JavaSrcModule fileutil() {
		return lazy(() -> völundrModule("fileutil").noTestResources()
				.mainDeps(stringToBytes()).testDeps(junit).end());
	}

	private JavaSrcModule statistics() {
		return lazy(() -> völundrModule("statistics").noTestResources()
				.mainDeps().testDeps(junit).end());
	}

	private JavaSrcModule stringToOutputstream() {
		return lazy(() -> völundrModule("string-to-outputstream")
				.noTestResources().mainDeps().testDeps(junit).end());
	}

	private JavaSrcModule stringToInputstream() {
		return lazy(() -> völundrModule("string-to-inputstream")
				.noTestResources().mainDeps(junit, stringToBytes())
				.testDeps(junit, linereader()).end());
	}

	private JavaSrcModule inputstreamToString() {
		return lazy(() -> völundrModule("inputstream-to-string")
				.noTestResources().mainDeps(commonsIo).testDeps(junit).end());
	}

	private JavaSrcModule volundrSmithy() {
		return lazy(() -> völundrModule("volundr-smithy").noTestResources()
				.mainDeps(asynchronousStreamReader(), bytesToString(),
						concurrent(), inputstreamToString(), linereader(),
						slf4jApi, streamReader(), stringToBytes(),
						stringToOutputstream(), stronglytypedSortedbag(),
						visitingInputstreams())
				.testDeps(asExpected(), junit).end());
	}

	private JavaSrcModule streamReader() {
		return lazy(() -> völundrModule("stream-reader").mainDeps(linereader())
				.testDeps(junit, stringToBytes(), stringToInputstream()).end());
	}

	private JavaSrcModule stronglytypedSortedbag() {
		return lazy(
				() -> völundrModule("stronglytyped-sortedbag").noTestResources()
						.mainDeps(commonsCollections).testDeps(junit).end());
	}

	private JavaSrcModule statisticsValueprovider() {
		return lazy(() -> völundrModule("statistics-valueprovider")
				.noTestResources()
				.mainDeps(statistics(), stronglytypedSortedbag())
				.testDeps(junit).end());
	}

	private JavaSrcModule asynchronousStreamReader() {
		return lazy(() -> völundrModule("asynchronous-stream-reader")
				.mainDeps(concurrent(), linereader(), slf4jApi, streamReader())
				.testDeps(junit, stringToBytes(), stringToInputstream())
				.testRuntimeDeps(slf4jLog4j12).end());
	}

	private JavaSrcModule graphJfreechart() {
		return lazy(() -> völundrModule("graph-jfreechart")
				.mainDeps(fileutil(), jfreechart, slf4jApi, spotbugsAnnotations)
				.testDeps(junit, statistics(), statisticsValueprovider())
				.testRuntimeDeps(slf4jLog4j12).end());
	}

	private JavaSrcModule graphJfreechartapi() {
		return lazy(() -> völundrModule("graph-jfreechart-api")
				.mainDeps(graphJfreechart(), statistics(),
						statisticsValueprovider(), stronglytypedSortedbag())
				.testDeps().testRuntimeDeps().end());
	}

	private JavaSrcModule osmoTestingStatistics() {
		return lazy(() -> völundrModule("osmo-testing-statistics")
				.mainDeps(graphJfreechart(), graphJfreechartapi(), slf4jApi,
						spotbugsAnnotations, statistics())
				.testDeps().testRuntimeDeps().end());
	}

	private JavaSrcModule threadengineApiExample() {
		return lazy(() -> völundrModule("threadengine-api-example").mainDeps()
				.testDeps(concurrent(), slf4jApi).testRuntimeDeps().end());
	}

	private JavaSrcModule junitUtils() {
		return lazy(() -> völundrModule("junit-utils").mainDeps(junit)
				.testDeps().testRuntimeDeps().end());
	}

	private JavaSrcModule stringSplitter() {
		return lazy(() -> völundrModule("string-splitter").mainDeps().testDeps()
				.testRuntimeDeps().end());
	}

	private JavaSrcModule visitingInputstreams() {
		return lazy(
				() -> völundrModule("visiting-inputstreams")
						.mainDeps(asynchronousStreamReader(), linereader(),
								streamReader())
						.testDeps().testRuntimeDeps().end());
	}

	private JavaSrcModule volundrOsmoTester() {
		return lazy(() -> völundrModule("volundr-osmo-tester")
				.mainDeps(slf4jApi, osmotester, osmoTestingStatistics(),
						spotbugsAnnotations)
				.testDeps().testRuntimeDeps().end());
	}

}
