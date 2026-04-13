package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Publisher;
import com.example.librarymis.service.PublisherService;
import com.example.librarymis.service.impl.PublisherServiceImpl;
import java.util.List;

public class PublisherController {
    private final PublisherService publisherService = new PublisherServiceImpl();

    public Publisher save(Publisher publisher) {
        // Mục đích: xử lý logic của hàm save.
        return publisherService.save(publisher);
    }

    public List<Publisher> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return publisherService.getAll();
    }

    public List<Publisher> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        return publisherService.search(keyword);
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        publisherService.delete(id);
    }
}
