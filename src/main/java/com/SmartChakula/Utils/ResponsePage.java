 package com.SmartChakula.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class ResponsePage<T> extends Response<List<T>> {

    @JsonIgnore
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    private Integer elements; // Total number of elements
    private Integer size = DEFAULT_PAGE_SIZE; // Elements in one page
    private Integer pages = 0; // Number of pages
    private Integer page = 1; // Current page

    public ResponsePage(Page<T> page) {
        super(page.getContent());
        setStatus(ResponseStatus.Success);
        setMessage(ResponseStatus.Success.toString());
        pages = page.getTotalPages();
        elements = (int) page.getTotalElements();
        size = page.getSize();
        this.page = page.getNumber() + 1; // Spring uses 0-based, we use 1-based
    }

    public ResponsePage(Exception e) {
        setStatus(ResponseStatus.Error);
        setMessage(Utils.getExceptionMessage(e));
    }

    public ResponsePage(String message) {
        setStatus(ResponseStatus.Error);
        setMessage(message);
    }

    public ResponsePage(Page<T> page, String message) {
        super(page.getContent());
        setMessage(message);
        setStatus(ResponseStatus.Warning);
        pages = page.getTotalPages();
        elements = (int) page.getTotalElements();
        size = page.getSize();
        this.page = page.getNumber() + 1;
    }

    public Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(ResponseStatus.Error, null, message);
    }

    public ResponsePage() {
        super();
    }
}