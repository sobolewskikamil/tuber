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
package com.github.sobolewskikamil.tuber.language.node.expression.arithmetic;

import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import com.github.sobolewskikamil.tuber.language.node.expression.BinaryNode;
import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.apache.commons.lang3.ArrayUtils;

@NodeInfo(shortName = "+")
public abstract class AddNode extends BinaryNode {

    @Specialization
    long add(long left, long right) {
        return Math.addExact(left, right);
    }

    @Specialization
    double add(double left, double right) {
        return Double.sum(left, right);
    }

    @Specialization
    ArrayType add(ArrayType left, ArrayType right) {
        return new ArrayType(ArrayUtils.addAll(left.getValues(), right.getValues()));
    }

    @Specialization(guards = "isAnyString(left, right)")
    String add(Object left, Object right) {
        return String.format("%s%s", left.toString(), right.toString());
    }

    boolean isAnyString(Object first, Object second) {
        return first instanceof String || second instanceof String;
    }

    @Fallback
    Object typeError(Object left, Object right) {
        throw TuberException.ofError(this, left, right);
    }
}
