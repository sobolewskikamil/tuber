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
package com.github.sobolewskikamil.tuber.language.node.expression.builtin;

import com.github.sobolewskikamil.tuber.language.Language;
import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import com.github.sobolewskikamil.tuber.language.node.RootNode;
import com.github.sobolewskikamil.tuber.language.node.expression.arithmetic.AddNode;
import com.github.sobolewskikamil.tuber.language.runtime.Context;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked assignment")
class BuiltinNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BuiltinNode node;

    @Test
    void shouldDelegateExecutionCallsToExecuteMethod() {
        // given
        VirtualFrame frame = mock(VirtualFrame.class);

        // when
        node.executeGeneric(frame);

        // then
        verify(node, times(1)).execute(frame);
    }

    @Test
    void shouldThrowExceptionWhenExecuteThrowsException() {
        // given
        VirtualFrame frame = mock(VirtualFrame.class);
        Node node = mock(AddNode.class);
        when(this.node.execute(frame)).thenThrow(new UnsupportedSpecializationException(node, new Node[]{}));

        // when
        TuberException exception = catchThrowableOfType(() -> this.node.executeGeneric(frame), TuberException.class);

        // then
        verify(this.node, times(1)).execute(frame);
        assertThat(exception).hasMessage("Error: operation \"+\" not defined.");
        assertThat(exception.getLocation()).isSameAs(node);
    }

    @Test
    void shouldReturnContext() {
        // given
        RootNode rootNode = mock(RootNode.class);
        Language language = mock(Language.class);
        when(rootNode.getLanguage(Language.class)).thenReturn(language);

        TruffleLanguage.ContextReference<Context> contextReference = mock(TruffleLanguage.ContextReference.class);
        when(language.getContextReference()).thenReturn(contextReference);

        Context context = mock(Context.class);
        when(contextReference.get()).thenReturn(context);

        when(node.getRootNode()).thenReturn(rootNode);
        InOrder inOrder = inOrder(rootNode, language, contextReference);

        // when
        Context result = node.getContext();

        // then
        assertThat(result).isSameAs(context);
        inOrder.verify(rootNode).getLanguage(Language.class);
        inOrder.verify(language).getContextReference();
        inOrder.verify(contextReference).get();
    }
}
