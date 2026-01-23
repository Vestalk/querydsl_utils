package my.utils.querydsl_utils.servise;

import lombok.RequiredArgsConstructor;
import my.utils.querydsl_utils.servise.other.field.FieldInfo;
import my.utils.querydsl_utils.servise.other.field.FieldInfoDto;
import my.utils.querydsl_utils.servise.other.field.FieldType;
import my.utils.querydsl_utils.servise.other.filter.FilterGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CommonService {

    private final Map<String, AbstractEntitySelectService<?>> entitySelectServiceMap;
    private final Map<String, AbstractProjectionSelectService<?, ?>> projectionSelectServiceMap;

    public List<?> findAllByFilters(String masterType, List<FilterGroup> filterGroups) {
        if (entitySelectServiceMap.containsKey(masterType)) {
            return entitySelectServiceMap.get(masterType).findAllByFilters(filterGroups);
        }
        if (projectionSelectServiceMap.containsKey(masterType)) {
            return projectionSelectServiceMap.get(masterType).findAllByFilters(filterGroups);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    public Page<?> getPageByFilters(String masterType, List<FilterGroup> filterGroups, Pageable pageable) {
        if (entitySelectServiceMap.containsKey(masterType)) {
            return entitySelectServiceMap.get(masterType).getPageByFilters(filterGroups, pageable);
        }
        if (projectionSelectServiceMap.containsKey(masterType)) {
            return projectionSelectServiceMap.get(masterType).getPageByFilters(filterGroups, pageable);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    public List<Map<String, Object>> findAllByFilters(String masterType, List<String> fields,
                                                      List<FilterGroup> filterGroups) {

        if (entitySelectServiceMap.containsKey(masterType)) {
            return entitySelectServiceMap.get(masterType).findAllByFilters(fields, filterGroups);
        }
        if (projectionSelectServiceMap.containsKey(masterType)) {
            return projectionSelectServiceMap.get(masterType).findAllByFilters(fields, filterGroups);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    public Page<Map<String, Object>> getPageByFilters(String masterType, List<String> fields,
                                                      List<FilterGroup> filterGroups, Pageable pageable) {

        if (entitySelectServiceMap.containsKey(masterType)) {
            return entitySelectServiceMap.get(masterType).getPageByFilters(fields, filterGroups, pageable);
        }
        if (projectionSelectServiceMap.containsKey(masterType)) {
            return projectionSelectServiceMap.get(masterType).getPageByFilters(fields, filterGroups, pageable);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    public List<FieldInfoDto> getFieldInfoDto(String masterType) {
        if (entitySelectServiceMap.containsKey(masterType)) {
            return serviceMapToFieldInfoDtos(entitySelectServiceMap, masterType);
        }
        if (projectionSelectServiceMap.containsKey(masterType)) {
            return serviceMapToFieldInfoDtos(projectionSelectServiceMap, masterType);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    public List<?> findDistinctFieldValuesByFilterGroups(String masterType, String field,
                                                         List<FilterGroup> filterGroups) {

        if (entitySelectServiceMap.containsKey(masterType)) {
            return entitySelectServiceMap.get(masterType).findDistinctFieldValuesByFilterGroups(field, filterGroups);
        }
        if (projectionSelectServiceMap.containsKey(masterType)) {
            return projectionSelectServiceMap.get(masterType).findDistinctFieldValuesByFilterGroups(field, filterGroups);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    private <T extends AbstractSelectService> List<FieldInfoDto> serviceMapToFieldInfoDtos(
            Map<String, T> serviceMap, String masterType) {

        return serviceMap.get(masterType)
                .getFieldMap()
                .entrySet()
                .stream()
                .map(entry -> {
                    String name = entry.getKey();
                    FieldInfo fieldInfo = entry.getValue();
                    String label = fieldInfo.getLabel();
                    FieldType type = fieldInfo.getType();
                    return new FieldInfoDto(name, label, type);
                })
                .toList();
    }
}
