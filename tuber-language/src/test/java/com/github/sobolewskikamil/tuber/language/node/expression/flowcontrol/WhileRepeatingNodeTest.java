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

import com.github.sobolewskikamil.tuber.language.node.StatementNode;
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.github.sobolewskikamil.tuber.language.node.expression.flowcontrol.exception.BreakException;
import com.github.sobolewskikamil.tuber.language.node.expression.flowcontrol.exception.ContinueException;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhileRepeatingNodeTest {
    private WhileRepeatingNode node;
    @Mock
    private ExpressionNode conditionNode;
    @Mock
    private StatementNode bodyNode;
    @Mock
    private VirtualFrame frame;

    @BeforeEach
    void setUp() {
        node = new WhileRepeatingNode(conditionNode, bodyNode);
    }

    @Test
    void shouldReturnFalseWhenConditionIsFalse() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(false);

        // when
        boolean result = node.executeRepeating(frame);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenAfterExecutingBodyNode() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(true);
        doNothing().when(bodyNode).executeVoid(frame);

        // when
        boolean result = node.executeRepeating(frame);

        // then
        assertThat(result).isTrue();
        verify(bodyNode, times(1)).executeVoid(frame);
    }

    @Test
    void shouldReturnFalseIfBodyNodeThrowsBreakException() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(true);
        doThrow(BreakException.getInstance()).when(bodyNode).executeVoid(frame);

        // when
        boolean result = node.executeRepeating(frame);

        // then
        assertThat(result).isFalse();
        verify(bodyNode, times(1)).executeVoid(frame);
    }

    @Test
    void shouldReturnTrueIfBodyNodeThrowsContinueException() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenReturn(true);
        doThrow(ContinueException.getInstance()).when(bodyNode).executeVoid(frame);

        // when
        boolean result = node.executeRepeating(frame);

        // then
        assertThat(result).isTrue();
        verify(bodyNode, times(1)).executeVoid(frame);
    }

    @Test
    void shouldThrowExceptionIfConditionNodeThrowsException() throws UnexpectedResultException {
        // given
        when(conditionNode.executeBoolean(frame)).thenThrow(new UnexpectedResultException("test"));

        // when
        UnsupportedSpecializationException exception = catchThrowableOfType(() -> node.executeRepeating(frame), UnsupportedSpecializationException.class);

        // then
        assertThat(exception.getNode()).isSameAs(node);
        assertThat(exception.getSuppliedNodes()).containsExactly(conditionNode);
        assertThat(exception.getSuppliedValues()).containsExactly("test");
    }
}
