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
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrNodeTest {
    private OrNode node;
    @Mock
    private ExpressionNode leftNode;
    @Mock
    private ExpressionNode rightNode;
    @Mock
    private VirtualFrame frame;

    @BeforeEach
    void setUp() {
        node = new OrNode(leftNode, rightNode);
    }

    @Test
    void shouldEvaluateRightWhenLeftIsFalse() {
        // when
        boolean result = node.shouldEvaluateRight(false);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotEvaluateRightWhenLeftIsTrue() {
        // when
        boolean result = node.shouldEvaluateRight(true);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @MethodSource("orValues")
    void shouldExecuteOrOperation(boolean left, boolean right, boolean expected) {
        // then
        assertThat(node.execute(left, right)).isEqualTo(expected);
    }

    private static Stream<Arguments> orValues() {
        return Stream.of(
                Arguments.of(true, true, true),
                Arguments.of(true, false, true),
                Arguments.of(false, true, true),
                Arguments.of(false, false, false)
        );
    }

    @Test
    void shouldReturnTrueIfLeftNodeReturnsTrue() throws UnexpectedResultException {
        // given
        when(leftNode.executeBoolean(frame)).thenReturn(true);

        // when
        Object result = node.executeGeneric(frame);

        // then
        assertThat(result).isEqualTo(true);
        verify(leftNode, times(1)).executeBoolean(frame);
        verify(rightNode, never()).executeBoolean(frame);
    }

    @Test
    void shouldOrConditionsWhenLeftIsFalse() throws UnexpectedResultException {
        // given
        when(leftNode.executeBoolean(frame)).thenReturn(false);
        when(rightNode.executeBoolean(frame)).thenReturn(true);

        // when
        Object result = node.executeGeneric(frame);

        // then
        assertThat(result).isEqualTo(true);
        verify(leftNode, times(1)).executeBoolean(frame);
        verify(rightNode, times(1)).executeBoolean(frame);
    }

    @Test
    void shouldThrowExceptionIfLeftNodeThrowsException() throws UnexpectedResultException {
        // given
        when(leftNode.executeBoolean(frame)).thenThrow(new UnexpectedResultException("test"));

        // when
        TuberException exception = catchThrowableOfType(() -> node.executeGeneric(frame), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"||\" not defined for String \"test\", ANY.");
        assertThat(exception.getLocation()).isSameAs(node);
    }

    @Test
    void shouldThrowExceptionIfRightNodeThrowsException() throws UnexpectedResultException {
        // given
        when(leftNode.executeBoolean(frame)).thenReturn(false);
        when(rightNode.executeBoolean(frame)).thenThrow(new UnexpectedResultException("test"));

        // when
        TuberException exception = catchThrowableOfType(() -> node.executeGeneric(frame), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"||\" not defined for Boolean false, String \"test\".");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
