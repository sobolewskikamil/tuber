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
package com.github.sobolewskikamil.tuber.language.node.expression.literal;

import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.Arrays;

public class ArrayLiteralNode extends ExpressionNode {
    private final ExpressionNode[] expressionNodes;

    public ArrayLiteralNode(ExpressionNode... expressionNodes) {
        this.expressionNodes = expressionNodes;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return execute(frame);
    }

    @Override
    public ArrayType executeArrayType(VirtualFrame frame) {
        return execute(frame);
    }

    private ArrayType execute(VirtualFrame frame) {
        Object[] executed = Arrays.stream(expressionNodes)
                .map(e -> e.executeGeneric(frame))
                .toArray(Object[]::new);
        return new ArrayType(executed);
    }
}
