package my.utils.querydsl_utils.servise;

import my.utils.querydsl_utils.servise.other.field.FieldInfo;
import my.utils.querydsl_utils.servise.other.field.FieldInfoDto;
import my.utils.querydsl_utils.servise.other.field.FieldType;
import my.utils.querydsl_utils.servise.other.filter.FilterGroup;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonFieldService {

    private final Map<String, AbstractSelectService> selectServices;

    public CommonFieldService(List<AbstractSelectService> entitySelectServices) {

        this.selectServices = initServiceMap(entitySelectServices);
    }

    public List<FieldInfoDto> getFieldInfoDto(String masterType) {
        if (selectServices.containsKey(masterType)) {
            return serviceMapToFieldInfoDtos(selectServices, masterType);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    private List<FieldInfoDto> serviceMapToFieldInfoDtos(Map<String, AbstractSelectService> serviceMap,
                                                         String masterType) {

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

    public List<?> findDistinctFieldValuesByFilterGroups(String masterType, String field, List<FilterGroup> filterGroups) {
        if (selectServices.containsKey(masterType)) {
            return selectServices.get(masterType).findDistinctFieldValuesByFilterGroups(field, filterGroups);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    private Map<String, AbstractSelectService> initServiceMap(
            List<AbstractSelectService> services) {

        return services.stream()
                .collect(Collectors.toMap(
                        AbstractSelectService::getMasterType,
                        Function.identity(),
                        (a, b) -> {
                            String err = String
                                    .format("Duplicate Master Type: %s (%s %s)",
                                            a.getMasterType(), a.getClass().getSimpleName(), b.getClass().getSimpleName());
                            throw new IllegalStateException(err);
                        }
                ));
    }
}
