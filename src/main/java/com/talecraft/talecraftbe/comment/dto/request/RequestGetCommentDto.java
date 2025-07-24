package com.talecraft.talecraftbe.comment.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class RequestGetCommentDto {

    private final int page = 0;
    private final int size = 10;
    private final String sort = "createdDate";
    private final String direction = "DESC";

    public Pageable toPageable() {
        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(dir, sort));
    }


}
