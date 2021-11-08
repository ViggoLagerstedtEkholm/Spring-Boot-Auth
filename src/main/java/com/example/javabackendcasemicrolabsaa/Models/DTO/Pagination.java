package com.example.javabackendcasemicrolabsaa.Models.DTO;

import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Pagination {
    private int PageFirstResultIndex;
    private int ResultsPerPage;
    private int TotalPages;
    private int TotalResults;
    private List<UserResponse> users = new ArrayList<>();

    public Pagination(int pageFirstResultIndex, int resultsPerPage, int totalPages) {
        PageFirstResultIndex = pageFirstResultIndex;
        ResultsPerPage = resultsPerPage;
        TotalPages = totalPages;
    }
}
