package com.example.librarymis.service;

import com.example.librarymis.model.entity.Publisher;
import java.util.List;

public interface PublisherService {
    Publisher save(Publisher publisher);
    List<Publisher> getAll();
    List<Publisher> search(String keyword);
    void delete(Long id);
}
