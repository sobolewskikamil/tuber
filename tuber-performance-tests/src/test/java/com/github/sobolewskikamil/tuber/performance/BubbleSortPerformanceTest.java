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

import org.openjdk.jmh.annotations.*;

public class BubbleSortPerformanceTest extends AbstractPerformanceTest {
    @Benchmark
    public void benchmarkTest(Context benchmarkContext) {
        super.benchmarkTest(benchmarkContext);
    }

    @State(Scope.Benchmark)
    public static class Context extends AbstractPerformanceTest.Context {
        @Param({"10", "100", "1000", "10000"})
        private int arraySize;

        @Setup
        public void setup() {
            super.setup();
        }

        @Override
        String getScript() {
            String template = "" +
                    "def main() {" +
                    "   array = createArray(%1$s);" +
                    "   sort(array, %1$s);" +
                    "}" +
                    "" +
                    "def createArray(length){" +
                    "   i = 0;" +
                    "   array = array(length);" +
                    "   while (i < length) {" +
                    "       array[i] = length - i;" +
                    "       i = i + 1;" +
                    "   }" +
                    "   return array;" +
                    "}" +
                    "" +
                    "def sort(array, length) {" +
                    "   i = 0;" +
                    "   while (i < length) {" +
                    "       j = 0;" +
                    "       while (j < length - 1) {" +
                    "           if (array[j] > array[j + 1]) {" +
                    "               temp = array[j];" +
                    "               array[j] = array[j + 1];" +
                    "               array[j + 1] = temp;" +
                    "           }" +
                    "           j = j + 1;" +
                    "       }" +
                    "       i = i + 1;" +
                    "   }" +
                    "}";
            return String.format(template, arraySize);
        }
    }
}
