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
import com.github.sobolewskikamil.tuber.language.grammar.TuberBaseVisitor;
import com.github.sobolewskikamil.tuber.language.grammar.TuberParser;
import com.github.sobolewskikamil.tuber.language.node.RootNode;
import com.github.sobolewskikamil.tuber.language.node.StatementNode;
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.github.sobolewskikamil.tuber.language.node.expression.access.*;
import com.github.sobolewskikamil.tuber.language.node.expression.arithmetic.*;
import com.github.sobolewskikamil.tuber.language.node.expression.call.CallNode;
import com.github.sobolewskikamil.tuber.language.node.expression.flowcontrol.*;
import com.github.sobolewskikamil.tuber.language.node.expression.literal.*;
import com.github.sobolewskikamil.tuber.language.node.expression.logical.*;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TuberNodeGeneratingVisitor extends TuberBaseVisitor<StatementNode> {
    private final Language language;
    private final Map<String, RootCallTarget> functions;

    private LexicalScope lexicalScope;
    private FrameDescriptor frameDescriptor;
    private Supplier<LexicalScope> lexicalScopeSupplier;
    private Supplier<FrameDescriptor> frameDescriptorSupplier;

    TuberNodeGeneratingVisitor(Language language) {
        this.language = language;
        this.functions = new HashMap<>();
        this.frameDescriptorSupplier = FrameDescriptor::new;
        this.lexicalScopeSupplier = LexicalScope::new;
    }

    TuberNodeGeneratingVisitor(Language language, Supplier<LexicalScope> lexicalScopeSupplier, Supplier<FrameDescriptor> frameDescriptorSupplier) {
        this(language);
        this.lexicalScope = lexicalScopeSupplier.get();
        this.lexicalScopeSupplier = lexicalScopeSupplier;
        this.frameDescriptor = frameDescriptorSupplier.get();
        this.frameDescriptorSupplier = frameDescriptorSupplier;
    }

    Map<String, RootCallTarget> getFunctions() {
        return functions;
    }

    @Override
    public StatementNode visitFunctionDeclaration(TuberParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.Identifier().getSymbol().getText();

        lexicalScope = lexicalScopeSupplier.get();
        frameDescriptor = frameDescriptorSupplier.get();

        BlockNode functionBodyBlockNode = getFunctionBodyBlockNode(ctx);
        ExpressionNode functionBodyNode = new FunctionBodyNode(functionBodyBlockNode);
        RootNode rootNode = new RootNode(language, frameDescriptor, functionBodyNode);

        frameDescriptor = null;
        lexicalScope = null;
        functions.put(functionName, Truffle.getRuntime().createCallTarget((com.oracle.truffle.api.nodes.RootNode) rootNode.deepCopy()));

        return functionBodyNode;
    }

    @Override
    public StatementNode visitIdList(TuberParser.IdListContext ctx) {
        List<TerminalNode> arguments = ctx.Identifier();
        StatementNode[] statementNodes = IntStream.range(0, ctx.Identifier().size())
                .mapToObj(i -> {
                    ReadArgumentNode readArgumentNode = new ReadArgumentNode(i);
                    String identifier = arguments.get(i).getSymbol().getText();
                    FrameSlot frameSlot = frameDescriptor.findOrAddFrameSlot(identifier);
                    lexicalScope.addLocal(identifier, frameSlot);
                    return WriteLocalVariableNodeGen.create(readArgumentNode, frameSlot);
                })
                .toArray(StatementNode[]::new);
        return new BlockNode(statementNodes);
    }

    @Override
    public StatementNode visitFunctionCall(TuberParser.FunctionCallContext ctx) {
        String functionToCall = ctx.Identifier().getSymbol().getText();
        ExpressionNode[] arguments = ctx.expression().stream()
                .map(this::visitExpression)
                .map(ExpressionNode.class::cast)
                .toArray(ExpressionNode[]::new);
        return new CallNode(new FunctionNode(language, functionToCall), arguments);
    }

    @Override
    public StatementNode visitNonIterationStatement(TuberParser.NonIterationStatementContext ctx) {
        if (ctx.statement() != null) {
            return visitStatement(ctx.statement());
        }
        if (ctx.ifStatement() != null) {
            return visitIfStatement(ctx.ifStatement());
        }
        throw new IllegalStateException(String.format("Unknown non iteration statement: %s", ctx.getText()));
    }

    @Override
    public StatementNode visitIterationStatement(TuberParser.IterationStatementContext ctx) {
        if (ctx.statement() != null) {
            return visitStatement(ctx.statement());
        }
        if (ctx.ifIterationStatement() != null) {
            return visitIfIterationStatement(ctx.ifIterationStatement());
        }
        if (ctx.Break() != null) {
            return new BreakNode();
        }
        if (ctx.Continue() != null) {
            return new ContinueNode();
        }
        throw new IllegalStateException(String.format("Unknown iteration statement: %s", ctx.getText()));
    }

    @Override
    public StatementNode visitStatement(TuberParser.StatementContext ctx) {
        if (ctx.assignment() != null) {
            return visitAssignment(ctx.assignment());
        }
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        }
        if (ctx.returnStatement() != null) {
            return visitReturnStatement(ctx.returnStatement());
        }
        if (ctx.whileStatement() != null) {
            return visitWhileStatement(ctx.whileStatement());
        }
        throw new IllegalStateException(String.format("Unknown statement: %s", ctx.getText()));
    }

    @Override
    public StatementNode visitWhileStatement(TuberParser.WhileStatementContext ctx) {
        ExpressionNode conditionNode = (ExpressionNode) visitWhileStat(ctx.whileStat());
        StatementNode iterationStatement = visitIterStat(ctx.iterStat());
        return new WhileNode(conditionNode, iterationStatement);
    }

    @Override
    public StatementNode visitWhileStat(TuberParser.WhileStatContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public StatementNode visitIterStat(TuberParser.IterStatContext ctx) {
        StatementNode[] collect = ctx.iterationStatement().stream()
                .map(this::visitIterationStatement)
                .toArray(StatementNode[]::new);
        return new BlockNode(collect);
    }

    @Override
    public StatementNode visitIfIterationStatement(TuberParser.IfIterationStatementContext ctx) {
        ExpressionNode ifStatNode = (ExpressionNode) visitIfStat(ctx.ifStat());
        StatementNode thenStatNode = visitThenIterStat(ctx.thenIterStat());
        StatementNode elseStatNode = ctx.elseIterStat() == null ? null : visitElseIterStat(ctx.elseIterStat());
        return new IfNode(ifStatNode, thenStatNode, elseStatNode);
    }

    @Override
    public StatementNode visitIfStat(TuberParser.IfStatContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public StatementNode visitThenIterStat(TuberParser.ThenIterStatContext ctx) {
        StatementNode[] collect = ctx.iterationStatement().stream()
                .map(this::visitIterationStatement)
                .toArray(StatementNode[]::new);
        return new BlockNode(collect);
    }

    @Override
    public StatementNode visitElseIterStat(TuberParser.ElseIterStatContext ctx) {
        StatementNode[] collect = ctx.iterationStatement().stream()
                .map(this::visitIterationStatement)
                .toArray(StatementNode[]::new);
        return new BlockNode(collect);
    }

    @Override
    public StatementNode visitIfStatement(TuberParser.IfStatementContext ctx) {
        ExpressionNode ifStatNode = (ExpressionNode) visitIfStat(ctx.ifStat());
        StatementNode thenStatNode = visitThenStat(ctx.thenStat());
        StatementNode elseStatNode = ctx.elseStat() == null ? null : visitElseStat(ctx.elseStat());
        return new IfNode(ifStatNode, thenStatNode, elseStatNode);
    }

    @Override
    public StatementNode visitThenStat(TuberParser.ThenStatContext ctx) {
        StatementNode[] collect = ctx.nonIterationStatement().stream()
                .map(this::visitNonIterationStatement)
                .toArray(StatementNode[]::new);
        return new BlockNode(collect);
    }

    @Override
    public StatementNode visitElseStat(TuberParser.ElseStatContext ctx) {
        StatementNode[] collect = ctx.nonIterationStatement().stream()
                .map(this::visitNonIterationStatement)
                .toArray(StatementNode[]::new);
        return new BlockNode(collect);
    }

    @Override
    public StatementNode visitAssignment(TuberParser.AssignmentContext ctx) {
        if (ctx.variableAssignment() != null) {
            return visitVariableAssignment(ctx.variableAssignment());
        }
        if (ctx.arrayElementAssignment() != null) {
            return visitArrayElementAssignment(ctx.arrayElementAssignment());
        }
        throw new IllegalStateException(String.format("Unknown assignment: %s", ctx.getText()));
    }

    @Override
    public StatementNode visitVariableAssignment(TuberParser.VariableAssignmentContext ctx) {
        String identifier = ctx.Identifier().getSymbol().getText();
        FrameSlot frameSlot = frameDescriptor.findOrAddFrameSlot(identifier);
        lexicalScope.addLocal(identifier, frameSlot);
        ExpressionNode valueNode = (ExpressionNode) visitExpression(ctx.expression());
        return WriteLocalVariableNodeGen.create(valueNode, frameSlot);
    }

    @Override
    public StatementNode visitArrayElementAssignment(TuberParser.ArrayElementAssignmentContext ctx) {
        String identifier = ctx.arrayElement().Identifier().getText();
        FrameSlot slot = lexicalScope.getLocals().get(identifier);
        ExpressionNode element = (ExpressionNode) visitExpression(ctx.expression());
        List<ExpressionNode> indexNodes = ctx.arrayElement().expression().stream()
                .map(this::visitExpression)
                .map(ExpressionNode.class::cast)
                .collect(Collectors.toList());
        return getWriteArrayElementNode(new LinkedList<>(indexNodes), element, ReadLocalVariableNodeGen.create(slot));
    }

    @Override
    public StatementNode visitReturnStatement(TuberParser.ReturnStatementContext ctx) {
        ExpressionNode expression = (ExpressionNode) visitExpression(ctx.expression());
        return new ReturnNode(expression);
    }

    @Override
    public StatementNode visitExpression(TuberParser.ExpressionContext ctx) {
        if (ctx.OpenParen() != null && ctx.CloseParen() != null) {
            return visitExpression(ctx.expression(0));
        }
        if (ctx.literal() != null) {
            return visitLiteral(ctx.literal());
        }
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        }
        if (ctx.array() != null) {
            return visitArray(ctx.array());
        }
        if (ctx.arrayElement() != null) {
            return visitArrayElement(ctx.arrayElement());
        }
        if (ctx.op != null) {
            Token op = ctx.op;
            if (op.getType() == TuberParser.Add) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return AddNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Subtract) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return SubNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Divide) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return DivNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Multiply) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return MulNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Modulus) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return ModNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Exp) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return ExpNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Equal) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return EqualNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.NotEqual) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return NotEqualNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.GreaterOrEqual) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return GreaterOrEqualNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.LessOrEqual) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return LessOrEqualNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Greater) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return GreaterNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.Less) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return LessNodeGen.create(left, right);
            }
            if (op.getType() == TuberParser.And) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return new AndNode(left, right);
            }
            if (op.getType() == TuberParser.Or) {
                ExpressionNode left = (ExpressionNode) visit(ctx.left);
                ExpressionNode right = (ExpressionNode) visit(ctx.right);
                return new OrNode(left, right);
            }
            if (op.getType() == TuberParser.Not) {
                ExpressionNode expression = (ExpressionNode) visit(ctx.expression(0));
                return NotNodeGen.create(expression);
            }
        }
        throw new IllegalStateException(String.format("Unknown expression: %s", ctx.getText()));
    }

    @Override
    public StatementNode visitArray(TuberParser.ArrayContext ctx) {
        if (ctx.expressionList() != null) {
            ExpressionNode[] expressions = ctx.expressionList().expression().stream()
                    .map(this::visitExpression)
                    .map(ExpressionNode.class::cast)
                    .toArray(ExpressionNode[]::new);
            return new ArrayLiteralNode(expressions);
        }
        return new ArrayLiteralNode();
    }

    @Override
    public StatementNode visitArrayElement(TuberParser.ArrayElementContext ctx) {
        String identifier = ctx.Identifier().getText();
        FrameSlot slot = lexicalScope.getLocals().get(identifier);
        List<ExpressionNode> expression = ctx.expression().stream()
                .map(this::visitExpression)
                .map(ExpressionNode.class::cast)
                .collect(Collectors.toList());
        return getReadArrayElementNode(new LinkedList<>(expression), ReadLocalVariableNodeGen.create(slot));
    }

    @Override
    public StatementNode visitLiteral(TuberParser.LiteralContext ctx) {
        String text = ctx.getText();
        if (ctx.Identifier() != null) {
            FrameSlot slot = lexicalScope.getLocals().get(text);
            return ReadLocalVariableNodeGen.create(slot);
        }
        if (ctx.Null() != null) {
            return new NullLiteralNode(NullType.getInstance());
        }
        if (ctx.Bool() != null) {
            return new BooleanLiteralNode(Boolean.valueOf(text));
        }
        if (ctx.String() != null) {
            String withoutQuotes = text.substring(1, text.length() - 1);
            return new StringLiteralNode(withoutQuotes);
        }
        if (ctx.Long() != null) {
            return new LongLiteralNode(Long.valueOf(text));
        }
        if (ctx.Double() != null) {
            return new DoubleLiteralNode(Double.valueOf(text));
        }
        throw new IllegalStateException(String.format("Unknown literal: %s", ctx.getText()));
    }

    private BlockNode getFunctionBodyBlockNode(TuberParser.FunctionDeclarationContext ctx) {
        if (ctx.idList() != null) {
            BlockNode argumentsAssignments = (BlockNode) visitIdList(ctx.idList());
            List<StatementNode> bodyStatements = ctx.nonIterationStatement().stream()
                    .map(this::visitNonIterationStatement)
                    .collect(Collectors.toList());
            StatementNode[] statementNodes = Stream.concat(argumentsAssignments.getStatementNodes().stream(), bodyStatements.stream())
                    .toArray(StatementNode[]::new);
            return new BlockNode(statementNodes);
        }
        return new BlockNode(ctx.nonIterationStatement().stream()
                .map(this::visitNonIterationStatement)
                .toArray(StatementNode[]::new));
    }

    private StatementNode getReadArrayElementNode(Queue<ExpressionNode> indexes, ExpressionNode currentNode) {
        if (indexes.isEmpty()) {
            return currentNode;
        }
        ExpressionNode indexNode = indexes.poll();
        ReadArrayElementNode newCurrent = new ReadArrayElementNode(currentNode, indexNode);
        return getReadArrayElementNode(indexes, newCurrent);
    }

    private StatementNode getWriteArrayElementNode(Queue<ExpressionNode> indexes, ExpressionNode element, ExpressionNode currentNode) {
        ExpressionNode indexNode = indexes.peek();
        if (indexes.size() == 1) {
            return new WriteArrayElementNode(currentNode, indexNode, element);
        }
        indexes.remove();
        ReadArrayElementNode newCurrent = new ReadArrayElementNode(currentNode, indexNode);
        return getWriteArrayElementNode(indexes, element, newCurrent);
    }
}
