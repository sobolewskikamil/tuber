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

import com.github.sobolewskikamil.tuber.language.Language;
import com.github.sobolewskikamil.tuber.language.grammar.TuberLexer;
import com.github.sobolewskikamil.tuber.language.grammar.TuberParser;
import com.oracle.truffle.api.RootCallTarget;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Map;

public class TuberParserFacade implements ParserFacade {
    @Override
    public Map<String, RootCallTarget> parseLanguage(Language language, String source) {
        TuberLexer lexer = new TuberLexer(CharStreams.fromString(source));
        TuberParser parser = new TuberParser(new CommonTokenStream(lexer));
        TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(language);
        TuberParser.ParseContext parse = parser.parse();
        visitor.visit(parse);
        return visitor.getFunctions();
    }
}
