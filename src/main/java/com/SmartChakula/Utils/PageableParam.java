package com.SmartChakula.Utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableParam {
    
    private int page = 0;
    private int size = 10;
    private String search = "";
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    
    public PageableParam() {}
    
    public PageableParam(int page, int size) {
        this.page = page;
        this.size = size;
    }
    
    public PageableParam(int page, int size, String search) {
        this.page = page;
        this.size = size;
        this.search = search;
    }
    
    public Pageable getPageable(boolean withSort) {
        if (withSort) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Sort sort = Sort.by(direction, sortBy);
            return PageRequest.of(page, size, sort);
        }
        return PageRequest.of(page, size);
    }
    
    public Pageable getNativePageable(boolean withSort) {
        return getPageable(withSort);
    }
    
    public String key() {
        return search != null ? search.toLowerCase().trim() : "";
    }
    
    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}