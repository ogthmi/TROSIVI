package com.n20.qlphongtro.userservice.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageUtil {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public static Pageable buildPageable(BasePageRequest pageRequest, Sort sort) {

        int pageNumber = 0;
        if (pageRequest.getPageNumber() != null && pageRequest.getPageNumber() > 0){
            pageNumber = pageRequest.getPageNumber();
        }

        int pageSize;
        if (pageRequest.getPageSize() != null &&
                pageRequest.getPageSize() > 0 && pageRequest.getPageSize() <= MAX_PAGE_SIZE){
            pageSize = pageRequest.getPageSize();
        } else {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    public static Sort buildSort(String sortBy, String direction, List<String> allowedSortFields) {
        if (sortBy == null || allowedSortFields == null || allowedSortFields.isEmpty()) return Sort.unsorted();

        String safeSortBy;
        if (allowedSortFields.contains(sortBy)) safeSortBy = sortBy;
        else safeSortBy = allowedSortFields.getFirst();

        Sort.Direction dir;
        if ("DESC".equalsIgnoreCase(direction)) dir = Sort.Direction.DESC;
        else dir = Sort.Direction.ASC;

        return Sort.by(dir, safeSortBy);
    }
}

