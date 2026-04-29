package com.hislink.domain.sample.service;

import com.hislink.domain.sample.dto.SampleItemCreateRequest;
import com.hislink.domain.sample.dto.SampleItemResponse;
import com.hislink.domain.sample.dto.SampleItemUpdateRequest;
import com.hislink.domain.sample.entity.SampleItem;
import com.hislink.domain.sample.repository.SampleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SampleItemService {

    private final SampleItemRepository sampleItemRepository;

    @Transactional
    public SampleItemResponse create(SampleItemCreateRequest request) {
        SampleItem sampleItem = SampleItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        return SampleItemResponse.from(sampleItemRepository.save(sampleItem));
    }

    public List<SampleItemResponse> findAll() {
        return sampleItemRepository.findAll()
                .stream()
                .map(SampleItemResponse::from)
                .collect(Collectors.toList());
    }

    public SampleItemResponse findById(Long id) {
        SampleItem sampleItem = getById(id);
        return SampleItemResponse.from(sampleItem);
    }

    @Transactional
    public SampleItemResponse update(Long id, SampleItemUpdateRequest request) {
        SampleItem sampleItem = getById(id);
        sampleItem.update(request.getTitle(), request.getDescription());
        return SampleItemResponse.from(sampleItem);
    }

    @Transactional
    public void delete(Long id) {
        SampleItem sampleItem = getById(id);
        sampleItemRepository.delete(sampleItem);
    }

    private SampleItem getById(Long id) {
        return sampleItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sample item not found. id=" + id));
    }
}
