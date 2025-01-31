package com.NBE3_4_2_Team4.domain.point.repository;



import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
