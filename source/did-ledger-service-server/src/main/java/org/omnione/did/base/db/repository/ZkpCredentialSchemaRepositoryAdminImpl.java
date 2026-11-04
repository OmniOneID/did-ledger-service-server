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
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.QZkpCredentialSchema;
import org.omnione.did.base.db.domain.ZkpCredentialSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ZkpCredentialSchemaRepositoryAdminImpl implements ZkpCredentialSchemaRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ZkpCredentialSchema> searchZkpCredentialSchema(String searchKey, String searchValue, Pageable pageable) {
        QZkpCredentialSchema q = QZkpCredentialSchema.zkpCredentialSchema;
        BooleanExpression predicate = buildPredicate(q, searchKey, searchValue);

        long total = queryFactory
                .select(q.count())
                .from(q)
                .where(predicate)
                .fetchOne();

        List<ZkpCredentialSchema> results = queryFactory
                .selectFrom(q)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, q))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression buildPredicate(QZkpCredentialSchema q, String searchKey, String searchValue) {
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "schemaId":
                    predicate = predicate.and(q.schemaId.containsIgnoreCase(searchValue));
                    break;
                case "name":
                    predicate = predicate.and(q.name.containsIgnoreCase(searchValue));
                    break;
                case "version":
                    predicate = predicate.and(q.version.containsIgnoreCase(searchValue));
                    break;
                case "tag":
                    predicate = predicate.and(q.tag.containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QZkpCredentialSchema q) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, q.createdAt));
        }

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "schemaId":
                    orders.add(new OrderSpecifier<>(direction, q.schemaId));
                    break;
                case "name":
                    orders.add(new OrderSpecifier<>(direction, q.name));
                    break;
                case "version":
                    orders.add(new OrderSpecifier<>(direction, q.version));
                    break;
                case "tag":
                    orders.add(new OrderSpecifier<>(direction, q.tag));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, q.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
