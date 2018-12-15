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

import com.github.sobolewskikamil.tuber.language.Language;
import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import com.github.sobolewskikamil.tuber.language.runtime.FunctionRegistry;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FunctionNodeTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Language language;
    @Mock
    private VirtualFrame frame;

    @Test
    void shouldReturnCallTargetForGivenName() {
        // given
        FunctionRegistry functionRegistry = new FunctionRegistry();
        RootCallTarget rootCallTarget = mock(RootCallTarget.class);
        functionRegistry.register("test", rootCallTarget);

        when(language.getContextReference().get().getFunctionRegistry()).thenReturn(functionRegistry);

        FunctionNode node = new FunctionNode(language, "test");

        // when
        Object result = node.executeGeneric(frame);

        // then
        assertThat(result).isEqualTo(rootCallTarget);
    }

    @Test
    void shouldThrowExceptionWhenCallTargetForGivenNameNotExists() {
        // given
        when(language.getContextReference().get().getFunctionRegistry()).thenReturn(new FunctionRegistry());

        FunctionNode node = new FunctionNode(language, "notExisting");

        // when
        TuberException exception = catchThrowableOfType(() -> node.executeGeneric(frame), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation not defined.");
        assertThat(exception.getLocation()).isEqualTo(node);
    }
}
