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
package com.github.sobolewskikamil.tuber.language.node.expression.logical;

import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import com.github.sobolewskikamil.tuber.language.node.expression.BinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = ">=")
public abstract class GreaterOrEqualNode extends BinaryNode {

    @Specialization
    boolean greaterOrEqual(long left, long right) {
        return left >= right;
    }

    @Specialization
    boolean greaterOrEqual(double left, double right) {
        return Double.compare(left, right) >= 0;
    }

    @Fallback
    Object typeError(Object left, Object right) {
        throw TuberException.ofError(this, left, right);
    }
}
