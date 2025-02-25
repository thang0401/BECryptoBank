// package com.cryptobank.backend.repository;

// import com.cryptobank.backend.entity.RolePermission;
// import com.cryptobank.backend.entity.RolePermissionKey;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;

// import java.util.List;

// public interface RolePermissionDAO extends JpaRepository<RolePermission, RolePermissionKey> {

//     @Query("SELECT new org.springframework.security.core.authority.SimpleGrantedAuthority(rp.permission.id) FROM RolePermission rp where rp.role.id = :roleId")
//     List<SimpleGrantedAuthority> findByRoleId(String roleId);

// }
