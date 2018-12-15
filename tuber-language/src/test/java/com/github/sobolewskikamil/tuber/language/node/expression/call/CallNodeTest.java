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
package com.github.sobolewskikamil.tuber.language.node.expression.call;

import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallNodeTest {
    @Mock
    private RootCallTarget rootCallTarget;
    @Mock
    private ExpressionNode functionNode;
    @Mock
    private ExpressionNode argumentNode1;
    @Mock
    private ExpressionNode argumentNode2;
    @Mock
    private VirtualFrame frame;

    @Test
    void shouldDeleteCallsToFunctionNodeAndArguments() {
        // given
        when(functionNode.executeGeneric(frame)).thenReturn(rootCallTarget);
        when(argumentNode1.executeGeneric(frame)).thenReturn("arg1");
        when(argumentNode2.executeGeneric(frame)).thenReturn("arg2");
        when(rootCallTarget.call("arg1", "arg2")).thenReturn("result");

        CallNode callNode = new CallNode(functionNode, new ExpressionNode[]{argumentNode1, argumentNode2});

        // when
        Object result = callNode.executeGeneric(frame);

        // then
        assertThat(result).isEqualTo("result");

        InOrder inOrder = Mockito.inOrder(functionNode, argumentNode1, argumentNode2, rootCallTarget);
        inOrder.verify(functionNode).executeGeneric(frame);
        inOrder.verify(argumentNode1).executeGeneric(frame);
        inOrder.verify(argumentNode2).executeGeneric(frame);
        inOrder.verify(rootCallTarget).call("arg1", "arg2");
    }
}
