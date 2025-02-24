package com.cryptobank.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interface chung cho các service có CRUD.<br>
 * Bao gồm các phương thức <code>get</code> <code>getAll</code> <code>create</code> <code>update</code> <code>delete</code>.
 * @param <T> Kiểu dữ liệu entity.
 */
public interface CRUDService<T> {

    /**
     * Tìm entity theo ID.
     * @param id ID entity cần tìm.
     * @return Entity nếu được tìm thấy.
     * @throws com.cryptobank.backend.exception.ResourceNotFoundException Nếu entity không tồn tại.
     */
    T get(UUID id);

    /**
     * Lấy danh sách tất cả entity.
     * @return Danh sách tất cả entity.
     */
    List<T> getAll();

    /**
     * Lấy danh sách phân trang tất cả entity.
     * @return Danh sách phân trang tất cả entity.
     */
    Page<T> getAll(Pageable pageable);

    /**
     * Lưu một entity mới.
     * @param entity Entity cần lưu.
     * @return Entity sau khi đã được lưu vào database.
     */
    T save(T entity);

    /**
     * Cập nhật entity có tồn tại trong database.
     * @param id ID entity cần cập nhật.
     * @param entity Dữ liệu entity mới để cập nhật.
     * @return Entity sau khi cập nhật.
     * @throws com.cryptobank.backend.exception.ResourceNotFoundException Nếu entity không tồn tại.
     */
    T update(UUID id, T entity);

    /**
     * Xóa entity theo ID.
     * @param id ID entity cần xóa.
     * @throws com.cryptobank.backend.exception.ResourceNotFoundException Nếu entity không tồn tại.
     */
    void delete(UUID id);

}
