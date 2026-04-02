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
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.QDid;
import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DidRepositoryAdminImpl implements DidRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    public Page<Did> searchDids(String searchKey, String searchValue, Pageable pageable) {
        QDid qDid = QDid.did1;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qDid.count())
                .from(qDid)
                .where(predicate)
                .fetchOne();

        List<Did> results = queryFactory
                    .selectFrom(qDid)
                    .where(predicate)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(getOrderSpecifier(pageable, qDid))
                    .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QDid qDid = QDid.did1;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "did":
                    predicate = predicate.and(qDid.did.containsIgnoreCase(searchValue));
                    break;
                case "role":
                    predicate = predicate.and(qDid.role.eq(RoleType.valueOf(searchValue)));
                    break;
                case "status":
                    predicate = predicate.and(qDid.status.eq(DidDocStatus.valueOf(searchValue)));
                    break;
                case "version":
                    predicate = predicate.and(qDid.version.eq(Short.valueOf(searchValue)));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QDid qDid) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qDid.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "did":
                    orders.add(new OrderSpecifier<>(direction, qDid.did));
                    break;
                case "role":
                    orders.add(new OrderSpecifier<>(direction, qDid.role));
                    break;
                case "status":
                    orders.add(new OrderSpecifier<>(direction, qDid.status));
                    break;
                case "version":
                    orders.add(new OrderSpecifier<>(direction, qDid.version));
                    break;
                case "terminatedTime":
                    orders.add(new OrderSpecifier<>(direction, qDid.terminatedTime));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qDid.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
