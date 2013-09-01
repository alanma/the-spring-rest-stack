package com.jl.crm.web.config;

import com.mangofactory.swagger.OperationComparator;
import com.wordnik.swagger.core.DocumentationOperation;
import org.springframework.stereotype.Component;

//@Component
public class NameOperationComparator implements OperationComparator {

    public int compare(DocumentationOperation first, DocumentationOperation second) {
        return first.getNickname().compareTo(second.getNickname());
    }
}
