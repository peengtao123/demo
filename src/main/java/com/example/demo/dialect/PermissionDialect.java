package com.example.demo.dialect;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限控制方言
 */
public class PermissionDialect extends AbstractProcessorDialect {

    private static final String DIALECT_NAME = "Permission Dialect";

    public PermissionDialect() {
        super(DIALECT_NAME, "perm", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<>();
        // 添加hasPermission属性处理器
        processors.add(new HasPermissionAttributeTagProcessor(dialectPrefix));
        return processors;
    }
}
