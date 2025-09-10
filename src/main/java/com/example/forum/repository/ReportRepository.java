package com.example.forum.repository;

import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

// Repositoryの宣言
@Repository
// 継承元のJpaRepositoryにfindAllメソッドが備わっているため、追記不要
// findAllメソッドは「SELECT * FROM report」みたいなもの
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByCreatedDateBetweenOrderByUpdatedDateDesc(Timestamp start, Timestamp end);
}
