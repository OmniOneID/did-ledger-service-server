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
import org.omnione.did.base.db.domain.QVcSchemaInfo;
import org.omnione.did.base.db.domain.VcSchemaInfo;
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
public class VcSchemaRepositoryAdminImpl implements VcSchemaRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    public Page<VcSchemaInfo> searchVcSchema(String searchKey, String searchValue, Pageable pageable) {
        QVcSchemaInfo qVcSchemaInfo = QVcSchemaInfo.vcSchemaInfo;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qVcSchemaInfo.count())
                .from(qVcSchemaInfo)
                .where(predicate)
                .fetchOne();

        List<VcSchemaInfo> results = queryFactory
                    .selectFrom(qVcSchemaInfo)
                    .where(predicate)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(getOrderSpecifier(pageable, qVcSchemaInfo))
                    .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QVcSchemaInfo qVcSchemaInfo = QVcSchemaInfo.vcSchemaInfo;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "schemaId":
                    predicate = predicate.and(qVcSchemaInfo.schemaId.containsIgnoreCase(searchValue));
                    break;
                case "title":
                    predicate = predicate.and(qVcSchemaInfo.title.containsIgnoreCase(searchValue));
                    break;
                case "version":
                    predicate = predicate.and(qVcSchemaInfo.version.containsIgnoreCase(searchValue));
                    break;
                case "description":
                    predicate = predicate.and(qVcSchemaInfo.description.containsIgnoreCase(searchValue));
                    break;
                case "did":
                    predicate = predicate.and(qVcSchemaInfo.did.containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QVcSchemaInfo qVcSchemaInfo) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qVcSchemaInfo.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "schemaId":
                    orders.add(new OrderSpecifier<>(direction, qVcSchemaInfo.schemaId));
                    break;
                case "title":
                    orders.add(new OrderSpecifier<>(direction, qVcSchemaInfo.title));
                    break;
                case "version":
                    orders.add(new OrderSpecifier<>(direction, qVcSchemaInfo.version));
                    break;
                case "description":
                    orders.add(new OrderSpecifier<>(direction, qVcSchemaInfo.description));
                    break;
                case "did":
                    orders.add(new OrderSpecifier<>(direction, qVcSchemaInfo.did));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qVcSchemaInfo.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
