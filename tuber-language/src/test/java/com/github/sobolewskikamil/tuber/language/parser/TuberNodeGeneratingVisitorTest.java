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
import com.github.sobolewskikamil.tuber.language.grammar.TuberParser;
import com.github.sobolewskikamil.tuber.language.node.StatementNode;
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.github.sobolewskikamil.tuber.language.node.expression.access.*;
import com.github.sobolewskikamil.tuber.language.node.expression.arithmetic.*;
import com.github.sobolewskikamil.tuber.language.node.expression.call.CallNode;
import com.github.sobolewskikamil.tuber.language.node.expression.flowcontrol.*;
import com.github.sobolewskikamil.tuber.language.node.expression.literal.*;
import com.github.sobolewskikamil.tuber.language.node.expression.logical.*;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.github.sobolewskikamil.tuber.language.parser.utils.TuberParserUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class TuberNodeGeneratingVisitorTest {
    private TuberNodeGeneratingVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new TuberNodeGeneratingVisitor(new Language());
    }

    @Nested
    @DisplayName("Literal")
    class Literal {
        @Test
        void shouldGenerateLongLiteralNode() {
            // given
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("1").literal();

            // when
            StatementNode node = visitor.visitLiteral(context);

            // then
            LongLiteralNode expected = new LongLiteralNode(1);
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateDoubleLiteralNode() {
            // given
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("1.0").literal();

            // when
            StatementNode node = visitor.visitLiteral(context);

            // then
            DoubleLiteralNode expected = new DoubleLiteralNode(1.0);
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateBooleanLiteralNode() {
            // given
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("true").literal();

            // when
            StatementNode node = visitor.visitLiteral(context);

            // then
            BooleanLiteralNode expected = new BooleanLiteralNode(true);
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateNullLiteralNode() {
            // given
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("null").literal();

            // when
            StatementNode node = visitor.visitLiteral(context);

            // then
            NullLiteralNode expected = new NullLiteralNode(NullType.getInstance());
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateReadLocalVariableNodeForIdentifier() {
            // given
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "a", new FrameDescriptor().addFrameSlot("a")
            );
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("a").literal();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(locals), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitLiteral(context);

            // then
            ReadLocalVariableNode expected = ReadLocalVariableNodeGen.create(locals.get("a"));
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateStringLiteralNodeForString() {
            // given
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("\"a\"").literal();

            // when
            StatementNode node = visitor.visitLiteral(context);

            // then
            StringLiteralNode expected = new StringLiteralNode("a");
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldThrowExceptionForUnknownLiteral() {
            // given
            TuberParser.LiteralContext context = TuberParserUtils.createParserForSource("-").literal();

            // when / then
            assertThatThrownBy(() -> visitor.visitLiteral(context))
                    .isExactlyInstanceOf(IllegalStateException.class)
                    .hasMessage("Unknown literal: -");
        }
    }

    @Nested
    @DisplayName("Expression")
    class Expression {
        @Test
        void shouldThrowExceptionWhenInvalidExpression() {
            // given
            TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("~").expression();

            // when / then
            assertThatThrownBy(() -> visitor.visitExpression(context))
                    .isExactlyInstanceOf(IllegalStateException.class)
                    .hasMessage("Unknown expression: ");
        }

        @Test
        void shouldGenerateFunctionCallNode() {
            // given
            Language language = mock(Language.class);
            when(language.getContextReference()).thenReturn(mock(TruffleLanguage.ContextReference.class));
            TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("fun()").expression();

            // when
            StatementNode node = new TuberNodeGeneratingVisitor(language).visitExpression(context);

            // then
            CallNode expected = new CallNode(new FunctionNode(language, "fun"), new ExpressionNode[]{});
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateReadArrayElementNode() {
            // given
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "a", new FrameDescriptor().addFrameSlot("a")
            );
            TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("a[0]").expression();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(locals), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitExpression(context);

            // then
            ReadArrayElementNode expected = new ReadArrayElementNode(
                    ReadLocalVariableNodeGen.create(locals.get("a")),
                    new LongLiteralNode(0)
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Nested
        @DisplayName("Arithmetic")
        class Arithmetic {
            @Test
            void shouldGenerateAddNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 + 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                AddNode expected = AddNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateSubNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 - 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                SubNode expected = SubNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateDivNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 / 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                DivNode expected = DivNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateMulNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 * 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                MulNode expected = MulNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateModNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 % 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                ModNode expected = ModNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateExpNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 ^ 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                ExpNode expected = ExpNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }
        }

        @Nested
        @DisplayName("Relational")
        class Relational {
            @Test
            void shouldGenerateEqualNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 == 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                EqualNode expected = EqualNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateNotEqualNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 != 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                NotEqualNode expected = NotEqualNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateGreaterOrEqualNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 >= 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                GreaterOrEqualNode expected = GreaterOrEqualNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateLessOrEqualNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 <= 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                LessOrEqualNode expected = LessOrEqualNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateGreaterNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 > 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                GreaterNode expected = GreaterNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateLessNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 < 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                LessNode expected = LessNodeGen.create(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }
        }

        @Nested
        @DisplayName("Logical")
        class Logical {
            @Test
            void shouldGenerateAndNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 && 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                AndNode expected = new AndNode(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateOrNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("1 || 2").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                OrNode expected = new OrNode(new LongLiteralNode(1), new LongLiteralNode(2));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateNotNode() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("!1").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                NotNode expected = NotNodeGen.create(new LongLiteralNode(1));
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }
        }

        @Nested
        @DisplayName("Complex")
        class Complex {
            @Test
            void shouldGenerateNestedArithmeticExpressionWithParen() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("(1 + 2) * 3").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                MulNode expected = MulNodeGen.create(
                        AddNodeGen.create(
                                new LongLiteralNode(1),
                                new LongLiteralNode(2)
                        ),
                        new LongLiteralNode(3)
                );
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateNestedLogicalExpressionWithParen() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("(1 > 2) && (3 < 4)").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                AndNode expected = new AndNode(
                        GreaterNodeGen.create(
                                new LongLiteralNode(1),
                                new LongLiteralNode(2)
                        ),
                        LessNodeGen.create(
                                new LongLiteralNode(3),
                                new LongLiteralNode(4)
                        )
                );
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }

            @Test
            void shouldGenerateNestedMixedExpressionWithParen() {
                // given
                TuberParser.ExpressionContext context = TuberParserUtils.createParserForSource("!((1 + 2) > (3 / 4)) || (5 && 6)").expression();

                // when
                StatementNode node = visitor.visitExpression(context);

                // then
                OrNode expected = new OrNode(
                        NotNodeGen.create(
                                GreaterNodeGen.create(
                                        AddNodeGen.create(
                                                new LongLiteralNode(1),
                                                new LongLiteralNode(2)
                                        ),
                                        DivNodeGen.create(
                                                new LongLiteralNode(3),
                                                new LongLiteralNode(4)
                                        )
                                )
                        ),
                        new AndNode(
                                new LongLiteralNode(5),
                                new LongLiteralNode(6)
                        )
                );
                assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            }
        }
    }

    @Nested
    @DisplayName("Statement")
    class Statement {
        private LexicalScope lexicalScope;
        private FrameDescriptor frameDescriptor;

        @BeforeEach
        void setUp() {
            lexicalScope = new LexicalScope();
            frameDescriptor = new FrameDescriptor();
            visitor = new TuberNodeGeneratingVisitor(new Language(), () -> lexicalScope, () -> frameDescriptor);
        }

        @Test
        void shouldGenerateAssignmentNode() {
            // given
            TuberParser.StatementContext context = TuberParserUtils.createParserForSource("a = 5;").statement();

            // when
            StatementNode node = visitor.visitStatement(context);

            // then
            WriteLocalVariableNode expected = WriteLocalVariableNodeGen.create(
                    new LongLiteralNode(5L),
                    frameDescriptor.findFrameSlot("a")
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of("a", frameDescriptor.findFrameSlot("a")));
        }

        @Test
        void shouldGenerateCallNode() {
            // given
            Language language = mock(Language.class);
            when(language.getContextReference()).thenReturn(mock(TruffleLanguage.ContextReference.class));
            TuberParser.StatementContext context = TuberParserUtils.createParserForSource("fun()").statement();

            // when
            StatementNode node = new TuberNodeGeneratingVisitor(language).visitStatement(context);

            // then
            CallNode expected = new CallNode(new FunctionNode(language, "fun"), new ExpressionNode[]{});
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateReturnNode() {
            // given
            TuberParser.StatementContext context = TuberParserUtils.createParserForSource("return 1;").statement();

            // when
            StatementNode node = visitor.visitStatement(context);

            // then
            StatementNode expected = new ReturnNode(new LongLiteralNode(1));
            AssertionsForClassTypes.assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateWhileNode() {
            // given
            String source = "" +
                    "while (true){" +
                    "}";
            TuberParser.StatementContext context = TuberParserUtils.createParserForSource(source).statement();

            // when
            StatementNode node = visitor.visitStatement(context);

            // then
            StatementNode expected = new WhileNode(
                    new BooleanLiteralNode(true),
                    new BlockNode(new StatementNode[]{})
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldThrowExceptionWhenUnknownStatement() {
            // given
            TuberParser.StatementContext context = TuberParserUtils.createParserForSource("~").statement();

            // when / then
            assertThatThrownBy(() -> visitor.visitStatement(context))
                    .isExactlyInstanceOf(IllegalStateException.class)
                    .hasMessage("Unknown statement: ");
        }
    }

    @Nested
    @DisplayName("Iteration statement")
    class IterationStatement {
        @Test
        void shouldGenerateStatementNode() {
            // given
            TuberParser.IterationStatementContext context = TuberParserUtils.createParserForSource("return 1;").iterationStatement();

            // when
            StatementNode node = visitor.visitIterationStatement(context);

            // then
            StatementNode expected = new ReturnNode(new LongLiteralNode(1));
            AssertionsForClassTypes.assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateIfIterationStatement() {
            // given
            String source = "" +
                    "if (1 == 2) {" +
                    "   return 1;" +
                    "}";
            TuberParser.IterationStatementContext context = TuberParserUtils.createParserForSource(source).iterationStatement();

            // when
            StatementNode node = visitor.visitIterationStatement(context);

            // then
            StatementNode expected = new IfNode(
                    EqualNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new BlockNode(new StatementNode[]{
                            new ReturnNode(
                                    new LongLiteralNode(1)
                            )
                    }),
                    null
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateBreakNode() {
            // given
            TuberParser.IterationStatementContext context = TuberParserUtils.createParserForSource("break;").iterationStatement();

            // when
            StatementNode node = visitor.visitIterationStatement(context);

            // then
            StatementNode expected = new BreakNode();
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateContinueNode() {
            // given
            TuberParser.IterationStatementContext context = TuberParserUtils.createParserForSource("continue;").iterationStatement();

            // when
            StatementNode node = visitor.visitIterationStatement(context);

            // then
            StatementNode expected = new BreakNode();
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldThrowExceptionWhenUnknownStatement() {
            // given
            TuberParser.IterationStatementContext context = TuberParserUtils.createParserForSource("~").iterationStatement();

            // when / then
            assertThatThrownBy(() -> visitor.visitIterationStatement(context))
                    .isExactlyInstanceOf(IllegalStateException.class)
                    .hasMessage("Unknown iteration statement: ");
        }
    }

    @Nested
    @DisplayName("Non iteration statement")
    class NonIterationStatement {
        @Test
        void shouldGenerateStatementNode() {
            // given
            TuberParser.NonIterationStatementContext context = TuberParserUtils.createParserForSource("return 1;").nonIterationStatement();

            // when
            StatementNode node = visitor.visitNonIterationStatement(context);

            // then
            StatementNode expected = new ReturnNode(new LongLiteralNode(1));
            AssertionsForClassTypes.assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateIfStatement() {
            // given
            String source = "" +
                    "if (1 == 2) {" +
                    "}";
            TuberParser.NonIterationStatementContext context = TuberParserUtils.createParserForSource(source).nonIterationStatement();

            // when
            StatementNode node = visitor.visitNonIterationStatement(context);

            // then
            StatementNode expected = new IfNode(
                    EqualNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new BlockNode(new StatementNode[]{}),
                    null
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldThrowExceptionWhenUnknownStatement() {
            // given
            TuberParser.NonIterationStatementContext context = TuberParserUtils.createParserForSource("~").nonIterationStatement();

            // when / then
            assertThatThrownBy(() -> visitor.visitNonIterationStatement(context))
                    .isExactlyInstanceOf(IllegalStateException.class)
                    .hasMessage("Unknown non iteration statement: ");
        }
    }

    @Nested
    @DisplayName("Array")
    class Array {
        @Test
        void shouldGenerateEmptyArray() {
            // given
            TuberParser.ArrayContext context = TuberParserUtils.createParserForSource("{}").array();

            // when
            StatementNode node = visitor.visitArray(context);

            // then
            ArrayLiteralNode expected = new ArrayLiteralNode();
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateArrayOfLiterals() {
            // given
            TuberParser.ArrayContext context = TuberParserUtils.createParserForSource("{1, true, 1.0, \"test\", null}").array();

            // when
            StatementNode node = visitor.visitArray(context);

            // then
            ArrayLiteralNode expected = new ArrayLiteralNode(
                    new LongLiteralNode(1),
                    new BooleanLiteralNode(true),
                    new DoubleLiteralNode(1.0),
                    new StringLiteralNode("test"),
                    new NullLiteralNode(NullType.getInstance())
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateArrayOfExpressions() {
            // given
            TuberParser.ArrayContext context = TuberParserUtils.createParserForSource("{1 + 2, 1 - 2, 1 * 2}").array();

            // when
            StatementNode node = visitor.visitArray(context);

            // then
            ArrayLiteralNode expected = new ArrayLiteralNode(
                    AddNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    SubNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    MulNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    )
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateNestedArray() {
            // given
            TuberParser.ArrayContext context = TuberParserUtils.createParserForSource("{{1, 2}, {}, {true}}").array();

            // when
            StatementNode node = visitor.visitArray(context);

            // then
            ArrayLiteralNode expected = new ArrayLiteralNode(
                    new ArrayLiteralNode(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new ArrayLiteralNode(),
                    new ArrayLiteralNode(
                            new BooleanLiteralNode(true)
                    )

            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("Array element")
    class ArrayElement {
        @Test
        void shouldReadElementOfArray() {
            // given
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "a", new FrameDescriptor().addFrameSlot("a")
            );
            TuberParser.ArrayElementContext context = TuberParserUtils.createParserForSource("a[0]").arrayElement();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(locals), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitArrayElement(context);

            // then
            ReadArrayElementNode expected = new ReadArrayElementNode(
                    ReadLocalVariableNodeGen.create(locals.get("a")),
                    new LongLiteralNode(0)
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldReadNestedElementOfArray() {
            // given
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "a", new FrameDescriptor().addFrameSlot("a")
            );
            TuberParser.ArrayElementContext context = TuberParserUtils.createParserForSource("a[0][1][2]").arrayElement();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(locals), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitArrayElement(context);

            // then
            ReadArrayElementNode expected = new ReadArrayElementNode(
                    new ReadArrayElementNode(
                            new ReadArrayElementNode(
                                    ReadLocalVariableNodeGen.create(locals.get("a")),
                                    new LongLiteralNode(0)
                            ),
                            new LongLiteralNode(1)
                    ),
                    new LongLiteralNode(2)
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("Assignment")
    class Assignment {
        private LexicalScope lexicalScope;
        private FrameDescriptor frameDescriptor;

        @BeforeEach
        void setUp() {
            lexicalScope = new LexicalScope();
            frameDescriptor = new FrameDescriptor();
            visitor = new TuberNodeGeneratingVisitor(new Language(), () -> lexicalScope, () -> frameDescriptor);
        }

        @Test
        void shouldGenerateArrayAssignment() {
            // given
            lexicalScope.addLocal("a", new FrameDescriptor().addFrameSlot("a"));
            TuberParser.AssignmentContext context = TuberParserUtils.createParserForSource("a[0] = 1;").assignment();

            // when
            StatementNode node = visitor.visitAssignment(context);

            // then
            WriteArrayElementNode expected = new WriteArrayElementNode(
                    ReadLocalVariableNodeGen.create(lexicalScope.getLocals().get("a")),
                    new LongLiteralNode(0),
                    new LongLiteralNode(1)
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateVariableAssignmentNode() {
            // given
            TuberParser.AssignmentContext context = TuberParserUtils.createParserForSource("a = 5;").assignment();

            // when
            StatementNode node = visitor.visitAssignment(context);

            // then
            WriteLocalVariableNode expected = WriteLocalVariableNodeGen.create(
                    new LongLiteralNode(5L),
                    frameDescriptor.findFrameSlot("a")
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of("a", frameDescriptor.findFrameSlot("a")));
        }

        @Test
        void shouldThrowExceptionWhenNotAssignment() {
            // given
            TuberParser.AssignmentContext context = TuberParserUtils.createParserForSource("5").assignment();

            // when / then
            assertThatThrownBy(() -> visitor.visitAssignment(context))
                    .isExactlyInstanceOf(IllegalStateException.class)
                    .hasMessage("Unknown assignment: 5");
        }
    }

    @Nested
    @DisplayName("Array assignment")
    class ArrayAssignment {
        @Test
        void shouldWriteElementToArray() {
            // given
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "a", new FrameDescriptor().addFrameSlot("a")
            );
            TuberParser.ArrayElementAssignmentContext context = TuberParserUtils.createParserForSource("a[0] = 1;").arrayElementAssignment();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(locals), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitArrayElementAssignment(context);

            // then
            WriteArrayElementNode expected = new WriteArrayElementNode(
                    ReadLocalVariableNodeGen.create(locals.get("a")),
                    new LongLiteralNode(0),
                    new LongLiteralNode(1)
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldWriteElementToNestedArray() {
            // given
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "a", new FrameDescriptor().addFrameSlot("a")
            );
            TuberParser.ArrayElementAssignmentContext context = TuberParserUtils.createParserForSource("a[0][1][2] = 1;").arrayElementAssignment();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(locals), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitArrayElementAssignment(context);

            // then
            WriteArrayElementNode expected = new WriteArrayElementNode(
                    new ReadArrayElementNode(
                            new ReadArrayElementNode(
                                    ReadLocalVariableNodeGen.create(locals.get("a")),
                                    new LongLiteralNode(0L)
                            ),
                            new LongLiteralNode(1L)
                    ),
                    new LongLiteralNode(2L),
                    new LongLiteralNode(1L)
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("Variable assignment")
    class VariableAssignment {
        private LexicalScope lexicalScope;
        private FrameDescriptor frameDescriptor;

        @BeforeEach
        void setUp() {
            lexicalScope = new LexicalScope();
            frameDescriptor = new FrameDescriptor();
            visitor = new TuberNodeGeneratingVisitor(new Language(), () -> lexicalScope, () -> frameDescriptor);
        }

        @Test
        void shouldGenerateSimpleAssignmentNode() {
            // given
            TuberParser.VariableAssignmentContext context = TuberParserUtils.createParserForSource("a = 5;").variableAssignment();

            // when
            StatementNode node = visitor.visitVariableAssignment(context);

            // then
            WriteLocalVariableNode expected = WriteLocalVariableNodeGen.create(
                    new LongLiteralNode(5L),
                    frameDescriptor.findFrameSlot("a")
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of("a", frameDescriptor.findFrameSlot("a")));
        }

        @Test
        void shouldGenerateExpressionAssignmentNode() {
            // given
            TuberParser.VariableAssignmentContext context = TuberParserUtils.createParserForSource("a = 1 + 2;").variableAssignment();

            // when
            StatementNode node = visitor.visitVariableAssignment(context);

            // then
            WriteLocalVariableNode expected = WriteLocalVariableNodeGen.create(
                    AddNodeGen.create(
                            new LongLiteralNode(1L),
                            new LongLiteralNode(2L)
                    ),
                    frameDescriptor.findFrameSlot("a")
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of("a", frameDescriptor.findFrameSlot("a")));
        }
    }

    @Nested
    @DisplayName("Function call")
    class FunctionCall {
        @Test
        void shouldGenerateCallNodeWithoutArguments() {
            // given
            Language language = mock(Language.class);
            when(language.getContextReference()).thenReturn(mock(TruffleLanguage.ContextReference.class));
            TuberParser.FunctionCallContext context = TuberParserUtils.createParserForSource("fun()").functionCall();

            // when
            StatementNode node = new TuberNodeGeneratingVisitor(language).visitFunctionCall(context);

            // then
            CallNode expected = new CallNode(new FunctionNode(language, "fun"), new ExpressionNode[]{});
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateCallNodeWithArguments() {
            // given
            Language language = mock(Language.class);
            when(language.getContextReference()).thenReturn(mock(TruffleLanguage.ContextReference.class));
            TuberParser.FunctionCallContext context = TuberParserUtils.createParserForSource("fun(1, \"test\", 1.0 + 2.0)").functionCall();

            // when
            StatementNode node = new TuberNodeGeneratingVisitor(language).visitFunctionCall(context);

            // then
            CallNode expected = new CallNode(new FunctionNode(language, "fun"), new ExpressionNode[]{
                    new LongLiteralNode(1L),
                    new StringLiteralNode("test"),
                    AddNodeGen.create(
                            new DoubleLiteralNode(1.0),
                            new DoubleLiteralNode(2.0)
                    )
            });
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("If iteration")
    class IfIteration {
        @Test
        void shouldGenerateEmptyIfIterationNode() {
            // given
            String source = "" +
                    "if (1 == 2) {" +
                    "}";
            TuberParser.IfStatementContext context = TuberParserUtils.createParserForSource(source).ifStatement();

            // when
            StatementNode node = visitor.visitIfStatement(context);

            // then
            StatementNode expected = new IfNode(
                    EqualNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new BlockNode(new StatementNode[]{}),
                    null
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateEmptyIfIterationNodeWithElse() {
            // given
            String source = "" +
                    "if (1 == 2) {" +
                    "} else {" +
                    "}";
            TuberParser.IfStatementContext context = TuberParserUtils.createParserForSource(source).ifStatement();

            // when
            StatementNode node = visitor.visitIfStatement(context);

            // then
            StatementNode expected = new IfNode(
                    EqualNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new BlockNode(new StatementNode[]{}),
                    new BlockNode(new StatementNode[]{})
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateIfIterationNodeWithElse() {
            // given
            String source = "" +
                    "if (1 == 2) {" +
                    "   x = 3 + 4;" +
                    "} else {" +
                    "   y = 5 - 6;" +
                    "}";
            FrameDescriptor frameDescriptor = new FrameDescriptor();
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "x", frameDescriptor.addFrameSlot("x"),
                    "y", frameDescriptor.addFrameSlot("y")
            );
            TuberParser.IfStatementContext context = TuberParserUtils.createParserForSource(source).ifStatement();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(Maps.newHashMap(locals)), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitIfStatement(context);

            // then
            StatementNode expected = new IfNode(
                    EqualNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new BlockNode(new StatementNode[]{
                            WriteLocalVariableNodeGen.create(
                                    AddNodeGen.create(
                                            new LongLiteralNode(3),
                                            new LongLiteralNode(4)
                                    ),
                                    locals.get("x"))
                    }),
                    new BlockNode(new StatementNode[]{
                            WriteLocalVariableNodeGen.create(
                                    SubNodeGen.create(
                                            new LongLiteralNode(5),
                                            new LongLiteralNode(6)
                                    ),
                                    locals.get("y"))
                    })
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateIfIterationNodeWithNestedIfNode() {
            // given
            String source = "" +
                    "if (1 == 2) {" +
                    "   if (3 == 4) {" +
                    "       x = 5 + 6;" +
                    "   } else {" +
                    "       y = 7 - 8;" +
                    "   }" +
                    "}";
            FrameDescriptor frameDescriptor = new FrameDescriptor();
            Map<String, FrameSlot> locals = ImmutableMap.of(
                    "x", frameDescriptor.addFrameSlot("x"),
                    "y", frameDescriptor.addFrameSlot("y")
            );
            TuberParser.IfStatementContext context = TuberParserUtils.createParserForSource(source).ifStatement();
            TuberNodeGeneratingVisitor visitor = new TuberNodeGeneratingVisitor(new Language(), () -> new LexicalScope(Maps.newHashMap(locals)), FrameDescriptor::new);

            // when
            StatementNode node = visitor.visitIfStatement(context);

            // then
            StatementNode expected = new IfNode(
                    EqualNodeGen.create(
                            new LongLiteralNode(1),
                            new LongLiteralNode(2)
                    ),
                    new BlockNode(new StatementNode[]{
                            new IfNode(
                                    EqualNodeGen.create(
                                            new LongLiteralNode(3),
                                            new LongLiteralNode(4)
                                    ),
                                    new BlockNode(new StatementNode[]{
                                            WriteLocalVariableNodeGen.create(
                                                    AddNodeGen.create(
                                                            new LongLiteralNode(5),
                                                            new LongLiteralNode(6)
                                                    ),
                                                    locals.get("x"))
                                    }),
                                    new BlockNode(new StatementNode[]{
                                            WriteLocalVariableNodeGen.create(
                                                    SubNodeGen.create(
                                                            new LongLiteralNode(7),
                                                            new LongLiteralNode(8)
                                                    ),
                                                    locals.get("y"))
                                    })
                            )
                    }),
                    null
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("Return")
    class ReturnStatement {
        @Test
        void shouldGenerateReturnNode() {
            // given
            TuberParser.ReturnStatementContext context = TuberParserUtils.createParserForSource("return 1;").returnStatement();

            // when
            StatementNode node = visitor.visitReturnStatement(context);

            // then
            StatementNode expected = new ReturnNode(new LongLiteralNode(1));
            AssertionsForClassTypes.assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("While stat")
    class WhileState {
        @Test
        void shouldGenerateConditionNode() {
            // given
            TuberParser.WhileStatContext context = TuberParserUtils.createParserForSource("while(true)").whileStat();

            // when
            StatementNode node = visitor.visitWhileStat(context);

            // then
            BooleanLiteralNode expected = new BooleanLiteralNode(true);
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("Iter stat")
    class IterStat {
        @Test
        void shouldGenerateBlockNodeWithNoIterationStatements() {
            // given
            TuberParser.IterStatContext context = TuberParserUtils.createParserForSource("{}").iterStat();

            // when
            StatementNode node = visitor.visitIterStat(context);

            // then
            BlockNode expected = new BlockNode(new StatementNode[]{});
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateBlockNodeWithIterationStatements() {
            // given
            String source = "" +
                    "{" +
                    "   break;" +
                    "   return 5;" +
                    "}";
            TuberParser.IterStatContext context = TuberParserUtils.createParserForSource(source).iterStat();

            // when
            StatementNode node = visitor.visitIterStat(context);

            // then
            BlockNode expected = new BlockNode(new StatementNode[]{
                    new BreakNode(),
                    new ReturnNode(new LongLiteralNode(5L))
            });
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("While")
    class While {
        @Test
        void shouldGenerateEmptyWhileLoop() {
            // given
            String source = "" +
                    "while (true){" +
                    "}";
            TuberParser.WhileStatementContext context = TuberParserUtils.createParserForSource(source).whileStatement();

            // when
            StatementNode node = visitor.visitWhileStatement(context);

            // then
            StatementNode expected = new WhileNode(
                    new BooleanLiteralNode(true),
                    new BlockNode(new StatementNode[]{})
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateWhileLoopWithBreak() {
            // given
            String source = "" +
                    "while (true){" +
                    "   break;" +
                    "}";
            TuberParser.WhileStatementContext context = TuberParserUtils.createParserForSource(source).whileStatement();

            // when
            StatementNode node = visitor.visitWhileStatement(context);

            // then
            StatementNode expected = new WhileNode(
                    new BooleanLiteralNode(true),
                    new BlockNode(new StatementNode[]{
                            new BreakNode()
                    })
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateWhileLoopWithContinue() {
            // given
            String source = "" +
                    "while(true){" +
                    "   continue;" +
                    "}";
            TuberParser.WhileStatementContext context = TuberParserUtils.createParserForSource(source).whileStatement();

            // when
            StatementNode node = visitor.visitWhileStatement(context);

            // then
            StatementNode expected = new WhileNode(
                    new BooleanLiteralNode(true),
                    new BlockNode(new StatementNode[]{
                            new ContinueNode()
                    })
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }

        @Test
        void shouldGenerateWhileLoopWithStatements() {
            // given
            String source = "" +
                    "while (true) {" +
                    "   if (1 == 2) {" +
                    "       break;" +
                    "   } else {" +
                    "       continue;" +
                    "   }" +
                    "}";
            TuberParser.WhileStatementContext context = TuberParserUtils.createParserForSource(source).whileStatement();

            // when
            StatementNode node = visitor.visitWhileStatement(context);

            // then
            StatementNode expected = new WhileNode(
                    new BooleanLiteralNode(true),
                    new BlockNode(new StatementNode[]{
                            new IfNode(
                                    EqualNodeGen.create(
                                            new LongLiteralNode(1),
                                            new LongLiteralNode(2)
                                    ),
                                    new BlockNode(new StatementNode[]{
                                            new BreakNode()
                                    }),
                                    new BlockNode(new StatementNode[]{
                                            new ContinueNode()
                                    })
                            )
                    })
            );
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
        }
    }

    @Nested
    @DisplayName("Id list")
    class IdList {
        private LexicalScope lexicalScope;
        private FrameDescriptor frameDescriptor;

        @BeforeEach
        void setUp() {
            lexicalScope = new LexicalScope();
            frameDescriptor = new FrameDescriptor();
            visitor = new TuberNodeGeneratingVisitor(null, () -> lexicalScope, () -> frameDescriptor);
        }

        @Test
        void shouldGenerateSingleId() {
            // given
            TuberParser.IdListContext context = TuberParserUtils.createParserForSource("a").idList();

            // when
            StatementNode node = visitor.visitIdList(context);

            // then
            BlockNode expected = new BlockNode(new StatementNode[]{
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(0),
                            frameDescriptor.findFrameSlot("a")
                    )
            });
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of(
                    "a", frameDescriptor.findFrameSlot("a")
            ));
        }

        @Test
        void shouldGenerateMultipleIds() {
            // given
            TuberParser.IdListContext context = TuberParserUtils.createParserForSource("a, b").idList();

            // when
            StatementNode node = visitor.visitIdList(context);

            // then
            BlockNode expected = new BlockNode(new StatementNode[]{
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(0),
                            frameDescriptor.findFrameSlot("a")
                    ),
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(1),
                            frameDescriptor.findFrameSlot("b")
                    )
            });
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of(
                    "a", frameDescriptor.findFrameSlot("a"),
                    "b", frameDescriptor.findFrameSlot("b")
            ));
        }
    }

    @Nested
    @DisplayName("Function declaration")
    class FunctionDeclaration {
        private LexicalScope lexicalScope;
        private FrameDescriptor frameDescriptor;

        @BeforeEach
        void setUp() {
            lexicalScope = new LexicalScope();
            frameDescriptor = new FrameDescriptor();
            visitor = new TuberNodeGeneratingVisitor(null, () -> lexicalScope, () -> frameDescriptor);
        }

        @Test
        void shouldGenerateFunctionDeclarationWithoutArgsAndBody() {
            // given
            String source = "" +
                    "def fun() {" +
                    "}";
            TuberParser.FunctionDeclarationContext context = TuberParserUtils.createParserForSource(source).functionDeclaration();

            // when
            StatementNode node = visitor.visitFunctionDeclaration(context);

            // then
            FunctionBodyNode expected = new FunctionBodyNode(new BlockNode(new StatementNode[]{}));
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(visitor.getFunctions().get("fun")).isNotNull();
            assertThat(lexicalScope.getLocals()).isEmpty();
        }

        @Test
        void shouldGenerateFunctionDeclarationWithArgsAndWithoutBody() {
            // given
            String source = "" +
                    "def fun(a, b) {" +
                    "}";
            TuberParser.FunctionDeclarationContext context = TuberParserUtils.createParserForSource(source).functionDeclaration();

            // when
            StatementNode node = visitor.visitFunctionDeclaration(context);

            // then
            FunctionBodyNode expected = new FunctionBodyNode(new BlockNode(new StatementNode[]{
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(0),
                            frameDescriptor.findFrameSlot("a")
                    ),
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(1),
                            frameDescriptor.findFrameSlot("b")
                    )
            }));
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(visitor.getFunctions().get("fun")).isNotNull();
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of(
                    "a", frameDescriptor.findFrameSlot("a"),
                    "b", frameDescriptor.findFrameSlot("b")
            ));
        }

        @Test
        void shouldGenerateFunctionDeclarationWithArgsAndBody() {
            // given
            String source = "" +
                    "def fun(a, b) {" +
                    "   c = 5;" +
                    "}";
            TuberParser.FunctionDeclarationContext context = TuberParserUtils.createParserForSource(source).functionDeclaration();

            // when
            StatementNode node = visitor.visitFunctionDeclaration(context);

            // then
            FunctionBodyNode expected = new FunctionBodyNode(new BlockNode(new StatementNode[]{
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(0),
                            frameDescriptor.findFrameSlot("a")
                    ),
                    WriteLocalVariableNodeGen.create(
                            new ReadArgumentNode(1),
                            frameDescriptor.findFrameSlot("b")
                    ),
                    WriteLocalVariableNodeGen.create(
                            new LongLiteralNode(5),
                            frameDescriptor.findFrameSlot("c")
                    )
            }));
            assertThat(node).isEqualToComparingFieldByFieldRecursively(expected);
            assertThat(visitor.getFunctions().get("fun")).isNotNull();
            assertThat(lexicalScope.getLocals()).isEqualTo(ImmutableMap.of(
                    "a", frameDescriptor.findFrameSlot("a"),
                    "b", frameDescriptor.findFrameSlot("b"),
                    "c", frameDescriptor.findFrameSlot("c")
            ));
        }
    }
}
