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
package com.github.sobolewskikamil.tuber.language.node.expression.builtin;

import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.io.PrintWriter;

@NodeInfo(shortName = "println")
public abstract class PrintlnBuiltinNode extends BuiltinNode {
    @TruffleBoundary
    private static void doPrint(PrintWriter out, long value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, double value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, boolean value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, String value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, NullType value) {
        out.println(value.toString());
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, ArrayType value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object value) {
        out.println(value);
    }

    @Specialization
    public long println(long value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public double println(double value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public boolean println(boolean value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public String println(String value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public NullType println(NullType value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public ArrayType println(ArrayType value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public Object println(Object value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }
}
