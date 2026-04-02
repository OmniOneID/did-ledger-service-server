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
import org.omnione.did.base.db.domain.QVcMetadata;
import org.omnione.did.base.db.domain.VcMetadata;
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
public class VcMetadataRepositoryAdminImpl implements VcMetadataRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    public Page<VcMetadata> searchVcMetadata(String searchKey, String searchValue, Pageable pageable) {
        QVcMetadata qVcMetadata = QVcMetadata.vcMetadata;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qVcMetadata.count())
                .from(qVcMetadata)
                .where(predicate)
                .fetchOne();

        List<VcMetadata> results = queryFactory
                    .selectFrom(qVcMetadata)
                    .where(predicate)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(getOrderSpecifier(pageable, qVcMetadata))
                    .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QVcMetadata qVcMetadata = QVcMetadata.vcMetadata;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "vcId":
                    predicate = predicate.and(qVcMetadata.vcId.containsIgnoreCase(searchValue));
                    break;
                case "issuerDid":
                    predicate = predicate.and(qVcMetadata.issuerDid.containsIgnoreCase(searchValue));
                    break;
                case "subjectDid":
                    predicate = predicate.and(qVcMetadata.subjectDid.containsIgnoreCase(searchValue));
                    break;
                case "vcSchema":
                    predicate = predicate.and(qVcMetadata.vcSchema.containsIgnoreCase(searchValue));
                    break;
                case "status":
                    predicate = predicate.and(qVcMetadata.status.containsIgnoreCase(searchValue));
                    break;
                case "formatVersion":
                    predicate = predicate.and(qVcMetadata.formatVersion.containsIgnoreCase(searchValue));
                    break;
                case "language":
                    predicate = predicate.and(qVcMetadata.language.containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QVcMetadata qVcMetadata) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qVcMetadata.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "vcId":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.vcId));
                    break;
                case "issuerDid":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.issuerDid));
                    break;
                case "subjectDid":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.subjectDid));
                    break;
                case "vcSchema":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.vcSchema));
                    break;
                case "issuanceDate":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.issuanceDate));
                    break;
                case "validFrom":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.validFrom));
                    break;
                case "validUntil":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.validUntil));
                    break;
                case "status":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.status));
                    break;
                case "formatVersion":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.formatVersion));
                    break;
                case "language":
                    orders.add(new OrderSpecifier<>(direction, qVcMetadata.language));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qVcMetadata.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
