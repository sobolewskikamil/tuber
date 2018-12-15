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
package com.github.sobolewskikamil.tuber.language.parser;

import com.google.common.collect.ImmutableMap;
import com.oracle.truffle.api.frame.FrameSlot;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class LexicalScopeTest {
    @Test
    void shouldCreateLexicalScopeWithLocals() {
        // given
        FrameSlot frameSlot = mock(FrameSlot.class);
        Map<String, FrameSlot> locals = ImmutableMap.of(
                "test", frameSlot
        );

        // when
        LexicalScope lexicalScope = new LexicalScope(locals);

        // then
        assertThat(lexicalScope.getLocals()).isEqualTo(locals);
    }

    @Test
    void shouldAddLocalToExistingLexicalScope() {
        // given
        LexicalScope lexicalScope = new LexicalScope(new HashMap<>());
        FrameSlot frameSlot = mock(FrameSlot.class);

        // when
        lexicalScope.addLocal("test", frameSlot);

        // then
        assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of("test", frameSlot));
    }
}
