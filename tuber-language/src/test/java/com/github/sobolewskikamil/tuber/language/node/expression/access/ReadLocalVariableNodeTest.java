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
package com.github.sobolewskikamil.tuber.language.node.expression.access;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadLocalVariableNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private ReadLocalVariableNode node;
    @Mock
    private FrameSlot slot;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VirtualFrame frame;

    @BeforeEach
    void setUp() {
        when(node.getSlot()).thenReturn(slot);
    }

    @Test
    void shouldReadLong() throws FrameSlotTypeException {
        // given
        when(frame.getLong(slot)).thenReturn(1L);

        // when
        long result = node.readLong(frame);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldReadDouble() throws FrameSlotTypeException {
        // given
        when(frame.getDouble(slot)).thenReturn(1.0);

        // when
        double result = node.readDouble(frame);

        // then
        assertThat(result).isEqualTo(1.0);
    }

    @Test
    void shouldReadBoolean() throws FrameSlotTypeException {
        // given
        when(frame.getBoolean(slot)).thenReturn(true);

        // when
        boolean result = node.readBoolean(frame);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    void shouldReadObjectIfIsObject() throws FrameSlotTypeException {
        // given
        when(frame.isObject(slot)).thenReturn(true);
        when(frame.getObject(slot)).thenReturn("test");

        // when
        Object result = node.readObject(frame);

        // then
        assertThat(result).isEqualTo("test");
    }

    @Test
    void shouldReadObjectIfIsNotObject() {
        // given
        when(frame.isObject(slot)).thenReturn(false);
        when(frame.getValue(slot)).thenReturn("test");

        // when
        Object result = node.readObject(frame);

        // then
        assertThat(result).isEqualTo("test");
        verify(frame).setObject(slot, "test");
    }

    @Test
    void shouldReturnTrueForLongWhenCheckingIfIsLong() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Long);

        // when
        boolean result = node.isLong(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotLongWhenCheckingIfIsLong() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Boolean);

        // when
        boolean result = node.isLong(frame);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForDoubleWhenCheckingIfIsDouble() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Double);

        // when
        boolean result = node.isDouble(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotDoubleWhenCheckingIfIsDouble() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Boolean);

        // when
        boolean result = node.isDouble(frame);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForBooleanWhenCheckingIfIsBoolean() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Boolean);

        // when
        boolean result = node.isBoolean(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotBooleanWhenCheckingIfIsBoolean() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Long);

        // when
        boolean result = node.isDouble(frame);

        // then
        assertThat(result).isFalse();
    }
}
