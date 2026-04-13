package com.example.librarymis.service;

import com.example.librarymis.model.entity.Fine;
import java.util.List;

public interface FineService {
    Fine save(Fine fine);
    List<Fine> getAll();
    List<Fine> getUnpaid();
    void delete(Long id);
    long countUnpaid();
}
