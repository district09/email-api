package gent.d09.servicefactory.email.api.module.common.util;

import io.quarkus.panache.common.Sort;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.HttpStatus;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.uri.expression.*;
import org.apache.olingo.odata2.core.uri.expression.PropertyExpressionImpl;

import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryVisitor implements ExpressionVisitor {
    private final Set<String> filterProperties;

    public QueryVisitor(Set<String> filterProperties) {
        this.filterProperties = filterProperties;
    }

    @Override
    public Object visitFilterExpression(FilterExpression filterExpression, String expressionString, Object expression) {
        return expression;
    }

    @Override
    public Object visitBinary(BinaryExpression binaryExpression, BinaryOperator binaryOperator, Object leftSide, Object rightSide) {
        if(binaryExpression.getLeftOperand() instanceof PropertyExpressionImpl && !filterProperties.contains(leftSide)) {
            throw new WebApplicationException("Can not filter entity on property "
                    + leftSide + ". The following properties are allowed: "
                    + String.join(", ", this.filterProperties), HttpStatus.SC_BAD_REQUEST);
        }

        if(binaryOperator.equals(BinaryOperator.EQ)) {
            return leftSide + " = " + "'" + rightSide + "'";
        } else if(binaryOperator == BinaryOperator.NE) {
            return leftSide + " != " + "'" + rightSide + "'";
        } else if(binaryOperator == BinaryOperator.OR) {
            return "(" + leftSide + ") OR (" + rightSide + ")";
        } else if(binaryOperator == BinaryOperator.AND) {
            return "(" + leftSide + ") AND (" + rightSide + ")";
        } else {
            throw new NotImplementedException("Operator " + binaryOperator.toString() +" is not implemented");
        }
    }

    @Override
    public Object visitOrderByExpression(OrderByExpression orderByExpression, String s, List<Object> list) {
        List<Sort.Column> columns = list.stream()
                .flatMap((obj) -> ((Sort) obj).getColumns().stream())
                .collect(Collectors.toList());
        Sort combined = null;
        for(Sort.Column column : columns) {
            if(combined == null) {
                combined = Sort.by(column.getName(), column.getDirection());
            } else {
                combined.and(column.getName(), column.getDirection());
            }
        }
        return combined;
    }

    @Override
    public Object visitOrder(OrderExpression orderExpression, Object field, SortOrder sortOrder) {
        Sort.Direction direction = sortOrder.equals(SortOrder.asc) ? Sort.Direction.Ascending : Sort.Direction.Descending;
        return Sort.by(field.toString(), direction);
    }

    @Override
    public Object visitLiteral(LiteralExpression literalExpression, EdmLiteral edmLiteral) {
        return Long.parseLong(edmLiteral.getLiteral());
    }

    @Override
    public Object visitMethod(MethodExpression methodExpression, MethodOperator methodOperator, List<Object> list) {
        throw new NotImplementedException("visitMethod is not implemented");
    }

    @Override
    public Object visitMember(MemberExpression memberExpression, Object o, Object o1) {
        throw new NotImplementedException("visitMember is not implemented");
    }

    @Override
    public Object visitProperty(PropertyExpression propertyExpression, String s, EdmTyped edmTyped) {
        return s;
    }

    @Override
    public Object visitUnary(UnaryExpression unaryExpression, UnaryOperator unaryOperator, Object o) {
        throw new NotImplementedException("visitUnary is not implemented");
    }
}
