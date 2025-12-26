package my.helper.querydsl_utils.servise;

import com.querydsl.core.types.Expression;
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
public abstract class AbstractProjectionSelectService<E, D> extends AbstractSelectService {

    private final Expression<D> expression;
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityPathBase<E> entityPathBase;

    public List<D> findAllByFilters(List<Filter> filters) {
        return findAllByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filters));
    }

    public List<D> findAllByPredicate(List<Predicate> predicates) {
        return modifyQuery(jpaQueryFactory.select(expression).from(entityPathBase))
                .where(predicates.toArray(Predicate[]::new))
                .fetch();
    }

    public Page<D> getPageByFilters(List<Filter> filters, Pageable pageable) {
        return getPageByPredicate(FilterToPredicateMapper.getPredicates(getFieldMap(), filters), pageable);
    }

    public Page<D> getPageByPredicate(List<Predicate> predicates, Pageable pageable) {
        JPAQuery<D> query = modifyQuery(jpaQueryFactory.select(expression).from(entityPathBase))
                .where(predicates.toArray(Predicate[]::new));
        Long total = query.clone().select(entityPathBase.count()).fetchOne();
        List<D> content = new ArrayList<>();
        if (total != 0) {
            content = pageable.getSort().isUnsorted() ?
                    query.fetch() : query.orderBy(getOrderSpecifiers(pageable)).fetch();
        }
        return new PageImpl<>(content, pageable, total);
    }

    protected abstract JPAQuery<D> modifyQuery(JPAQuery<D> query);
}
