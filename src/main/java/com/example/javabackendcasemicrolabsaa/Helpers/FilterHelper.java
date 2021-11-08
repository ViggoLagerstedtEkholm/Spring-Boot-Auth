package com.example.javabackendcasemicrolabsaa.Helpers;

import com.example.javabackendcasemicrolabsaa.Models.DTO.Pagination;
import com.example.javabackendcasemicrolabsaa.Models.DTO.UserResponse;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FilterHelper {
    public static Pagination CalculateOffsets(int count, int page, int resultPerPage)
    {
        int TotalPages = (count + resultPerPage - 1) / resultPerPage;
        int PageFirstResultIndex = (page - 1) * resultPerPage;
        return new Pagination(PageFirstResultIndex, resultPerPage, TotalPages);
    }

    public static Pagination getPaginationResult(List<UserResponse> users, int count, int page, int resultsPerPage){
        Pagination pagination = FilterHelper.CalculateOffsets(count, page, resultsPerPage);
        pagination.setTotalResults(count);

        int endIndex = Math.min(pagination.getPageFirstResultIndex() + resultsPerPage, users.size());
        int startIndex = pagination.getPageFirstResultIndex();
        if(pagination.getPageFirstResultIndex() < 0 || pagination.getPageFirstResultIndex() > users.size()){
            startIndex = 0;
        }

        log.info("Start index {}", startIndex);
        log.info("End index {}", endIndex);
        List<UserResponse> range = users.subList(startIndex, endIndex);
        pagination.setUsers(range);
        return pagination;
    }
}
