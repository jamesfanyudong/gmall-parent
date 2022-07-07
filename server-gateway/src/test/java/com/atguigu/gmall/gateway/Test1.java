package com.atguigu.gmall.gateway;


import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Slf4j
public class Test1 {

    //96e79218965eb72c92a549dd5a330112


    public static void main(String[] args) {
        //1.拿到一个表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("new String('121212')");
        String message = exp.getValue(String.class);
        log.info("message=【{}】",message);

    }
}
