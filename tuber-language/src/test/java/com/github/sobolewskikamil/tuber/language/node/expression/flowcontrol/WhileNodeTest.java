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
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhileNodeTest {
    private WhileNode node;
    @Mock
    private ExpressionNode conditionNode;
    @Mock
    private StatementNode bodyNode;
    @Mock
    private VirtualFrame frame;

    @BeforeEach
    void setUp() {
        node = new WhileNode(conditionNode, bodyNode);
    }

    @Test
    void shouldEvaluateLoopGivenTimes() throws UnexpectedResultException {
        // given
        AtomicInteger counter = new AtomicInteger(5);
        doAnswer(arg -> counter.getAndDecrement() != 0).when(conditionNode).executeBoolean(frame);
        doNothing().when(bodyNode).executeVoid(frame);

        // when
        node.executeVoid(frame);

        // then
        verify(bodyNode, times(5)).executeVoid(frame);
    }

    @Test
    void shouldEvaluateLoopGivenTimesWithContinueException() throws UnexpectedResultException {
        // given
        AtomicInteger counter = new AtomicInteger(5);
        doAnswer(arg -> counter.getAndDecrement() != 0).when(conditionNode).executeBoolean(frame);
        doAnswer(inv -> {
            if (counter.get() == 3) {
                throw ContinueException.getInstance();
            }
            return null;
        }).when(bodyNode).executeVoid(frame);

        // when
        node.executeVoid(frame);

        // then
        verify(bodyNode, times(5)).executeVoid(frame);
    }

    @Test
    void shouldEvaluateLoopGivenTimesWithBreakException() throws UnexpectedResultException {
        // given
        AtomicInteger counter = new AtomicInteger(5);
        doAnswer(arg -> counter.getAndDecrement() != 0).when(conditionNode).executeBoolean(frame);
        doAnswer(inv -> {
            if (counter.get() == 3) {
                throw BreakException.getInstance();
            }
            return null;
        }).when(bodyNode).executeVoid(frame);

        // when
        node.executeVoid(frame);

        // then
        verify(bodyNode, times(2)).executeVoid(frame);
    }
}
