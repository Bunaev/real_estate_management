package com.search_service.service;

import com.search_service.entity.Developer;
import com.search_service.repository.DeveloperRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepo devRepo;

    @InjectMocks
    private DeveloperService developerService;

    @Test
    void findAll_shouldReturnDevelopers() {
        Developer dev = Developer.builder().id(1L).name("Главстрой").build();
        when(devRepo.findAll()).thenReturn(List.of(dev));

        List<Developer> result = developerService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Главстрой");
    }

    @Test
    void create_shouldSaveDeveloper() {
        Developer dev = Developer.builder().id(1L).name("ЛенСпецСтрой").build();
        when(devRepo.save(any(Developer.class))).thenReturn(dev);

        Developer result = developerService.create("ЛенСпецСтрой");
        assertThat(result.getName()).isEqualTo("ЛенСпецСтрой");
    }
}