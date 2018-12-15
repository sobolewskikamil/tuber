/*
 * Copyright (c) 2018 Kamil Sobolewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.sobolewskikamil.tuber.performance;

import com.github.sobolewskikamil.tuber.launcher.TuberLauncher;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

abstract class AbstractPerformanceTest {
    @Test
    void runBenchmarks() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName())
                .mode(Mode.AverageTime)
                .timeUnit(MILLISECONDS)
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(10))
                .threads(1)
                .warmupForks(0)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .jvmArgs("-server", "-Xms2048M", "-Xmx2048M", "-XX:+UseG1GC"/*, "-XX:-UseJVMCIClassLoader"*/)
                .build();
        new Runner(opt).run();
    }

    void benchmarkTest(Context benchmarkContext) {
        // given
        String script = benchmarkContext.script;
        TuberLauncher executor = benchmarkContext.executor;

        // when
        executor.launchFromString(script);
    }

    @State(Scope.Benchmark)
    public abstract static class Context {
        TuberLauncher executor;
        String script;

        protected void setup() {
            executor = new TuberLauncher(System.in, System.out);
            script = getScript();
        }

        abstract String getScript();
    }
}
