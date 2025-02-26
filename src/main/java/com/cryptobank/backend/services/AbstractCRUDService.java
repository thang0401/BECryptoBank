package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.BaseEntity;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.List;

/**
 * Lớp trừu tượng cung cấp triển khai CRUD chung cho các entity.
 *
 * @param <T> Kiểu dữ liệu entity kế thừa từ {@link BaseEntity}.
 */
@Slf4j
public abstract class AbstractCRUDService<T extends BaseEntity, ID> implements CRUDService<T, ID> {

    private final JpaRepository<T, ID> repository;
    private final Class<T> entityClass;

    /**
     * @param repository Thao tác với database
     * @param entityClass Chủ yếu lấy tên entity để debug chi tiết hơn
     */
    public AbstractCRUDService(JpaRepository<T, ID> repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    /**
     * @throws com.cryptobank.backend.exception.ResourceNotFoundException Nếu entity không tồn tại.
     */
    @Override
    public T get(ID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi của " + entityClass.getSimpleName() + " với id: " + id));
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<T> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<T> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    /**
     * @see JpaRepository#save(Object)
     */
    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    /**
     * @throws com.cryptobank.backend.exception.ResourceNotFoundException Nếu entity không tồn tại.
     */
    @Override
    public T update(ID id, T entity) {
        return repository
                .findById(id)
                .map(existing -> {
                    // TODO: tự động kiểm tra và cập nhật các thuộc tính (bỏ qua null)
                    //  testing BeanUtils#copyProperties
                    BeanUtils.copyProperties(entity, existing, getNullPropertyNames(existing));
                    return repository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi của " + entityClass.getSimpleName() + " với id: " + id));
    }

    /**
     * @throws com.cryptobank.backend.exception.ResourceNotFoundException Nếu entity không tồn tại.
     */
    @Override
    public void delete(ID id) {
        repository
                .findById(id)
                .ifPresentOrElse(
//                        repository::delete,
                        entity -> {
//                            entity.setDeleted(true);
                            repository.save(entity);
                        },
                        () -> {
                            throw new ResourceNotFoundException("Không tìm thấy bản ghi của " + entityClass.getSimpleName() + " với id: " + id);
                        }
                );
    }

    protected JpaRepository<T, ID> getJpaRepository() {
        return repository;
    }

    private String[] getNullPropertyNames(T source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

}
