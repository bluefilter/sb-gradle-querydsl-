package io.querydsl.service;

import io.querydsl.entity.CostcoProductThumbnail;
import io.querydsl.repository.CostcoProductThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CostcoProductThumbnailService {

    private final CostcoProductThumbnailRepository repository;

    public CostcoProductThumbnail saveProduct(String name, byte[] thumbnail) {
        CostcoProductThumbnail product = new CostcoProductThumbnail(name, thumbnail);
        return repository.save(product);
    }

    public List<CostcoProductThumbnail> getAllProducts() {
        return repository.findAll();
    }

    public Optional<CostcoProductThumbnail> getProductById(UUID id) {
        return repository.findById(id);
    }

    public void deleteProduct(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NoSuchElementException("해당 ID의 제품을 찾을 수 없습니다: " + id);
        }
    }
}

