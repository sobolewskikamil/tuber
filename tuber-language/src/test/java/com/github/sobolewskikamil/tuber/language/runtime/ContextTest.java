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
package com.github.sobolewskikamil.tuber.language.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContextTest {
    @Test
    void shouldProperlyInitializeContext() {
        // given
        TruffleLanguage.Env env = mock(TruffleLanguage.Env.class);
        InputStream in = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        when(env.in()).thenReturn(in);
        when(env.out()).thenReturn(out);

        // when
        Context context = new Context(null, env);

        // then
        assertThat(context.getInput()).isEqualToComparingFieldByFieldRecursively(new BufferedReader(new InputStreamReader(in)));
        assertThat(context.getOutput()).isEqualToComparingFieldByFieldRecursively(new PrintWriter(out, true));
        assertThat(context.getFunctionRegistry().lookup("println")).isPresent();
        assertThat(context.getFunctionRegistry().lookup("readln")).isPresent();
        assertThat(context.getFunctionRegistry().lookup("array")).isPresent();
        assertThat(context.getFunctionRegistry().lookup("length")).isPresent();
        assertThat(context.getFunctionRegistry().lookup("currentTimeMillis")).isPresent();
    }
}
