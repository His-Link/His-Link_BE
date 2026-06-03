package com.hislink.domain.techstack.config;

import com.hislink.domain.techstack.entity.TechStack;
import com.hislink.domain.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechStackDataInitializer implements ApplicationRunner {

    private static final List<String> DEFAULT_STACKS = List.of(
            "React",
            "Vue",
            "Spring Boot",
            "Node.js",
            "Python",
            "TypeScript",
            "MySQL",
            "PostgreSQL",
            "Docker",
            "Figma"
    );

    private final TechStackRepository techStackRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (techStackRepository.count() > 0) {
            return;
        }
        for (String name : DEFAULT_STACKS) {
            techStackRepository.save(TechStack.builder().name(name).build());
        }
    }
}
