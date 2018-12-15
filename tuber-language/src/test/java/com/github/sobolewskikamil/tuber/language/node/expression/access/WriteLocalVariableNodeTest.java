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
class WriteLocalVariableNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private WriteLocalVariableNode node;
    @Mock
    private FrameSlot slot;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VirtualFrame frame;

    @BeforeEach
    void setUp() {
        when(node.getSlot()).thenReturn(slot);
    }

    @Test
    void shouldWriteLong() {
        // when
        long result = node.writeLong(frame, 1L);

        // then
        assertThat(result).isEqualTo(1L);
        verify(frame.getFrameDescriptor()).setFrameSlotKind(slot, FrameSlotKind.Long);
        verify(frame).setLong(slot, 1L);
    }

    @Test
    void shouldWriteDouble() {
        // when
        double result = node.writeDouble(frame, 1.0);

        // then
        assertThat(result).isEqualTo(1.0);
        verify(frame.getFrameDescriptor()).setFrameSlotKind(slot, FrameSlotKind.Double);
        verify(frame).setDouble(slot, 1.0);
    }

    @Test
    void shouldWriteBoolean() {
        // when
        boolean result = node.writeBoolean(frame, true);

        // then
        assertThat(result).isEqualTo(true);
        verify(frame.getFrameDescriptor()).setFrameSlotKind(slot, FrameSlotKind.Boolean);
        verify(frame).setBoolean(slot, true);
    }

    @Test
    void shouldWriteObject() {
        // when
        Object result = node.write(frame, "test");

        // then
        assertThat(result).isEqualTo("test");
        verify(frame.getFrameDescriptor()).setFrameSlotKind(slot, FrameSlotKind.Object);
        verify(frame).setObject(slot, "test");
    }

    @Test
    void shouldReturnTrueForLongWhenCheckingIfIsLongOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Long);

        // when
        boolean result = node.isLongOrIllegal(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForIllegalWhenCheckingIfIsLongOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Illegal);

        // when
        boolean result = node.isLongOrIllegal(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForBooleanWhenCheckingIfIsLongOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Boolean);

        // when
        boolean result = node.isLongOrIllegal(frame);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForDoubleWhenCheckingIfIsDoubleOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Double);

        // when
        boolean result = node.isDoubleOrIllegal(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForIllegalWhenCheckingIfIsDoubleOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Illegal);

        // when
        boolean result = node.isDoubleOrIllegal(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForBooleanWhenCheckingIfIsDoubleOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Boolean);

        // when
        boolean result = node.isDoubleOrIllegal(frame);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForBooleanWhenCheckingIfIsBooleanOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Boolean);

        // when
        boolean result = node.isBooleanOrIllegal(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForIllegalWhenCheckingIfIsBooleanOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Illegal);

        // when
        boolean result = node.isBooleanOrIllegal(frame);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForDoubleWhenCheckingIfIsBooleanOrIllegal() {
        // given
        when(frame.getFrameDescriptor().getFrameSlotKind(slot)).thenReturn(FrameSlotKind.Double);

        // when
        boolean result = node.isBooleanOrIllegal(frame);

        // then
        assertThat(result).isFalse();
    }
}
