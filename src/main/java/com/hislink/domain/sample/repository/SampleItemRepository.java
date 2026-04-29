package com.hislink.domain.sample.repository;

import com.hislink.domain.sample.entity.SampleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleItemRepository extends JpaRepository<SampleItem, Long> {
}
