package com.xelops.actionplan.service;


import com.xelops.actionplan.domain.CustomFieldOption;
import com.xelops.actionplan.dto.CustomFieldOptionSimplifiedDto;
import com.xelops.actionplan.mapper.CustomFieldMapper;
import com.xelops.actionplan.repository.CustomFieldOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomFieldService {

    private final CustomFieldOptionRepository customFieldOptionRepository;
    private final CustomFieldMapper customFieldMapper;

    public List<CustomFieldOptionSimplifiedDto> getCustomFieldOptions(String type) {
        log.info("Start service getCustomFieldOption | type: {}", type);
        List<CustomFieldOption> options = customFieldOptionRepository.findAllByCustomField_TypeOrderByLabel(type);
        List<CustomFieldOptionSimplifiedDto> optionDtoList = customFieldMapper.toCustomFieldOptionSimplifiedDto(options);
        log.info("End service getCustomFieldOption | type: {} | options found: {}", type, optionDtoList.size());
        return optionDtoList;
    }

    /**
     * Creates or retrieves a custom field option.
     * If the option DTO has an ID, it retrieves the existing option.
     * If the option DTO has no ID, it tries to find by label first, else it throws.
     *
     * @param optionDto The custom field option DTO
     * @param customFieldType The type of custom field
     * @return The CustomFieldOption entity or null if not found and no ID provided
     */
    @Transactional
    public CustomFieldOption getCustomFieldOption(
            CustomFieldOptionSimplifiedDto optionDto,
            String customFieldType
    ) {
        log.info("Start service getOrCreateCustomFieldOption | optionDto: {}, type: {}", optionDto, customFieldType);

        if (optionDto == null) {
            log.warn("Option DTO is null, returning null");
            return null;
        }

        if (optionDto.getId() != null) {
            log.info("Option has ID: {}, using existing option", optionDto.getId());
            return CustomFieldOption.builder()
                    .id(optionDto.getId())
                    .build();
        }

        log.info("No ID provided, looking up option by label: {}", optionDto.getLabel());

        // Try to find existing option by label -- todo to change to avoid duplicate options with same label
        return customFieldOptionRepository.findByLabelAndCustomField_Type(optionDto.getLabel(), customFieldType)
                .orElse(null);
    }
}
