package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.GuidePersistenceMapper;

class GuideRepositoryAdapterTest {

    private GuideJpaRepository guideJpaRepository;
    private GuidePersistenceMapper guidePersistenceMapper;
    private GuideRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        guideJpaRepository = mock(GuideJpaRepository.class);
        guidePersistenceMapper = mock(GuidePersistenceMapper.class);
        adapter = new GuideRepositoryAdapter(
                guideJpaRepository,
                guidePersistenceMapper);
    }

    @Test
    void updatesExistingGuideEntityInsteadOfMappingANewVersionlessEntity() {
        GuideProfile profile = GuideProfile.builder().id(42L).build();
        GuideEntity managedEntity = new GuideEntity();
        managedEntity.setId(profile.getId());
        managedEntity.setVersion(3L);
        when(guideJpaRepository.findById(profile.getId()))
                .thenReturn(Optional.of(managedEntity));
        when(guideJpaRepository.save(managedEntity)).thenReturn(managedEntity);
        when(guidePersistenceMapper.toDomain(managedEntity)).thenReturn(profile);

        GuideProfile saved = adapter.save(profile);

        assertSame(profile, saved);
        verify(guidePersistenceMapper).updateEntity(profile, managedEntity);
        verify(guideJpaRepository).save(managedEntity);
    }
}
