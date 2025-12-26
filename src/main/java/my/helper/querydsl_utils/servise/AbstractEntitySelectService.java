package my.helper.querydsl_utils.servise;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import my.helper.querydsl_utils.servise.other.Filter;
import my.helper.querydsl_utils.servise.other.FilterToPredicateMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractEntitySelectService<T> extends AbstractSelectService {

    private final EntityPathBase<T> entityPathBase;
    private final JPAQueryFactory jpaQueryFactory;

    public List<T> findAllByFilters(List<Filter> filters) {
        return findAllByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filters));
    }

    public List<T> findAllByPredicate(List<Predicate> predicates) {
        return jpaQueryFactory.select(entityPathBase)
                .from(entityPathBase)
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    public Page<T> getPageByFilters(List<Filter> filters, Pageable pageable) {
        return getPageByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filters), pageable);
    }

    public Page<T> getPageByPredicate(List<Predicate> predicates, Pageable pageable) {
        JPAQuery<T> query = jpaQueryFactory.select(entityPathBase)
                .from(entityPathBase).where(predicates.toArray(Predicate[]::new));
        Long total = query.clone().select(entityPathBase.count()).fetchOne();
        List<T> content = new ArrayList<>();
        if (total != 0) {
            content = query.orderBy(getOrderSpecifiers(pageable)).fetch();
        }
        return new PageImpl<>(content, pageable, total);
    }
}
