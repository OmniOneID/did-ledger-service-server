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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.omnione.did.base.db.domain.QVcStatusHistory;
import org.omnione.did.base.db.domain.VcStatusHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VcStatusHistoryRepositoryAdminImpl implements VcStatusHistoryRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    public Page<VcStatusHistory> searchVcStatusHistory(String searchKey, String searchValue, Pageable pageable) {
        QVcStatusHistory qVcStatusHistory = QVcStatusHistory.vcStatusHistory;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qVcStatusHistory.count())
                .from(qVcStatusHistory)
                .where(predicate)
                .fetchOne();

        List<VcStatusHistory> results = queryFactory
                .selectFrom(qVcStatusHistory)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qVcStatusHistory))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QVcStatusHistory qVcStatusHistory = QVcStatusHistory.vcStatusHistory;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "vcId":
                    predicate = predicate.and(qVcStatusHistory.vcId.containsIgnoreCase(searchValue));
                    break;
                case "fromStatus":
                    predicate = predicate.and(qVcStatusHistory.fromStatus.stringValue().containsIgnoreCase(searchValue));
                    break;
                case "toStatus":
                    predicate = predicate.and(qVcStatusHistory.toStatus.stringValue().containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QVcStatusHistory qVcStatusHistory) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, qVcStatusHistory.changedAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "vcId":
                    orders.add(new OrderSpecifier<>(direction, qVcStatusHistory.vcId));
                    break;
                case "fromStatus":
                    orders.add(new OrderSpecifier<>(direction, qVcStatusHistory.fromStatus));
                    break;
                case "toStatus":
                    orders.add(new OrderSpecifier<>(direction, qVcStatusHistory.toStatus));
                    break;
                case "changedAt":
                    orders.add(new OrderSpecifier<>(direction, qVcStatusHistory.changedAt));
                    break;
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, qVcStatusHistory.createdAt));
                    break;
                case "updatedAt":
                    orders.add(new OrderSpecifier<>(direction, qVcStatusHistory.updatedAt));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.DESC, qVcStatusHistory.changedAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
