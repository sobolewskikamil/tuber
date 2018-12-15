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
package com.github.sobolewskikamil.tuber.integration;

import com.github.sobolewskikamil.tuber.launcher.TuberLauncher;
import org.apache.commons.io.input.NullInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AlgorithmTest {
    private ByteArrayOutputStream out;
    private TuberLauncher launcher;

    @BeforeEach
    void setup() {
        out = new ByteArrayOutputStream();
        launcher = new TuberLauncher(new NullInputStream(10), out);
    }

    @Test
    void shouldComputeFactorial() {
        // given
        String source = "" +
                "def main() {" +
                "   number = 5;" +
                "   result = factorial(number);" +
                "   println(result);" +
                "}" +
                "" +
                "def factorial(n) {" +
                "   if (n < 1) {" +
                "       return 1;" +
                "   }" +
                "   return n * factorial(n - 1);" +
                "}";

        // when
        launcher.launchFromString(source);

        // then
        int result = Integer.parseInt(out.toString().trim());
        assertThat(result).isEqualTo(120);
    }

    @Test
    void shouldComputeFibonacci() {
        // given
        String source = "" +
                "def main() {" +
                "   number = 10;" +
                "   result = fib(number);" +
                "   println(result);" +
                "}" +
                "" +
                "def fib(n) {" +
                "   if (n < 2) {" +
                "       return n;" +
                "   }" +
                "   return fib(n - 1) + fib(n - 2);" +
                "}";

        // when
        launcher.launchFromString(source);

        // then
        int result = Integer.parseInt(out.toString().trim());
        assertThat(result).isEqualTo(55);
    }

    @Test
    void shouldBubbleSortArray() {
        // given
        String source = "" +
                "def main() {" +
                "   array = {4, 3, 2, 1};" +
                "   sort(array, 4);" +
                "   println(array);" +
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

        // when
        launcher.launchFromString(source);

        // then
        assertThat(out.toString().trim()).isEqualTo("[1, 2, 3, 4]");
    }
}
