package com.localy.backend.domain.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class PageResult<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
}