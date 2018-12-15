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
package com.github.sobolewskikamil.tuber.language.node.expression.flowcontrol;

import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import com.github.sobolewskikamil.tuber.language.node.StatementNode;
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IfNodeTest {
    @Mock
    private ExpressionNode conditionNode;
    @Mock
    private ExpressionNode thenPartNode;
    @Mock
    private VirtualFrame frame;

    @Test
    void shouldExecuteThenPartWhenConditionIsTrue() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(true);

        IfNode node = new IfNode(conditionNode, thenPartNode, null);

        // when
        node.executeVoid(frame);

        // then
        InOrder inOrder = inOrder(conditionNode, thenPartNode);
        inOrder.verify(conditionNode, times(1)).executeBoolean(frame);
        inOrder.verify(thenPartNode, times(1)).executeVoid(frame);
    }

    @Test
    void shouldExecuteNothingWhenConditionIsFalseAndNotElsePart() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(false);

        IfNode node = new IfNode(conditionNode, thenPartNode, null);

        // when
        node.executeVoid(frame);

        // then
        InOrder inOrder = inOrder(conditionNode, thenPartNode);
        inOrder.verify(conditionNode, times(1)).executeBoolean(frame);
        inOrder.verify(thenPartNode, never()).executeVoid(frame);
    }

    @Test
    void shouldExecuteElsePartWhenConditionIsFalse() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(false);
        StatementNode elsePartNode = mock(StatementNode.class);

        IfNode node = new IfNode(conditionNode, thenPartNode, elsePartNode);

        // when
        node.executeVoid(frame);

        // then
        InOrder inOrder = inOrder(conditionNode, thenPartNode, elsePartNode);
        inOrder.verify(conditionNode, times(1)).executeBoolean(frame);
        inOrder.verify(thenPartNode, never()).executeVoid(frame);
        inOrder.verify(elsePartNode, times(1)).executeVoid(frame);
    }

    @Test
    void shouldThrowExceptionWhenConditionNodeThrowsException() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenThrow(new UnexpectedResultException("test"));

        IfNode node = new IfNode(conditionNode, null, null);

        // when
        TuberException exception = catchThrowableOfType(() -> node.executeVoid(frame), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"if\" not defined for String \"test\".");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
