/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.base.db.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.omnione.did.base.db.domain.QDid;
import org.omnione.did.base.db.domain.QDidDocumentStatusHistory;
import org.omnione.did.repository.v1.admin.dto.did.DidDocumentStatusHistoryWithDidDto;
import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DidDocumentStatusHistoryRepositoryAdminImpl implements DidDocumentStatusHistoryRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    public Page<DidDocumentStatusHistory> searchDidDocumentStatusHistories(String searchKey, String searchValue, Pageable pageable) {
        QDidDocumentStatusHistory qDidDocumentStatusHistory = QDidDocumentStatusHistory.didDocumentStatusHistory;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qDidDocumentStatusHistory.count())
                .from(qDidDocumentStatusHistory)
                .where(predicate)
                .fetchOne();

        List<DidDocumentStatusHistory> results = queryFactory
                .selectFrom(qDidDocumentStatusHistory)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qDidDocumentStatusHistory))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<DidDocumentStatusHistoryWithDidDto> searchDidDocumentStatusHistoriesWithDid(String searchKey, String searchValue, Pageable pageable) {
        QDidDocumentStatusHistory qDidDocumentStatusHistory = QDidDocumentStatusHistory.didDocumentStatusHistory;
        QDid qDid = QDid.did1;
        BooleanExpression predicate = buildPredicateWithDid(searchKey, searchValue, qDidDocumentStatusHistory, qDid);

        long total = queryFactory
                .select(qDidDocumentStatusHistory.count())
                .from(qDidDocumentStatusHistory)
                .leftJoin(qDid).on(qDidDocumentStatusHistory.didId.eq(qDid.id))
                .where(predicate)
                .fetchOne();

        List<DidDocumentStatusHistoryWithDidDto> results = queryFactory
                .select(Projections.constructor(DidDocumentStatusHistoryWithDidDto.class,
                        qDidDocumentStatusHistory.id,
                        qDidDocumentStatusHistory.didId,
                        qDid.did,
                        qDidDocumentStatusHistory.version,
                        qDidDocumentStatusHistory.fromStatus,
                        qDidDocumentStatusHistory.toStatus,
                        qDidDocumentStatusHistory.reason,
                        qDidDocumentStatusHistory.changedAt,
                        qDidDocumentStatusHistory.createdAt,
                        qDidDocumentStatusHistory.updatedAt))
                .from(qDidDocumentStatusHistory)
                .leftJoin(qDid).on(qDidDocumentStatusHistory.didId.eq(qDid.id))
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifierWithDid(pageable, qDidDocumentStatusHistory, qDid))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QDidDocumentStatusHistory qDidDocumentStatusHistory = QDidDocumentStatusHistory.didDocumentStatusHistory;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "didId":
                    predicate = predicate.and(qDidDocumentStatusHistory.didId.eq(Long.valueOf(searchValue)));
                    break;
                case "version":
                    predicate = predicate.and(qDidDocumentStatusHistory.version.eq(Short.valueOf(searchValue)));
                    break;
                case "fromStatus":
                    predicate = predicate.and(qDidDocumentStatusHistory.fromStatus.eq(DidDocStatus.valueOf(searchValue)));
                    break;
                case "toStatus":
                    predicate = predicate.and(qDidDocumentStatusHistory.toStatus.eq(DidDocStatus.valueOf(searchValue)));
                    break;
                case "reason":
                    predicate = predicate.and(qDidDocumentStatusHistory.reason.containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public BooleanExpression buildPredicateWithDid(String searchKey, String searchValue, QDidDocumentStatusHistory qDidDocumentStatusHistory, QDid qDid) {
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "did":
                    predicate = predicate.and(qDid.did.containsIgnoreCase(searchValue));
                    break;
                case "version":
                    predicate = predicate.and(qDidDocumentStatusHistory.version.eq(Short.valueOf(searchValue)));
                    break;
                case "fromStatus":
                    predicate = predicate.and(qDidDocumentStatusHistory.fromStatus.eq(DidDocStatus.valueOf(searchValue)));
                    break;
                case "toStatus":
                    predicate = predicate.and(qDidDocumentStatusHistory.toStatus.eq(DidDocStatus.valueOf(searchValue)));
                    break;
                case "reason":
                    predicate = predicate.and(qDidDocumentStatusHistory.reason.containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QDidDocumentStatusHistory qDidDocumentStatusHistory) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, qDidDocumentStatusHistory.changedAt));
        }

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "id":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.id));
                    break;
                case "didId":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.didId));
                    break;
                case "version":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.version));
                    break;
                case "fromStatus":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.fromStatus));
                    break;
                case "toStatus":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.toStatus));
                    break;
                case "reason":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.reason));
                    break;
                case "changedAt":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.changedAt));
                    break;
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.createdAt));
                    break;
                case "updatedAt":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.updatedAt));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.DESC, qDidDocumentStatusHistory.changedAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    public OrderSpecifier<?>[] getOrderSpecifierWithDid(Pageable pageable, QDidDocumentStatusHistory qDidDocumentStatusHistory, QDid qDid) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, qDidDocumentStatusHistory.changedAt));
        }

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "id":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.id));
                    break;
                case "did":
                    orders.add(new OrderSpecifier<>(direction, qDid.did));
                    break;
                case "version":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.version));
                    break;
                case "fromStatus":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.fromStatus));
                    break;
                case "toStatus":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.toStatus));
                    break;
                case "reason":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.reason));
                    break;
                case "changedAt":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.changedAt));
                    break;
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.createdAt));
                    break;
                case "updatedAt":
                    orders.add(new OrderSpecifier<>(direction, qDidDocumentStatusHistory.updatedAt));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.DESC, qDidDocumentStatusHistory.changedAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
