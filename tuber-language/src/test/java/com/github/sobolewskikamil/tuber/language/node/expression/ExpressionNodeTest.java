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
package com.github.sobolewskikamil.tuber.language.node.expression;

import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpressionNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private ExpressionNode node;
    @Mock
    private VirtualFrame frame;

    @Test
    void shouldDelegateCallsToExecuteGeneric() {
        // when
        node.executeVoid(frame);

        // then
        verify(node, times(1)).executeGeneric(frame);
    }

    @Test
    void shouldCreateWrapper() {
        // given
        ProbeNode probeNode = mock(ProbeNode.class);

        // when
        InstrumentableNode.WrapperNode wrapper = node.createWrapper(probeNode);

        // then
        assertThat(wrapper).isExactlyInstanceOf(ExpressionNodeWrapper.class);
        assertThat(wrapper.getDelegateNode()).isSameAs(node);
        assertThat(wrapper.getProbeNode()).isSameAs(probeNode);
    }

    @Test
    void shouldExecuteLong() throws UnexpectedResultException {
        // given
        long expected = 1L;
        when(node.executeGeneric(frame)).thenReturn(expected);

        // when
        long result = node.executeLong(frame);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenResultIsNotLong() {
        // given
        when(node.executeGeneric(frame)).thenReturn("test");

        // when / then
        assertThatThrownBy(() -> node.executeLong(frame))
                .isExactlyInstanceOf(UnexpectedResultException.class);
    }

    @Test
    void shouldExecuteDouble() throws UnexpectedResultException {
        // given
        double expected = 1.0;
        when(node.executeGeneric(frame)).thenReturn(expected);

        // when
        double result = node.executeDouble(frame);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenResultIsNotDouble() {
        // given
        when(node.executeGeneric(frame)).thenReturn("test");

        // when / then
        assertThatThrownBy(() -> node.executeDouble(frame))
                .isExactlyInstanceOf(UnexpectedResultException.class);
    }

    @Test
    void shouldExecuteBoolean() throws UnexpectedResultException {
        // given
        boolean expected = true;
        when(node.executeGeneric(frame)).thenReturn(expected);

        // when
        boolean result = node.executeBoolean(frame);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenResultIsNotBoolean() {
        // given
        when(node.executeGeneric(frame)).thenReturn("test");

        // when / then
        assertThatThrownBy(() -> node.executeBoolean(frame))
                .isExactlyInstanceOf(UnexpectedResultException.class);
    }

    @Test
    void shouldExecuteString() throws UnexpectedResultException {
        // given
        String expected = "test";
        when(node.executeGeneric(frame)).thenReturn(expected);

        // when
        String result = node.executeString(frame);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenResultIsNotString() {
        // given
        when(node.executeGeneric(frame)).thenReturn(1);

        // when / then
        assertThatThrownBy(() -> node.executeString(frame))
                .isExactlyInstanceOf(UnexpectedResultException.class);
    }

    @Test
    void shouldExecuteNullType() throws UnexpectedResultException {
        // given
        NullType expected = NullType.getInstance();
        when(node.executeGeneric(frame)).thenReturn(expected);

        // when
        NullType result = node.executeNullType(frame);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenResultIsNotNullType() {
        // given
        when(node.executeGeneric(frame)).thenReturn("test");

        // when / then
        assertThatThrownBy(() -> node.executeNullType(frame))
                .isExactlyInstanceOf(UnexpectedResultException.class);
    }

    @Test
    void shouldExecuteArrayType() throws UnexpectedResultException {
        // given
        ArrayType expected = new ArrayType();
        when(node.executeGeneric(frame)).thenReturn(expected);

        // when
        ArrayType result = node.executeArrayType(frame);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenResultIsNotArrayType() {
        // given
        when(node.executeGeneric(frame)).thenReturn("test");

        // when / then
        assertThatThrownBy(() -> node.executeArrayType(frame))
                .isExactlyInstanceOf(UnexpectedResultException.class);
    }
}
