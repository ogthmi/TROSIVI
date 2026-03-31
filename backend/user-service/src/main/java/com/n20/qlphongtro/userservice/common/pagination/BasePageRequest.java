package com.n20.qlphongtro.userservice.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BasePageRequest {
    private Integer pageNumber;
    private Integer pageSize;

    private String sortBy;
    private String direction;

    private String keyword;
}
