package my.utils.querydsl_utils.servise;

import my.utils.querydsl_utils.servise.other.field.FieldInfo;
import my.utils.querydsl_utils.servise.other.field.FieldInfoDto;
import my.utils.querydsl_utils.servise.other.field.FieldType;
import my.utils.querydsl_utils.servise.other.filter.FilterGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonFieldService {

    private final Map<String, AbstractEntitySelectService<?>> entitySelectServices;
    private final Map<String, AbstractProjectionSelectService<?, ?>> projectionSelectServiceMap;

    public CommonFieldService(List<AbstractEntitySelectService<?>> entitySelectServices,
                              List<AbstractProjectionSelectService<?, ?>> projectionSelectServices) {

        this.entitySelectServices = initServiceMap(entitySelectServices, 0);
        this.projectionSelectServiceMap = initServiceMap(projectionSelectServices, 1);
    }

    private static <S> Map<String, S> initServiceMap(List<S> services, int genericIndex) {

        Map<String, S> map = new HashMap<>();
        for (S s : services) {
            Type gs = s.getClass().getGenericSuperclass();
            if (!(gs instanceof ParameterizedType pt)) continue;
            Type type = pt.getActualTypeArguments()[genericIndex];
            Class<?> clazz = (Class<?>) type;
            String masterType = clazz.getSimpleName();
            map.put(masterType, s);
        }

        return map;
    }

    public List<FieldInfoDto> getFieldInfoDto(String masterType) {
        if (entitySelectServices.containsKey(masterType)) {
            return serviceMapToFieldInfoDtos(entitySelectServices, masterType);
        } else if (projectionSelectServiceMap.containsKey(masterType)) {
            return serviceMapToFieldInfoDtos(projectionSelectServiceMap, masterType);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }

    private List<FieldInfoDto> serviceMapToFieldInfoDtos(Map<String, ? extends AbstractSelectService> serviceMap,
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

    public List<?> findDistinctFieldValues(String masterType, String field, List<FilterGroup> filterGroups) {
        if (entitySelectServices.containsKey(masterType)) {
            return entitySelectServices.get(masterType).findDistinctFieldValuesByFilterGroups(field, filterGroups);
        } else if (projectionSelectServiceMap.containsKey(masterType)) {
            return projectionSelectServiceMap.get(masterType).findDistinctFieldValuesByFilterGroups(field, filterGroups);
        }
        throw new RuntimeException("Unsupported MasterType: " + masterType);
    }
}
