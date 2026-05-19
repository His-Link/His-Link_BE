package com.hislink.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PageableSupport {

    private PageableSupport() {
    }

    public static Pageable sanitize(
            Pageable pageable,
            int maxSize,
            Set<String> allowedSortProperties,
            String defaultSortProperty,
            Sort.Direction defaultDirection
    ) {
        int size = Math.min(Math.max(pageable.getPageSize(), 1), maxSize);
        int page = Math.max(pageable.getPageNumber(), 0);

        Sort sort = sanitizeSort(pageable.getSort(), allowedSortProperties, defaultSortProperty, defaultDirection);
        return PageRequest.of(page, size, sort);
    }

    private static Sort sanitizeSort(
            Sort sort,
            Set<String> allowedSortProperties,
            String defaultSortProperty,
            Sort.Direction defaultDirection
    ) {
        if (sort == null || sort.isUnsorted()) {
            return Sort.by(defaultDirection, defaultSortProperty);
        }

        List<Sort.Order> validOrders = new ArrayList<>();
        for (Sort.Order order : sort) {
            if (allowedSortProperties.contains(order.getProperty())) {
                validOrders.add(order);
            }
        }

        if (validOrders.isEmpty()) {
            return Sort.by(defaultDirection, defaultSortProperty);
        }
        return Sort.by(validOrders);
    }
}
