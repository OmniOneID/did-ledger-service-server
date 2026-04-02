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
import org.omnione.did.base.db.constant.ApiKeyRole;
import org.omnione.did.base.db.domain.ApiKey;
import org.omnione.did.base.db.domain.QApiKey;
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
public class ApiKeyRepositoryApiKeyImpl implements ApiKeyRepositoryApiKey {
    private final JPAQueryFactory queryFactory;

    public Page<ApiKey> searchApiKeys(String searchKey, String searchValue, Pageable pageable) {
        QApiKey qApiKey = QApiKey.apiKey1;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qApiKey.count())
                .from(qApiKey)
                .where(predicate)
                .fetchOne();

        List<ApiKey> results = queryFactory
                    .selectFrom(qApiKey)
                    .where(predicate)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(getOrderSpecifier(pageable, qApiKey))
                    .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QApiKey qApiKey = QApiKey.apiKey1;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "name":
                    predicate = predicate.and(qApiKey.name.containsIgnoreCase(searchValue));
                    break;
                case "maskedApiKey":
                    predicate = predicate.and(qApiKey.maskedApiKey.containsIgnoreCase(searchValue));
                    break;
                case "role":
                    predicate = predicate.and(qApiKey.role.eq(ApiKeyRole.valueOf(searchValue)));
                    break;
                case "isActive":
                    predicate = predicate.and(qApiKey.isActive.eq(Boolean.parseBoolean(searchValue)));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QApiKey qApiKey) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, qApiKey.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "name":
                    orders.add(new OrderSpecifier<>(direction, qApiKey.name));
                    break;
                case "role":
                    orders.add(new OrderSpecifier<>(direction, qApiKey.role));
                    break;
                case "expiresAt":
                    orders.add(new OrderSpecifier<>(direction, qApiKey.expiresAt));
                    break;
                case "lastUsedAt":
                    orders.add(new OrderSpecifier<>(direction, qApiKey.lastUsedAt));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.DESC, qApiKey.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

}
