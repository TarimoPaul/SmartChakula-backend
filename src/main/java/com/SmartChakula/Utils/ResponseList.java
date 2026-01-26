package com.SmartChakula.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseList<T> {
    private ResponseStatus status = ResponseStatus.Success;
    private List<T> data = new ArrayList<>();
    private String message = "Success";
    private int pageSize;
    private int totalPages;
    private int currentPage;
    private long totalItems;

    // Constructor for data list
    public ResponseList(List<T> data) {
        this.data = data;
        this.status = ResponseStatus.Success;
        this.message = "Success";
        this.pageSize = data != null ? data.size() : 0;
        this.totalItems = data != null ? data.size() : 0;
    }

    // Constructor for message only
    public ResponseList(String message) {
        this.message = message;
        this.status = ResponseStatus.Success;
    }

    // Constructor for message and status
    public ResponseList(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    // *** FIXED CONSTRUCTOR - Main one that service uses ***
    public ResponseList(ResponseStatus status, List<T> data, String message) {
        this.status = status;
        this.data = data != null ? data : new ArrayList<>();
        this.message = message;
        this.pageSize = this.data.size();
        this.totalItems = this.data.size();
        this.totalPages = this.data.size() > 0 ? 1 : 0;
    }

    // *** REMOVED THE PROBLEMATIC CONSTRUCTOR ***
    // public ResponseList(ResponseStatus status, Object ignored, String message) {
    //     this.status = status;
    //     this.message = message;
    //     // This was ignoring the data parameter!
    // }

    // Method to add all items to the data list
    public void addAll(List<T> items) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.addAll(items);
        this.pageSize = this.data.size();
        this.totalItems = this.data.size();
    }

    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    // Override setData to update pagination info
    public void setData(List<T> data) {
        this.data = data != null ? data : new ArrayList<>();
        this.pageSize = this.data.size();
        this.totalItems = this.data.size();
        this.totalPages = this.data.size() > 0 ? 1 : 0;
    }

    // Helper method for error responses
    public static <T> ResponseList<T> error(String message) {
        ResponseList<T> response = new ResponseList<>();
        response.setStatus(ResponseStatus.Error);
        response.setMessage(message);
        response.setData(new ArrayList<>());
        return response;
    }

    // Helper method for success responses
    public static <T> ResponseList<T> success(List<T> data, String message) {
        return new ResponseList<>(ResponseStatus.Success, data, message);
    }
}