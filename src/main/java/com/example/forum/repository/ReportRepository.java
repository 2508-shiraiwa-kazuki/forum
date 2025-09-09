package com.example.forum.repository;

import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositoryの宣言
@Repository
// 継承元のJpaRepositoryにfindAllメソッドが備わっているため、追記不要
// findAllメソッドは「SELECT * FROM report」みたいなもの
public interface ReportRepository extends JpaRepository<Report, Integer> {
}
