package com.prj.flashdeal.global.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PageResponse(Page<T> page){
        content = page.getContent();
        pageNumber = page.getNumber();
        pageSize = page.getSize();
        totalElements = page.getTotalElements();
        totalPages = page.getTotalPages();
    }

}
